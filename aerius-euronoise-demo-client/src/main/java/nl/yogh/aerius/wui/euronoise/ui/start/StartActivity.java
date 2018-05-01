package nl.yogh.aerius.wui.euronoise.ui.start;

import com.google.inject.Inject;

import nl.yogh.aerius.wui.euronoise.ui.start.StartView.Presenter;
import nl.yogh.gwt.wui.activity.EventActivity;

public class StartActivity extends EventActivity<Presenter, StartView> implements Presenter {
  @Inject
  public StartActivity(final StartView view) {
    super(view);
  }

  @Override
  public Presenter getPresenter() {
    return this;
  }
}
