package nl.yogh.aerius.wui.euronoise.component.menu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class MenuWidget extends Composite {
  private static final MenuWidgetUiBinder UI_BINDER = GWT.create(MenuWidgetUiBinder.class);

  interface MenuWidgetUiBinder extends UiBinder<Widget, MenuWidget> {}

  public MenuWidget() {
    initWidget(UI_BINDER.createAndBindUi(this));
  }
}
