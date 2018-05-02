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
package nl.yogh.aerius.wui.widget;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;

import nl.yogh.aerius.wui.resources.R;

/**
 * Simple widget showing a 'loading' image with some styling.
 */
public class ContentLoadingWidget extends SimplePanel {
  public ContentLoadingWidget() {
    final Image img = new Image(R.images().waitingAnimation());

    setWidget(img);

    img.getElement().getStyle().setWidth(24, Unit.PX);
    img.getElement().getStyle().setHeight(24, Unit.PX);
  }
}