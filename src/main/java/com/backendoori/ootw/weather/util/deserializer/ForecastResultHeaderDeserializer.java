package com.backendoori.ootw.weather.util.deserializer;

import java.io.IOException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ForecastResultHeaderDeserializer extends JsonDeserializer<ForecastResultHeader> {

    @Override
    public ForecastResultHeader deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        JsonNode headerNode = node.findValue("response")
            .get("header");

        return new ForecastResultHeader(headerNode.get("resultCode").asText(), headerNode.get("resultMsg").asText());
    }

}
