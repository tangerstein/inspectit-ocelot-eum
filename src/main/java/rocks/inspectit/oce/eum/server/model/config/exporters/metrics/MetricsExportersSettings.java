package rocks.inspectit.oce.eum.server.model.config.exporters.metrics;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;

/**
 * Settings for metrics exporters.
 */
@Data
@NoArgsConstructor
public class MetricsExportersSettings {

    @Valid
    private PrometheusExporterSettings prometheus;
}
