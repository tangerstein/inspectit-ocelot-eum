package rocks.inspectit.oce.eum.server.model.config;

import lombok.Data;
import lombok.Singular;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
/**
 * Defines the mapping of a beacon value to a OpenCensus Measure and the corresponding views.
 */
public class MetricDefinition {

    /**
     * The type of the measure; can be either long or double
     */
    private MeasureType measureType;

    /**
     * List of tags, which will be published within the exposed view of OpenCensus.
     */
    private List<String> tagKeys = new ArrayList<>();

    /**
     * Name of measure
     */
    @NotBlank
    private String name;

    /**
     * Name of the boomerang beacon field, which is mapped to a view.
     */
    @NotBlank
    private String beaconField;

    /**
     * Description
     */
    private Optional<String> description = Optional.empty();

    /**
     * Unit
     */
    @NotBlank
    private String unit;

    /**
     * Aggregation types
     */
    private List<AggregationType> aggregations;

    /**
     * Only relevant if aggregation is "HISTOGRAM".
     * In this case this list defines the boundaries of the buckets in the histogram
     */
    @Singular
    private List<@NotNull Double> bucketBoundaries;

    /**
     * Returns name of view
     * @param type
     * @return
     */
    public String getViewName(AggregationType type){
        return getName() +"/" + type.getReadableName();
    }

    @AssertFalse(message = "When using HISTOGRAM aggregation you must specify the bucket-boundaries!")
    boolean isBucketBoundariesNotSpecifiedForHistogram() {
        return aggregations.stream().anyMatch(a -> (a == AggregationType.HISTOGRAM)) && CollectionUtils.isEmpty(bucketBoundaries);
    }

    @AssertTrue(message = "When using HISTOGRAM the specified bucket-boundaries must be sorted in ascending order and must contain each value at most once!")
    boolean isBucketBoundariesSorted() {
        if (aggregations.stream().anyMatch(a -> (a == AggregationType.HISTOGRAM)) && !CollectionUtils.isEmpty(bucketBoundaries)) {
            Double previous = null;
            for (double boundary : bucketBoundaries) {
                if (previous != null && previous >= boundary) {
                    return false;
                }
                previous = boundary;
            }
        }
        return true;
    }
}

