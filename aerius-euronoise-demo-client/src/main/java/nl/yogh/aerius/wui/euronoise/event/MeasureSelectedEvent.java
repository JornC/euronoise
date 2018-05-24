package nl.yogh.aerius.wui.euronoise.event;

import java.util.Set;

import nl.yogh.aerius.wui.domain.RoadMeasure;
import nl.yogh.gwt.wui.event.SimpleGenericEvent;

public class MeasureSelectedEvent extends SimpleGenericEvent<RoadMeasure> {
  public MeasureSelectedEvent(final RoadMeasure value) {
    super(value);
  }
}
