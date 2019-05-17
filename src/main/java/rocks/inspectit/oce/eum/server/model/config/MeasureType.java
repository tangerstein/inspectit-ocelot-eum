package rocks.inspectit.oce.eum.server.model.config;

import io.opencensus.stats.Measure;

/**
 * Defines type of measure and creates measure of the declared type.
 */
public enum MeasureType {
    LONG{
        @Override
        public Measure createMeasure(MetricDefinition metricDefinition) {
            return Measure.MeasureLong.create(metricDefinition.getName(), metricDefinition.getDescription().orElse(""), metricDefinition.getUnit());
        }

        @Override
        public boolean isParsable(String measurement) {
            try{
                java.lang.Long.parseLong(measurement);
                return true;
            } catch (NumberFormatException e){
                return false;
            }
        }
    },
    Double{
        @Override
        public Measure createMeasure(MetricDefinition metricDefinition) {
            return Measure.MeasureDouble.create(metricDefinition.getName(), metricDefinition.getDescription().orElse(""), metricDefinition.getUnit());
        }
        @Override
        public boolean isParsable(String measurement) {
            try{
                java.lang.Double.parseDouble(measurement);
                return true;
            } catch (NumberFormatException e){
                return false;
            }
        }
    };

    /**
     * Returns the measure of the specific type
     * @param metricDefinition
     * @return Measure
     */
    public abstract Measure createMeasure(MetricDefinition metricDefinition);

    /**
     * Checks, whether the given measurement is parsable to the specific type.
     * @param measurement
     * @return true, if parsable
     */
    public abstract boolean isParsable(String measurement);

}

