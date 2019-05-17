package rocks.inspectit.oce.eum.server.model.config.exporters.metrics;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * Settings for the OpenCensus Prometheus metrics exporters.
 */
@Data
@NoArgsConstructor
public class PrometheusExporterSettings {

    /**
     * If true, the inspectIT Agent will try to start a Prometheus metrics exporters.
     */
    private boolean enabled;

    /**
     * The hostname on which the /metrics endpoint of prometheus will be started.
     */
    @NotBlank
    private String host;

    /**
     * The port on which the /metrics endpoint of prometheus will be started.
     */
    @Min(0)
    @Max(65535)
    private int port;
}
