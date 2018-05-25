/*
 * Copyright Dutch Ministry of Economic Affairs
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package nl.yogh.aerius.wui.euronoise.component.map;

import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;

import nl.overheid.aerius.geo.event.MapEventBus;
import nl.overheid.aerius.geo.wui.Map;
import nl.overheid.aerius.geo.wui.util.MapUtil;
import nl.overheid.aerius.geo.wui.util.SelectFeatureEvent;
import nl.yogh.aerius.wui.euronoise.event.CalculateCompleteEvent;
import nl.yogh.aerius.wui.euronoise.event.CalculateInitEvent;
import nl.yogh.aerius.wui.euronoise.event.ClearTabSelectionEvent;
import nl.yogh.aerius.wui.euronoise.event.DisplayResultsEvent;
import nl.yogh.aerius.wui.euronoise.event.MeasureSelectedEvent;
import nl.yogh.aerius.wui.euronoise.event.ResultValueSelectedEvent;
import nl.yogh.aerius.wui.euronoise.event.RoadHighlightEvent;
import nl.yogh.aerius.wui.euronoise.event.ShowIndustryEvent;
import nl.yogh.aerius.wui.euronoise.event.ShowRailsEvent;
import nl.yogh.aerius.wui.euronoise.event.ShowRoadsEvent;
import nl.yogh.aerius.wui.euronoise.ui.start.RoadsDataTable;
import nl.yogh.gwt.wui.widget.EventComposite;

public class MapViewImpl extends EventComposite implements MapView {
  private static final MapViewImplUiBinder UI_BINDER = GWT.create(MapViewImplUiBinder.class);

  private static final String SELECTED_MARKER = "data:image/svg+xml,<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 55 82' width='29' height='44'><defs><style>.cls-1{fill:#fff;}.cls-2{fill:#e0700d;}</style></defs><title>Artboard 1</title><path class='cls-1' d='M14.25,2.15A12.79,12.79,0,0,0,1.18,15.23c0,4.29,2.06,7.48,4.24,10.85a43.48,43.48,0,0,1,5.73,12.75,3.2,3.2,0,0,0,6.2,0,43.48,43.48,0,0,1,5.73-12.75c2.18-3.37,4.24-6.56,4.24-10.85A12.79,12.79,0,0,0,14.25,2.15Z'/><path class='cls-1' d='M27.32,2.15C12.42,2.15,1.18,13.4,1.18,28.31c0,8.57,4.12,15,8.48,21.7a86,86,0,0,1,11.45,25.5,6.42,6.42,0,0,0,12.42,0A86.22,86.22,0,0,1,45,50c4.36-6.75,8.48-13.13,8.48-21.7C53.46,13.4,42.22,2.15,27.32,2.15Z'/><circle class='cls-1' cx='27.53' cy='28.24' r='14.96'/><path class='cls-2' d='M27.32,5.15c-13.19,0-23.14,10-23.14,23.16,0,7.68,3.71,13.42,8,20.07A89.4,89.4,0,0,1,24,74.75a3.41,3.41,0,0,0,6.6,0A89.4,89.4,0,0,1,42.46,48.38c4.3-6.65,8-12.39,8-20.07C50.46,15.11,40.51,5.15,27.32,5.15Zm0,36.34A13.15,13.15,0,1,1,40.46,28.35,13.14,13.14,0,0,1,27.32,41.49Z'/><circle cx='27.32' cy='28.35' r='6.44'/></svg>";
  private static final String INFO_MARKER_SVG = "data:image/svg+xml,<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 34 34' width='32' height='32'><title>an-location</title><path d='M17,3.64a8.39,8.39,0,0,0-8.57,8.58c0,2.84,1.37,5,3,7.43a33.2,33.2,0,0,1,4.38,9.77,1.26,1.26,0,0,0,2.45,0,33,33,0,0,1,4.38-9.77c1.59-2.46,3-4.59,3-7.43A8.38,8.38,0,0,0,17,3.64ZM17,17.1a4.87,4.87,0,1,1,4.87-4.87A4.87,4.87,0,0,1,17,17.1Z' fill='#d63327'/><circle cx='17' cy='12.23' r='2.39' fill='#d63327'/></svg>";


  interface MapViewImplUiBinder extends UiBinder<Widget, MapViewImpl> {}

  interface MapViewImplEventBinder extends EventBinder<MapViewImpl> {}

  private final MapViewImplEventBinder EVENT_BINDER = GWT.create(MapViewImplEventBinder.class);

  private final Map map;

  @UiField(provided = true) Widget mapPanel;

  private boolean loadMap;

  private String tabSelection;

  private String vanillaRoad;

  @Inject
  public MapViewImpl(final Map map) {
    this.map = map;
    this.mapPanel = map.asWidget();

    initWidget(UI_BINDER.createAndBindUi(this));
  }

  @Override
  public void setEventBus(final EventBus eventBus) {
    EVENT_BINDER.bindEventHandlers(this, eventBus);

    if (eventBus instanceof MapEventBus) {
      setMapEventBus((MapEventBus) eventBus);
    } else {
      map.setEventBus(eventBus);
      map.registerEventCohort(this);
    }
  }

  @Override
  protected void onLoad() {
    super.onLoad();

    if (loadMap) {
      loadMap();
    }
  }

  public void setMapEventBus(final MapEventBus eventBus) {
    super.setEventBus(eventBus);

    MapUtil.setEventBus(eventBus);
    MapUtil.setBaseMapUid(eventBus.getScopeId());

    if (!mapPanel.isAttached()) {
      loadMap = true;
    } else {
      GWT.log("Loading map..");
      loadMap();
    }
  }

  private void loadMap() {
    MapUtil.loadInfrastructureLayers();
    MapUtil.prepareMarkersLayer(null, null, SELECTED_MARKER);
  }

  @EventHandler
  public void onFeateSelectedEvent(final SelectFeatureEvent e) {
    MapUtil.setSelectedMarker(e.getValue());
  }

  @EventHandler
  public void onCalculationInitEvent(final CalculateInitEvent e) {
    map.switchToBaseLayer();
  }

  @EventHandler
  public void onCalculateCompleteEvent(final CalculateCompleteEvent e) {
    MapUtil.hideInfrastructureLayers();
    MapUtil.showIsoLines();
  }

  @EventHandler
  public void showResultsEvent(final DisplayResultsEvent e) {
    MapUtil.setResultValue("");
    MapUtil.setInteractionEnabled(true);
    MapUtil.hideIsoLines();
  }

  @EventHandler
  public void onSelectRails(final ShowRailsEvent e) {
    MapUtil.showInfrastructureLayers("Rail");
    tabSelection = "Rail";
  }

  @EventHandler
  public void onSelectIndustry(final ShowIndustryEvent e) {
    MapUtil.showInfrastructureLayers("");
    tabSelection = "Industry";
  }

  @EventHandler
  public void onSelectRoads(final ShowRoadsEvent e) {
    showGeneralizedRoad();
    tabSelection = "Road";
  }

  public void showGeneralizedRoad() {
    MapUtil.showInfrastructureLayers("Road");
    MapUtil.setResultValue("Road");
  }

  @EventHandler
  public void onClearTabSelectionEvent(final ClearTabSelectionEvent e) {
    MapUtil.setResultValue("");
    MapUtil.hideInfrastructureLayers();
  }

  @EventHandler
  public void onSelectRoad(final RoadHighlightEvent e) {
    if (e.getValue() == null) {
      showGeneralizedRoad();
    } else {
      vanillaRoad = e.getValue().equals(RoadsDataTable.OWN) ? "OWN" : "A10";
      MapUtil.showInfrastructureLayers(vanillaRoad);
      eventBus.fireEvent(new ResultValueSelectedEvent(e.getValue().equals(RoadsDataTable.OWN) ? "OWN" : "A10_zonder"));
    }
  }

  @EventHandler
  public void onMeasureSelected(final MeasureSelectedEvent e) {
    MapUtil.setResultValue(e.getValue() == null || e.getValue().isEmpty() ? "A10_zonder" : "A10_metMa");

    String append = e.getValue().stream().map(v -> v.getType()).distinct().sorted().collect(Collectors.joining("_"));

    if (e.getValue() != null && !e.getValue().isEmpty()) {
      MapUtil.showInfrastructureLayers(vanillaRoad + "_" + append);
    } else {
      MapUtil.showInfrastructureLayers(vanillaRoad);
    }
  }

  @EventHandler
  public void onResultTypeSelected(final ResultValueSelectedEvent e) {
    MapUtil.setResultValue(e.getValue());
  }

  public void showBuildings() {
    // final IsLayer<Layer> bagLayer = MapUtil.prepareBAGLayer(map, projection, epsg);
    // eventBus.fireEvent(new LayerAddedCommand(bagLayer));

    // final IsLayer<Layer> bagWfsLayers = MapUtil.prepareWFSBAGLayer();
    // eventBus.fireEvent(new LayerAddedCommand(bagWfsLayers));
    MapUtil.loadBuildings();
  }

  public void onResize() {
    Scheduler.get().scheduleDeferred(() -> map.onResize());
  }
}
