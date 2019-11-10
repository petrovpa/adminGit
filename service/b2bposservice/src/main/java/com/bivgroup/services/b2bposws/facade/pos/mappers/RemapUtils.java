package com.bivgroup.services.b2bposws.facade.pos.mappers;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class RemapUtils {

    @SuppressWarnings("unchecked")
    public static String findProdProgSysNameInProdConfByProgramCode(Map<String, Object> prodConf, String programCode) {
        Map<String, Object> prodver = (Map<String, Object>) prodConf.get("PRODVER");
        List<Map<String, Object>> prodprogs = (List<Map<String, Object>>) prodver.get("PRODPROGS");
        Optional<String> prodProgSysname = prodprogs.stream()
                // найдем programCode
                .filter(program -> Objects.equals(programCode, program.get("PROGCODE")))
                // для найденной программы вернем соостветвующий SYSNAME (PRODPROGSYSNAME)
                .map(program -> (String) program.get("SYSNAME"))
                // возьмем первый подходящий
                .findFirst();
        return prodProgSysname.orElse(null);
    }

}
