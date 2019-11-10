/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.bivsberposws.facade.pos.custom;

import static com.bivgroup.services.bivsberposws.system.Constants.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.log4j.Logger;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.WsConstants;
import static ru.diasoft.services.inscore.system.WsConstants.*;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import static ru.diasoft.utils.XMLUtil.convertFloatToDate;

/**
 *
 * @author Admin
 */
@BOName("FlexteraContractCustom")
public class FlexteraContractCustomFacade extends BaseFacade {

    // наименования ключей
    //private static final String METHOD_NAME = "METHODNAME";
    private static final String CONTRACT_ID = "CONTRID";
    private static final String FLEXTERA = "FLEXTERA";
    private static final String EXPRESS = "EXPRESS";

    private final Class THIS_FACADE_CLASS = this.getClass();

    // объекты для работы с датами и числами
    private static final DateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
    private static final NumberFormat moneyFormatter = NumberFormat.getNumberInstance(new Locale("ru"));

    private final Logger log = Logger.getLogger(THIS_FACADE_CLASS);

    // соответствие названий рисков именам ключей для сумм в программе из расширенных атрибутов
    // (для формирования стандартного списка рисков из расширенных атрибутов программы для HIB)
    private static final String[][] namesAndKeys = {
        {"Отделка", "interiorAndEquipment"},
        {"Движимое имущество", "movableProperty"},
        {"Гражданская ответственность", "civilLiability"}
    };

    // списки ключей для проверки необходимости отдельных запросов дополнительных сведений
    // заполняются при формировании infoMap - ссылка на список передается при объявлении field, сам список дополняется при указании inputNames для field
    private static ArrayList<String> contactKeys = new ArrayList<String>();
    private static ArrayList<String> addressKeys = new ArrayList<String>();
    private static ArrayList<String> documentKeys = new ArrayList<String>();

    //private final Method[] methods = this.getClass().getDeclaredMethods();
    private static Info infoMap;

    public FlexteraContractCustomFacade() {
        super();

        moneyFormatter.setMaximumFractionDigits(2);
        moneyFormatter.setMinimumFractionDigits(2);

        infoMap = new Info();

        // <editor-fold defaultstate="collapsed" desc="инициализация infoMap">
        infoMap.block("СТРАХОВАТЕЛЬ")
                //
                .field("Фамилия Имя Отчество:")
                .showIfEmpty()
                .inputNames("INSFULLNAME", "INSBRIEFNAME", "INSUREDNAME")
                //
                .field("Дата рождения:")
                .inputNames("INSBIRTHDATE")
                .formatter("getDateStr")
                //
                .field("Место рождения:")
                .inputNames("INSBIRTHPLACE")
                //
                .field("Пол:")
                .inputNames("INSGENDER")
                .formatter("getGenderStr");

        infoMap.block("ДОКУМЕНТ")
                //
                .field("Вид документа:", documentKeys)
                //.showIfEmpty()
                .inputNames("INSDOCTYPE", "DOCTYPE")
                .formatter("getDocNameByType")
                //
                .field("Вид документа:", documentKeys)
                .inputNames("INSDOCTYPENAME", "DOCTYPENAME")
                //
                .field("Серия:", documentKeys)
                .showIfEmpty()
                .inputNames("INSDOCSERIES", "DOCSERIES")
                //
                .field("Номер:", documentKeys)
                .showIfEmpty()
                .inputNames("INSDOCNUMBER", "DOCNUMBER")
                //
                .field("Дата выдачи:", documentKeys)
                .inputNames("INSISSUEDATE", "ISSUEDATE")
                .formatter("getDateStr")
                //
                .field("Кем выдан:", documentKeys)
                .inputNames("INSISSUEDBY", "ISSUEDBY")
                //
                .field("Код подразделения:", documentKeys)
                .inputNames("INSISSUERCODE", "ISSUERCODE");

        infoMap.block("КОНТАКТНЫЕ ДАННЫЕ")
                //
                // todo: дополнить стандартными видами контактных данных
                .field("Мобильный телефон:", contactKeys)
                .inputNames("MobilePhone", "INSPHONE")
                //
                .field("Личная электронная почта:", contactKeys)
                .inputNames("PersonalEmail", "INSEMAIL")
                //
                .field("Место жительства (регистрации):", addressKeys)
                .inputNames("INSADDRESSTEXT1", "RegisterAddress")
                //
                //.field("Почтовый индекс:")
                //.inputNames("INSPOSTALCODE");
                //
                .field("Место пребывания (нахождения):", addressKeys)
                .inputNames("PermanentAddress")
                //
                .field("Фактический адрес:", addressKeys)
                .inputNames("FactAddress")
                //
                .field("Адрес места работы:", addressKeys)
                .inputNames("WorkAddress")
                //
                .field("Почтовый адрес:", addressKeys)
                .inputNames("PostAddress");

        infoMap.block("СУММЫ ПО ДОГОВОРУ")
                //
                .field("Полная страховая сумма:")
                .inputNames("INSAMVALUE")
                .formatter("getSumStr")
                //
                .field("Валюта страховой суммы:")
                .inputNames("INSAMCURRENCYID")
                .formatter("getCurrencyStr")
                //
                .field("Полная страховая премия:")
                .inputNames("PREMVALUE", "PREMIUM")
                .formatter("getSumStr")
                //
                .field("Валюта страховой премии:")
                .inputNames("PREMCURRENCYID", "PREMIUMCURRENCYID")
                .formatter("getCurrencyStr");

        infoMap.block("СВЕДЕНИЯ О ДОГОВОРЕ")
                //
                .field("Дата начала действия:")
                .inputNames("STARTDATE")
                .formatter("getDateStr")
                //
                .field("Дата окончания действия:")
                .inputNames("FINISHDATE")
                .formatter("getDateStr")
                //
                .field("Срок действия (дней):")
                .inputNames("DURATION")
                //
                .field("Тип имущества:")
                .inputNames("OBJECTS", "CONTROBJLIST")
                .formatter("getObjStr")
                //
                // для HIB - тип имущества указан в явном виде
                .field("Тип имущества:")
                .inputNames("OBJNAME")
                //
                // для HIB - адрес имущества указан в явном виде
                .field("Адрес имущества:")
                .inputNames("OBJADDRESSTEXT1")
                //
                .field("Риски:")
                .inputNames("RISKLIST", "RISKS")
                .formatter("getRiskStr");

        infoMap.block("ОБЪЕКТ").isList().inputNames("OBJECTS", "CONTROBJLIST")
                //
                .field("Наименование:")
                .showIfEmpty()
                .inputNames("NAME", "OBJTYPESYSNAME")
                .formatter("getObjNameBySysName")
                //
                .field("Адрес:")
                .showIfEmpty()
                .inputNames("ADDRESSTEXT1")
                //
                .field("Площадь:")
                .inputNames("OBJAREA")
                //
                .field("Страховая сумма:")
                .inputNames("INSAMVALUE")
                .formatter("getSumStr")
                //
                .field("Валюта страховой суммы:")
                .inputNames("INSAMCURRENCYID")
                .formatter("getCurrencyStr")
                //
                .field("Страховая премия:")
                .inputNames("PREMVALUE")
                .formatter("getSumStr")
                //
                .field("Валюта страховой премии:")
                .inputNames("PREMCURRENCYID", "PREMIUMCURRENCYID")
                .formatter("getCurrencyStr")
                //
                .field("Риски:")
                .inputNames("RISKLIST", "RISKS")
                .formatter("getRiskStr");

        infoMap.block("ЗАСТРАХОВАННЫЙ").isList().inputNames("INSUREDLIST")
                //
                .field("Фамилия Имя Отчество:")
                .inputNames("FULLNAME")
                //
                .field("Фамилия:")
                .inputNames("LASTNAME")
                //
                .field("Имя:")
                .inputNames("FIRSTNAME")
                //
                .field("Отчество:")
                .inputNames("MIDDLENAME")
                //
                .field("Страховая сумма:")
                .inputNames("INSAMVALUE")
                .formatter("getSumStr")
                //
                .field("Валюта страховой суммы:")
                .inputNames("INSAMCURRENCYID")
                .formatter("getCurrencyStr")
                //
                .field("Страховая премия:")
                .inputNames("PREMVALUE")
                .formatter("getSumStr")
                //
                .field("Валюта страховой премии:")
                .inputNames("PREMCURRENCYID", "PREMIUMCURRENCYID")
                .formatter("getCurrencyStr");

        infoMap.block("КРЕДИТНЫЙ ДОГОВОР")
                //
                .field("Номер кредитного договора:")
                .inputNames("CREDCONTRNUM")
                //
                .field("Дата заключения:")
                .inputNames("credDate", "CREDDATE")
                .formatter("getDateStr");
        // </editor-fold>

    }

    private class Field {

        public String name;
        public boolean showIfEmpty = false;
        public Method formatter = null;
        public ArrayList<String> inputNames = new ArrayList<String>();
        private ArrayList<String> requestGroupKeys = null;
        private final Block parentBlock;

        public Field(String name, Block parentBlock, ArrayList<String> requestGroupKeys) {
            this.name = name;
            this.parentBlock = parentBlock;
            this.requestGroupKeys = requestGroupKeys;
        }

        public Field inputNames(String... inputNames) {
            this.inputNames.addAll(Arrays.asList(inputNames));
            if (this.requestGroupKeys != null) {
                this.requestGroupKeys.addAll(Arrays.asList(inputNames));
            }
            return this;
        }

        public Field showIfEmpty() {
            this.showIfEmpty = true;
            return this;
        }

        public Field hideIfEmpty() {
            this.showIfEmpty = false;
            return this;
        }

        public Field formatter(String methodName) {

            Method method = null;

            try {
                method = THIS_FACADE_CLASS.getDeclaredMethod(methodName, new Class[]{Object.class});
            } catch (NoSuchMethodException ex) {
                log.debug("Исключение при определении метода с именем " + methodName + ": ", ex);
            } catch (SecurityException ex) {
                log.debug("Исключение при определении метода с именем " + methodName + ": ", ex);
            }

            this.formatter = method;
            return this;
        }

        public Field field(String fieldname) {
            return this.field(fieldname, null);
        }

        public Field field(String fieldname, ArrayList<String> requestGroupKeys) {
            Field newField = parentBlock.field(fieldname, requestGroupKeys);
            return newField;
        }

    }

    private class Block {

        public String name;
        public boolean isList = false;
        public ArrayList<Field> fields = new ArrayList<Field>();
        public ArrayList<String> inputNames = new ArrayList<String>();

        public Block(String name) {
            this.name = name;
        }

        public Field field(String fieldname, ArrayList<String> requestGroupKeys) {
            Field field = new Field(fieldname, this, requestGroupKeys);
            fields.add(field);
            return field;
        }

        public Field field(String fieldname) {
            return this.field(fieldname, null);
        }

        public Block isList() {
            isList = true;
            return this;
        }

        public Block inputNames(String... inputNames) {
            this.inputNames.addAll(Arrays.asList(inputNames));
            return this;
        }

    }

    private class Info {

        public ArrayList<Block> infos = new ArrayList<Block>();

        public Block block(String blockName) {
            Block block = new Block(blockName);
            this.infos.add(block);
            return block;
        }

        public Block get(int index) {
            return infos.get(index);
        }

        public int size() {
            return infos.size();
        }

    }

    private static String getDateStr(Object date) {
        return " " + dateFormatter.format(date) + " ";
    }

    // todo: заменить на запрос справочника из базы (желательно реализовать в методе веб-сервиса)
    private static String getGenderStr(Object genderCode) {
        if (genderCode != null) {
            if ("0".equals(genderCode.toString())) {
                return ("Мужской");
            } else {
                return ("Женский");
            }
        }
        return ("Не указан");
    }

    // todo: заменить на запрос справочника из базы (желательно реализовать в методе веб-сервиса)
    private static String getDocNameByType(Object docTypeObj) {
        String docType = docTypeObj.toString();
        if ("PassportRF".equalsIgnoreCase(docType)) {
            return "Паспорт РФ";
        }
        if ("ForeignPassportRF".equalsIgnoreCase(docType)) {
            return "Загранпаспорт гражданина РФ";
        }
        if ("DiplomaticPassport".equalsIgnoreCase(docType)) {
            return "Дипломатический паспорт гражданина РФ";
        }
        if ("ForeignPassport".equalsIgnoreCase(docType)) {
            return "Паспорт иностранного гражданина";
        }
        if ("MigrationCard".equalsIgnoreCase(docType)) {
            return "Миграционная карта";
        }
        return docType;
    }

    private static String getSumStr(Object obj) {
        // todo: реализовать форматирование числа вида "1 234 567,89"
        // todo: "руб." заменить на валюту, договора могут создаваться не в рублях пока только в VZR (он же TRAVEL)
        // return obj.toString() + " руб.";
        String sum = moneyFormatter.format(obj);
        return sum;

    }

    // todo: заменить на запрос справочника из базы (желательно реализовать в методе веб-сервиса)
    private static String getCurrencyStr(Object currencyIDObj) {
        String currencyID = currencyIDObj.toString();
        //
        if ("1".equals(currencyID)) {
            return "Российский рубль";
        }
        if ("2".equals(currencyID)) {
            return "Доллар США";
        }
        if ("3".equals(currencyID)) {
            return "Евро";
        }
        return "Российский рубль";
    }

    private static String getObjStr(Object obj) {
        //
        Object objectName;
        Map<String, Object> object = (Map<String, Object>) obj;
        objectName = object.get("NAME");
        if (objectName == null) {
            objectName = object.get("OBJTYPESYSNAME");
        }
        if (objectName == null) {
            objectName = "";
        }
        return getObjNameBySysName(objectName);
    }

    private static String getRiskStr(Object obj) {
        //
        Object name;
        Map<String, Object> risk = (Map<String, Object>) obj;
        name = risk.get("PRODRISKNAME");
        if (name == null) {
            name = risk.get("PRODRISKSYSNAME");
        }
        if (name == null) {
            name = "";
        }
        Object insAmValue = risk.get("INSAMVALUE");
        if (insAmValue != null) {
            // todo: "руб." заменить на валюту, договора могут создаваться не в рублях пока только в VZR (он же TRAVEL)
            // name = name + " (на сумму " + insAmValue + " руб.)";
            return name.toString() + " (на сумму " + getSumStr(insAmValue) + ")";
        }
        return name.toString();
    }

    // todo: заменить на запрос справочника из базы (желательно реализовать в методе веб-сервиса)
    private static String getObjNameBySysName(Object obj) {
        //
        String sysname = obj.toString();
        if (("house".equalsIgnoreCase(sysname)) || ("firstHouse".equalsIgnoreCase(sysname))) {
            return "Дом";
        }
        if ("flat".equalsIgnoreCase(sysname)) {
            return "Квартира";
        }
        //todo: иногда не срабатывает ветка "Второй дом"
        if ("secondHouse".equalsIgnoreCase(sysname)) {
            return "Второй дом";
        }
        if ("sauna".equalsIgnoreCase(sysname)) {
            return "Баня";
        }
        if ("other".equalsIgnoreCase(sysname)) {
            return "Иная постройка";
        }
        return sysname;
    }

    public Map<String, Object> callService(String serviceName, String methodName, Map<String, Object> params) throws Exception {
        return this.callService(serviceName, methodName, params, params);
    }

    public Map<String, Object> callService(String serviceName, String methodName, Map<String, Object> params, Map<String, Object> authCreds) throws Exception {
        // логин и пароль для вызова других методов веб-сервиса                    
        String login = authCreds.get(LOGIN).toString();
        String password = authCreds.get(PASSWORD).toString();

        long callTimer = System.currentTimeMillis();
        log.debug("Вызван метод " + methodName + " с параметрами:\n\n" + params.toString() + "\n");
        Map<String, Object> callResult = this.callService(serviceName, methodName, params, login, password);
        callTimer = System.currentTimeMillis() - callTimer;
        log.debug("Метод " + methodName + " выполнился за " + callTimer + " мс. и вернул результат:\n\n" + callResult.toString() + "\n");

        // возвращаем пароль в набор параметров (вызов метода сервиса его изымает из них)
        authCreds.put(PASSWORD, password);

        return callResult;
    }

    public ArrayList<Map<String, Object>> callServiceList(String serviceName, String methodName, Map<String, Object> params, Map<String, Object> authCreds) throws Exception {
        Map<String, Object> callResultAsMap = this.callService(serviceName, methodName, params, authCreds);
        Object callResultResult = callResultAsMap.get(RESULT);

        // дополнительное преобразование "пустого" ответа в пустой список
        // (если штатный запрос не выбрал из БД данных, то ключ RESULT по какой-то причине будет указывать не на пустой список, а на пустую карту)
        ArrayList<Map<String, Object>> callResultAsList;
        if ((callResultResult != null) && (callResultResult instanceof ArrayList)) {
            callResultAsList = (ArrayList<Map<String, Object>>) callResultResult;
        } else {
            callResultAsList = new ArrayList<Map<String, Object>>();
        }

        return callResultAsList;
    }

    // дополнительная обработка результата для более удобной подготовки данных для интерфесов в flextera
    private Map<String, Object> prepareFlexteraContract(final Map<String, Object> params, Map<String, Object> authCreds, Boolean isExpress) {

        long timer = System.currentTimeMillis();
        log.debug("Вызван prepareFlexteraContract с параметрами:\n\n" + params.toString() + "\n");

        // создание копии входных параметров (сами они подвергаться изменениям не должны)
        Map<String, Object> contract = new HashMap<String, Object>();
        contract.putAll(params);

        // преобразование всех "дробных" дат
        convertFloatToDate(contract);

        // при наличии в основных данных списка застрахованных лиц необходимо убрать дублирующий его список объектов
        if (contract.get("INSUREDLIST") != null) {
            contract.remove("OBJECTS");
            contract.remove("CONTROBJLIST");
        }

        // если в сведениях о договоре присутствует хотя бы один элемент с контактными данными - не дополнять основные данные сведениями из списка контактов
        Boolean additionalContactsRequest = true;
        for (String contactKey : contactKeys) {
            if (contract.get(contactKey) != null) {
                additionalContactsRequest = false;
            }
        }
        if (additionalContactsRequest) {
            // дополнение основных данных сведениями из списка контактов
            // ключ - системное имя типа контактной информации        
            Map<String, Object> contactParams = new HashMap<String, Object>();
            contactParams.put("PARTICIPANTID", contract.get("INSUREDID"));
            ArrayList<Map<String, Object>> contacts = null;
            try {
                contacts = callServiceList(WsConstants.CRMWS, "contactGetListByParticipantId", contactParams, authCreds);
            } catch (Exception ex) {
                log.debug("Произошло исключение при получении контактов участника (с ИД = " + contract.get("INSUREDID") + "): ", ex);
            }
            for (int ct = 0; ct < contacts.size(); ct++) {
                Object contactTypeSysName = contacts.get(ct).get("CONTACTTYPESYSNAME");
                // без замены данных, переданных в явном виде
                String contactTypeSysNameStr = contactTypeSysName.toString();
                if (contract.get(contactTypeSysNameStr) == null) {
                    contract.put(contactTypeSysNameStr, contacts.get(ct).get("VALUE"));
                }
            }
        }

        // если в сведениях о договоре присутствует хотя бы один элемент с данными о адресах - не дополнять основные данные сведениями из списка адресов
        Boolean additionalAddressRequest = true;
        for (String addressKey : addressKeys) {
            if (contract.get(addressKey) != null) {
                additionalAddressRequest = false;
            }
        }
        if (additionalAddressRequest) {
            // дополнение основных данных сведениями из списка адресов
            // ключ - системное имя типа адреса        
            Map<String, Object> contactParams = new HashMap<String, Object>();
            contactParams.put("PARTICIPANTID", contract.get("INSUREDID"));
            ArrayList<Map<String, Object>> addresses = null;
            try {
                addresses = callServiceList(WsConstants.CRMWS, "addressGetListByParticipantId", contactParams, authCreds);
            } catch (Exception ex) {
                log.debug("Произошло исключение при получении адресов участника (с ИД = " + contract.get("INSUREDID") + "): ", ex);
            }
            for (int ct = 0; ct < addresses.size(); ct++) {
                Object addressTypeSysName = addresses.get(ct).get("ADDRESSTYPESYSNAME");
                // без замены данных, переданных в явном виде
                String addressTypeSysNameStr = addressTypeSysName.toString();
                if (contract.get(addressTypeSysNameStr) == null) {
                    contract.put(addressTypeSysNameStr, addresses.get(ct).get("ADDRESSTEXT1"));
                }
            }
        }

        // если в сведениях о договоре присутствует хотя бы один элемент с данными о документах - не дополнять основные данные сведениями из списка документов
        Boolean additionalDocumentRequest = true;
        for (String documentKey : documentKeys) {
            if (contract.get(documentKey) != null) {
                additionalDocumentRequest = false;
            }
        }
        if (additionalDocumentRequest) {
            // дополнение основных данных сведениями из списка документов
            // ключи - используются указанные в documentKeys       
            Map<String, Object> documentsParams = new HashMap<String, Object>();
            documentsParams.put("PARTICIPANTID", contract.get("INSUREDID"));
            ArrayList<Map<String, Object>> documents = null;
            try {
                documents = callServiceList(WsConstants.CRMWS, "personDocGetListByParticipantId", documentsParams, authCreds);
            } catch (Exception ex) {
                log.debug("Произошло исключение при получении документов участника (с ИД = " + contract.get("INSUREDID") + "): ", ex);
            }
            //for (int ct = 0; ct < addresses.size(); ct++) {
            //    Object addressTypeSysName = addresses.get(ct).get("ADDRESSTYPESYSNAME");
            //    // без замены данных, переданных в явном виде
            //    String addressTypeSysNameStr = addressTypeSysName.toString();
            //    if (contract.get(addressTypeSysNameStr) == null) {
            //        contract.put(addressTypeSysNameStr, addresses.get(ct).get("ADDRESSTEXT1"));
            //    }
            //}            
            for (String documentKey : documentKeys) {
                // без замены данных, переданных в явном виде
                if (contract.get(documentKey) == null) {
                    Object documentValue = documents.get(0).get(documentKey);
                    if (documentValue != null) {
                        contract.put(documentKey, documentValue);
                    }
                }
            }
        }

        if (!isExpress) {
            // для HIB - формирование стандартного списка рисков из расширенных атрибутов программы
            // (и дополнение основных данных полученным списком)
            // атрибуты выбираются из той программы, чье системное имя указано в основных данных
            Object programSysName = contract.get("PROGSYSNAME");
            Map<String, Object> progParams = new HashMap<String, Object>();
            progParams.put("CALCVERID", contract.get("PRODCONFID"));
            // todo: должно быть переменной, зависеть от продукта (но пока что требуется только для HIB)
            progParams.put("NAME", "Ins.Hib.Prog.Sums");
            ArrayList<Map<String, Object>> progExts = null;
            try {
                progExts = callServiceList(WsConstants.INSTARIFICATORWS, "dsGetCalculatorHandbookData", progParams, authCreds);
            } catch (Exception ex) {
                log.debug("Произошло исключение при получении расширенных атрибутов программы (с ИД = " + contract.get("INSUREDID") + "): ", ex);
            }

            if ((programSysName != null) && (progExts != null) && (contract.get("RISKS") == null)) {
                for (int pe = 0; pe < progExts.size(); pe++) {
                    Map<String, Object> program = progExts.get(pe);
                    String programName = program.get("prodprogsysname").toString();
                    if (programName.equalsIgnoreCase(programSysName.toString())) {
                        ArrayList<Map> risks = new ArrayList<Map>();
                        // namesAndKeys - соответствие названий рисков именам ключей для сумм в программе из расширенных атрибутов
                        for (int r = 0; r < namesAndKeys.length; r++) {
                            Map<String, Object> risk = new HashMap<String, Object>();
                            risk.put("PRODRISKNAME", namesAndKeys[r][0]);
                            risk.put("INSAMVALUE", program.get(namesAndKeys[r][1]));
                            risks.add(risk);
                        }
                        contract.put("RISKS", risks);
                        break;
                    }
                }
            }

        }

        timer = System.currentTimeMillis() - timer;
        log.debug("Метод prepareFlexteraContract выполнился за " + timer + " мс. и вернул результат:\n\n" + contract.toString() + "\n");

        return contract;
    }

    private ArrayList<Map> generateFlexteraExtTableContent(Map<String, Object> params) {

        long timer = System.currentTimeMillis();
        log.debug("Вызван generateFlexteraExtTableContent с параметрами:\n\n" + params.toString() + "\n");

        Map<String, Object> contract = (Map<String, Object>) params;

        ArrayList<Map> extTableContent = new ArrayList<Map>();

        // обход разделов
        for (int i = 0; i < infoMap.size(); i++) {
            Block block = infoMap.get(i);
            // выбор одного раздела или нескольких (если раздел помечен как повторяющийся)
            ArrayList<Map<String, Object>> scopes = new ArrayList<Map<String, Object>>();
            if (block.isList) {
                ArrayList<String> listsNames = block.inputNames;
                for (int lst = 0; lst < listsNames.size(); lst++) {
                    ArrayList<Map<String, Object>> listOfBlocks = (ArrayList<Map<String, Object>>) contract.get(listsNames.get(lst));
                    if (listOfBlocks != null) {
                        scopes = listOfBlocks;
                        break;
                    }
                }
            } else {
                scopes.add(contract);
            }

            // обход содержимого разделов
            for (int s = 0; s < scopes.size(); s++) {
                Map<String, Object> scope = scopes.get(s);
                Map<String, Object> groupNameLine = new HashMap<String, Object>();
                groupNameLine.put("NAME", block.name);
                extTableContent.add(groupNameLine);
                int count = 0;
                // обход списка выводимых атрибутов текущего раздела
                for (int j = 0; j < block.fields.size(); j++) {
                    Field fld = block.fields.get(j);
                    Method formatter = fld.formatter;
                    // обход списка наименований входящих параметров - до первого непустого параметра
                    for (int k = 0; k < fld.inputNames.size(); k++) {
                        String inputName = fld.inputNames.get(k);
                        Object rawData = scope.get(inputName);
                        if (rawData != null) {

                            // формирование массива, даже из одного значения
                            ArrayList<Object> rawValues = new ArrayList<Object>();
                            if (rawData instanceof List) {
                                rawValues = (ArrayList) rawData;
                            } else {
                                rawValues.add(rawData);
                            }

                            // инициализация с именем - только для первой строки
                            Map<String, Object> dataLine = new HashMap<String, Object>();
                            dataLine.put("SUBNAME", fld.name);
                            // отображать нумерацию если более одной строки
                            boolean showLineNums = false;
                            if (rawValues.size() > 1) {
                                showLineNums = true;
                            }

                            for (int t = 0; t < rawValues.size(); t++) {
                                Object value = null;
                                Object rawValue = rawValues.get(t);
                                // по умолчанию - значение используется без преобразования
                                value = rawValues.get(t);
                                // если задана функция форматирования, то она применяется (при неудаче - значение остается без преобразования)
                                if (formatter != null) {
                                    try {
                                        value = formatter.invoke(null, rawValue);
                                    } catch (IllegalAccessException ex) {
                                        log.debug("Форматирование значения не выполнено, произошло исключение при вызове метода " + formatter.getName() + ": ", ex);
                                    } catch (IllegalArgumentException ex) {
                                        log.debug("Форматирование значения не выполнено, произошло исключение при вызове метода " + formatter.getName() + ": ", ex);
                                    } catch (InvocationTargetException ex) {
                                        log.debug("Форматирование значения не выполнено, произошло исключение при вызове метода " + formatter.getName() + ": ", ex);
                                    }
                                }
                                // отображать нумерацию если более одной строки
                                if (showLineNums) {
                                    value = (t + 1) + ". " + value;
                                }
                                // результат добавляется в выводимый объект
                                dataLine.put("VALUE", value);
                                extTableContent.add(dataLine);
                                count += 1;
                                // очистка для следующей итерации
                                dataLine = new HashMap<String, Object>();
                            }

                            // выход из цикла при первом непустом блоке данных
                            break;
                        }
                    }

                    // безусловное отображение помеченного атрибута если по нему нет данных
                    if ((count == 0) && (fld.showIfEmpty)) {
                        Map<String, Object> emptyDataLine = new HashMap<String, Object>();
                        emptyDataLine.put("SUBNAME", fld.name);
                        emptyDataLine.put("VALUE", "Не указано");
                        extTableContent.add(emptyDataLine);
                        count += 1;
                    }
                }

                // отказ от заголовка раздела, если раздел пуст
                if (count == 0) {
                    extTableContent.remove(extTableContent.size() - 1); // .pop();
                }
            }
        }

        timer = System.currentTimeMillis() - timer;
        log.debug("Метод generateFlexteraExtTableContent выполнился за " + timer + " мс. и вернул результат:\n\n" + extTableContent.toString() + "\n");

        return extTableContent;
    }

    /**
     * промежуточный метод - вызывает действительный метод (определенный по
     * номеру договора) для получения данных договора и возвращает результат его
     * выполнения добавляя данные для отображения в универсальном интерфейсе
     * просмотра договора в flextera
     *
     * @param params
     * @return
     *
     * @throws Exception
     */
    //@WsMethod(requiredParams = {METHOD_NAME, CONTRACT_ID})
    @WsMethod(requiredParams = {CONTRACT_ID})
    public Map<String, Object> dsFlexteraContractBrowseEx(Map<String, Object> params) throws Exception {

        long timer = System.currentTimeMillis();
        log.debug("Вызван dsFlexteraContractBrowseEx с параметрами:\n\n" + params.toString() + "\n");

        // определение флага экспресс-режима
        Boolean isExpress = true;
        Object isExpressObj = params.remove(EXPRESS);
        if (isExpressObj != null) {
            isExpress = (Boolean) isExpressObj;
        }

        Map<String, Object> rawResult;
        Map<String, Object> result;

        // возвращать надо всегда только один договор
        params.put(RETURN_AS_HASH_MAP, TRUE);

        Map<String, Object> defaultConract = callService(WsConstants.INSPOSWS, "dsContractBrowseListByParamEx", params);
        int prodConfID = ((Long) defaultConract.get("PRODCONFID")).intValue();

        // имя вызываемого действительного метода для получения данных договора
        // String method = params.remove(METHOD_NAME).toString();
        String method = null;
        switch (prodConfID) {
            // HIB - Защита дома Онлайн
            case 1050:
                // скопировано из hibContractViewEx.PFD, там метод был именно с Hab
                method = "dsHabContractBrowseListParamEx";
                break;
            // CIB - Защита банковской карты Онлайн
            case 1060:
                // скопировано из cibContractViewEx.PFD, там метод был именно с Hab
                method = "dsHabContractBrowseListParamEx";
                break;
            // VZR - Страхование путешественников Онлайн (он же TRAVEL)
            case 1070:
                method = "dsTravelContractBrowseEx";
                break;
            // SIS - Защита имущества сотрудников сбербанка Онлайн
            case 1080:
                method = "dsSisContractBrowseListByParamEx";
                break;
            // MORT - Страхование ипотеки Онлайн для клиентов ОАО «Сбербанк России» (он же MORTGAGE)
            case 1090:
                method = "dsMortgageContractBrowseListByParamEx";
                break;
        }

        if ((method != null) && (!isExpress)) {
            // если действительный метод выбран и отключен экспресс-режим
            log.debug("Определен вызываемый действительный метод: " + method);
            // вызов действительного метода для получения данных договора
            // имя сервиса, пока - константа
            String service = BIVSBERPOSWS;
            rawResult = callService(service, method, params);
        } else {
            // неизвестный вид договора или включен экспресс-режим - используется стандартный метод
            log.debug("Вызываемый действительный метод не определен, применен стандартный метод.");
            // повторный вызов метода не требуется, так как он уже выполнялся ранее
            //result = callService(WsConstants.INSPOSWS, "dsContractBrowseListByParamEx", params);
            // вместо этого - используем полученные данные без изменений
            rawResult = defaultConract;
        }

        // разворачивание "обертки" договора
        // для случаев, когда вызванный действительный метод игнорирует ключ RETURN_AS_HASH_MAP (как, например, dsTravelContractBrowseEx)
        if (rawResult.get("CONTRMAP") != null) {
            result = (Map<String, Object>) rawResult.get("CONTRMAP");
        } else if (rawResult.get(RESULT) != null) {
            result = (Map<String, Object>) rawResult.get("RESULT");
        } else {
            result = rawResult;
        }

        // дополнительная обработка копии результата для более удобной подготовки данных для интерфесов в flextera
        Map<String, Object> preparedFlexteraContract = prepareFlexteraContract(result, params, isExpress);

        // к превоначальному результату вызова действительного метода добавляется только будущее содержимое таблички на универсальном интерфейсе просмотра договора в flextera
        ArrayList<Map> flexteraExtTableContent = generateFlexteraExtTableContent(preparedFlexteraContract);
        result.put(FLEXTERA, flexteraExtTableContent);

        timer = System.currentTimeMillis() - timer;
        log.debug("Метод dsFlexteraContractBrowseEx выполнился за " + timer + " мс. и вернул результат:\n\n" + result.toString() + "\n");

        return result;

    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsContractBrowseListByParamForReprintAndResend(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = null;
        if (params.get("ISB2BFLAG") != null) {
            boolean isB2BMode = Boolean.valueOf(params.get("ISB2BFLAG").toString());
            if (isB2BMode) {
                result = this.callService(B2BPOSWS, "dsB2BContractBrowseListByParamForReprintAndResend", params, login, password);
                return result;
            }
        }
        result = this.selectQuery("dsContractBrowseListByParamForReprintAndResend", "dsContractBrowseListByParamForReprintAndResendCount", params);
        return result;
    }
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsContractBrowseListByParamSenderTest(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = null;
        result = this.selectQuery("dsContractBrowseListByParamSenderTest", "dsContractBrowseListByParamSenderTestCount", params);
        return result;
    }

}
