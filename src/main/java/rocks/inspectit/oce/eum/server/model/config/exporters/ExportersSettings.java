package rocks.inspectit.oce.eum.server.model.config.exporters;

import lombok.Data;
import lombok.NoArgsConstructor;
import rocks.inspectit.oce.eum.server.model.config.exporters.metrics.MetricsExportersSettings;

import javax.validation.Valid;

/**
 * Settings for metrics and trace exporters of OpenCensus.
 */
@Data
@NoArgsConstructor
public class ExportersSettings {

    @Valid
    private MetricsExportersSettings metrics;
}
