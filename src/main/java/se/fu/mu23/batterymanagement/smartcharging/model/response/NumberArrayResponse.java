package se.fu.mu23.batterymanagement.smartcharging.model.response;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;
import java.util.List;

@JsonDeserialize(using = NumberArrayDeserializer.class)
public record NumberArrayResponse(List<Double> values) {
}


class NumberArrayDeserializer extends JsonDeserializer<NumberArrayResponse> {
    @Override
    public NumberArrayResponse deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        List<Double> values = ctxt.readValue(p, ctxt.getTypeFactory().constructCollectionType(List.class, Double.class));
        return new NumberArrayResponse(values);
    }
}