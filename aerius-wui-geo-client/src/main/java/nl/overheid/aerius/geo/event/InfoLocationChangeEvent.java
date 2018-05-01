package nl.overheid.aerius.geo.event;

import nl.overheid.aerius.geo.domain.ReceptorPoint;
import nl.yogh.gwt.wui.event.SimpleGenericEvent;

public class InfoLocationChangeEvent extends SimpleGenericEvent<ReceptorPoint> {
  public InfoLocationChangeEvent() {}

  public InfoLocationChangeEvent(final ReceptorPoint value) {
    super(value);
  }
}
