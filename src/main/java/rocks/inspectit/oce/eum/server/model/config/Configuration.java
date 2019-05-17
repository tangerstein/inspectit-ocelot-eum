package rocks.inspectit.oce.eum.server.model.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import rocks.inspectit.oce.eum.server.model.config.exporters.ExportersSettings;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * The configuration of the inspectit-ocelot-eum-server
 */
@ConfigurationProperties("inspectit-ocelot-eum-server")
@Component
@Data
@Validated
public class Configuration {
    /**
     * List of metric definitions
     */
    @Valid
    private List<MetricDefinition> metrics = new ArrayList<>();

    /**
     * Map of tags
     */
    @Valid
    private Map<String, TagDefinition> tags = new HashMap<>();

    /**
     * The exporters settings
     */
    @Valid
    private ExportersSettings exporters;
}
