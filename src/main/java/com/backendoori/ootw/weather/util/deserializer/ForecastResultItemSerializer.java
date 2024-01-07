package com.backendoori.ootw.weather.util.deserializer;

import java.io.IOException;
import com.backendoori.ootw.weather.dto.forecast.BaseDateTime;
import com.backendoori.ootw.weather.dto.forecast.ForecastResultItem;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class ForecastResultItemSerializer extends JsonDeserializer<ForecastResultItem> {

    @Override
    public ForecastResultItem deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec()
            .readTree(p);

        String baseDate = node.findValue("baseDate").asText();
        String baseTime = node.findValue("baseTime").asText();
        BaseDateTime baseDateTime = new BaseDateTime(baseDate, baseTime);

        String fcstDate = node.findValue("fcstDate").asText();
        String fcstTime = node.findValue("fcstTime").asText();
        BaseDateTime fcstDateTime = new BaseDateTime(fcstDate, fcstTime);

        String category = node.findValue("category").asText();
        String fcstValue = node.findValue("fcstValue").asText();

        int nx = node.findValue("nx").asInt();
        int ny = node.findValue("ny").asInt();

        return new ForecastResultItem(baseDateTime, fcstDateTime, category, fcstValue, nx, ny);
    }

}
