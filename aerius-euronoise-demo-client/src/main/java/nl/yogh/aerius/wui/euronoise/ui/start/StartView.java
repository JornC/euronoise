package nl.yogh.aerius.wui.euronoise.ui.start;

import com.google.inject.ImplementedBy;

import nl.yogh.aerius.wui.euronoise.ui.start.StartView.Presenter;
import nl.yogh.gwt.wui.widget.EventView;

@ImplementedBy(StartViewImpl.class)
public interface StartView extends EventView<Presenter> {
  public interface Presenter {}
}
