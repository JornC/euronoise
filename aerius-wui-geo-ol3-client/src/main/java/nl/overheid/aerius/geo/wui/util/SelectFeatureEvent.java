package nl.overheid.aerius.geo.wui.util;

import nl.yogh.gwt.wui.event.SimpleGenericEvent;
import ol.Feature;

public class SelectFeatureEvent extends SimpleGenericEvent<Feature> {
  public SelectFeatureEvent(Feature feature) {
    super(feature);
  }
}
