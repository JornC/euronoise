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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import nl.overheid.aerius.geo.event.MapEventBus;
import nl.yogh.gwt.wui.widget.HasEventBus;

public class OpenLayers3Map extends Composite implements Map {
  private final String uniqueId;

  private final MapLayoutPanel map;

  private final Panel mapPanel;

  private boolean attached;
  private boolean loaded;

  private MapEventBus eventBus;

  @Inject
  public OpenLayers3Map(final MapLayoutPanel map) {
    this.map = map;
    this.mapPanel = new SimplePanel();

    uniqueId = "openlayers-3-map-" + DOM.createUniqueId();

    initWidget(mapPanel);

    getElement().setId(uniqueId);
  }

  @Override
  protected void onLoad() {
    loaded = true;

    if (!attached) {
      Scheduler.get().scheduleDeferred(() -> attach());
    }
  }

  @Override
  public void setEventBus(final EventBus eventBus) {
    GWT.log("Initializing map event bus with id: " + uniqueId);
    this.eventBus = new MapEventBus(eventBus, uniqueId);
    map.setEventBus(this.eventBus);
  }

  @Override
  public void attach() {
    if (attached) {
      return;
    }

    if (loaded) {
      map.setTarget(uniqueId);

      attached = true;
    }
  }

  @Override
  public void registerEventCohort(final HasEventBus cohort) {
    cohort.setEventBus(eventBus);
  }

  @Override
  public void onResize() {
    map.resize();
  }
}
