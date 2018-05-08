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
package nl.overheid.aerius.geo.wui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;

import nl.overheid.aerius.geo.command.LayerAddedCommand;
import nl.overheid.aerius.geo.command.LayerHiddenCommand;
import nl.overheid.aerius.geo.command.LayerOpacityCommand;
import nl.overheid.aerius.geo.command.LayerRemovedCommand;
import nl.overheid.aerius.geo.command.LayerVisibleCommand;
import nl.overheid.aerius.geo.domain.IsLayer;
import nl.overheid.aerius.geo.epsg.EPSG;
import nl.overheid.aerius.geo.util.ReceptorUtil;
import nl.overheid.aerius.geo.wui.util.MapUtil;
import nl.yogh.gwt.wui.command.Command;
import nl.yogh.gwt.wui.widget.HasEventBus;
import ol.Map;
import ol.layer.Layer;
import ol.proj.Projection;

public class MapLayoutPanel implements HasEventBus {
  interface MapLayoutPanelEventBinder extends EventBinder<MapLayoutPanel> {}

  private final MapLayoutPanelEventBinder EVENT_BINDER = GWT.create(MapLayoutPanelEventBinder.class);

  private final EPSG epsg;
  private Map map;
  private EventBus eventBus;
  private final ReceptorUtil receptorUtil;

  private List<Layer> deferred = new ArrayList<>();

  @Inject
  public MapLayoutPanel(final EPSG epsg, final ReceptorUtil receptorUtil) {
    this.epsg = epsg;
    this.receptorUtil = receptorUtil;

    MapUtil.prepareEPSG(epsg);
  }

  @Override
  public void setEventBus(final EventBus eventBus) {
    this.eventBus = eventBus;

    EVENT_BINDER.bindEventHandlers(this, eventBus);
  }

  public void setTarget(final String uniqueId) {
    final Projection projection = Projection.get(epsg.getEpsgCode());
    if (projection == null) {
      throw new RuntimeException("Projection not available while this is mandatory. " + epsg.getEpsgCode());
    }

    map = MapUtil.prepareMap(uniqueId, projection);

    // Base layer preparation. COMMAND OR EVENT!?
    final IsLayer<Layer> baseLayer = MapUtil.prepareBaseLayer(map, projection, epsg);
    eventBus.fireEvent(new LayerAddedCommand(baseLayer));

    MapUtil.prepareControls(map);

    // TODO Move this elsewhere and add through event (non-interactive, top-layer)
    MapUtil.prepareInformationLayer(map, projection, eventBus, receptorUtil);
    
    completeDeferred();
  }

  private void completeDeferred() {
    for (Layer l : deferred) {
      addLayer(l);
    }
  }

  @EventHandler
  void onLayerAddedCommand(final LayerAddedCommand c) {
    finishCommand(c, addLayer(c.getIsLayer()));
  }

  @EventHandler
  void onLayerRemovedCommand(final LayerRemovedCommand c) {
    finishCommand(c, removeLayer(c.getIsLayer()));
  }

  @EventHandler
  void onLayerVisibleCommand(final LayerVisibleCommand c) {
    finishCommand(c, setLayerVisible(c.getIsLayer(), true));
  }

  @EventHandler
  void onLayerHiddenCommand(final LayerHiddenCommand c) {
    finishCommand(c, setLayerVisible(c.getIsLayer(), false));
  }

  @EventHandler
  void onLayerOpacityCommand(final LayerOpacityCommand c) {
    setLayerOpacity(c.getIsLayer(), c.getOpacity());
  }

  private void finishCommand(final Command<?> c, final boolean success) {
    if (!success) {
      c.silence();
    }
  }

  public void resize() {
    map.updateSize();
  }

  /**
   * To be used only as a delegate method from a command handler and not directly.
   */
  private boolean addLayer(final IsLayer<Layer> layer) {
    return addLayer(layer.asLayer());
  }

  /**
   * To be used only as a delegate method from a command handler and not directly.
   */
  private boolean removeLayer(final IsLayer<Layer> layer) {
    return removeLayer(layer.asLayer());
  }

  /**
   * To be used only as a delegate method from a command handler and not directly.
   */
  private boolean setLayerVisible(final IsLayer<Layer> isLayer, final boolean visible) {
    if (isLayer.asLayer().getVisible() == visible) {
      return false;
    }

    isLayer.asLayer().setVisible(visible);

    return true;
  }

  private void setLayerOpacity(final IsLayer<Layer> isLayer, final double opacity) {
    isLayer.asLayer().setOpacity(opacity);
  }

  /**
   * To be used only as a delegate method from an command handler and not directly.
   */
  private boolean addLayer(final Layer layer) {
    if (map == null) {
      deferAddLayer(layer);
      return false;
    }
    
    map.addLayer(layer);

    // TODO Implement action indication
    return true;
  }

  private void deferAddLayer(Layer layer) {
    deferred.add(layer);
  }

  /**
   * To be used only as a delegate method from an command handler and not directly.
   */
  private boolean removeLayer(final Layer baseLayer) {
    map.removeLayer(baseLayer);

    // TODO Implement action indication
    return true;
  }
}
