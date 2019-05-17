package rocks.inspectit.oce.eum.server.model.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum AggregationType {
    LAST_VALUE("last value"),

    SUM("sum"),

    COUNT("count"),

    HISTOGRAM("histogram");
    
    @Getter
    private String readableName;
}

