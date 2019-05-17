package rocks.inspectit.oce.eum.server.model.config;

import lombok.Data;


/**
 * Defines the tag value and the corresponding configuration flags.
 */
@Data
public class TagDefinition {

    /**
     * If true, the tag is added to each defined view.
     */
    private boolean global;

    /**
     * If true, the tag value is derived from the beacon. The tag value defines the key of the beacon entry.
     */
    private boolean deriveFromBeacon;

    /**
     * The tag value
     */
    private String value;

}
