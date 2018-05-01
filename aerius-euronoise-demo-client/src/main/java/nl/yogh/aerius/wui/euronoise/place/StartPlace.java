package nl.yogh.aerius.wui.euronoise.place;

import java.util.Map;

import nl.yogh.gwt.wui.place.ApplicationPlace;

public class StartPlace extends ApplicationPlace {
  public static class Tokenizer extends StartPlace.AbstractTokenizer<StartPlace> {
    @Override
    protected StartPlace createPlace() {
      return new StartPlace();
    }
  }

  public abstract static class AbstractTokenizer<T extends StartPlace> extends ApplicationPlace.Tokenizer<T> {
    @Override
    protected void updatePlace(final Map<String, String> tokens, final T place) {}

    @Override
    protected void setTokenMap(final T place, final Map<String, String> tokens) {}
  }

  public StartPlace copy() {
    return copyTo(new StartPlace());
  }

  public <E extends StartPlace> E copyTo(final E copy) {
    return copy;
  }

  @Override
  public String toString() {
    return "StartPlace []";
  }
}
