package nl.yogh.aerius.wui.euronoise;

import nl.yogh.aerius.wui.euronoise.place.StartPlace;
import nl.yogh.aerius.wui.euronoise.ui.start.StartActivity;

public interface EuroNoiseActivityFactory {
  StartActivity createStartPresenter(StartPlace place);
}
