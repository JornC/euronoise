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
import nl.yogh.aerius.wui.domain.RoadEmission;
import nl.yogh.aerius.wui.euronoise.event.ClearTabSelectionEvent;
import nl.yogh.aerius.wui.euronoise.event.ResultValueSelectedEvent;
import nl.yogh.aerius.wui.euronoise.event.RoadHighlightEvent;
import nl.yogh.aerius.wui.euronoise.event.ShowIndustryEvent;
import nl.yogh.aerius.wui.euronoise.event.ShowRailsEvent;
import nl.yogh.aerius.wui.euronoise.event.ShowRoadsEvent;
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

  @UiField FeatureTracker featureTracker;

  @UiField SwitchPanel switchPanel;

  @UiField FocusPanel roads;
  @UiField FocusPanel rails;
  @UiField FocusPanel industry;

  @UiField CustomStyle style;

  private Widget selected;

  @UiField SimplePanel railsButton;
  @UiField SimplePanel roadsButton;
  @UiField SimplePanel industryButton;
  @UiField SimplePanel airTrafficButton;

  @UiField SwitchPanel contentSwitchPanel;

  @UiField RoadsDataTable roadsData;

  @UiField CompensationPanel compensationPanel;

  public StartViewImpl() {
    initWidget(UI_BINDER.createAndBindUi(this));

    railsButton.getElement().getStyle().setProperty("WebkitMaskImage", "url(" + R.images().sourceTrain().getSafeUri().asString() + ")");
    roadsButton.getElement().getStyle().setProperty("WebkitMaskImage", "url(" + R.images().sourceRoad().getSafeUri().asString() + ")");
    industryButton.getElement().getStyle().setProperty("WebkitMaskImage", "url(" + R.images().sourceIndustry().getSafeUri().asString() + ")");
    airTrafficButton.getElement().getStyle().setProperty("WebkitMaskImage", "url(" + R.images().sourceAirTraffic().getSafeUri().asString() + ")");

    roadsData.addValueChangeHandler(e -> setCompensationMeasures(e.getValue()));
  }

  private void setCompensationMeasures(final RoadEmission value) {
    compensationPanel.setVisible(value != null);

    eventBus.fireEvent(new RoadHighlightEvent(value != null ? value.getName() : null));

    compensationPanel.reset();
  }

  @UiHandler("rails")
  public void onRailsClick(final ClickEvent e) {
    if (isSelected(rails)) {
      unselect();
      return;
    }

    selectRails();
  }

  @UiHandler("industry")
  public void onIndustryClick(final ClickEvent e) {
    if (isSelected(industry)) {
      unselect();
      return;
    }

    selectIndustry();
  }

  @UiHandler("roads")
  public void onRoadsClick(final ClickEvent e) {
    if (isSelected(roads)) {
      unselect();
      return;
    }

    selectRoads();
  }

  private boolean isSelected(final FocusPanel elem) {
    return elem == selected;
  }

  private void unselect() {
    if (selected != null) {
      selected.removeStyleName(style.selected());
    }

    this.selected = null;

    eventBus.fireEvent(new ClearTabSelectionEvent());
    contentSwitchPanel.hideWidget();
  }

  private void select(final Panel selection) {
    if (selected != null) {
      selected.removeStyleName(style.selected());
    }

    this.selected = selection;
    selection.addStyleName(style.selected());
  }

  private void selectRails() {
    contentSwitchPanel.showWidget(0);
    eventBus.fireEvent(new ResultValueSelectedEvent("Railverk"));
    select(rails);
    eventBus.fireEvent(new ShowRailsEvent());
  }

  private void selectIndustry() {
    contentSwitchPanel.showWidget(2);
    eventBus.fireEvent(new ResultValueSelectedEvent("Industrie"));
    select(industry);
    eventBus.fireEvent(new ShowIndustryEvent());
  }

  private void selectRoads() {
    contentSwitchPanel.showWidget(1);
    select(roads);
    eventBus.fireEvent(new ResultValueSelectedEvent("Road"));
    eventBus.fireEvent(new ShowRoadsEvent());
    roadsData.reset();
    compensationPanel.reset();
  }

  @Override
  public void setPresenter(final Presenter presenter) {}

  @EventHandler
  public void onFeatureSelectionEvent(final SelectFeatureEvent e) {
    switchPanel.showWidget(0);
  }

  @Override
  public void setEventBus(final EventBus eventBus) {
    super.setEventBus(eventBus, compensationPanel, featureTracker, roadsData);

    EVENT_BINDER.bindEventHandlers(this, eventBus);
  }
}
