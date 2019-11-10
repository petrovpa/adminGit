package com.bivgroup.services.b2bposws.facade.pos.mappers.keyrelations;
// todo: сменить имя пакета на более лаконичное, например *.mappers.rules или т.п. (после сведения веток)

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bivgroup.services.b2bposws.facade.pos.mappers.UniversalJXPathMapper.CONSTANT_BOOLEAN_FALSE;
import static com.bivgroup.services.b2bposws.facade.pos.mappers.UniversalJXPathMapper.CONSTANT_BOOLEAN_TRUE;

public interface OpenAPIRequestToUnionContractKeyRelations {

    public static final String TRUE = CONSTANT_BOOLEAN_TRUE;
    public static final String FALSE = CONSTANT_BOOLEAN_FALSE;

    List<String[]> UNION_CONTRACT_COMMON_KEY_RELATIONS_LIST = BaseKeyRelations.createKeyRelationList(new String[][]{
            //
            {"data/insAmount", "insAmValue"},
            {"data/insAmount", "creditDebt"},
            {"data/startDate", "startDate"},
            {"data/finishDate", "finishDate"},
            //
            {"data/attributes/insObject", "objTypeSysName", "0 > house; 1 > flat", ""},
            
            // наличие дерева
            {"data/isWood", "isWooden", "true > 1; false > 0", ""},
            //год постройки
            {"data/yearOfConstruction", "buildYear"},
            
            //
            {"data/attributes/adultsAndChildren", "adultCount"},
            // аналогично Rest2ToApiRestRemap.territoryCodeToTerritoryIndex
            // {"data/attributes/insuranceTerritory", "territorySysName", "00001 > NoUSARF; 00002 > USA; 00003 > RF", "NoUSARF"},
            {"data/attributes/insuranceTerritory", "territorySysName", "0 > NoUSARF; 1 > USA; 2 > RF", "NoUSARF"},
            {"data/attributes/babes", "babyCount"},
            {"data/attributes/dayCount", "duration"},
            {"data/attributes/old", "oldCount"},
            // {"data/attributes/optionSport", "isPacketSport"},
            // аналогично Rest2ToApiRestRemap.insProgramsMapping
            // {"data/attributes/insuranceProgram", "prodProgSysName", "00001 > VZR_BASIC; 00002 > VZR_CLASSIC; 00003 > VZR_PREMIUM; 00004 > VZR_RFCLASSIC", "VZR_BASIC"},
            {"data/attributes/insuranceProgram", "prodProgSysName", "0 > VZR_BASIC; 1 > VZR_CLASSIC; 2 > VZR_PREMIUM; 3 > VZR_RFCLASSIC", "VZR_BASIC"},
            {"data/attributes/annualPolicy", "policyTypeSysName", "0 > oneTrip; 1 > manyTrips", ""},
            {"data/attributes/annualPolicyType", "policyTypeSysName", "0 > manyTrips; 1 > manyTrips", "oneTrip"},
            //
            {"data/startDate", "travelStartDate"},
            {"data/finishDate", "travelFinishDate"},
            // Спортивный
            {"data/risks[@systemName = 'VZRsporttools']/systemName", "isPacketSport", "VZRsporttools > " + TRUE, FALSE},
            {"data/risks[@systemName = 'VZRskypass']/systemName", "isPacketSport", "VZRskypass > " + TRUE, FALSE},
            // Защита багажа
            {"data/risks[@systemName = 'VZRlootlost']/systemName", "isPacketLoot", "VZRlootlost > " + TRUE, FALSE},
            {"data/risks[@systemName = 'VZRlootdelay']/systemName", "isPacketLoot", "VZRlootdelay > " + TRUE, FALSE},
            {"data/risks[@systemName = 'VZRflightdelay']/systemName", "isPacketLoot", "VZRflightdelay > " + TRUE, FALSE},
            // Особый случай
            {"data/risks[@systemName = 'VZRtripstop']/systemName", "isPacketAccident", "VZRtripstop > " + TRUE, FALSE},
            {"data/risks[@systemName = 'VZRns']/systemName", "isPacketAccident", "VZRns > " + TRUE, FALSE},
            // Личный адвокат
            {"data/risks[@systemName = 'VZRjuridical']/systemName", "isPacketJur", "VZRjuridical > " + TRUE, FALSE},
            {"data/risks[@systemName = 'VZRgo']/systemName", "isPacketJur", "VZRgo > " + TRUE, FALSE},
            // Предусмотрительный
            {"data/risks[@systemName = 'VZRtripcancel']/systemName", "isPacketTripCancel", "VZRtripcancel > " + TRUE, FALSE},
            //
            {"data/program", "prodProgSysName"}
            //{"data/attributes/", ""},
            // todo: добавить остальные правила и соотношения (при необходимости)
    });

    Map<String, Class> UNION_CONTRACT_KEY_CLASSES = BaseKeyRelations.createClassMap(
            new Object[]{"MAP", HashMap.class},
            new Object[]{"LIST", ArrayList.class}
    );

}
