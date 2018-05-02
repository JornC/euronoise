package nl.yogh.aerius.wui.euronoise.component;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

import nl.yogh.aerius.wui.euronoise.event.CalculateCompleteEvent;
import nl.yogh.aerius.wui.euronoise.event.CalculateInitEvent;
import nl.yogh.gwt.wui.widget.EventComposite;

public class CalculateWidget extends EventComposite {
  private static final CalculateWidgetUiBinder UI_BINDER = GWT.create(CalculateWidgetUiBinder.class);

  interface CalculateWidgetUiBinder extends UiBinder<Widget, CalculateWidget> {}

  @UiField FlowPanel infoPanel;
  @UiField FlowPanel progressPanel;
  @UiField FlowPanel completePanel;

  public CalculateWidget() {
    initWidget(UI_BINDER.createAndBindUi(this));
  }

  @Override
  protected void onLoad() {
    super.onLoad();

    Scheduler.get().scheduleFixedDelay(() -> {
      CalculateWidget.this.getElement().getStyle().setHeight(110, Unit.PX);
      return false;
    }, 1000);
  }

  @UiHandler("calculate")
  public void onCalculateClick(final ClickEvent e) {
    eventBus.fireEvent(new CalculateInitEvent());

    infoPanel.getElement().getStyle().setHeight(0, Unit.PX);
    CalculateWidget.this.getElement().getStyle().setHeight(58, Unit.PX);

    Scheduler.get().scheduleFixedDelay(() -> {
      progressPanel.getElement().getStyle().setHeight(0, Unit.PX);
      completePanel.getElement().getStyle().setHeight(110, Unit.PX);
      CalculateWidget.this.getElement().getStyle().setHeight(110, Unit.PX);
      eventBus.fireEvent(new CalculateCompleteEvent());
      return false;
    }, 3000);
  }

  @UiHandler("close")
  public void onCloseClick(ClickEvent e) {
    CalculateWidget.this.getElement().getStyle().setHeight(0, Unit.PX);
  }
}
