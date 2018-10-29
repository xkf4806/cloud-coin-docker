package com.ourdax.coindocker.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by hongzong.li on 6/28/16.
 */
public class JsonUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final ObjectMapper orderedObjectMapper = new ObjectMapper();

    static {
        SimpleModule module = new SimpleModule("DateTimeModule", Version.unknownVersion());
        module.addSerializer(Date.class, new DateSerializer());
        module.addDeserializer(Date.class, new DateDeserializer());


        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.registerModule(module);


        orderedObjectMapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        orderedObjectMapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
        orderedObjectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        objectMapper.registerModule(module);
        objectMapper.registerModule(module);
    }

    /**
     * Return the underlying {code ObjectMapper} instance.
     */
    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }


    public static ObjectMapper getOrderedObjectMapper() {
        return orderedObjectMapper;
    }

    /**
     * Serialize an object to a json string.
     */
    public static String toString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (IOException e) {
            throw new RuntimeException("Json serialize failed", e);
        }
    }

    public static String toOrderedString(Object obj) {
        try {
            return orderedObjectMapper.writeValueAsString(obj);
        } catch (IOException e) {
            throw new RuntimeException("Json serialize failed", e);
        }
    }

    /**
     * Parse a json string to a map.
     */
    public static Map<String, Object> parse(String json) {
        if (StringUtils.isEmpty(json)) {
            return Collections.emptyMap();
        }
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = getObjectMapper().readValue(json, Map.class);
            return map;
        } catch (IOException e) {
            throw new RuntimeException("Parse to map failed", e);
        }
    }

    /**
     * Parse a json string to a concrete class.
     */
    public static <T> T parse(String json, Class<T> clazz) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        try {
            return getObjectMapper().readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException("Parse to class failed", e);
        }
    }

    /**
     * Parse a json string to a generic class.
     */
    public static <T> T parse(String json, TypeReference<T> typeReference) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        try {
            return getObjectMapper().readValue(json, typeReference);
        } catch (IOException e) {
            throw new RuntimeException("Parse to generic class failed", e);
        }
    }

    static class DateSerializer extends JsonSerializer<Date> {

        @Override
        public void serialize(Date date, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeString(date != null ? DateFormatUtil.format4y2M2d2H2m2s(date) : "null");
        }
    }

    @SuppressWarnings("deprecation")
    static class DateDeserializer extends JsonDeserializer<Date> {
        @Override
        public Date deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            String date = jp.getText();
            if (date != null && !date.isEmpty()) {
                try {
                    return DateFormatUtil.parse4y2M2d2H2m2s(date);
                } catch (Exception e) {

                    throw new JsonParseException("cannot parse date string: " + date, jp.getCurrentLocation(), e);
                }
            }
            return null;
        }
    }

}


