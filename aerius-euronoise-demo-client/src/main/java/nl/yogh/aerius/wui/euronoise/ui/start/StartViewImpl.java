package nl.yogh.aerius.wui.euronoise.ui.start;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import nl.yogh.gwt.wui.widget.EventComposite;

public class StartViewImpl extends EventComposite implements StartView {
  private static final StartViewImplUiBinder UI_BINDER = GWT.create(StartViewImplUiBinder.class);

  interface StartViewImplUiBinder extends UiBinder<Widget, StartViewImpl> {}

  public StartViewImpl() {
    initWidget(UI_BINDER.createAndBindUi(this));
  }

  @Override
  public void setPresenter(final Presenter presenter) {

  }

  @Override
  public void setEventBus(final EventBus eventBus) {
    GWT.log("setting event");
    super.setEventBus(eventBus);
  }
}
