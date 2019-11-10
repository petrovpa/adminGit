package com.bivgroup.services.b2bposws.facade.pos.underwriting;

import com.bivgroup.services.b2bposws.system.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import org.apache.log4j.Logger;
import ru.diasoft.services.inscore.system.WsConstants;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static ru.diasoft.services.inscore.system.WsConstants.*;

public class Util {

    private static final Logger logger = Logger.getLogger(Util.class);

    public static <T> T deserializeJsonSimple(String json, Class<T> deserializeClass) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, deserializeClass);
        } catch (IOException e) {
            logger.error("can't deserialize json value " + json + " to class " + deserializeClass.getSimpleName(), e);
            return null;
        }
    }

    public static String serializeJsonSimple(Object o) {
        try {
            ObjectWriter mapper = new ObjectMapper()
                    .writerWithDefaultPrettyPrinter()
                    .with(new StdDateFormat());
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            logger.error("can't serialize object of class " + o.getClass(), e);
            return "";
        }
    }

    public static Map toMap(String json) {
        ObjectMapper mapper = new ObjectMapper();
        Map map;
        try {
            map = mapper.readValue(json, new TypeReference<Map>() {
            });
        } catch (IOException e) {
            return new HashMap();
        }
        return map;
    }

    public static void main(String[] args) throws IOException {
        Map map = new HashMap() {
            {
                put("AAA", "BBBB");
            }
        };
        System.out.println(serializeJsonSimple(map));
    }

    public static final Map<String, Object> toMap(Object o) {
        ObjectMapper oMapper = new ObjectMapper();
        return oMapper.convertValue(o, Map.class);
    }

    public static final SimpleDateFormat format() {
        return new SimpleDateFormat("dd.MM.yyyy");
    }

    public static Map wrapListToMap(List<Map> list) {
        Map map = new HashMap();
        map.put(RET_STATUS, RET_STATUS_OK);
        map.put(RESULT, list);
        map.put(TOTALCOUNT, list.size());
        return map;
    }

}
