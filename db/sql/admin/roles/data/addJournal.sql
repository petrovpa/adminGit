-- Журнал ролей
INSERT INTO B2B_JOURNAL (ID, NAME, SYSNAME, SQLDATA, CREATEDATE, CREATEUSERID, UPDATEDATE, UPDATEUSERID, DATAPROVIDERID)
VALUES (1100, 'Журнал ролей', 'ADMINROLESMANAGE', '[
  {"headerName":"№","width":50, "type":"COUNTER"},
  {"headerName":"ИД","field":"ROLEID","hide":true},
  {"headerName":"Системное наименование","field":"ROLESYSNAME"},
  {"headerName":"Наименование роли","field":"ROLENAME"},
  {"headerName":"Описание","field":"DESCRIPTION"},
  {"headerName":"Период действия с","field":"FROMDATE"},
  {"headerName":"Период действия по","field":"TODATE"}
]', null, null, null, null, 20900);

INSERT INTO B2B_JOURNALPARAM (ID, JOURNALID, NAME, SYSNAME, NOTE, SEQUENCE, ISCOMPLEX, DATATYPEID, HANDBOOKID, URLCOMPONENT, ISREQUIRED, PARENTID, MAINPARAMID, PARAMSHOWEXPR, CREATEDATE, CREATEUSERID, UPDATEDATE, UPDATEUSERID, DATAPROVIDERID, KEYFIELD, NAMEFIELD, NAMESPACE, TABLEALIAS)
VALUES (1100, 1100, 'Наименование роли', 'ROLENAME', 'Наименование роли', 1, 0, 1, null, null, null, null, null, null, null, null, null, null, null, null, null, 'ROLENAME', 'T');

-- Журнал прав для роли
INSERT INTO B2B_JOURNAL (ID, NAME, SYSNAME, SQLDATA, CREATEDATE, CREATEUSERID, UPDATEDATE, UPDATEUSERID, DATAPROVIDERID) VALUES (1300, 'Журнал прав для роли', 'ADMINRIGHTSMANAGE', '[
  {"headerName":"№","width":50, "type":"COUNTER"},
  {"headerName":"ИД","field":"RIGHTID","hide":true},
  {"headerName":"Наименование роли","field":"RIGHTNAME"},
  {"headerName":"Тип права","field":"RIGHTTYPE"}
]', null, null, null, null, 1300);

INSERT INTO INS_DATAPROV (DATAPROVID, DISCRIMINATOR, METHODNAME, NAME, SERVICENAME)
VALUES (1300, 20, 'dsUserRightBrowseListByParamEx', 'Список прав', 'b2bposws');

INSERT INTO B2B_JOURNALPARAM (ID, JOURNALID, NAME, SYSNAME, NOTE, SEQUENCE, ISCOMPLEX, DATATYPEID, HANDBOOKID, URLCOMPONENT, ISREQUIRED, PARENTID, MAINPARAMID, PARAMSHOWEXPR, CREATEDATE, CREATEUSERID, UPDATEDATE, UPDATEUSERID, DATAPROVIDERID, KEYFIELD, NAMEFIELD, NAMESPACE, TABLEALIAS)
VALUES (1300, 1300, 'Наименование права', 'RIGHTNAME', 'Наименование права', 1, 0, 1, null, null, null, null, null, null, null, null, null, null, null, null, null, 'RIGHTNAME', 'T');
INSERT INTO B2B_JOURNALPARAM (ID, JOURNALID, NAME, SYSNAME, NOTE, SEQUENCE, ISCOMPLEX, DATATYPEID, HANDBOOKID, URLCOMPONENT, ISREQUIRED, PARENTID, MAINPARAMID, PARAMSHOWEXPR, CREATEDATE, CREATEUSERID, UPDATEDATE, UPDATEUSERID, DATAPROVIDERID, KEYFIELD, NAMEFIELD, NAMESPACE, TABLEALIAS)
VALUES (1301, 1300, 'Тип права', 'RIGHTTYPE', 'Тип права', 2, 0, 6, null, null, null, null, null, null, null, null, null, null, 1301, 'SYSNAME', 'NAME', 'RIGHTTYPE', 'T');

INSERT INTO INS_DATAPROV (DATAPROVID, DISCRIMINATOR, METHODNAME, NAME, SERVICENAME)
VALUES (1301, 20, 'dsRightTypeList', 'Типы права', 'b2bposws');