package nl.yogh.aerius.wui.euronoise.component.menu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import nl.yogh.aerius.wui.resources.R;

public class MenuWidget extends Composite {
  private static final MenuWidgetUiBinder UI_BINDER = GWT.create(MenuWidgetUiBinder.class);

  interface MenuWidgetUiBinder extends UiBinder<Widget, MenuWidget> {}

  @UiField SimplePanel layersButton;
  @UiField SimplePanel sourcesButton;

  @UiField FlowPanel sources;
  @UiField FlowPanel layers;

  public MenuWidget() {
    initWidget(UI_BINDER.createAndBindUi(this));

    layersButton.getElement()
        .getStyle()
        .setProperty("WebkitMaskImage", "url(" + R.images().layers().getSafeUri()
            .asString() + ")");

    sourcesButton.getElement()
        .getStyle()
        .setProperty("WebkitMaskImage", "url(" + R.images().sources().getSafeUri()
            .asString() + ")");
  }
  
  @UiHandler("logo")
  public void onLogoPanelClick(ClickEvent e) {
    Window.Location.reload();
  }
}
