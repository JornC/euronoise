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
import nl.yogh.aerius.wui.euronoise.event.CalculateCompleteEvent;
import nl.yogh.aerius.wui.euronoise.event.RoadHighlightEvent;
import nl.yogh.gwt.wui.widget.EventComposite;

public class MapViewImpl extends EventComposite implements MapView {
  private static final MapViewImplUiBinder UI_BINDER = GWT.create(MapViewImplUiBinder.class);

  interface MapViewImplUiBinder extends UiBinder<Widget, MapViewImpl> {}

  interface MapViewImplEventBinder extends EventBinder<MapViewImpl> {}

  private final MapViewImplEventBinder EVENT_BINDER = GWT.create(MapViewImplEventBinder.class);

  private final Map map;

  @UiField(provided = true) Widget mapPanel;

  private boolean loadMap;

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
  }

  @EventHandler
  public void onCalculateCompleteEvent(final CalculateCompleteEvent e) {
    MapUtil.hideInfrastructureLayers();

    //    MapUtil.displayMarkers();
  }

  @EventHandler
  public void onSelectRoad(final RoadHighlightEvent e) {
    GWT.log("Gotta highlight road: " + e.getValue());
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
