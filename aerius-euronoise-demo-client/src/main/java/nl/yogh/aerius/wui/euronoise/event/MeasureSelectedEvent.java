package nl.yogh.aerius.wui.euronoise.event;

import java.util.Set;

import nl.yogh.aerius.wui.domain.RoadMeasure;
import nl.yogh.gwt.wui.event.SimpleGenericEvent;

public class MeasureSelectedEvent extends SimpleGenericEvent<Set<RoadMeasure>> {
  public MeasureSelectedEvent(final Set<RoadMeasure> value) {
    super(value);
  }
}
