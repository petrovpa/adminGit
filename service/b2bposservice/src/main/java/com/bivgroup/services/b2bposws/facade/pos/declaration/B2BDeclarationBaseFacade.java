package com.bivgroup.services.b2bposws.facade.pos.declaration;

import com.bivgroup.services.b2bposws.facade.B2BDictionaryBaseFacade;
import com.bivgroup.services.b2bposws.system.Constants;

public class B2BDeclarationBaseFacade extends B2BDictionaryBaseFacade {

    protected static final String CLIENT_PROFILE_ID_PARAMNAME = "clientProfileId";
    protected static final String STATEID_PARAMNAME= "stateId";
    protected static final String DECLARATION_ID_PARAMNAME= "id";

    protected static final String DECLARATION_MAP_PARAMNAME = "DECLARATIONMAP";
    protected static final String DECLARATION_CONTRACT_DATE_PARAMNAME = "contractDate";

    /** Заявление. Инициатор (Initiator) - имя поля */
    protected static final String DECLARATION_INITIATOR_TYPE_FIELDNAME = "initiator";
    /** Заявление. Инициатор (Initiator) - значение для вариана 'Страхователь' */
    protected static final Long DECLARATION_INITIATOR_TYPE_INSURER = 0L;
    /** Заявление. Инициатор (Initiator) - значение для вариана 'Страховщик' */
    protected static final Long DECLARATION_INITIATOR_TYPE_ASSURER = 1L;

    /** Заявление. Тип получателя (TypeRecipient) - имя поля */
    protected static final String DECLARATION_RECIPIENT_TYPE_FIELDNAME = "typeRecipient";
    /** Заявление. Тип получателя (TypeRecipient) - значение для вариана 'Иное лицо' */
    protected static final Long DECLARATION_RECIPIENT_TYPE_OTHER = 0L;
    /** Заявление. Тип получателя (TypeRecipient) - значение для вариана 'Заявитель' */
    protected static final Long DECLARATION_RECIPIENT_TYPE_DECLARER = 1L;

    protected static final String B2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS;
    //protected static final String SIGNB2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS; // !только для отладки!
    protected static final String SIGNB2BPOSWS_SERVICE_NAME = Constants.SIGNB2BPOSWS;
    protected static final String THIS_SERVICE_NAME = B2BPOSWS_SERVICE_NAME;
    
 // Date masks
    protected static final String DELIMETR_SPACE = " ";
    protected static final String MASK_START_DAY_TIME24 = DELIMETR_SPACE + "00:00:00";
    protected static final String MASK_END_DAY_TIME24 = DELIMETR_SPACE + "23:59:59";

}
