package com.bivgroup.services.b2bposws.facade.pos.custom.export;

import com.bivgroup.seaweedfs.client.AssignParams;
import com.bivgroup.seaweedfs.client.Assignation;
import com.bivgroup.seaweedfs.client.ReplicationStrategy;
import com.bivgroup.seaweedfs.client.WeedFSClient;
import com.bivgroup.seaweedfs.client.WeedFSClientBuilder;
import com.bivgroup.services.b2bposws.system.Constants;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.swing.text.MaskFormatter;
import org.apache.log4j.Logger;
import ru.diasoft.services.config.Config;
import ru.diasoft.services.csvexport.impl.CSVExporterImpl;
import ru.diasoft.services.csvexport.interfaces.CSVExporter;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author averichevsm
 */
@BOName("XLSExportCustom")
public class XLSExportCustomFacade extends ExportCustomFacade {

    private final Logger logger = Logger.getLogger(this.getClass());
    private static final String B2BPOSWS = Constants.B2BPOSWS;
    public static final String SERVICE_NAME = Constants.B2BPOSWS;

    final private String EncryptionPassword = "2E0XNZA9YYXJ9M6LF5XLP7GD4WXB9F7FXD8IISTAXBL2T5FQZ2X";
    final private byte[] Salt = {
        (byte) 0xa3, (byte) 0x23, (byte) 0x34, (byte) 0x2c,
        (byte) 0xf1, (byte) 0xd5, (byte) 0x31, (byte) 0x19
    };

    private Map<String, String> listsSysNameFieldsByListNames;
    //protected Map<String, String> columnNamesByKeyNames;
    private Set<String> alwaysSkippedKeyNames;

    private Map<String, Object> makeReportKeyMap(String field, String headerName) {
        Map<String, Object> result = new HashMap<>();
        columnNamesByKeyNames.put(field, headerName);
        result.put("headerName", headerName);
        result.put("field", field);
        return result;
    }

    private List<Map<String, Object>> getCapitalFieldPositionList() {
        columnNamesByKeyNames = new LinkedHashMap<String, String>();
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        result.add(makeReportKeyMap(".CONTRNUMBER", "Номер"));
        result.add(makeReportKeyMap(".CONTRPOLNUM", "Номер полиса"));
        result.add(makeReportKeyMap(".CONTRPOLSER", "Серия полиса"));
        result.add(makeReportKeyMap(".PRODPROGSTR", "Программа страхования"));
        result.add(makeReportKeyMap(".EMPTYSTR", "Сегмент"));
        result.add(makeReportKeyMap(".SALECHANNELMERC", "Канал продаж \"Меркурий\""));
        result.add(makeReportKeyMap(".SALECHANNEL", "Канал продаж"));
        result.add(makeReportKeyMap(".INSAMCURRENCYSTR", "Валюта договора"));
        result.add(makeReportKeyMap(".SIGNDATE", "Дата заключения договора"));
        result.add(makeReportKeyMap(".STARTDATE", "Дата начала страхования"));
        result.add(makeReportKeyMap(".FINISHDATE", "Дата окончания страхования"));
        result.add(makeReportKeyMap(".TERMSTR", "Срок страхования (в годах)"));
        result.add(makeReportKeyMap(".PAYVARSTR", "Периодичность взносов"));
        result.add(makeReportKeyMap(".INSURERMAP.FIO", "ФИО страхователя"));  //QQQQ
        result.add(makeReportKeyMap(".INSURERMAP.GENDERSTR", "Пол страхователя"));
        result.add(makeReportKeyMap(".INSURERMAP.BIRTHDATE", "Дата рождения страхователя"));

        result.add(makeReportKeyMap(".INSURERMAP.documentList.PassAndBorn.DOCTYPENAME", "Тип документа удостоверяющего личность страхователя"));
        result.add(makeReportKeyMap(".INSURERMAP.documentList.PassAndBorn.DOCSERIES", "Серия документа, удостоверяющего личность Страхователя"));
        result.add(makeReportKeyMap(".INSURERMAP.documentList.PassAndBorn.DOCNUMBER", "Номер документа, удостоверяющего личность Страхователя"));
        result.add(makeReportKeyMap(".INSURERMAP.documentList.PassAndBorn.ISSUEDBY", "Кем выдан документ, удостоверяющий личность Страхователя"));
        result.add(makeReportKeyMap(".INSURERMAP.documentList.PassAndBorn.ISSUEDATE", "Дата выдачи документа, удостоверяющего личность Страхователя"));
        result.add(makeReportKeyMap(".INSURERMAP.documentList.PassAndBorn.ISSUERCODE", "Код подразделения (если имеется) документа, удостоверяющего личность Страхователя"));

        result.add(makeReportKeyMap(".INSURERMAP.extAttributeList2.birthPlaceExt.EXTATTVAL_VALUE", "Страна рождения Страхователя"));
        result.add(makeReportKeyMap(".INSURERMAP.extAttributeList2.unResident.EXTATTVAL_VALUESTR", "Резидент РФ (Страхователь)"));
        result.add(makeReportKeyMap(".INSURERMAP.CITIZENSHIPSTR", "Гражданство страхователя"));
        result.add(makeReportKeyMap(".INSURERMAP.BIRTHPLACE", "Место рождения Страхователя"));
        result.add(makeReportKeyMap(".INSURERMAP.INN", "ИНН РФ Страхователя"));
        result.add(makeReportKeyMap(".INSURERMAP.extAttributeList2.INNUSA.EXTATTVAL_VALUE", "ИНН США Страхователя"));
        result.add(makeReportKeyMap(".INSURERMAP.extAttributeList2.INNOther.EXTATTVAL_VALUE", "ИНН Другая страна Страхователя"));

        result.add(makeReportKeyMap(".INSURERMAP.addressList.FactAddress.ADDRESSTEXT2", "Фактический адрес"));
        result.add(makeReportKeyMap(".INSURERMAP.addressList.FactAddress.POSTALCODE", "Адрес страхователя: Индекс"));

        result.add(makeReportKeyMap(".INSURERMAP.addressList.FactAddress.REGION", "Адрес страхователя: Регион"));
        result.add(makeReportKeyMap(".INSURERMAP.addressList.FactAddress.DISTRICT", "Адрес страхователя: Район"));
        result.add(makeReportKeyMap(".INSURERMAP.addressList.FactAddress.CITY", "Адрес страхователя: Город"));
        result.add(makeReportKeyMap(".INSURERMAP.addressList.FactAddress.STREET", "Адрес страхователя: Улица"));
        result.add(makeReportKeyMap(".INSURERMAP.addressList.FactAddress.HOUSE", "Адрес страхователя: Дом"));
        result.add(makeReportKeyMap(".EMPTYSTR", "Адрес страхователя: Строение"));
        result.add(makeReportKeyMap(".INSURERMAP.addressList.FactAddress.FLAT", "Адрес страхователя: Квартира"));

        result.add(makeReportKeyMap(".INSURERMAP.contactList.MobilePhone.VALUE", "Мобильный телефон страхователя"));
        result.add(makeReportKeyMap(".INSURERMAP.contactList.FactAddressPhone.VALUE", "Домашний телефон страхователя"));
        result.add(makeReportKeyMap(".INSURERMAP.contactList.PersonalEmail.VALUE", "Электронная почта страхователя"));

        result.add(makeReportKeyMap(".INSURERMAP.extAttributeList2.education.EXTATTVAL_VALUESTR", "Сведения о работе Страхователя: Профессия"));
        result.add(makeReportKeyMap(".INSURERMAP.extAttributeList2.EmployerName.EXTATTVAL_VALUE", "Сведения о работе Страхователя: Наименование организации"));
        result.add(makeReportKeyMap(".INSURERMAP.extAttributeList2.activityBusinessKind.EXTATTVAL_VALUESTR", "Сведения о работе Страхователя: Сфера деятельности"));
        result.add(makeReportKeyMap(".INSURERMAP.extAttributeList2.Position.EXTATTVAL_VALUE", "Сведения о работе Страхователя: Должность"));

        result.add(makeReportKeyMap(".CONTREXTMAP.insurerIsInsuredSTR", "Страхователь является застрахованным (да/нет)"));
        result.add(makeReportKeyMap(".INSURERMAP.extAttributeList2.MaritalStatus.EXTATTVAL_VALUESTR", "Семейное положение страхователя"));

        result.add(makeReportKeyMap(".INSURERMAP.documentList.РазрВр.DOCTYPENAME", "Данные документа, подтверждающего право на пребывание/проживание в РФ, Страхователя"));
        result.add(makeReportKeyMap(".INSURERMAP.documentList.РазрВр.DOCSERIES", "Серия документа, подтверждающего право на пребывание/проживание в РФ Страхователя"));
        result.add(makeReportKeyMap(".INSURERMAP.documentList.РазрВр.DOCNUMBER", "Номер документа, подтверждающего право на пребывание/проживание в РФ, Страхователя"));
        result.add(makeReportKeyMap(".INSURERMAP.documentList.РазрВр.RESSTARTDATE", "Данные документа, подтверждающего право на пребывание/проживание в РФ, Страхователя: срок пребывания с"));
        result.add(makeReportKeyMap(".INSURERMAP.documentList.РазрВр.RESFINISHDATE", "Данные документа, подтверждающего право на пребывание/проживание в РФ, Страхователя: срок пребывания по"));

        result.add(makeReportKeyMap(".INSURERMAP.documentList.MigrationCard.DOCTYPENAME", "Серия и номер миграционной карты Страхователя"));
//        result.add(makeReportKeyMap(".INSURERMAP.documentList.MigrationCard.DOCSERIES", "Данные миграционной карты Страхователя: Серия"));
        result.add(makeReportKeyMap(".INSURERMAP.documentList.MigrationCard.DOCSERIES", "Данные миграционной карты страхователя: номер карты, дата начала и дата окончания срока пребывания в Российской Федерации"));
        result.add(makeReportKeyMap(".INSURERMAP.documentList.MigrationCard.DOCNUMBER", "Данные миграционной карты Страхователя: Номер"));
        result.add(makeReportKeyMap(".INSURERMAP.documentList.MigrationCard.MIGSTARTDATE", "Данные миграционной карты Страхователя: Срок пребывания с"));
        result.add(makeReportKeyMap(".INSURERMAP.documentList.MigrationCard.MIGFINISHDATE", "Данные миграционной карты Страхователя: Срок пребывания по"));

        result.add(makeReportKeyMap(".INSURERMAP.extAttributeList2.taxResidentOtherCountry.EXTATTVAL_VALUE", "Статус налогового резидента иностранного государства страхователя: другая страна"));
        result.add(makeReportKeyMap(".INSURERMAP.extAttributeList2.isTaxResidentUSA.EXTATTVAL_VALUESTR", "Статус налогового резидента иностранного государства застрахованного: США"));

        result.add(makeReportKeyMap(".INSUREDMAP.FIO", "ФИО Застрахованного"));  //QQQQ
        result.add(makeReportKeyMap(".INSUREDMAP.GENDERSTR", "Пол застраховавнного лица"));
        result.add(makeReportKeyMap(".INSUREDMAP.BIRTHDATE", "Дата рождения Застрахованного лица"));

        result.add(makeReportKeyMap(".INSUREDMAP.documentList.PassAndBorn.DOCTYPENAME", "Тип документа удостоверяющего личность Застрахованного лица"));
        result.add(makeReportKeyMap(".INSUREDMAP.documentList.PassAndBorn.DOCSERIES", "Серия документа, удостоверяющего личность Застрахованного"));
        result.add(makeReportKeyMap(".INSUREDMAP.documentList.PassAndBorn.DOCNUMBER", "Номер документа, удостоверяющего личность Застрахованного"));
        result.add(makeReportKeyMap(".INSUREDMAP.documentList.PassAndBorn.ISSUEDBY", "Кем выдан документ, удостоверяющий личность Застрахованного"));
        result.add(makeReportKeyMap(".INSUREDMAP.documentList.PassAndBorn.ISSUEDATE", "Дата выдачи документа, удостоверяющего личность Застрахованного"));
        result.add(makeReportKeyMap(".INSUREDMAP.documentList.PassAndBorn.ISSUERCODE", "Код подразделения документа, удостоверяющего личность Застрахованного  (если имеется):"));

        result.add(makeReportKeyMap(".INSUREDMAP.addressList.FactAddress.ADDRESSTEXT2", "Фактический адрес застрахованного лица"));
        result.add(makeReportKeyMap(".INSUREDMAP.addressList.RegisterAddress.ADDRESSTEXT2", "Адрес регистрации застрахованного лица"));
        result.add(makeReportKeyMap(".INSUREDMAP.contactList.MobilePhone.VALUE", "Мобильный телефон застрахованного лица"));
        result.add(makeReportKeyMap(".INSUREDMAP.contactList.FactAddressPhone.VALUE", "Домашний телефон застрахованного лица"));
        result.add(makeReportKeyMap(".INSUREDMAP.contactList.PersonalEmail.VALUE", "E-mail Застрахованного лица"));

        result.add(makeReportKeyMap(".INSUREDMAP.extAttributeList2.education.EXTATTVAL_VALUESTR", "Сведения о работе Застрахованного: Профессия"));
        result.add(makeReportKeyMap(".INSUREDMAP.extAttributeList2.EmployerName.EXTATTVAL_VALUE", "Сведения о работе Застрахованного: Наименование организации"));
        result.add(makeReportKeyMap(".INSUREDMAP.extAttributeList2.activityBusinessKind.EXTATTVAL_VALUESTR", "Сведения о работе Застрахованного: Сфера деятельности"));
        result.add(makeReportKeyMap(".INSUREDMAP.extAttributeList2.Position.EXTATTVAL_VALUE", "Сведения о работе Застрахованного: Должность"));

        result.add(makeReportKeyMap(".INSUREDMAP.extAttributeList2.unResident.EXTATTVAL_VALUESTR", "Резидент Застрахованного"));
        result.add(makeReportKeyMap(".INSUREDMAP.CITIZENSHIPSTR", "Гражданство застрахованного лица"));
        result.add(makeReportKeyMap(".INSUREDMAP.BIRTHPLACE", "Место рождения Застрахованного лица"));
        result.add(makeReportKeyMap(".INSUREDMAP.extAttributeList2.birthPlaceExt.EXTATTVAL_VALUE", "Страна рождения Застрахованного"));
        result.add(makeReportKeyMap(".INSUREDMAP.INN", "ИНН Застрахованного лица"));
        result.add(makeReportKeyMap(".INSUREDMAP.extAttributeList2.MaritalStatus.EXTATTVAL_VALUESTR", "Семейное положение Застрахованного лица"));
        result.add(makeReportKeyMap(".INSUREDMAP.CITIZENSHIPSTR", "Гражданство застрахованного другой страны"));
        result.add(makeReportKeyMap(".PREMVALUE", "Страховая премия в валюте договора"));
        result.add(makeReportKeyMap(".CURRANCYRATE", "Курс валюты"));
        result.add(makeReportKeyMap(".PREMVALUERUB", "Эквивалент в рублях"));

        result.add(makeReportKeyMap(".riskList.survivalDeath.INSAMVALUE", "Страховая сумма Дожитие в валюте договора"));
        result.add(makeReportKeyMap(".CONTREXTMAP.deathGSS1Year", "Страховая сумма Смерть по любой причине 1-й год  в валюте договора"));
        result.add(makeReportKeyMap(".CONTREXTMAP.deathGSS2Year", "Страховая сумма Смерть по любой причине 2-й год  в валюте договора"));
        result.add(makeReportKeyMap(".CONTREXTMAP.deathGSS3Year", "Страховая сумма Смерть по любой причине 3-й год"));
        result.add(makeReportKeyMap(".riskList.survivalDeath.INSAMVALUE", "Страховая сумма по риску «Смерть с выплатой к сроку»  в валюте договора"));
        result.add(makeReportKeyMap(".riskList.accidentDeath.INSAMVALUE", "Страховая сумма по риску «Смерть Застрахованного лица в результате несчастного случая» в валюте договора"));

//        result.add(makeReportKeyMap(".BANKTERRITORY", "Территориальный банк"));
        result.add(makeReportKeyMap(".BANKREGION", "Регион отделения банка"));
//        result.add(makeReportKeyMap(".BANKREGION", "ОСБ"));
//        result.add(makeReportKeyMap(".BANKREGION", "ВСП"));
        result.add(makeReportKeyMap(".CREATEUSERMAP.FIO", "Клиентский менеджер"));
        result.add(makeReportKeyMap(".STATENAME", "Статус договора"));
        result.add(makeReportKeyMap(".BANKREGION", "Тип выпуска договора"));
        result.add(makeReportKeyMap(".BANKREGION", "Дата направления в СК на андеррайтинг"));
        result.add(makeReportKeyMap(".AGENTTARIF", "Тариф"));
        result.add(makeReportKeyMap(".AGENTPREM", "Размер комиссионного вознаграждения"));

        makeBeneficary("0", "1", result);
        makeBeneficary("1", "2", result);
        makeBeneficary("2", "3", result);
        makeBeneficary("3", "4", result);
        makeBeneficary("4", "5", result);
        makeBeneficary("5", "6", result);
        makeBeneficary("6", "7", result);
        makeBeneficary("7", "8", result);
        // в самом конце
//        result.add(makeReportKeyMap(".ERROR", "Ошибка");
        return result;
    }

    private List<Map<String, Object>> getInvestFieldPositionList() {
        columnNamesByKeyNames = new LinkedHashMap<String, String>();
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        result.add(makeReportKeyMap(".NO", "Дата"));
        result.add(makeReportKeyMap(".CONTRNUMBERSTR", "Номер полиса"));
        result.add(makeReportKeyMap(".PRODPROGSTR", "Программа"));
        result.add(makeReportKeyMap(".SEGMENT", "Сегмент"));
        result.add(makeReportKeyMap(".STARTDATE", "Дата начала страхования"));
        result.add(makeReportKeyMap(".FINISHDATE", "Дата окончания"));
        result.add(makeReportKeyMap(".TERMSTR", "Срок программы"));
        result.add(makeReportKeyMap(".INSURERMAP.FIO", "ФИО страхователя"));  //QQQQ
        result.add(makeReportKeyMap(".INSURERMAP.BIRTHDATE", "Дата рождения страхователя"));
        result.add(makeReportKeyMap(".INSURERMAP.addressList.RegisterAddress.ADDRESSTEXT2", "Адрес Страхователя"));
        result.add(makeReportKeyMap(".INSURERMAP.documentList.PassportRF.FULLSTR", "Паспортные данные Страхователя"));
        result.add(makeReportKeyMap(".INSURERMAP.contactList.MobilePhone.VALUE", "Телефон Страхователя"));
        result.add(makeReportKeyMap(".INSURERMAP.contactList.PersonalEmail.VALUE", "Электронная почта Страхователя"));

        result.add(makeReportKeyMap(".INSUREDMAP.FIO", "ФИО Застрахованного лица"));  //QQQQ
        result.add(makeReportKeyMap(".INSUREDMAP.BIRTHDATE", "Дата рождения Застрахованного лица"));
        result.add(makeReportKeyMap(".INSUREDMAP.addressList.RegisterAddress.ADDRESSTEXT2", "Адрес Застрахованного лица"));
        result.add(makeReportKeyMap(".INSUREDMAP.documentList.PassportRF.FULLSTR", "Паспортные данные Застрахованного лица"));
        result.add(makeReportKeyMap(".INSUREDMAP.contactList.MobilePhone.VALUE", "Телефон Застрахованного лица"));
        result.add(makeReportKeyMap(".INSUREDMAP.contactList.PersonalEmail.VALUE", "Электронная почта Застрахованного лица"));

        result.add(makeReportKeyMap(".CONTREXTMAP.fundSTR", "Базовый актив"));
        result.add(makeReportKeyMap(".CONTREXTMAP.assuranceLevelStr", "Уровень гарантии"));

        result.add(makeReportKeyMap(".INSAMVALUEINRUB", "Страховая сумма (рубли)"));
        result.add(makeReportKeyMap(".INSAMVALUEINUSD", "Страховая сумма (доллары)"));
        result.add(makeReportKeyMap(".PREMVALUEINRUB", "Страховой взнос (рубли)"));
        result.add(makeReportKeyMap(".PREMVALUEINUSD", "Страховой взнос (доллары)"));
        result.add(makeReportKeyMap(".INSAMCURRENCYSTR", "Валюта"));
        result.add(makeReportKeyMap(".CURRENCY", "Эквивалент в рублях"));
        result.add(makeReportKeyMap(".NO", "п/п"));
        result.add(makeReportKeyMap(".PAYVARSTR", "Периодичность оплаты взносов: 0 - единовременно"));
        result.add(makeReportKeyMap(".SALECHANNEL", "Канал продаж"));

        //result.add(makeReportKeyMap(".BANKTERRITORY", "Территориальный банк"));
        result.add(makeReportKeyMap(".BANKREGION", "Район / Регион"));
        //result.add(makeReportKeyMap(".OSB", "ОСБ"));
        //result.add(makeReportKeyMap(".BANKREGION", "ВСП"));
        result.add(makeReportKeyMap(".CREATEUSERMAP.FIO", "ФИО сотрудника"));
        result.add(makeReportKeyMap(".NO", "АВ банка"));
        result.add(makeReportKeyMap(".NO", "АВ банка (НДС)"));
        result.add(makeReportKeyMap(".NO", "АВ банка (без НДС)"));
        result.add(makeReportKeyMap(".STATENAME", "Статус"));
        result.add(makeReportKeyMap(".CONTREXTMAP.isAutopilotTakeProfitSTR", "TakeProfit"));
        result.add(makeReportKeyMap(".CONTREXTMAP.isAutopilotStopLossSTR", "StopLoss"));
        result.add(makeReportKeyMap(".NO", "Стратегия"));
        result.add(makeReportKeyMap(".NO", "Комментарии"));
        result.add(makeReportKeyMap(".NO", "Комментарии 2"));
        result.add(makeReportKeyMap(".NO", "Передан в бухгалтерию (дата)"));
        result.add(makeReportKeyMap(".NO", "Отметка о выплате АВ"));
        result.add(makeReportKeyMap(".NO", "Отметка об инвестировании"));
        result.add(makeReportKeyMap(".CONTRPOLSER", "Серия полиса"));
        result.add(makeReportKeyMap(".NO", "% Гарантийный фонд"));
        result.add(makeReportKeyMap(".NO", "% Рисковый фонд"));
//        result.add(makeReportKeyMap(".NO", "Гарантийный фонд"));
//        result.add(makeReportKeyMap(".NO", "Рисковый фонд"));
//        result.add(makeReportKeyMap(".NO", "ФИО сотрудника СК  (коуч)"));
//        result.add(makeReportKeyMap(".NO", "ФИО РМ"));

        result.add(makeReportKeyMap(".INSURERMAP.GENDERSTR", "Пол Страхователя"));
//        result.add(makeReportKeyMap(".NO", "Рисковый фонд"));

        result.add(makeReportKeyMap(".INSURERMAP.extAttributeList2.unResident.EXTATTVAL_VALUESTR", "Резидент Страхователь"));
        result.add(makeReportKeyMap(".INSURERMAP.CITIZENSHIPSTR", "Гражданство Страхователя"));

        //      result.add(makeReportKeyMap(".BANKTERRITORY", "Территориальный банк"));
        result.add(makeReportKeyMap(".BANKREGION", "Регион отделения банка"));
        //    result.add(makeReportKeyMap(".BANKREGION", "ОСБ"));
        //    result.add(makeReportKeyMap(".BANKREGION", "ВСП"));
//        result.add(makeReportKeyMap(".BANKREGION", "Клиентский менеджер"));
//        result.add(makeReportKeyMap(".STATENAME", "Статус договора"));
        result.add(makeReportKeyMap(".BANKREGION", "Тип выпуска договора"));
//        result.add(makeReportKeyMap(".BANKREGION", "Дата направления в СК на андеррайтинг"));
//        result.add(makeReportKeyMap(".AGENTTARIF", "Тариф"));
//        result.add(makeReportKeyMap(".AGENTPREM", "Размер комиссионного вознаграждения"));

        makeBeneficaryInvest("0", "1", result);
        makeBeneficaryInvest("1", "2", result);
        makeBeneficaryInvest("2", "3", result);
        makeBeneficaryInvest("3", "4", result);
        result.add(makeReportKeyMap(".INSURERMAP.addressList.FactAddress.ADDRESSTEXT2", "Фактический адрес"));
        result.add(makeReportKeyMap(".CONTREXTMAP.autopilotTakeProfitPerc", "Profit"));
        result.add(makeReportKeyMap(".CONTREXTMAP.autopilotStopLossPerc", "Loss"));
//        result.add(makeReportKeyMap(".NO", "ТИП ОШИБКИ: Несоответствие серии и параметров договора"));
//        result.add(makeReportKeyMap(".NO", "ТИП ОШИБКИ: Несоответствие параметров договора"));
//        result.add(makeReportKeyMap(".NO", "ТИП ОШИБКИ: Не заполнены или некорректные данные Страхователя или Застрахованного"));
//        result.add(makeReportKeyMap(".NO", "ТИП ОШИБКИ: Несоответствие общей премии в валюте договора и эквивалента в рублях"));
//        result.add(makeReportKeyMap(".NO", "ТИП ОШИБКИ: Несоответствие лимитам на минимальный страховой взнос"));
//        result.add(makeReportKeyMap(".NO", "Наличие ошибки. Количество записей с ошибкой: 0 (0% от общего числа записей)"));
        result.add(makeReportKeyMap(".NO", "Результат обработки инфы по застрахованному лицу"));
        result.add(makeReportKeyMap(".NO", "Передача реестра в МСЦ (Ответственное Хранение)"));
        result.add(makeReportKeyMap(".INSUREDMAP.GENDERSTR", "Пол Застрахованного"));
        result.add(makeReportKeyMap(".INSUREDMAP.INN", "ИНН Застрахованного лица"));

        makeBeneficaryInvest1("0", "1", result);
        makeBeneficaryInvest1("1", "2", result);
        makeBeneficaryInvest1("2", "3", result);
        makeBeneficaryInvest1("3", "4", result);
        result.add(makeReportKeyMap("INSURERMAP.BIRTHPLACE", "Место рождения страхователя"));
        result.add(makeReportKeyMap("INSUREDMAP.BIRTHPLACE", "Место рождения застрахованного"));

        result.add(makeReportKeyMap("BENEFICIARYLIST.0.BIRTHPLACE", "Место рождения выг 1"));
        result.add(makeReportKeyMap("BENEFICIARYLIST.1.BIRTHPLACE", "Место рождения выг 2"));
        result.add(makeReportKeyMap("BENEFICIARYLIST.2.BIRTHPLACE", "Место рождения выг 3"));
        result.add(makeReportKeyMap("BENEFICIARYLIST.3.BIRTHPLACE", "Место рождения выг 4"));

        result.add(makeReportKeyMap(".NO", " Дата загрузки в ОИС "));
        result.add(makeReportKeyMap(".NO", "  "));
        result.add(makeReportKeyMap(".NO", "ISIN / Фонд"));
        result.add(makeReportKeyMap(".NO", "Комментарий ИСЖ <Плахотник>"));
        result.add(makeReportKeyMap(".NO", "Нагрузка 1 по риску «Смерть ЛП» %"));
        result.add(makeReportKeyMap(".NO", "Нагрузка 1 по риску «Смерть НС» %"));
        result.add(makeReportKeyMap(".NO", "Нагрузка 2 по риску «Смерть ЛП» ‰"));
        result.add(makeReportKeyMap(".NO", "Нагрузка 2 по риску «Смерть НС» ‰"));

        // в самом конце
//        result.add(makeReportKeyMap(".ERROR", "Ошибка");
        return result;
    }

    public XLSExportCustomFacade() {

        // протоколирование времени, затраченного на вызовы (влияет только на вызовы через callServiceTimeLogged)
        IS_CALLS_TIME_LOGGED = true;

        alwaysSkippedKeyNames = new HashSet<String>();
        alwaysSkippedKeyNames.add("ROWSTATUS");

        alwaysSkippedKeyNames.add("EXTATTVAL_OBJID");
        alwaysSkippedKeyNames.add("EXTATTVAL_ATTID");
        alwaysSkippedKeyNames.add("EXTATTVAL_ID");
        alwaysSkippedKeyNames.add("EXTATTTYPE_PUBLICNAME");
        alwaysSkippedKeyNames.add("EXTATT_DEFAULTVALUE");
        alwaysSkippedKeyNames.add("EXTATTTYPE_SYSNAME");
        alwaysSkippedKeyNames.add("EXTOBJTYPE_SYSNAME");
        alwaysSkippedKeyNames.add("EXTOBJTYPE_PUBLICNAME");

        alwaysSkippedKeyNames.add("CONTACTPERSONID");
        alwaysSkippedKeyNames.add("CONTACTTYPEID");
        alwaysSkippedKeyNames.add("CONTACTID");

        alwaysSkippedKeyNames.add("LT_CREATEDATE");
        alwaysSkippedKeyNames.add("LT_CREATEDATETIME");
        alwaysSkippedKeyNames.add("CT_CREATEDATE");
        alwaysSkippedKeyNames.add("CT_CREATEDATETIME");

        listsSysNameFieldsByListNames = new HashMap<String, String>();
        listsSysNameFieldsByListNames.put("riskList", "PRODRISKSYSNAME");
        listsSysNameFieldsByListNames.put("documentList", "DOCTYPESYSNAME");
        listsSysNameFieldsByListNames.put("addressList", "ADDRESSTYPESYSNAME");
        listsSysNameFieldsByListNames.put("contactList", "CONTACTTYPESYSNAME");
        listsSysNameFieldsByListNames.put("extAttributeList2", "EXTATT_SYSNAME");
        listsSysNameFieldsByListNames.put("BENEFICIARYLIST", "*"); // * - использовать индекс элемента

        columnNamesByKeyNames = new LinkedHashMap<String, String>();
        columnNamesByKeyNames.put(".PRODUCTMAP.PRODVER.PROD.NAME", "Продукт");
        columnNamesByKeyNames.put(".CONTRNUMBER", "Номер договора");
        columnNamesByKeyNames.put(".CONTRPOLSER", "Номер полиса");
        columnNamesByKeyNames.put(".CONTRPOLNUM", "Серия полиса");
        columnNamesByKeyNames.put(".STATENAME", "Состояние");
        //columnNamesByKeyNames.put(".PRODPROGID", "Программа");
        columnNamesByKeyNames.put(".PRODPROGSTR", "Программа");
        columnNamesByKeyNames.put(".INSAMVALUE", "Страховая сумма");
        columnNamesByKeyNames.put(".AGENTPREM", "Премия агента");
        columnNamesByKeyNames.put(".AGENTTARIF", "Премия агента");
        columnNamesByKeyNames.put(".INSAMVALUEINRUB", "Страховая сумма в рублях");
        columnNamesByKeyNames.put(".INSAMVALUEINUSD", "Страховая сумма в валюте");
        columnNamesByKeyNames.put(".PREMVALUEINRUB", "Размер взноса в рублях");
        columnNamesByKeyNames.put(".PREMVALUEINUSD", "Размер взноса в валюте");
        columnNamesByKeyNames.put(".PREMVALUE", "Размер взноса");
        columnNamesByKeyNames.put(".STARTDATE", "Начало срока действия");
        columnNamesByKeyNames.put(".FINISHDATE", "Окончание срока действия");
        //columnNamesByKeyNames.put(".TERMID", "Срок страхования (лет)");
        columnNamesByKeyNames.put(".TERMSTR", "Срок страхования");
        //columnNamesByKeyNames.put(".PAYVARID", "Периодичность взносов");
        columnNamesByKeyNames.put(".PAYVARSTR", "Периодичность взносов");

        columnNamesByKeyNames.put(".CREATEUSERMAP.FIO", "фио продавца.");

        //columnNamesByKeyNames.put(".CONTREXTMAP.insurerIsInsured", "Страхователь является застрахованным");
        columnNamesByKeyNames.put(".CONTREXTMAP.insurerIsInsuredSTR", "Страхователь является застрахованным");
        //columnNamesByKeyNames.put(".CONTREXTMAP.insuredGender", "Пол застрахованного");
        columnNamesByKeyNames.put(".CONTREXTMAP.insuredGenderSTR", "Пол застрахованного");
        columnNamesByKeyNames.put(".CONTREXTMAP.insuredBirthDATE", "Дата рождения застрахованного");
        //columnNamesByKeyNames.put(".INSAMCURRENCYID", "Валюта страхования");
        columnNamesByKeyNames.put(".INSAMCURRENCYSTR", "Валюта страхования");
        columnNamesByKeyNames.put(".CURRENCYRATE", "Курс валюты страхования");
        columnNamesByKeyNames.put(".CONTREXTMAP.insuredDeclComplianceSTR", "Клиент соответствует декларации застрахованного");
        //columnNamesByKeyNames.put(".CONTREXTMAP.isAutopilot", "Опция АВТОПИЛОТ подключена");
        columnNamesByKeyNames.put(".CONTREXTMAP.isAutopilotSTR", "Опция АВТОПИЛОТ подключена");
        //columnNamesByKeyNames.put(".CONTREXTMAP.isAutopilotTakeProfit", "Автопилот ВВЕРХ (Take profit)");
        columnNamesByKeyNames.put(".CONTREXTMAP.isAutopilotTakeProfitSTR", "Автопилот ВВЕРХ (Take profit)");
        //columnNamesByKeyNames.put(".CONTREXTMAP.autopilotTakeProfitPerc", "Автопилот ВВЕРХ (Take profit) процент");
        columnNamesByKeyNames.put(".CONTREXTMAP.autopilotTakeProfitPercValueWithPct", "Автопилот ВВЕРХ (Take profit) процент");
        //columnNamesByKeyNames.put(".CONTREXTMAP.isAutopilotStopLoss", "Автопилот ВНИЗ (Stop Loss)");
        columnNamesByKeyNames.put(".CONTREXTMAP.isAutopilotStopLossSTR", "Автопилот ВНИЗ (Stop Loss)");
        //columnNamesByKeyNames.put(".CONTREXTMAP.autopilotStopLossPerc", "Автопилот ВНИЗ (Stop Loss) процент");
        columnNamesByKeyNames.put(".CONTREXTMAP.autopilotStopLossPercValueWithPct", "Автопилот ВНИЗ (Stop Loss) процент");
        //columnNamesByKeyNames.put(".CONTREXTMAP.assuranceLevelIncreased", "Повышенный уровень гарантии");
        columnNamesByKeyNames.put(".CONTREXTMAP.assuranceLevelIncreasedSTR", "Повышенный уровень гарантии");
        //columnNamesByKeyNames.put(".CONTREXTMAP.assuranceLevel", "Уровень гарантии");
        columnNamesByKeyNames.put(".CONTREXTMAP.assuranceLevelStr", "Уровень гарантии");

        columnNamesByKeyNames.put(".riskList.survivalDeath.INSAMVALUE", "Дожитие ЗЛ и смерть по любой причине - Страховая сумма");
        columnNamesByKeyNames.put(".riskList.accidentDeath.INSAMVALUE", "Смерть ЗЛ в результате несчастного случая - Страховая сумма");
        //новые системные имена рисков маяка классического
        columnNamesByKeyNames.put(".riskList.RB-ILI0_MAIN_PROGRAM.INSAMVALUE", "Дожитие ЗЛ и смерть по любой причине - Страховая сумма");
        columnNamesByKeyNames.put(".riskList.RB-ILI0_DEATH_DUE_ACC.INSAMVALUE", "Смерть ЗЛ в результате несчастного случая - Страховая сумма");
        //новые системные имена рисков маяка купонного
        columnNamesByKeyNames.put(".riskList.RB_ILIK_MAIN_PROGRAM.INSAMVALUE", "Дожитие ЗЛ и смерть по любой причине - Страховая сумма");
        columnNamesByKeyNames.put(".riskList.RB_ILIK_DEATH_DUE_ACC.INSAMVALUE", "Смерть ЗЛ в результате несчастного случая - Страховая сумма");

        columnNamesByKeyNames.put(".riskList.ILI0_MAIN_PROGRAM.INSAMVALUE", "Дожитие ЗЛ и смерть по любой причине - Страховая сумма");
        columnNamesByKeyNames.put(".riskList.ILI0_DEATH_DUE_ACC.INSAMVALUE", "Смерть ЗЛ в результате несчастного случая - Страховая сумма");

        columnNamesByKeyNames.put(".CONTREXTMAP.deathGSS1Year", "Смерть по любой причине - ГСС в 1-й год страхования");
        columnNamesByKeyNames.put(".CONTREXTMAP.deathGSS2Year", "Смерть по любой причине - ГСС в 2-ой год страхования");
        columnNamesByKeyNames.put(".CONTREXTMAP.deathGSS3Year", "Смерть по любой причине - ГСС в 3-й год страхования");
        columnNamesByKeyNames.put(".CONTREXTMAP.marketRateOfReturn", "Текущая рыночная доходность");
        columnNamesByKeyNames.put(".CONTREXTMAP.fund", "Фонд");

        Map<String, String> addressColumnNamesByKeyNames = new LinkedHashMap<String, String>();
        addressColumnNamesByKeyNames.put(".REGION", "Регион");
        addressColumnNamesByKeyNames.put(".CITY", "Город или населенный пункт");
        addressColumnNamesByKeyNames.put(".ADDRESSTEXT2", "Город или населенный пункт");
        addressColumnNamesByKeyNames.put(".STREET", "Улица");
        addressColumnNamesByKeyNames.put(".HOUSE", "Дом, литер, корпус, строение");
        addressColumnNamesByKeyNames.put(".FLAT", "Квартира");
        addressColumnNamesByKeyNames.put(".POSTALCODE", "Индекс");

        Map<String, String> documentColumnNamesByKeyNames = new LinkedHashMap<String, String>();
        documentColumnNamesByKeyNames.put(".DOCTYPENAME", "Наименование документа");
        documentColumnNamesByKeyNames.put(".FULLSTR", "Полные данные документа");
        documentColumnNamesByKeyNames.put(".DOCSERIES", "Серия");
        documentColumnNamesByKeyNames.put(".DOCNUMBER", "Номер");
        documentColumnNamesByKeyNames.put(".ISSUEDATE", "Дата выдачи");
        documentColumnNamesByKeyNames.put(".ISSUEDBY", "Кем выдан");

        LinkedHashMap<String, String> passportRFColumnNamesByKeyNames = new LinkedHashMap<String, String>();
        passportRFColumnNamesByKeyNames.putAll(documentColumnNamesByKeyNames);
        passportRFColumnNamesByKeyNames.put(".ISSUERCODE", "Код подразделения");

        LinkedHashMap<String, String> foreignAddDocsColumnNamesByKeyNames = new LinkedHashMap<String, String>();
        foreignAddDocsColumnNamesByKeyNames.putAll(documentColumnNamesByKeyNames);
        foreignAddDocsColumnNamesByKeyNames.remove(".ISSUEDATE");
        foreignAddDocsColumnNamesByKeyNames.remove(".ISSUEDBY");

        // Документ на право проживания (пребывания)
        LinkedHashMap<String, String> foreignResDocColumnNamesByKeyNames = new LinkedHashMap<String, String>();
        foreignResDocColumnNamesByKeyNames.putAll(foreignAddDocsColumnNamesByKeyNames);
//        foreignResDocColumnNamesByKeyNames.put(".RESSTARTDATE", "Срок пребывания с");
//        foreignResDocColumnNamesByKeyNames.put(".RESFINISHDATE", "Срок пребывания по");
        foreignResDocColumnNamesByKeyNames.put(".VALIDFROMDATE", "Срок пребывания с");
        foreignResDocColumnNamesByKeyNames.put(".VALIDTODATE", "Срок пребывания по");

        // Миграционная карта
        LinkedHashMap<String, String> foreignMigDocColumnNamesByKeyNames = new LinkedHashMap<String, String>();
        foreignMigDocColumnNamesByKeyNames.putAll(foreignAddDocsColumnNamesByKeyNames);
//        foreignMigDocColumnNamesByKeyNames.put(".MIGSTARTDATE", "Срок пребывания с");
//        foreignMigDocColumnNamesByKeyNames.put(".MIGFINISHDATE", "Срок пребывания по");
        foreignMigDocColumnNamesByKeyNames.put(".VALIDFROMDATE", "Срок пребывания с");
        foreignMigDocColumnNamesByKeyNames.put(".VALIDTODATE", "Срок пребывания по");
        foreignMigDocColumnNamesByKeyNames.put(".SERNUM", "Серия номер");

        Map<String, String> insurerColumnNamesByKeyNames = new LinkedHashMap<String, String>();
        // Общая информация
        insurerColumnNamesByKeyNames.put(".LASTNAME", "Фамилия");
        insurerColumnNamesByKeyNames.put(".FIRSTNAME", "Имя");
        insurerColumnNamesByKeyNames.put(".MIDDLENAME", "Отчество");
        insurerColumnNamesByKeyNames.put(".FIO", "ФИО");
        insurerColumnNamesByKeyNames.put(".BIRTHDATE", "Дата рождения");
        //insurerColumnNamesByKeyNames.put(".GENDER", "Пол");
        insurerColumnNamesByKeyNames.put(".GENDERSTR", "Пол");
        //insurerColumnNamesByKeyNames.put(".CITIZENSHIP", "Гражданство");
        insurerColumnNamesByKeyNames.put(".CITIZENSHIPSTR", "Гражданство");
        insurerColumnNamesByKeyNames.put(".INN", "ИНН");
        //insurerColumnNamesByKeyNames.put(".extAttributeList2.MaritalStatus.EXTATTVAL_VALUE", "Семейное положение");
        insurerColumnNamesByKeyNames.put(".extAttributeList2.MaritalStatus.EXTATTVAL_VALUESTR", "Семейное положение");
        insurerColumnNamesByKeyNames.put(".extAttributeList2.birthPlaceExt.EXTATTVAL_VALUE", "Страна рождения");
        insurerColumnNamesByKeyNames.put(".BIRTHPLACE", "Место рождения");
        //insurerColumnNamesByKeyNames.put(".extAttributeList2.unResident.EXTATTVAL_VALUE", "Нерезидент");
        insurerColumnNamesByKeyNames.put(".extAttributeList2.unResident.EXTATTVAL_VALUESTR", "Нерезидент");
        // Cтатус налогового резидента иностранного государства
        //insurerColumnNamesByKeyNames.put(".extAttributeList2.isTaxResidentUSA.EXTATTVAL_VALUE", "Налоговый резидент США");
        insurerColumnNamesByKeyNames.put(".extAttributeList2.isTaxResidentUSA.EXTATTVAL_VALUESTR", "Налоговый резидент США");
        insurerColumnNamesByKeyNames.put(".extAttributeList2.INNUSA.EXTATTVAL_VALUE", "ИНН США");
        insurerColumnNamesByKeyNames.put(".extAttributeList2.taxResidentOtherCountry.EXTATTVAL_VALUE", "Налоговый резидент другой страны");
        insurerColumnNamesByKeyNames.put(".extAttributeList2.INNOther.EXTATTVAL_VALUE", "ИНН другой страны");
        insurerColumnNamesByKeyNames.put(".extAttributeList2.residencePermitForeignCountry.EXTATTVAL_VALUE", "Вид на жительство в иностранном государстве");
        // документы
        addSubMapLevel(insurerColumnNamesByKeyNames, ".documentList.PassAndBorn", "Объединение паспорта и свидетельства", passportRFColumnNamesByKeyNames);
        addSubMapLevel(insurerColumnNamesByKeyNames, ".documentList.BornCertificate", "Свидетельство о рождении", documentColumnNamesByKeyNames);
        addSubMapLevel(insurerColumnNamesByKeyNames, ".documentList.PassportRF", "Паспорт гражданина РФ", passportRFColumnNamesByKeyNames);
        addSubMapLevel(insurerColumnNamesByKeyNames, ".documentList.ForeignPassport", "Паспорт иностранного гражданина", documentColumnNamesByKeyNames);
        addSubMapLevel(insurerColumnNamesByKeyNames, ".documentList.MigrationCard", "Миграционная карта", foreignMigDocColumnNamesByKeyNames);
        addSubMapLevel(insurerColumnNamesByKeyNames, ".documentList.РазрВр", "Документ на право проживания", foreignResDocColumnNamesByKeyNames);
        // адреса
        addSubMapLevel(insurerColumnNamesByKeyNames, ".addressList.FactAddress", "Фактический адрес", addressColumnNamesByKeyNames);
        addSubMapLevel(insurerColumnNamesByKeyNames, ".addressList.RegisterAddress", "Адрес регистрации", addressColumnNamesByKeyNames);
        // Сведения о работе
        insurerColumnNamesByKeyNames.put(".extAttributeList2.education.EXTATTVAL_VALUESTR", "Текущая профессия");
        insurerColumnNamesByKeyNames.put(".extAttributeList2.EmployerName.EXTATTVAL_VALUE", "Наименование организации");
        insurerColumnNamesByKeyNames.put(".extAttributeList2.activityBusinessKind.EXTATTVAL_VALUESTR", "Сфера деятельности");
        insurerColumnNamesByKeyNames.put(".extAttributeList2.Position.EXTATTVAL_VALUE", "Должность");
        // Контактные данные
        insurerColumnNamesByKeyNames.put(".contactList.MobilePhone.VALUE", "Контактный телефон");
        insurerColumnNamesByKeyNames.put(".contactList.FactAddressPhone.VALUE", "Дополнительный телефон");
        insurerColumnNamesByKeyNames.put(".contactList.PersonalEmail.VALUE", "Электронная почта");

        addSubMapLevel(columnNamesByKeyNames, ".INSURERMAP", "Страхователь", insurerColumnNamesByKeyNames);
        addSubMapLevel(columnNamesByKeyNames, ".INSUREDMAP", "Застрахованный", insurerColumnNamesByKeyNames);

        Map<String, String> beneficiaryColumnNamesByKeyNames = new LinkedHashMap<String, String>();
        beneficiaryColumnNamesByKeyNames.put(".RISK", "Риск");
        //beneficiaryColumnNamesByKeyNames.put(".TYPEID", "Тип");
        beneficiaryColumnNamesByKeyNames.put(".TYPESTR", "Тип");
        beneficiaryColumnNamesByKeyNames.put(".FIO", "ФИО");
        beneficiaryColumnNamesByKeyNames.put(".PASSPORT", "Паспорт");
        beneficiaryColumnNamesByKeyNames.put(".PART", "Процент");
        addSubMapLevel(beneficiaryColumnNamesByKeyNames, ".PARTICIPANTMAP", "", insurerColumnNamesByKeyNames);

        addSubMapLevel(columnNamesByKeyNames, ".BENEFICIARYLIST.0", "Выгодоприобретатель 1", beneficiaryColumnNamesByKeyNames);
        addSubMapLevel(columnNamesByKeyNames, ".BENEFICIARYLIST.1", "Выгодоприобретатель 2", beneficiaryColumnNamesByKeyNames);
        addSubMapLevel(columnNamesByKeyNames, ".BENEFICIARYLIST.2", "Выгодоприобретатель 3", beneficiaryColumnNamesByKeyNames);
        addSubMapLevel(columnNamesByKeyNames, ".BENEFICIARYLIST.3", "Выгодоприобретатель 4", beneficiaryColumnNamesByKeyNames);
        addSubMapLevel(columnNamesByKeyNames, ".BENEFICIARYLIST.4", "Выгодоприобретатель 5", beneficiaryColumnNamesByKeyNames);
        addSubMapLevel(columnNamesByKeyNames, ".BENEFICIARYLIST.5", "Выгодоприобретатель 6", beneficiaryColumnNamesByKeyNames);

        // в самом конце
        columnNamesByKeyNames.put(".ERROR", "Ошибка");
    }

    private void addSubMapLevel(Map<String, String> mainMap, String parentKey, String parentValue, Map<String, String> subMap) {
        String valuePrefix = parentValue.isEmpty() ? "" : parentValue + " - ";
        for (Map.Entry<String, String> entry : subMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            mainMap.put(parentKey + key, valuePrefix + value);
        }
    }

    /*
    protected String getUseSeaweedFS() {
        String login;
        Config config = Config.getConfig(SERVICE_NAME);
        login = config.getParam("USESEAWEEDFS", "FALSE");
        return login;
    }

    protected String getSeaweedFSUrl() {
        String login;
        Config config = Config.getConfig(SERVICE_NAME);
        login = config.getParam("SEAWEEDFSURL", "");
        return login;
    }
    */

    protected void flatDataMap(Map<String, Object> flatData, Map<String, Object> data, String dataNodePath) {
        //boolean isTargetClassString = String.class.equals(targetClass);
        //Map<String, Object> additionalTimeEntries = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String keyName = entry.getKey();
            Object value = entry.getValue();
            if (value != null) {
                String keyFullName = dataNodePath + "." + keyName;
                if (value instanceof Map) {
                    Map<String, Object> map = (Map<String, Object>) value;
                    flatDataMap(flatData, map, keyFullName);
                } else if (value instanceof List) {

                    String sysNameField = listsSysNameFieldsByListNames.get(keyName);
                    if (sysNameField != null) {

                        ArrayList<Object> list = (ArrayList<Object>) value;
                        for (int i = 0; i < list.size(); i++) {
                            Object rawElement = list.get(i);
                            if (rawElement instanceof Map) {
                                Map<String, Object> element = (Map<String, Object>) rawElement;
                                String elementSysName;
                                if (sysNameField.equals("*")) {
                                    elementSysName = Integer.toString(i);
                                } else {
                                    elementSysName = getStringParam(element, sysNameField);
                                }
                                flatDataMap(flatData, element, keyFullName + "." + elementSysName);
                            }
                        }

                    }
                } else if (!alwaysSkippedKeyNames.contains(keyName)) {
                    flatData.put(keyFullName, value);
                    //logger.debug(keyFullName + " = " + value);
                }
            }
        }

    }

    private Map<String, Object> makeFlatContrMap(Map<String, Object> contrMap) {
        Map<String, Object> flatContrMap = new HashMap<String, Object>();
        flatDataMap(flatContrMap, contrMap, "");
        return flatContrMap;
    }

    private Map<String, Object> cleanFlatContrMap(Map<String, Object> flatContrMap) {
        logger.debug("cleanFlatContrMap...");
        Map<String, Object> cleanFlatContrMap = new HashMap<String, Object>();
        for (Map.Entry<String, String> entry : columnNamesByKeyNames.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            Object flatContrMapValue = flatContrMap.get(key);
            if (flatContrMapValue == null) {
                flatContrMapValue = "";
            }
            //logger.debug(key + " = " + flatContrMapValue);
            cleanFlatContrMap.put(key, flatContrMapValue);
        }
        logger.debug("cleanFlatContrMap finished.");
        return cleanFlatContrMap;
    }

    private List<Map<String, Object>> prepareExportMap(Map<String, Object> flatContrMap) {
        List<Map<String, Object>> preparedExportMapRowBean = new ArrayList<Map<String, Object>>();
        int i = 0;
        for (Map.Entry<String, String> entry : columnNamesByKeyNames.entrySet()) {
            String key = entry.getKey();
            //Object value = entry.getValue();
            Map<String, Object> exportCellBean = new HashMap<String, Object>();
            exportCellBean.put("position", i);
            Object flatContrMapValue = flatContrMap.get(key);
            if ((flatContrMapValue != null) && (flatContrMapValue instanceof Double) && (key.endsWith("VALUE"))) {
                flatContrMapValue = String.format("%.2f", getDoubleParam(flatContrMapValue));
            }
            exportCellBean.put("value", flatContrMapValue);
            preparedExportMapRowBean.add(exportCellBean);
            i++;
        }
        return preparedExportMapRowBean;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BBrowseContract4Export2Any(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> loadRes = null;
        if (params.get("OBJECTID") != null) {
            Long contractID = getLongParamLogged(params, "OBJECTID");
            logger.debug("Export: begin prepare data for contrid = " + contractID);

            Map<String, Object> loadParam = new HashMap<String, Object>();
            loadParam.put("CONTRID", contractID);
            loadParam.put("SKIPDATESRECALC", true);
            loadParam.put(RETURN_AS_HASH_MAP, true);
            //String dataProviderMethodName = "dsB2BSberLifePrintDocDataProvider";
            String dataProviderMethodName = "dsB2BSberLifeExportReportDataProvider";
            try {
                loadRes = this.callServiceTimeLogged(B2BPOSWS, dataProviderMethodName, loadParam, login, password);
            } catch (Exception ex) {
                loadRes = new HashMap<String, Object>();
                loadRes.put("CONTRID", contractID);
                loadRes.put("ERROR", ex.getMessage());
                logger.error(String.format("Exception during getting contract data by %s for contract with CONTRID = %d! Details: ", dataProviderMethodName, contractID), ex);
            }

            // здесь необходимо догрузить сущности не поддержанные универсальной загрузкой.
            // платежи
            if (loadRes != null) {
                if (loadRes.get("CONTRNODEID") != null) {
                    Long contrNodeId = (Long) loadRes.get("CONTRNODEID");
                    Map<String, Object> planParams = new HashMap<String, Object>();
                    planParams.put("CONTRNODEID", contrNodeId);
                    //planParams.put("ReturnAsHashMap", "TRUE");
                    Map<String, Object> qPlanRes = this.callServiceTimeLogged(B2BPOSWS, "dsB2BPaymentFactBrowseListByParam", planParams, login, password);
                    loadRes.put("PAYMENTLIST", WsUtils.getListFromResultMap(qPlanRes));
                }
                // получение графика оплаты
                if (loadRes.get("CONTRID") != null) {
                    Map<String, Object> factParams = new HashMap<String, Object>();
                    Long contrId = Long.valueOf(loadRes.get("CONTRID").toString());
                    factParams.put("CONTRID", contrId);
                    Map<String, Object> qFactRes = this.callServiceTimeLogged(B2BPOSWS, "dsB2BPaymentBrowseListByParam", factParams, login, password);
                    loadRes.put("PAYMENTSCHEDULELIST", qFactRes.get(RESULT));

                }
                //
                prepareDocs(loadRes);
                // загрузка секций по договору и расчет премии агента
                calcAgentPrem(loadRes, login, password);
                
                Map<String, Object> flatLoadRes = makeFlatContrMap(loadRes);
                Map<String, Object> flatCleanLoadRes = cleanFlatContrMap(flatLoadRes);
                /*List<Map<String, Object>> preparedExportMapRowBean = prepareExportMap(flatCleanLoadRes);
                loadRes.clear();*/
                loadRes.put("EXPORTROWBEAN", flatLoadRes);
            }

            logger.debug("Export: end prepare data for contrid = " + contractID);
        } else if (params.get("EXPORTDATAID") != null) {
            // строковый список идентификаторов объектов
            Long exportDataID = getLongParam(params.get("EXPORTDATAID"));
            logger.debug("Export: begin prepare data for exportDataId = " + exportDataID);
            String objectIDsListStr = getObjectIDsListStrByExportDataID(exportDataID, login, password);

            // подготовка параметров для запрос списка со сведениями объектов
            Map<String, Object> queryParams = new HashMap<String, Object>();
            queryParams.put("CONTRIDLIST", objectIDsListStr);
            Map<String, Object> dataList = null;
            // параметры для постраничных запросов, фомируются angular-гридом
            queryParams.put("PAGE", params.get("PAGE"));
            queryParams.put("ROWSCOUNT", params.get("ROWSCOUNT"));
            queryParams.put("CP_TODAYDATE", new Date());

            // запрос списка со сведениями объектов
            loadRes = this.callService(B2BPOSWS, "dsB2BContractBrowseListByParamExShort", queryParams, login, password);

        } else {
            logger.error("dsB2BBrowseContract4export2XML has not OBJECTID or EXPORTDATAID in params");

        }
        return loadRes;
    }

    protected List<Map<String, Object>> getObjectIDsListByExportDataID(Long exportDataID, String login, String password) throws Exception {

        logger.debug("Getting objects ids list for export data with id (EXPORTDATAID) = " + exportDataID);
        Map<String, Object> contentParams = new HashMap<String, Object>();
        contentParams.put("EXPORTDATAID", exportDataID);
        Map<String, Object> content = this.callService(B2BPOSWS, "dsB2BExportDataContentBrowseListByParam", contentParams, login, password);
        List<Map<String, Object>> objectIDsList = WsUtils.getListFromResultMap(content);
        logger.debug("Objects ids list = " + objectIDsList);

        return objectIDsList;
    }

    private void prepareDocs(Map<String, Object> loadRes) {

        if (loadRes.get("CURRENCYRATE") == null) {
            loadRes.put("CURRENCYRATE", 1);
        }
        if (loadRes.get("INSAMCURRENCYID") != null) {
            if ("1".equals(getStringParam(loadRes.get("INSAMCURRENCYID")))) {
                // рубли
                loadRes.put("INSAMVALUEINRUB", loadRes.get("INSAMVALUE"));
            } else {
                // валюта
                loadRes.put("INSAMVALUEINUSD", loadRes.get("INSAMVALUE"));
            }
        }
        if (loadRes.get("PREMCURRENCYID") != null) {
            if ("1".equals(getStringParam(loadRes.get("PREMCURRENCYID")))) {
                // рубли
                loadRes.put("PREMVALUEINRUB", loadRes.get("PREMVALUE"));
            } else {
                // валюта
                loadRes.put("PREMVALUEINUSD", loadRes.get("PREMVALUE"));
            }
        }

        processPartDocs(loadRes, (Map<String, Object>) loadRes.get("INSURERMAP"));
        processPartDocs(loadRes, (Map<String, Object>) loadRes.get("INSUREDMAP"));
        List<Map<String, Object>> memberList = (List<Map<String, Object>>) loadRes.get("MEMBERLIST");
        if (null != memberList) {
            for (Map<String, Object> map : memberList) {
                Map<String, Object> participantMap = (Map<String, Object>) map.get("PARTICIPANTMAP");
                processPartDocs(loadRes, participantMap);
                if (participantMap.get("FIO") != null) {
                    map.put("FIO", participantMap.get("FIO"));
                }
                if (participantMap.get("PASSPORT") != null) {
                    map.put("PASSPORT", participantMap.get("PASSPORT"));
                }
            }
        }
        List<Map<String, Object>> beneficiaryList = (List<Map<String, Object>>) loadRes.get("BENEFICIARYLIST");
        if (null != beneficiaryList) {
            for (Map<String, Object> map : beneficiaryList) {
                Map<String, Object> participantMap = (Map<String, Object>) map.get("PARTICIPANTMAP");
                map.put("FIO", map.get("TYPESTR"));
                if (participantMap != null) {
                    processPartDocs(loadRes, participantMap);
                    if (participantMap.get("FIO") != null) {
                        map.put("FIO", participantMap.get("FIO"));
                    }
                    if (participantMap.get("PASSPORT") != null) {
                        map.put("PASSPORT", participantMap.get("PASSPORT"));
                    }
                }
            }
        }

        Map<String, Object> createUserMap = (Map<String, Object>) loadRes.get("CREATEUSERMAP");
        if ((null != createUserMap) && (createUserMap.get("LASTNAME") != null)) {
            String fio = getStringParam(createUserMap.get("LASTNAME")) + " " + getStringParam(createUserMap.get("FIRSTNAME"));
            if (createUserMap.get("MIDDLENAME") != null) {
                fio = fio + " " + getStringParam(createUserMap.get("MIDDLENAME"));
            }
            createUserMap.put("FIO", fio);
        }
    }

    private void processPartDocs(Map<String, Object> loadRes, Map<String, Object> partMap) {
        if (partMap != null) {
            if (partMap.get("LASTNAME") != null) {
                String fio = getStringParam(partMap.get("LASTNAME")) + " " + getStringParam(partMap.get("FIRSTNAME"));
                if (partMap.get("MIDDLENAME") != null) {
                    fio = fio + " " + getStringParam(partMap.get("MIDDLENAME"));
                }
                partMap.put("FIO", fio);
            }
            String passport = "";
            List<Map<String, Object>> docList = (List<Map<String, Object>>) partMap.get("documentList");
            if (docList != null) {
                Map<String, Object> newDocMap = new HashMap<>();
                for (Map<String, Object> docMap : docList) {
                    String curtypesysname = getStringParam(docMap.get("DOCTYPESYSNAME"));
                    if ("MigrationCard".equalsIgnoreCase(curtypesysname)) {
                        String fullDocStr = getStringParam(docMap.get("DOCSERIES"))
                                + " " + getStringParam(docMap.get("DOCNUMBER"))
                                + " " + getStringParam(docMap.get("VALIDFROMDATE"))
                                + " " + getStringParam(docMap.get("VALIDTODATE"));
                        docMap.put("FULLSTR", fullDocStr);
                        String fullDocSerNum = getStringParam(docMap.get("DOCSERIES"))
                                + " " + getStringParam(docMap.get("DOCNUMBER"));
                        docMap.put("SERNUM", fullDocSerNum);
                    } else {
                        String fullDocStr = getStringParam(docMap.get("DOCTYPENAME")) + " " + getStringParam(docMap.get("DOCSERIES"))
                                + " " + getStringParam(docMap.get("DOCNUMBER"));
                        docMap.put("FULLSTR", fullDocStr);
                        passport = fullDocStr;
                    }
                    if ("BornCertificate".equalsIgnoreCase(curtypesysname)) {
                        newDocMap.putAll(docMap);
                    } else if ("PassportRF".equalsIgnoreCase(curtypesysname)) {
                        newDocMap.putAll(docMap);

                    }
                }

                newDocMap.put("DOCTYPESYSNAME", "PassAndBorn");
                docList.add(newDocMap);
            }

            List<Map<String, Object>> contactList = (List<Map<String, Object>>) partMap.get("contactList");
            if (contactList != null) {
                Map<String, Object> newDocMap = new HashMap<>();
                String contacts = "";
                for (Map<String, Object> contactMap : contactList) {
                    String val = getStringParam(contactMap.get("VALUE"));
                    if ("MobilePhone".equalsIgnoreCase(getStringParam(contactMap.get("CONTACTTYPESYSNAME")))) {
                        try {
                            val = formatMobilePhone(val);                            
                        } catch (Exception e) {
                        }
                    }
                    if ("MobilePhone".equalsIgnoreCase(getStringParam(contactMap.get("CONTACTTYPESYSNAME")))) {
                        try {
                            val = formatMobilePhone(val);                            
                        } catch (Exception e) {
                        }
                    }
                    if (contacts.isEmpty()) {
                        contacts = val;
                    } else {
                        contacts = contacts + ", " + val;
                    }

                }

                partMap.put("CONTACTS", contacts);
            }

            partMap.put("PASSPORT", passport);
        }
    }

    @WsMethod(requiredParams = {"EXPORTDATAID", "TEMPLATEID"})
    public Map<String, Object> dsB2BExportDataCreateNonSQLXLSReport(Map<String, Object> params) throws Exception {

        logger.debug("Expor1t data report creating...");

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = new HashMap<String, Object>();

        // идентификатор обрабатываемой записи
        Long exportDataID = getLongParam(params.get("EXPORTDATAID"));
        logger.debug("Export data record id (EXPORTDATAID) = " + exportDataID);

        // получение шаблона обрабатываемой записифввreasdf
        Long templateID = getLongParam(params.get("TEMPLATEID"));
        logger.debug("Template id for this export data (TEMPLATEID) = " + templateID);
        Map<String, Object> template = getExportDataTemplateByID(templateID, login, password);

        // имя метода по получению данных
        String dataMethod = getStringParamLogged(template, "DATAMETHOD");

        Map<String, Object> contentParams = new HashMap<String, Object>();
        contentParams.put("EXPORTDATAID", exportDataID);
        Map<String, Object> content = this.callServiceTimeLogged(B2BPOSWS, "dsB2BContractExportDataContentBrowseListByParamEx", contentParams, login, password);
        // список мап с ИД
        List<Map<String, Object>> idList = WsUtils.getListFromResultMap(content);

        // формирование списка со сведениями объектов
        // c протоколированием времени 
        long dataListPrepareTimerMs = System.currentTimeMillis();
        logger.debug("Data list(s) preparing started.");

        List<Map<String, Object>> dataList = new ArrayList<>();
        List<Map<String, Object>> dataList1 = new ArrayList<>();
        logger.debug("data list prapare " + idList.size() + " contr");
        int count = 0;
        for (Map<String, Object> idParam : idList) {
            idParam.put(RETURN_AS_HASH_MAP, true);
            count++;

            Map<String, Object> dataRes = (Map<String, Object>) this.callServiceTimeLogged(B2BPOSWS, dataMethod, idParam, login, password);
            Map<String, Object> dataItem = (Map<String, Object>) dataRes.get("EXPORTROWBEAN");
            if (dataItem != null) {
                String prodName = idParam.get("PRODSYSNAME").toString();
                if (prodName != null) {
                    dataItem.put(".CONTRNUMBERSTR", getStringParam(dataItem.get(".CONTRPOLSER")) + " № " + getStringParam(dataItem.get(".CONTRPOLNUM")));
                    String termStr = getStringParam(dataItem.get(".TERMSTR"));
                    termStr = getStringParam(termStr).replaceAll("[^0-9]", "");
                    dataItem.put(".TERMSTR", termStr);
                    Date now = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                    dataItem.put(".TODAYDATESTR", sdf.format(now));
                    //if (prodName.equalsIgnoreCase("B2B_CAPITAL")) {
                    if (prodName.equalsIgnoreCase("ACCELERATION_RB-FL")) {
                        dataItem.put("EMPTYSTR", "");
                        dataItem.put(".SALECHANNEL", "Сбербанк Первый");
                        dataItem.put(".SALECHANNELMERC", "");
                        dataItem.put(".SEGMENT", "Росбанк");
                        dataItem.put(".PRODPROGSTR", "Капитал");
                        genSumFormattedStr(dataItem, ".PREMVALUE");
                        genSumFormattedStr(dataItem, ".PREMVALUERUB");
                        genSumFormattedStr(dataItem, ".riskList.survivalDeath.INSAMVALUE");
                        genSumFormattedStr(dataItem, ".riskList.accidentDeath.INSAMVALUE");
                        genSumFormattedStr(dataItem, ".CONTREXTMAP.deathGSS1Year");
                        genSumFormattedStr(dataItem, ".CONTREXTMAP.deathGSS2Year");
                        genSumFormattedStr(dataItem, ".CONTREXTMAP.deathGSS3Year");

                        dataList.add(dataItem);
                    //} else if (prodName.equalsIgnoreCase("B2B_INVEST_NUM1") || prodName.equalsIgnoreCase("B2B_INVEST_COUPON")) {
                    } else if (prodName.equalsIgnoreCase("LIGHTHOUSE") || prodName.equalsIgnoreCase("SMART_POLICY_RB_ILIK")
                            || prodName.equalsIgnoreCase("SMART_POLICY") || prodName.equalsIgnoreCase("SMART_POLICY_LIGHT")) {
                        dataItem.put("EMPTYSTR", "");
                        dataItem.put(".SALECHANNEL", "Сбербанк Первый");
                        dataItem.put(".SALECHANNELMERC", "");
                        dataItem.put(".SEGMENT", "Приват");
                        dataItem.put(".PRODPROGSTR", "Маяк");
                        dataItem.put("OSB", "Росбанк");

                        String fundStr = getFundStr(dataItem, login, password);

                        dataItem.put(".CONTREXTMAP.fundSTR", fundStr);

                        dataList1.add(dataItem);
//                    } else if (prodName.equalsIgnoreCase("B2B_RIGHT_DECISION")) {
                    } else if (prodName.equalsIgnoreCase("FAMALYASSETS_RB-FCC0")) {
                        dataItem.put("EMPTYSTR", "");
                        dataItem.put(".SALECHANNEL", "Сбербанк Первый");
                        dataItem.put(".SALECHANNELMERC", "");
                        dataItem.put(".SEGMENT", "Премьер");
                        dataItem.put(".PRODPROGSTR", "Семейный актив");
                        dataItem.put("OSB", "Росбанк");

                        String fundStr = getFundStr(dataItem, login, password);

                        dataItem.put(".CONTREXTMAP.fundSTR", fundStr);

                        dataList.add(dataItem);
                    //} else if (prodName.equalsIgnoreCase("B2B_FIRST_STEP")) {
                    } else if (prodName.equalsIgnoreCase("FIRSTCAPITAL_RB-FCC0")) {
                        dataItem.put("EMPTYSTR", "");
                        dataItem.put(".SALECHANNEL", "Сбербанк Первый");
                        dataItem.put(".SALECHANNELMERC", "");
                        dataItem.put(".SEGMENT", "Первый");
                        dataItem.put(".PRODPROGSTR", "Первый капитал");
                        dataItem.put("OSB", "Росбанк");

                        String fundStr = getFundStr(dataItem, login, password);

                        dataItem.put(".CONTREXTMAP.fundSTR", fundStr);

                        dataList.add(dataItem);
                    }else if (prodName.equalsIgnoreCase("B2B_BORROWER_PROTECT")) {
                        dataItem.put("EMPTYSTR", "");
                        dataItem.put(".SALECHANNEL", "Сбербанк Первый");
                        dataItem.put(".SALECHANNELMERC", "");
                        dataItem.put(".SEGMENT", "Первый");
                        dataItem.put(".PRODPROGSTR", "Защищенный заемщик");
                        dataItem.put("OSB", "Росбанк");

                        String fundStr = getFundStr(dataItem, login, password);

                        dataItem.put(".CONTREXTMAP.fundSTR", fundStr);

                        dataList.add(dataItem);
                    }
                }
            }
            logger.debug("ata list processed " + count + " of " + idList.size() + " contr");
        }
        //logger.debug("dataList = " + dataList); // отключено, слишком большая запись в протокол
        //logger.debug("dataList1 = " + dataList1); // отключено, слишком большая запись в протокол
        // протоколирование времени формирования списка со сведениями объектов
        dataListPrepareTimerMs = System.currentTimeMillis() - dataListPrepareTimerMs;
        logger.debug(String.format("Data list(s) preparing executed in %d milliseconds (approximately %.5f seconds).", dataListPrepareTimerMs, ((double) dataListPrepareTimerMs) / 1000.0));

        // подготовка параметров для генерации отчета
        Map<String, Object> reportData = new HashMap<String, Object>();
        //reportData.put("COLUMNLIST", );
        reportData.put("DATALIST", dataList);
        reportData.put("COLUMNLIST", getCapitalFieldPositionList());
        reportData.put("DATALIST1", dataList1);
        reportData.put("COLUMNLIST1", getInvestFieldPositionList());
        String reportFormat = ".xls";
        boolean needReprint = true;
        if (needReprint) {
            // пытаемся получить файл
            logger.debug("remove attach doc for expData: " + exportDataID.toString());
            Map<String, Object> getMap = new HashMap<String, Object>();
            getMap.put("OBJID", exportDataID);
            Map<String, Object> getRes = this.callService(Constants.B2BPOSWS, "dsB2BExportData_BinaryFile_BinaryFileBrowseListByParam", getMap, login, password);
            if (getRes != null) {
                if (getRes.get(RESULT) != null) {
                    List<Map<String, Object>> binFileList = (List<Map<String, Object>>) getRes.get(RESULT);
                    if (!binFileList.isEmpty()) {
                        logger.debug("binFile for remove: " + binFileList.size());
                        for (Map<String, Object> binFile : binFileList) {
                            if (binFile.get("BINFILEID") != null) {
                                // если нужна перепечать - грохнуть все прикрепленные к договору документы.
                                Map<String, Object> delMap = new HashMap<String, Object>();
                                delMap.put("BINFILEID", binFile.get("BINFILEID"));
                                this.callService(Constants.B2BPOSWS, "dsB2BExportData_BinaryFile_deleteBinaryFileInfo", delMap, login, password);
                            }
                        }

                    }
                }
            }
        }
        String uploadPath = getUploadFilePath();
        // генерация отчета
        String templateName = getStringParam(template.get("REPTEMPLATENAME"));

        String reportName = genNoSQLExportReport(reportData, reportFormat, templateName, login, password);

        /*
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd-hh.mm.ss");
        Date now = new Date();
        Date shiftNow = new Date(now.getTime());
        */

        String userFileName = getExportDataFilenameDateStr() + " " + getStringParam(template.get("CAPTION")) + ".xls";

        String expDataFilePath = uploadPath + "/" + reportName;
        String expDataFileName = reportName;
        if (getUseSeaweedFS().equalsIgnoreCase("TRUE")) {
            String masterUrlString = getSeaweedFSUrl();
            URL masterURL = new URL(masterUrlString);
            WeedFSClient client = WeedFSClientBuilder.createBuilder().setMasterUrl(masterURL).build();
            Assignation a = client.assign(new AssignParams("b2battach", ReplicationStrategy.TwiceOnRack));
            int size = client.write(a.weedFSFile, a.location, new FileInputStream(new File(expDataFilePath)), expDataFileName);
            if (size == 0) {
                throw new Exception("Unable to write file to SeaweedFS");
            }
            expDataFilePath = a.weedFSFile.fid;
        } else {
            expDataFilePath = reportName;
        }
        Map<String, Object> expDataBinParams = new HashMap<String, Object>();
        expDataBinParams.put("OBJID", exportDataID);
        expDataBinParams.put("FILENAME", userFileName);
        expDataBinParams.put("FILEPATH", expDataFilePath);
        expDataBinParams.put("FILESIZE", new File(expDataFilePath).length());
        expDataBinParams.put("FILETYPEID", 1015);
        expDataBinParams.put("FILETYPENAME", userFileName);
        expDataBinParams.put("FSID", expDataFilePath);
        this.callService(B2BPOSWS, "dsB2BExportData_BinaryFile_createBinaryFileInfo", expDataBinParams, login, password);

        // шифрование имен файлов отчета для возврата в angular-интерфейс
        //String encryptedFileNamesStr = getEncryptedFileNamesStr(reportName);
        String encryptedFileNamesStr = getEncryptedFileNamesStr(expDataFilePath, userFileName, getUseSeaweedFS().equalsIgnoreCase("TRUE"));

        if (!encryptedFileNamesStr.isEmpty()) {
            result.put("ENCRIPTEDFILENAME", encryptedFileNamesStr);
        }

        // сгенерированный отчет выдать пользователю в интерфейсе
        logger.debug(
                "Export data report creating finish.");
        return result;

    }


    /* private List<Map<String, Object>> getAgentTariffList(Map<String, Object> loadRes, String login, String password) {
        if ()
    }*/
    private String formatMobilePhone(String val) throws ParseException {
        MaskFormatter mf = new MaskFormatter("(###)-###-####");
        mf.setValueContainsLiteralCharacters(false);
        return mf.valueToString(val);
    }
}
