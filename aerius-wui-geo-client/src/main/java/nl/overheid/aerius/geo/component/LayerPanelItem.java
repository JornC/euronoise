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
package nl.overheid.aerius.geo.component;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import nl.overheid.aerius.geo.command.LayerHiddenCommand;
import nl.overheid.aerius.geo.command.LayerOpacityCommand;
import nl.overheid.aerius.geo.command.LayerVisibleCommand;
import nl.overheid.aerius.geo.domain.IsLayer;
import nl.overheid.aerius.geo.resources.GeoResources;
import nl.yogh.gwt.wui.widget.FlipButton;
import nl.yogh.gwt.wui.widget.SliderBar;

public class LayerPanelItem extends Composite {
  private static final LayerPanelItemUiBinder UI_BINDER = GWT.create(LayerPanelItemUiBinder.class);

  interface LayerPanelItemUiBinder extends UiBinder<Widget, LayerPanelItem> {}

  @UiField(provided = true) FlipButton flipButton;
  @UiField(provided = true) ToggleButton removeButton;
  @UiField Label name;

  @UiField(provided = true) SliderBar opacitySlider;
  @UiField Panel legend;

  private final EventBus eventBus;

  private final IsLayer<?> layer;

  private boolean hidden;

  public LayerPanelItem(final IsLayer<?> layer, final EventBus eventBus, final GeoResources res) {
    this.layer = layer;
    this.eventBus = eventBus;
    this.flipButton = new FlipButton(v -> legend.setVisible(v));
    flipButton.setClosedRotation(FlipButton.EAST);

    removeButton = new ToggleButton(new Image(res.toggleLayerOn()), new Image(res.toggleLayerOff()));

    opacitySlider = new SliderBar(o -> onSetLayerOpacity(o));

    initWidget(UI_BINDER.createAndBindUi(this));
    flipButton.init();

    name.setText(layer.getInfo().getTitle());

    setLayerVisible(true);
  }

  public void setLayerVisible(final boolean visible) {
    removeButton.setDown(!visible);
    this.hidden = !visible;
  }

  @UiHandler("removeButton")
  public void onRemoveButtonClick(final ClickEvent e) {
    eventBus.fireEvent(hidden ? new LayerVisibleCommand(layer) : new LayerHiddenCommand(layer));
  }

  public void onSetLayerOpacity(final double o) {
    eventBus.fireEvent(new LayerOpacityCommand(layer, o));
  }

  public void setLayerOpacity(final double opacity) {
    opacitySlider.setValue(opacity);
  }
}
