package com.bivgroup.flextera.insurance.mail.websms;

public interface constsSections {

    // SMPP section variable

    /**
     * Константа тип SMSMESSAGE. Идентифицирует СМС сообщение
     */
    static final String SMSMESSAGE = "SMSMessage";
    /**
     * Константа тип SMSNUMBER. Идентифицирует SMS номер
     */
    static final String SMSNUMBER = "SMSNum";
    /**
     * Константа CONFIG опеределяет имя сервиса, чей конфиг считывать
     */
    static final String CONFIG = "websmsws";
    /**
     * Константа, определяющая IP СМС центра
     */
    static final String SMSCENTERIP = "ip";
    /**
     * Константа, определяющая порт СМС центра
     */
    static final String SMSCENTERPORT = "port";
    /**
     * Константа SystemID для подключения к СМС центру
     */
    static final String SMSCENTERSYSTEMID = "SystemID";
    /**
     * Константа Password для подключения к СМС центру
     */
    static final String SMSCENTERPASSWORD = "Password";
    /**
     * Идентификтор оператора.
     */
    static final String SMSCOPERATOR = "Operator";
    /**
     * Идентификатор пропуска блока
     */
    static final String SMS_SKIP = "SMS_SKIP";
    /**
     * Служебная константа
     */
    static final String SMSCENTERENCLINK = "EnquireLink";
    /**
     * Константа для полущения данных типа "номер с которого отправляют"
     */
    static final String SMSCENTERSRCADDR = "SourceAddr";
    /**
     * Служебная константа
     */
    static final String SMSCENTERSERVICETYPE = "ServiceType";
    /**
     * Source TON. Параметр оператора
     */
    static final String SMSSOURCETON = "SourceTON";
    /**
     * Source NPI. Параметр оператора
     */
    static final String SMSSOURCENPI = "SourceNPI";
    /**
     * Dest TON. Параметр оператора
     */
    static final String SMSDESTTON = "DestTON";
    /**
     * Dest NPI. Параметр оператора
     */
    static final String SMSDESTNPI = "DestNPI";

    // SMTP section variable
    /**
     * Константа для получения значения параметра SMTPMESSAGE
     */
    static final String SMTPMESSAGE = "SMTPMESSAGE";
    /**
     * Константа для получения значения параметра SMTPUSER
     */
    static final String SMTPUSER = "SMTPUser";
    /**
     * Константа для получения значения параметра SMTPPASSWORD
     */
    static final String SMTPPASSWORD = "SMTPPassword";
    /**
     * Константа для получения значения параметра SMTPHOST
     */
    static final String SMTPHOST = "SMTPHost";
    /**
     * Константа для получения значения параметра SMTPPORT
     */
    static final String SMTPPORT = "SMTPPort";
    /**
     * Константа для получения значения параметра SMTPSUBJECT
     */
    static final String SMTPSUBJECT = "SMTPSubject";
    /**
     * Константа для получения значения параметра SMTPFROM
     */
    static final String SMTPFROM = "SMTPFrom";

    static final String USESMTPSSL = "USESMTPSSL";
    
    /**
     * Константа для получения значения параметра SMTPReceipt
     */
    static final String SMTPReceipt = "SMTPReceipt";

    /**
     * Константа для получения значения параметра ATTACHMENTMAP
     * содержит Map<ИмяАттача,ПутьДоФайлаАттача>
     */
    static final String SMTPAttachmentMap = "ATTACHMENTMAP";
    
    static final String HTMLText = "HTMLTEXT";

    // MIME header section
    /**
     * Константа для получения значения параметра ReturnPath
     */
    static final String ReturnPath = "ReturnPath";
    /**
     * Константа для получения значения параметра XMailer
     */
    static final String XMailer = "XMailer";
    /**
     * Константа для получения значения параметра MIMEVersion
     */
    static final String MIMEVersion = "MIMEVersion";
    /**
     * Константа для получения значения параметра ContentTransferEncoding
     */
    static final String ContentTransferEncoding = "ContentTransferEncoding";
    /**
     * Константа для получения значения параметра ContentType
     */
    static final String ContentType = "ContentType";
}
