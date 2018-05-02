package nl.yogh.aerius.wui.euronoise.ui.start;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;

import nl.overheid.aerius.geo.wui.util.SelectFeatureEvent;
import nl.yogh.gwt.wui.widget.EventComposite;
import nl.yogh.gwt.wui.widget.SwitchPanel;

public class StartViewImpl extends EventComposite implements StartView {
  private static final StartViewImplUiBinder UI_BINDER = GWT.create(StartViewImplUiBinder.class);

  interface StartViewImplUiBinder extends UiBinder<Widget, StartViewImpl> {}

  interface StartViewImplEventBinder extends EventBinder<StartViewImpl> {}

  private final StartViewImplEventBinder EVENT_BINDER = GWT.create(StartViewImplEventBinder.class);

  @UiField SwitchPanel switchPanel;

  public StartViewImpl() {
    initWidget(UI_BINDER.createAndBindUi(this));
  }

  @Override
  public void setPresenter(final Presenter presenter) {}

  @EventHandler
  public void onFeatureSelectionEvent(SelectFeatureEvent e) {
    switchPanel.showWidget(1);
  }

  @Override
  public void setEventBus(EventBus eventBus) {
    super.setEventBus(eventBus);

    EVENT_BINDER.bindEventHandlers(this, eventBus);
  }
}
