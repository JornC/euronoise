package nl.yogh.aerius.wui.euronoise.event;

import nl.yogh.gwt.wui.event.SimpleGenericEvent;

public class ResultValueSelectedEvent extends SimpleGenericEvent<String> {
  public ResultValueSelectedEvent(String value) {
    super(value);
  }
}
