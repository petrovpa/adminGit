DELETE FROM B2B_JOURNAL WHERE ID=11;
DELETE FROM B2B_JOURNALPARAM WHERE JOURNALID=11;
DELETE FROM INS_DATAPROV WHERE DATAPROVID=20700;

INSERT INTO B2B_JOURNAL (ID, NAME, SYSNAME, SQLDATA, CREATEDATE, CREATEUSERID, UPDATEDATE, UPDATEUSERID, DATAPROVIDERID)
VALUES (11, 'Журнал Пользователи', 'ADMINUSERSLIST', '[{"headerName":"№", "width":1, "type":"COUNTER", "suppressSorting":true},
 {"headerName":"ИД","width":90, "field":"USERACCOUNTID", "hide":true},
 {"headerName":"ИД Пользователя", "width":90, "field":"USERID", "hide":true},
 {"headerName":"ИД Подразделения","width":90, "field":"DEPARTMENT", "hide":true},
 {"headerName":"ИД Работника", "width":90, "field":"EMPLOYEEID", "hide":true},
 {"headerName":"Статус", "field":"STATUS", "width":10, "field":"STATUS", "type":"CALC"},
 {"headerName":"Логин","width":30, "field":"LOGIN"},
 {"headerName":"Имя пользователя", "width":120, "field":"USERNAME"},
 {"headerName":"Адрес электронной почты", "width":90, "field":"EMAIL"},
 {"headerName":"Телефон", "width":30, "field":"PHONE1"},
 {"headerName":"Подразделение", "width":20, "field":"DEPARTMENTSHORTNAME"},
 {"headerName":"Дата истечения действия пароля", "width":90, "field":"PWDEXPDATE"},
 {"headerName":"Дата создания", "width":90, "field":"CREATIONDATE"},
 {"headerName":"Пользователь AD", "width":30, "field":"ADUSERLOGIN"}]', null, null, null, null, 20700);

INSERT INTO B2B_JOURNALPARAM (ID, JOURNALID, NAME, SYSNAME, NOTE, SEQUENCE, ISCOMPLEX, DATATYPEID, HANDBOOKID, URLCOMPONENT, ISREQUIRED, PARENTID, MAINPARAMID, PARAMSHOWEXPR, CREATEDATE, CREATEUSERID, UPDATEDATE, UPDATEUSERID, DATAPROVIDERID, KEYFIELD, NAMEFIELD, NAMESPACE, TABLEALIAS) VALUES (2211, 11, 'Пользователь AD', 'ADUSERLOGIN', 'Пользователь AD', 1, 0, 1, null, null, null, null, null, null, null, null, null, null, null, null, null, 'ADUSERLOGIN', 'T');
INSERT INTO B2B_JOURNALPARAM (ID, JOURNALID, NAME, SYSNAME, NOTE, SEQUENCE, ISCOMPLEX, DATATYPEID, HANDBOOKID, URLCOMPONENT, ISREQUIRED, PARENTID, MAINPARAMID, PARAMSHOWEXPR, CREATEDATE, CREATEUSERID, UPDATEDATE, UPDATEUSERID, DATAPROVIDERID, KEYFIELD, NAMEFIELD, NAMESPACE, TABLEALIAS) VALUES (53, 11, 'Имя пользователя', 'USERNAME', 'Имя пользователя', 1, 0, 1, null, null, null, null, null, null, null, null, null, null, null, null, null, 'LASTNAME', 'T2');
INSERT INTO B2B_JOURNALPARAM (ID, JOURNALID, NAME, SYSNAME, NOTE, SEQUENCE, ISCOMPLEX, DATATYPEID, HANDBOOKID, URLCOMPONENT, ISREQUIRED, PARENTID, MAINPARAMID, PARAMSHOWEXPR, CREATEDATE, CREATEUSERID, UPDATEDATE, UPDATEUSERID, DATAPROVIDERID, KEYFIELD, NAMEFIELD, NAMESPACE, TABLEALIAS) VALUES (54, 11, 'Показать удаленных', 'ISNEEDSHOWDELETEDUSERS', 'Показать удаленных', 1, 0, 6, 6, null, null, null, null, null, null, null, null, null, null, 'sysName', 'name', 'ISNEEDSHOWDELETEDUSERS', 'T');
INSERT INTO B2B_JOURNALPARAM (ID, JOURNALID, NAME, SYSNAME, NOTE, SEQUENCE, ISCOMPLEX, DATATYPEID, HANDBOOKID, URLCOMPONENT, ISREQUIRED, PARENTID, MAINPARAMID, PARAMSHOWEXPR, CREATEDATE, CREATEUSERID, UPDATEDATE, UPDATEUSERID, DATAPROVIDERID, KEYFIELD, NAMEFIELD, NAMESPACE, TABLEALIAS) VALUES (55, 11, 'Логин', 'LOGIN', 'Логин пользователя', 1, 0, 1, null, null, null, null, null, null, null, null, null, null, null, null, null, 'LOGIN', 'T');
INSERT INTO B2B_JOURNALPARAM (ID, JOURNALID, NAME, SYSNAME, NOTE, SEQUENCE, ISCOMPLEX, DATATYPEID, HANDBOOKID, URLCOMPONENT, ISREQUIRED, PARENTID, MAINPARAMID, PARAMSHOWEXPR, CREATEDATE, CREATEUSERID, UPDATEDATE, UPDATEUSERID, DATAPROVIDERID, KEYFIELD, NAMEFIELD, NAMESPACE, TABLEALIAS) VALUES (56, 11, 'Дата создания', 'CREATIONDATE', 'Дата создания', 1, 0, 4, null, null, null, null, null, null, null, null, null, null, null, null, null, 'CREATIONDATE', 'T');

INSERT INTO INS_DATAPROV (DATAPROVID, DISCRIMINATOR, METHODNAME, NAME, SERVICENAME) VALUES (20700, 20, 'dsAdminGetUsersList', 'Получить список пользователей', 'b2bposws');
