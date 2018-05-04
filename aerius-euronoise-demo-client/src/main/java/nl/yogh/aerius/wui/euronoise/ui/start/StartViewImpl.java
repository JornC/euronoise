package nl.yogh.aerius.wui.euronoise.ui.start;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;

import nl.overheid.aerius.geo.wui.util.SelectFeatureEvent;
import nl.yogh.aerius.wui.resources.R;
import nl.yogh.gwt.wui.widget.EventComposite;
import nl.yogh.gwt.wui.widget.SwitchPanel;

public class StartViewImpl extends EventComposite implements StartView {
  private static final StartViewImplUiBinder UI_BINDER = GWT.create(StartViewImplUiBinder.class);

  interface StartViewImplUiBinder extends UiBinder<Widget, StartViewImpl> {}

  interface StartViewImplEventBinder extends EventBinder<StartViewImpl> {}

  private final StartViewImplEventBinder EVENT_BINDER = GWT.create(StartViewImplEventBinder.class);

  public interface CustomStyle extends CssResource {
    String selected();
  }

  @UiField SwitchPanel switchPanel;

  @UiField FocusPanel roads;
  @UiField FocusPanel rails;

  @UiField CustomStyle style;

  private Widget selected;

  @UiField SimplePanel railsButton;
  @UiField SimplePanel roadsButton;
  @UiField SimplePanel industryButton;
  @UiField SimplePanel airTrafficButton;

  @UiField SwitchPanel contentSwitchPanel;

  public StartViewImpl() {
    initWidget(UI_BINDER.createAndBindUi(this));

    railsButton.getElement()
        .getStyle()
        .setProperty("WebkitMaskImage", "url(" + R.images().sourceTrain().getSafeUri()
            .asString() + ")");
    roadsButton.getElement()
        .getStyle()
        .setProperty("WebkitMaskImage", "url(" + R.images().sourceRoad().getSafeUri()
            .asString() + ")");
    industryButton.getElement()
        .getStyle()
        .setProperty("WebkitMaskImage", "url(" + R.images().sourceIndustry().getSafeUri()
            .asString() + ")");
    airTrafficButton.getElement()
        .getStyle()
        .setProperty("WebkitMaskImage", "url(" + R.images().sourceAirTraffic().getSafeUri()
            .asString() + ")");
  }

  @UiHandler("rails")
  public void onRailsClick(ClickEvent e) {
    contentSwitchPanel.showWidget(0);
    select(rails);
  }

  private void select(Panel selection) {
    if (selected != null) {
      selected.removeStyleName(style.selected());
    }

    this.selected = selection;
    selection.addStyleName(style.selected());
  }

  @UiHandler("roads")
  public void onRoadsClick(ClickEvent e) {
    contentSwitchPanel.showWidget(1);
    select(roads);
  }

  @Override
  public void setPresenter(final Presenter presenter) {}

  @EventHandler
  public void onFeatureSelectionEvent(SelectFeatureEvent e) {
    switchPanel.showWidget(0);
  }

  @Override
  public void setEventBus(EventBus eventBus) {
    super.setEventBus(eventBus);

    EVENT_BINDER.bindEventHandlers(this, eventBus);
  }
}
