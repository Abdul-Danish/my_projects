package com.rules.ser;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

public class CustomDeserializer extends JsonDeserializer {

    @Override
    public JsonNode deserialize(JsonParser jsonParsor, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectCodec oc = jsonParsor.getCodec();
        JsonNode jsonNode = oc.readTree(jsonParsor);
        return jsonNode;
    }

}
