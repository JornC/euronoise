package nl.overheid.aerius.geo.command;

import nl.overheid.aerius.geo.domain.ReceptorPoint;
import nl.overheid.aerius.geo.event.InfoLocationChangeEvent;
import nl.yogh.gwt.wui.command.SimpleGenericCommand;

public class InfoLocationChangeCommand extends SimpleGenericCommand<ReceptorPoint, InfoLocationChangeEvent> {
  public InfoLocationChangeCommand(final ReceptorPoint obj) {
    super(obj);
  }

  @Override
  protected InfoLocationChangeEvent createEvent(final ReceptorPoint value) {
    return new InfoLocationChangeEvent(value);
  }
}
