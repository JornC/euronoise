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
package nl.overheid.aerius.geo.command;

import nl.overheid.aerius.geo.domain.IsLayer;
import nl.overheid.aerius.geo.event.LayerAddedEvent;
import nl.overheid.aerius.geo.event.LayerChangeEvent.CHANGE;

public class LayerAddedCommand extends LayerChangeCommand<LayerAddedEvent> {
  private boolean base;

  public LayerAddedCommand(final IsLayer<?> layer) {
    super(layer, CHANGE.ADDED);
  }

  public LayerAddedCommand(final IsLayer<?> layer, final boolean base) {
    this(layer);
    this.base = base;
  }

  public boolean isBase() {
    return base;
  }

  public void setBase(final boolean base) {
    this.base = base;
  }

  @Override
  protected LayerAddedEvent createEvent(final IsLayer<?> value) {
    return new LayerAddedEvent(value);
  }
}
