package nl.yogh.aerius.wui.euronoise.ui.start;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;

import nl.yogh.gwt.wui.widget.EventComposite;
import nl.yogh.gwt.wui.widget.SwitchPanel;

public class CompensationPanel extends EventComposite {
  private static final CompensationPanelUiBinder UI_BINDER = GWT.create(CompensationPanelUiBinder.class);

  interface CompensationPanelUiBinder extends UiBinder<Widget, CompensationPanel> {}

  @UiField SwitchPanel switchPanel;

  public interface CustomStyle extends CssResource {
    String selected();
  }

  @UiField CustomStyle style;

  @UiField Widget leftTab;
  @UiField Widget rightTab;

  private Widget selected;

  public CompensationPanel() {
    initWidget(UI_BINDER.createAndBindUi(this));

    setSelectedTab(leftTab);
  }

  private void setSelectedTab(final Widget tab) {
    if (selected != null) {
      selected.setStyleName(style.selected(), false);
    }

    tab.setStyleName(style.selected(), true);
    switchPanel.showWidget(0);
    selected = tab;
  }

  @UiHandler("leftTab")
  public void onLeftTabClick(final ClickEvent e) {
    setSelectedTab(leftTab);
    switchPanel.showWidget(0);
  }

  @UiHandler("rightTab")
  public void onRightTabClick(final ClickEvent e) {
    setSelectedTab(rightTab);
    switchPanel.showWidget(1);
  }
}
