package de.sotterbeck.iumetro.infra.postgres.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jooq.Converter;
import org.jooq.JSONB;

public class JsonbConverter<T> implements Converter<JSONB, T> {

    private final ObjectMapper objectMapper;
    private final JavaType targetType;

    public JsonbConverter(ObjectMapper objectMapper, JavaType targetType) {
        this.objectMapper = objectMapper;
        this.targetType = targetType;
    }

    @Override
    public T from(JSONB jsonb) {
        if (jsonb == null || jsonb.data().isBlank()) return null;
        try {
            return objectMapper.readValue(jsonb.data(), targetType);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to deserialize JSONB value.", e);
        }
    }

    @Override
    public JSONB to(T value) {
        try {
            return JSONB.valueOf(objectMapper.writeValueAsString(value));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to serialize JSONB value.", e);
        }
    }

    @Override
    public Class<JSONB> fromType() {
        return JSONB.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<T> toType() {
        return (Class<T>) Object.class;
    }

}
