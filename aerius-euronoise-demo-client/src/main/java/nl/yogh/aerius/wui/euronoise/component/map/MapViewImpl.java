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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;

import nl.overheid.aerius.geo.command.LayerAddedCommand;
import nl.overheid.aerius.geo.domain.IsLayer;
import nl.overheid.aerius.geo.event.MapEventBus;
import nl.overheid.aerius.geo.wui.Map;
import nl.overheid.aerius.geo.wui.util.MapUtil;
import nl.yogh.gwt.wui.widget.EventComposite;
import ol.layer.Layer;

public class MapViewImpl extends EventComposite implements MapView {
  private static final MapViewImplUiBinder UI_BINDER = GWT.create(MapViewImplUiBinder.class);

  interface MapViewImplUiBinder extends UiBinder<Widget, MapViewImpl> {}

  interface MapViewImplEventBinder extends EventBinder<MapViewImpl> {}

  private final MapViewImplEventBinder EVENT_BINDER = GWT.create(MapViewImplEventBinder.class);

  private boolean attached;

  private final Map map;

  @UiField(provided = true) Widget mapPanel;

  @Inject
  public MapViewImpl(final Map map) {
    this.map = map;
    this.mapPanel = map.asWidget();

    initWidget(UI_BINDER.createAndBindUi(this));
  }

  public void show() {
    if (!attached) {
      map.attach();

      attached = true;
    }
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

  public void setMapEventBus(final MapEventBus eventBus) {
    super.setEventBus(eventBus);

    MapUtil.setBaseMapUid(eventBus.getScopeId());

    MapUtil.loadInfrastructureLayers();
  }

  public void showBuildings() {
    // final IsLayer<Layer> bagLayer = MapUtil.prepareBAGLayer(map, projection, epsg);
    // eventBus.fireEvent(new LayerAddedCommand(bagLayer));

//    final IsLayer<Layer> bagWfsLayers = MapUtil.prepareWFSBAGLayer();
//    eventBus.fireEvent(new LayerAddedCommand(bagWfsLayers));
    MapUtil.loadBuildings();
  }
}
