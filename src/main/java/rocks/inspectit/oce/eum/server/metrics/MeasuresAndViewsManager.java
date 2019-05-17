package rocks.inspectit.oce.eum.server.metrics;

import io.opencensus.common.Scope;
import io.opencensus.stats.*;
import io.opencensus.tags.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rocks.inspectit.oce.eum.server.model.config.AggregationType;
import rocks.inspectit.oce.eum.server.model.config.Configuration;
import rocks.inspectit.oce.eum.server.model.config.TagDefinition;
import rocks.inspectit.oce.eum.server.model.config.MetricDefinition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Central component, which is responsible for writing beacon entries as OpenCensus views.
 */
@Component
public class MeasuresAndViewsManager {
    /**
     * Measures, which are created.
     */
    HashMap<String, Measure> measures = new HashMap<>();

    @Autowired
    private Configuration configuration;

    /**
     * Processes boomerang beacon
     *
     * @param beacon The beacon containing arbitrary key-value pairs.
     */
    public void processBeacon(Map<String, String> beacon) {
        for (MetricDefinition metricDefinition : configuration.getMetrics()) {
            if (beacon.containsKey(metricDefinition.getBeaconField())) {
                updateMeasures(metricDefinition);
                try (Scope scope = getTagContext(beacon).buildScoped()) {
                    recordMeasure(metricDefinition, beacon.get(metricDefinition.getBeaconField()));
                }
            }
        }
    }

    /**
     * Records the measure,
     *
     * @param metricDefinition The configuration of the metric, which is activated
     * @param value            The value, which is going to be written.
     */
    private void recordMeasure(MetricDefinition metricDefinition, String value) {
        StatsRecorder recorder = Stats.getStatsRecorder();
        if (metricDefinition.getMeasureType().isParsable(value)) {
            switch (metricDefinition.getMeasureType()) {
                case LONG:
                    recorder.newMeasureMap().put((Measure.MeasureLong) measures.get(metricDefinition.getName()), Long.parseLong(value)).record();
                    break;
                case Double:
                    recorder.newMeasureMap().put((Measure.MeasureDouble) measures.get(metricDefinition.getName()), Double.parseDouble(value)).record();
                    break;
            }

        }
    }

    /**
     * Updates the measures
     *
     * @param metricDefinition
     */
    private void updateMeasures(MetricDefinition metricDefinition) {
        if (!measures.containsKey(metricDefinition.getName())) {
            Measure measure = metricDefinition.getMeasureType().createMeasure(metricDefinition);
            measures.put(metricDefinition.getName(), measure);
            updateViews(metricDefinition);
        }
    }

    /**
     * Creates a new {@link View}, if a view for the given metricDefinition was not created, yet.
     *
     * @param metricDefinition
     */
    private void updateViews(MetricDefinition metricDefinition) {
        ViewManager viewManager = Stats.getViewManager();

        for (AggregationType aggregationType : metricDefinition.getAggregations()) {
            String viewName = metricDefinition.getViewName(aggregationType);
            if (viewManager.getAllExportedViews().stream().noneMatch(view -> view.getName().asString().equals(viewName))) {
                Aggregation aggregation = createAggregation(aggregationType, metricDefinition);
                List<TagKey> tagKeys = getTagsForView(metricDefinition).entrySet().stream().map(tag -> TagKey.create(tag.getKey())).collect(Collectors.toList());
                View view = View.create(View.Name.create(viewName), metricDefinition.getDescription().orElse(""), measures.get(metricDefinition.getName()), aggregation, tagKeys);
                viewManager.registerView(view);
            }
        }
    }

    /**
     * Returns all tags, which are exposed for the given metricDefinition
     *
     * @param metricDefinition
     * @return Map of tags
     */
    private Map<String, TagDefinition> getTagsForView(MetricDefinition metricDefinition) {
        Map<String, TagDefinition> tags = configuration.getTags().entrySet()
                .stream()
                .filter(tag -> tag.getValue().isGlobal()
                        || metricDefinition.getTagKeys().contains(tag.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return tags;
    }

    /**
     * Builds TagContext
     *
     * @param beacon Used to resolve tag values, which refer to a beacon entry
     */
    private TagContextBuilder getTagContext(Map<String, String> beacon) {
        TagContextBuilder tagContextBuilder = Tags.getTagger().currentBuilder();

        for (Map.Entry<String, TagDefinition> tag : configuration.getTags().entrySet()) {
            TagDefinition tagDefinition = tag.getValue();

            if (tagDefinition.isDeriveFromBeacon()) {
                if (beacon.containsKey(tagDefinition.getValue())) {
                    tagContextBuilder.put(TagKey.create(tag.getKey()), TagValue.create(beacon.get(tagDefinition.getValue())), TagMetadata.create(TagMetadata.TagTtl.NO_PROPAGATION));
                }
            } else {
                tagContextBuilder.put(TagKey.create(tag.getKey()), TagValue.create(tagDefinition.getValue()), TagMetadata.create(TagMetadata.TagTtl.NO_PROPAGATION));
            }
        }
        return tagContextBuilder;
    }

    /**
     * Creates an aggregation depending on the given {@link AggregationType}
     *
     * @param type
     * @param metricDefinition
     * @return
     */
    private Aggregation createAggregation(AggregationType type, MetricDefinition metricDefinition) {
        switch (type) {
            case COUNT:
                return Aggregation.Count.create();
            case SUM:
                return Aggregation.Sum.create();
            case HISTOGRAM:
                return Aggregation.Distribution.create(BucketBoundaries.create(metricDefinition.getBucketBoundaries()));
            case LAST_VALUE:
                return Aggregation.LastValue.create();
            default:
                throw new RuntimeException("Unhandled aggregation type: " + type);
        }
    }

}