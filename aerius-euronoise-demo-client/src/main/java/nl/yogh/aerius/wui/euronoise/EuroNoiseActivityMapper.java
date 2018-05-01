package nl.yogh.aerius.wui.euronoise;

import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;

import nl.yogh.aerius.wui.euronoise.place.StartPlace;
import nl.yogh.gwt.wui.activity.Activity;
import nl.yogh.gwt.wui.activity.ActivityMapper;
import nl.yogh.gwt.wui.place.ApplicationPlace;
import nl.yogh.gwt.wui.place.DefaultPlace;
import nl.yogh.gwt.wui.place.Place;

public class EuroNoiseActivityMapper implements ActivityMapper {
  private final EuroNoiseActivityFactory factory;

  @Inject
  public EuroNoiseActivityMapper(@DefaultPlace final ApplicationPlace place, final EuroNoiseActivityFactory factory) {
    this.factory = factory;
  }

  @Override
  public Activity<?> getActivity(final Place place) {
    Activity<?> presenter = null;

    presenter = tryGetActivity(place);

    if (presenter == null) {
      GWT.log("Presenter is null: Place ends up nowhere. " + place);
      throw new RuntimeException("No Presenter found for place " + place);
    }

    return presenter;
  }

  private Activity<?> tryGetActivity(final Place place) {
    Activity<?> presenter = null;

    if (place instanceof StartPlace) {
      presenter = factory.createStartPresenter((StartPlace) place);
    }

    return presenter;
  }
}
