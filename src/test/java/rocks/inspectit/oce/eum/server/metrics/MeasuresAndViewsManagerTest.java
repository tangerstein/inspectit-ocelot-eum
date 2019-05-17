package rocks.inspectit.oce.eum.server.metrics;

import io.opencensus.stats.Stats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.inspectit.oce.eum.server.model.config.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Tests {@link MeasuresAndViewsManager}
 */
@ExtendWith(MockitoExtension.class)
public class MeasuresAndViewsManagerTest {


    @Mock
    Configuration configuration = new Configuration();

    @InjectMocks
    MeasuresAndViewsManager measuresAndViewsManager = new MeasuresAndViewsManager();

    MetricDefinition dummyMetricDefinition;

    HashMap<String, TagDefinition> tags;

    @Nested
    class ProcessBeacon{
        @BeforeEach
        void setupConfiguration(){
            dummyMetricDefinition = new MetricDefinition();
            dummyMetricDefinition.setAggregations(Arrays.asList(AggregationType.COUNT, AggregationType.SUM, AggregationType.HISTOGRAM));
            dummyMetricDefinition.setName("Dummy metric name");
            dummyMetricDefinition.setBeaconField("dummy_beacon_field");
            dummyMetricDefinition.setBucketBoundaries(Arrays.asList(0d,100d));
            dummyMetricDefinition.setDescription(Optional.of("Dummy description"));
            dummyMetricDefinition.setMeasureType(MeasureType.Double);
            dummyMetricDefinition.setUnit("ms");
            dummyMetricDefinition.setTagKeys(Arrays.asList("TAG_1", "TAG_2"));

            TagDefinition tagDefinition1 = new TagDefinition();
            tagDefinition1.setValue("tag_value_1");

            TagDefinition tagDefinition2 = new TagDefinition();
            tagDefinition2.setValue("tag_value_2");

            TagDefinition globalTagDefinition = new TagDefinition();
            globalTagDefinition.setDeriveFromBeacon(true);
            globalTagDefinition.setGlobal(true);
            globalTagDefinition.setValue("u");

            tags = new HashMap<>();
            tags.put("TAG_1", tagDefinition1);
            tags.put("TAG_2", tagDefinition1);
            tags.put("URL", globalTagDefinition);
        }

        @Test
        void verifyNoViewIsGeneratedWithEmptyBeacon(){
            when(configuration.getMetrics()).thenReturn(Arrays.asList(dummyMetricDefinition));
            HashMap<String, String> emptyBeacon =  new HashMap<>();
            measuresAndViewsManager.processBeacon(emptyBeacon);

            assertThat(Stats.getViewManager().getAllExportedViews()).isEmpty();
        }

        @Test
        void verifyNoViewIsGeneratedWithFullBeacon(){
            when(configuration.getMetrics()).thenReturn(Arrays.asList(dummyMetricDefinition));
            HashMap<String, String> beacon =  new HashMap<String, String>();
            beacon.put("fake_ beacon_field", "12d");
            measuresAndViewsManager.processBeacon(beacon);

            assertThat(Stats.getViewManager().getAllExportedViews()).isEmpty();
        }

        @Test
        void verifyViewsAreGeneratedNoGlobalTagIsSet(){
            when(configuration.getMetrics()).thenReturn(Arrays.asList(dummyMetricDefinition));
            when(configuration.getTags()).thenReturn(tags);
            HashMap<String, String> beacon =  new HashMap<String, String>();
            beacon.put("dummy_beacon_field", "12d");
            measuresAndViewsManager.processBeacon(beacon);

            assertThat(Stats.getViewManager().getAllExportedViews()).hasSize(3);
            assertThat(Stats.getViewManager().getAllExportedViews()).allMatch(view -> view.getMeasure().getName().equals("Dummy metric name"));
            assertThat(Stats.getViewManager().getAllExportedViews()).anyMatch(view -> view.getColumns().size() == 3);
        }
    }
}
