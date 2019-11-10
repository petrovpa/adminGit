package com.bivgroup.services.gosuslygi;

import com.bivgroup.config.Config;
import com.bivgroup.utils.ParamGetter;
import static com.bivgroup.utils.ParamGetter.getStringParam;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import ru.CryptoPro.JCP.JCP;
import com.bivgroup.utils.RequestWorker;


import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.http.client.methods.HttpGet;

/**
 * Класс для получения маркера идентификации (idToken) и ФИО пользователя от сервисов ЕСИА для аутентификации пользователя в системе-клиенте.
 * Валидность данного маркера подтверждает успешную аутентификацию в сервисе ЕСИА.
 *
 * Для полученя idToken сначало необходимо получить авторизационный код (уникальный для каждого пользователя). <br><br>
 *
 * Общий сценарий взаимодействия данной библиотеки с внешней системой:
 * <UL>
 *     <LI>Интерфейс внешней системы, по нажатию на кнопку "Войти через ЕСИА", обращается к методу {@link #getAuthUrl()}. </LI>
 *     <LI>Затем интерфейс делает редирект по полученной ссылке и запоминает полученный параметр state.</LI>
 *     <LI>Пользователь проходит авторизацию в ЕСИА и дает согласие на выдачу прав на аутентификацию в системе-клиенте.</LI>
 *     <LI>ЕСИА перенаправляет пользователя обратно на сайт системы-клиента, интерфейс получает авторизационный код (code)
 *     новый state для дальнейшей аутентификации в параметрах redirect ссылки. Скриншоты в задаче (#13960).</LI>
 *     <LI>Если state от метода {@link #getAuthUrl()} совпал с новым state (пришедшим с авторизационным кодом), то интерфейс обращается к методу
 *     {@link #getAuthentificatedUserInfo(String)}, передавая полученный в предыдущем шаге authCode и переходит к следующему пункту,
 *     иначе выводит окно с ошибкой ("Невалидный авторизационный код").</LI>
 *     <LI>{@link #getAuthentificatedUserInfo(String)} формирует новый запрос на получение маркера идентификации (idToken), ФИО, даты рождения,
 *     номера снилс, номера инн, мобильного номера и сведений о документе удостоверяющем личность. Получая все эти данные,
 *     проводит валидацию idToken и в случае успешной проверки возвращает эти значения внешней системе.</LI>
 *     <LI>Если в ЛК есть пользователь с таким ФИО, ДР, то авторизуем его в ЛК и сохраняем его личный токен, иначе если совпадение не найдено
 *     или оно неоднозначно (несколько Ивановых Сергеев Петровичей родившихся 1 января, например) - интерфейс выводит ошибку.
 *     Тексты ошибок и их причины в ФТ (задача #13960).</LI>
 * </UL>
 *
 * @author eremeevas
 */
public class EsiaAuthentificator {

    private Logger logger = Logger.getLogger(EsiaAuthentificator.class);

    private final RequestWorker requestWorker = new RequestWorker();
    
    /**
     * Ссылка на сервис ЕСИА для получения авторизационного ключа
     */
    private final static String AUTH_URI = "AUTH_URI";
    /**
     * Ссылка на сервис ЕСИА для получения idToken
     */
    private final static String TOKEN_URI = "TOKEN_URI";
    
    /**
     * Ссылка на сервис ЕСИА для получения данных о клиенте
     */
    private final static String PRNS_URI = "PRNS_URI";
    
        
    /**
     * Ссылка на ЕСИА для логаута
     */
    private final static String ESIA_URI = "ESIA_URI";
    
    /**
     * Ссылка передаваемая сервису ЕСИА для редиректа (на страницу системы-клиента) после успешной выдачи авторизационного ключа
     */
    private final static String REDIRECT_URI = "REDIRECT_URI";

    /**
     * Перечесление запрашиваемой у ЕСИА информации о пользователе
     */
    private final static String SCOPE = "SCOPE";

    /**
     * Мнемоника системы-клиента выдаваемая при регистрации ИС в ЕСИА
     */
    private final static String CLIENT_ID = "CLIENT_ID";

    /**
     * Пароль к контейнеру ключей и сертификатов ЕСИА
     */
    private final static String CONTAINER_PASSWORD = "CONTAINER_PASSWORD";

    /**
     * Имя контейнера в криптопровайдере (JCP 2.0)
     */
    private final static String SIGNER_ALIAS_NAME = "SIGNER_ALIAS_NAME";

    /**
     * Имя класса имплементации (полное, с пакетом) цифровой подписи
     */
    private final static String ENCODE_IMPLEMENTATION_NAME = "ENCODE_IMPLEMENTATION_NAME";
    
    /**
     * Путь к сертификатам
     */
    private static final String CERTFILESPATH_CONFIG_NAME = "CERTSFILESPATH";


    /**
     * Конфиг параметр-флаг "показывать всегда" кнопку
     */
    private static final String ESIA_BUTTON_SHOW_ALWAYS = "ESIA_BUTTON_SHOW_ALWAYS";
    /**
     * Конфиг параметр-флаг "никогда не показывать" кнопку
     */
    private static final String ESIA_BUTTON_SHOW_NEVER = "ESIA_BUTTON_SHOW_NEVER";
    /**
     * Конфиг параметр-флпг "показывать по наличию параметра" кнопку
     */
    private static final String ESIA_BUTTON_SHOW_BY_PARAM = "ESIA_BUTTON_SHOW_BY_PARAM";

    /**
     * Конфигурационный файл
     */
    Config config;

    /**
     * Конструктор добавления конфига, для обеспечения абстракции подключаемой системы-клиента
     * @throws com.bivgroup.services.gosuslygi.EsiaAuthentificationException
     * @throws java.io.IOException
     */
    public EsiaAuthentificator () throws EsiaAuthentificationException, IOException {

        config = Config.getConfig("pa2signws");

    }

    private Map<String,Object> buildExceptionErrorResponse() {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("error","2");
        result.put("errorText","Внутренняя ошибка");
        return result;
    }

    private Map<String,Object> buildLogicErrorResponse() {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("errorText","Ссылку отображать не надо");
        result.put("error","1");
        return result;
    }

    private Map<String,Object> buildLinkResponse() {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("error","0");
        return result;
    }
 
    
    /**
     * Получение конфига для отображения или скрытия кнопки входа
     * @param params
     * @return 
     */    
    public Map<String, Object> getEsiaShowButtonConfig(Map<String, Object> params) {
          
            if (config.getParam(ESIA_BUTTON_SHOW_ALWAYS,null).equalsIgnoreCase("true"))
                return buildLinkResponse();
            if (config.getParam(ESIA_BUTTON_SHOW_NEVER,null).equalsIgnoreCase("true"))
                return buildLogicErrorResponse();
            if (config.getParam(ESIA_BUTTON_SHOW_BY_PARAM,null).equalsIgnoreCase("false"))
                return buildLogicErrorResponse();
            if (params.containsKey("showButtonESIA") && params.get("showButtonESIA").equals("true"))
                return buildLinkResponse();
            else
                return buildLogicErrorResponse();
    }

    /**
     * Метод вызывающийся при нажатии "Войти через ЕСИА" для получения авторизационного кода
     *
     * @return интерфейс содержащий готовую строку для вызова popup (скорее redirect) с интерфейса и state для валидации авторизационного кода
     * @throws EsiaAuthentificationException возникает при формировании возвращаемого URL
     * @throws java.net.URISyntaxException
     */
    public AuthRequestInfo getAuthUrl() throws EsiaAuthentificationException, URISyntaxException {

        URI authEsiaURI;
        URI callbackURI;

        callbackURI = new URI(config.getParam(REDIRECT_URI, null));
        authEsiaURI = new URI(config.getParam(AUTH_URI, null));

        //начинаем создавать нашу ссылку для return
        URIBuilder authUri;
        authUri = new URIBuilder(authEsiaURI);

        String requestDateParam = formatDateToESIA(new Date());
        authUri.addParameter("timestamp",requestDateParam); //добавляем timestamp - время/дата в нужном формате для ЕСИА
        authUri.addParameter("scope",config.getParam(SCOPE, null)); // все необходимые скопы с инфой о пользователе
        Base64UrlEncoder encoder = new Base64UrlEncoder();      //создаем encoder для добавления client_secret
        String stateTmp = UUID.randomUUID().toString();
        authUri.addParameter("client_secret",
                encoder.base64UrlEncode(createNewClientSecret(
                        config.getParam(SCOPE, null),
                        stateTmp,
                        config.getParam(CLIENT_ID, null),
                        requestDateParam)));

        authUri.addParameter("response_type","code");
        authUri.addParameter("redirect_uri", String.valueOf(callbackURI));
        authUri.addParameter("state",stateTmp);
//        authUri.addParameter("display","popup"); //TODO разкоментить при тестировании с интерфейсом для открытия в новом окне
        authUri.addParameter("client_id",config.getParam(CLIENT_ID, null));

        //маркер обновления не нужен - так как доступ необходим только в присутствии владельца
        authUri.addParameter("access_type", "online");

        logger.debug("Request params : ");
        for (NameValuePair paramPair : authUri.getQueryParams())
        {
            logger.debug(paramPair.getName() + " = " + paramPair.getValue());
            if (paramPair.getName().equals("client_secret")) {
                logger.debug("Decoded client_secret : ");
                logger.debug(new String(encoder.base64UrlDecode(paramPair.getValue())));
            }
        }

        String returnURL;
        try {
            returnURL = authUri.build().toURL().toString();
        } catch (MalformedURLException | URISyntaxException e) {
            logger.error(e.getMessage());
            throw new EsiaAuthentificationException("EsiaAuthentificator: authUri build exception - bad format",e);
        }
        logger.debug("Final URL: " + returnURL);
        return new AuthRequestInfo() {
            @Override
            public String getURL() {
                return returnURL;
            }

            @Override
            public String getState() {
                return stateTmp;
            }
            
            @Override
            public String getErrorText() {
                return "";
            }
            
            @Override
            public String getErrorCode() {
                return "0";
            }
        };
    }

    /**
     * Метод получения всевозможных данных о пользователе от ЕСИА, необходимых для авторизации в системе-клиенте.
     * Формирует новый запрос на получение данных, проводит валидацию пришедшего маркера идентификации и
     * отдает внешней системе для сохранения в случае успешной проверки.
     *
     * @param authCode код авторизации
     * @return {@link AuthorizedUserInfo} - интерфейс для получения инфомации об авторизованном пользователе
     * @throws EsiaAuthentificationException
     */
    public AuthorizedUserInfo getAuthentificatedUserInfo(String authCode) throws EsiaAuthentificationException {

        UserInfoResponse userInfoResponse;
        EsiaMarkerAnswer esiaMarkerAnswer;
        DocUserResponse docUserResponse;
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {

            HttpPost request = new HttpPost(config.getParam(TOKEN_URI, null));

            ArrayList<NameValuePair> postParameters;            //заполняем необходимые параметры запроса по документации
            postParameters = new ArrayList<>();
            postParameters.add(new BasicNameValuePair("client_id", config.getParam(CLIENT_ID, null)));
            postParameters.add(new BasicNameValuePair("code", authCode));
            postParameters.add(new BasicNameValuePair("grant_type", "authorization_code"));
            Base64UrlEncoder encoder = new Base64UrlEncoder();
            String state = UUID.randomUUID().toString();
            String requestDateParam = formatDateToESIA(new Date());
            postParameters.add(new BasicNameValuePair("client_secret",
                    encoder.base64UrlEncode((createNewClientSecret(
                            config.getParam(SCOPE, null) ,
                            state,
                            config.getParam(CLIENT_ID, null),
                            requestDateParam)))));
            postParameters.add(new BasicNameValuePair("state", state));
            postParameters.add(new BasicNameValuePair("redirect_uri", config.getParam(REDIRECT_URI, null)));
            postParameters.add(new BasicNameValuePair("scope", config.getParam(SCOPE, null)));
            postParameters.add(new BasicNameValuePair("timestamp", requestDateParam));
            postParameters.add(new BasicNameValuePair("token_type", "Bearer"));

            request.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));

            HttpResponse response = client.execute(request);

            String esiaMarkerAnswerResponse = EntityUtils.toString(response.getEntity());
            logger.debug("EsiaIdToken + esiaMarkerAnswer Response : " + esiaMarkerAnswerResponse);
            
            esiaMarkerAnswer = requestWorker.deserializeJSON(esiaMarkerAnswerResponse, EsiaMarkerAnswer.class);
          

            if (!ValidateIdToken(esiaMarkerAnswer.getIdToken()))
                throw new EsiaAuthentificationException("EsiaAuthentificator : idToken validation failed!");
            
            
            String[] marker_parts = esiaMarkerAnswer.getIdToken().split("\\.");

            String header =  new String(encoder.base64UrlDecode(marker_parts[0]));
            String payload = new String(encoder.base64UrlDecode(marker_parts[1]));
         
            Map<String, Map<String, Integer>> payloadMap = new HashMap<String, Map<String, Integer>>();
            payloadMap = requestWorker.deserializeJSON(payload, HashMap.class);
                     
            Map<String, Integer> oidMap = payloadMap.get("urn:esia:sbj");
            Integer oid = oidMap.get("urn:esia:sbj:oid");
            
            String prns_uri = config.getParam(PRNS_URI, null);
            HttpGet requestUserInfo = new HttpGet(prns_uri + oid.toString());
            requestUserInfo.setHeader("Content-Type", "application/x-www-form-urlencoded");
            requestUserInfo.setHeader("Authorization", "Bearer " + esiaMarkerAnswer.getAccessToken());

            HttpResponse responseUserInfo = client.execute(requestUserInfo);
            String userInfoResponseStr = EntityUtils.toString(responseUserInfo.getEntity(), "UTF-8");
            logger.debug("UserInfo Response : " + userInfoResponseStr);
             
            userInfoResponse = requestWorker.deserializeJSON(userInfoResponseStr, UserInfoResponse.class);
            
            HttpGet requestDocUserInfo = new HttpGet(prns_uri + oid.toString() + "/docs/" + userInfoResponse.getIdDoc());
            
            requestDocUserInfo.setHeader("Content-Type", "application/x-www-form-urlencoded");
            requestDocUserInfo.setHeader("Authorization", "Bearer " + esiaMarkerAnswer.getAccessToken());
            
            HttpResponse responseDocUserInfo = client.execute(requestDocUserInfo);
         
            String docUserInfoResponseStr = EntityUtils.toString(responseDocUserInfo.getEntity(), "UTF-8");
            
            docUserResponse = requestWorker.deserializeJSON(docUserInfoResponseStr, DocUserResponse.class);
            
            
          

        } catch (IOException e) {
            throw new EsiaAuthentificationException("EsiaAuthentificator : IOException in getAuthentificatedUserInfo() method");
        }

        return new AuthorizedUserInfo() {
            @Override
            public String getIdToken() {
                return esiaMarkerAnswer.getIdToken();
            }
            
            @Override
            public String getFirstName() {
                return userInfoResponse.getFirstName();
            }
            
            @Override
            public String getLastName() {
                return userInfoResponse.getLastName();
            }
            
            @Override
            public String getMiddleName() {
                return userInfoResponse.getMiddleName();
            }

            @Override
            public String getBirthDate() {
                return userInfoResponse.getBirthDate();
            }

            @Override
            public String getGender() {
                return userInfoResponse.getGender();
            }

            @Override
            public String getSnils() {
                return userInfoResponse.getSnils();
            }

            @Override
            public String getInn() {
                return userInfoResponse.getInn();
            }

            @Override
            public String getNumber() {
                return docUserResponse.getNumber();
            }
            
            @Override
            public String getSeries() {
                return docUserResponse.getSeries();
            }

            @Override
            public String getMobileNumber() {
                return userInfoResponse.getMobileNumber();
            }
            
            @Override
            public String getStatusAccount() {
                return userInfoResponse.getStatusAccount();
            }
            
            @Override
            public boolean getVerifyingAccount() {
                return userInfoResponse.isVerifyingAccount();
            }

            @Override
            public String getVrfStuDoc() {
                return docUserResponse.getVrfStu();
            }
            
            @Override
            public String getTypeDoc() {
                return docUserResponse.getType();
            }
        };
    }

    /**
     * Метод для валидации полученного маркера доступа от ЕСИА.
     * <br><br>
     * После получения маркера идентификации система-клиент должна произвести валидацию
     * маркера идентификации, которая включает в себя следующие проверки:
     * <br>
     * 1.Проверка идентификатора (мнемоники) ЕСИА, содержащейся в маркере идентификации.
     * <br>
     * 2. Проверка идентификатора (мнемоники) системы-клиента, т.е. именно система-клиент
     * должна быть указана в качестве адресата маркера идентификации.
     * <br>
     * 3. Проверка подписи маркера идентификации (с использованием указанного в маркере
     * алгоритма).
     * <br>
     * 4. Текущее время должно быть не позднее, чем время прекращения срока действия маркера
     * идентификации.
     *
     * @param id_token маркер идентификации
     * @return true если маркер валиден, иначе false
     * @throws EsiaAuthentificationException
     */
    private boolean ValidateIdToken(String id_token) throws EsiaAuthentificationException {

        //TODO допилить валидацию во время дебага (надо увидеть глазами) ответ от ЕСИА
        if (id_token != null)
        {
            for (String partOfToken : id_token.split(".")) {
                Base64UrlEncoder encoder = new Base64UrlEncoder();
                try {
                    encoder.base64UrlDecode(partOfToken);
                } catch (Exception e) {
                    throw new EsiaAuthentificationException("EsiaAuthentificator: id_token decoding exception");
                }
            }

        }
        else throw new EsiaAuthentificationException("EsiaAuthentificator: id_token is NULL");
        return true;
    }


    /**
     * Создание параметра client_secret (название из документации) для запросов к ЕСИА
     *
     * @param state используемый в формируемом запросе
     * @param date дата запроса (используется в нескольких параметрах (timestamp), поэтому сохраняется)
     * @return подписанный параметр client_secret для запроса
     */
    private byte[] createNewClientSecret(String scope, String state, String clientId, String date) throws EsiaAuthentificationException {
       // String certFiles = "/home/andreyboo/certificates/tensor.cer;/home/andreyboo/certificates/golovnoy.cer"; //TODO локальный путь до данных сертефикатов
        String certFiles = config.getParam(CERTFILESPATH_CONFIG_NAME, null); 
        Signer signer;
        try {
            signer = loadService(Signer.class);
        } catch (Exception e) {
                    throw new EsiaAuthentificationException("");
        }
        final String secretStringForSign = scope + date + clientId + state;

        byte[] signedSecret;
        try {
/*            signedSecret = signer.signFromStore(JCP.HD_STORE_NAME,null,config.getString(SIGNER_ALIAS_NAME),
                    config.getString(CONTAINER_PASSWORD), JCP.PROVIDER_NAME,secretStringForSign.getBytes(StandardCharsets.UTF_8));*/
            signedSecret = signer.signFromStoreAndCertFiles (JCP.HD_STORE_NAME,null,config.getParam(SIGNER_ALIAS_NAME, null),
                    config.getParam(CONTAINER_PASSWORD, null),certFiles, JCP.PROVIDER_NAME,secretStringForSign.getBytes(StandardCharsets.UTF_8));

        } catch (Exception e) {
            throw new EsiaAuthentificationException("EsiaAuthentificator : EsiaGostSigner.signFromStore failed");
        }

        logger.debug("Param <client_secret> without encode: ");
        logger.debug(signedSecret);
        return signedSecret;
    }

    /**
     * Формат даты под требования ЕСИА из документации
     *
     * @param date текущая дата
     * @return дата в требуемом формате
     */
    private String formatDateToESIA(Date date) {
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss Z");
//        SimpleDateFormat formatForDateNow = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss +0000");
        return  formatForDateNow.format(date);
    }

    /**
     * Логаут из системы (пока не знаю зачем, но пусть будет)
     */
    public void Logout () throws IOException {
            String client_id = config.getParam(CLIENT_ID, null);
            String redirect_uri = config.getParam(REDIRECT_URI, null);
            String esia_uri = config.getParam(ESIA_URI, null);
            HttpGet requestLogout = new HttpGet(esia_uri+"/idp/ext/Logout?client_id="+client_id+"&redirect_uri="+redirect_uri);
            requestLogout.setHeader("Content-Type", "application/x-www-form-urlencoded");
           // requestUserInfo.setHeader("Authorization", "Bearer " + esiaMarkerAnswer.getAccessToken());
            CloseableHttpClient client = HttpClientBuilder.create().build();
            HttpResponse responseLogout = client.execute(requestLogout);
            String requestLogoutStr = EntityUtils.toString(responseLogout.getEntity(), "UTF-8");
            logger.debug("UserInfo Response : " + requestLogoutStr);
    }
    
    public static <T> T loadService(Class<T> clazz) {
        ServiceLoader<T> impl = ServiceLoader.load(clazz);

        T result = null;
        for (T loadedImpl : impl) {
            result = loadedImpl;
            if (result != null) {
                break;
            }
        }

        if (result == null) throw new RuntimeException(
                "Cannot find implementation for: " + clazz);

        return result;
    }

}
