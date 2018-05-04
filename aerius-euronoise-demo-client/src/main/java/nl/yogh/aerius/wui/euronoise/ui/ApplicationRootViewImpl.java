package nl.yogh.aerius.wui.euronoise.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;

import nl.overheid.aerius.geo.wui.util.SelectFeatureEvent;
import nl.yogh.aerius.wui.ApplicationRootView;
import nl.yogh.aerius.wui.euronoise.component.CalculateWidget;
import nl.yogh.aerius.wui.euronoise.component.map.MapViewImpl;
import nl.yogh.aerius.wui.euronoise.event.CalculateInitEvent;
import nl.yogh.gwt.wui.place.PlaceController;
import nl.yogh.gwt.wui.widget.EventComposite;
import nl.yogh.gwt.wui.widget.NotificationPanel;

public class ApplicationRootViewImpl extends EventComposite implements ApplicationRootView {
  private static final ApplicationRootViewImplUiBinder UI_BINDER = GWT.create(ApplicationRootViewImplUiBinder.class);

  interface ApplicationRootViewImplUiBinder extends UiBinder<Widget, ApplicationRootViewImpl> {}

  interface ApplicationRootViewImplEventBinder extends EventBinder<ApplicationRootViewImpl> {}

  private final ApplicationRootViewImplEventBinder EVENT_BINDER = GWT.create(ApplicationRootViewImplEventBinder.class);

  @UiField(provided = true) MapViewImpl map;
  @UiField CalculateWidget calculateWidget;
  @UiField SimplePanel contentPanel;
  @UiField NotificationPanel notificationPanel;

  @Inject
  public ApplicationRootViewImpl(final EventBus eventBus, final PlaceController placeController, final MapViewImpl map) {
    this.map = map;

    initWidget(UI_BINDER.createAndBindUi(this));

    setEventBus(eventBus);
  }

  @Override
  public void setWidget(final IsWidget w) {
    contentPanel.setWidget(w);
  }

  @EventHandler
  public void onCalculateScenario(CalculateInitEvent e) {
    map.showBuildings();
  }
  
  @EventHandler
  public void onSelectFeatureEvent(SelectFeatureEvent e) {
    map.onResize();
  }

  @Override
  public void setEventBus(final EventBus eventBus) {
    super.setEventBus(eventBus, map, notificationPanel, calculateWidget);

    EVENT_BINDER.bindEventHandlers(this, eventBus);
  }
}
