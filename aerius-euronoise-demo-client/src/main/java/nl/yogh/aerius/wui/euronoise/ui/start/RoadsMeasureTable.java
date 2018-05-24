package nl.yogh.aerius.wui.euronoise.ui.start;

import java.util.ArrayList;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

import nl.yogh.aerius.wui.domain.RoadMeasure;
import nl.yogh.gwt.wui.widget.EventComposite;
import nl.yogh.gwt.wui.widget.table.SimpleInteractiveClickDivTable;
import nl.yogh.gwt.wui.widget.table.TextColumn;

public class RoadsMeasureTable extends EventComposite implements HasValueChangeHandlers<RoadMeasure> {
  private static final RoadsMeasureTableUiBinder UI_BINDER = GWT.create(RoadsMeasureTableUiBinder.class);

  interface RoadsMeasureTableUiBinder extends UiBinder<Widget, RoadsMeasureTable> {}

  @UiField SimpleInteractiveClickDivTable<RoadMeasure> divTable;

  @UiField(provided = true) TextColumn<RoadMeasure> emissionColumn;

  public RoadsMeasureTable() {
    emissionColumn = new TextColumn<RoadMeasure>() {
      @Override
      public String getValue(final RoadMeasure object) {
        return String.valueOf((int) Math.round(object.getValue()));
      }
    };

    initWidget(UI_BINDER.createAndBindUi(this));
    divTable.setSingleSelectionModel();
    divTable.getSelectionModel().addSelectionChangeHandler(e -> {
      ValueChangeEvent.fire(RoadsMeasureTable.this, ((SingleSelectionModel<RoadMeasure>) divTable.getSelectionModel()).getSelectedObject());
    });

    voodoo();
  }

  private void voodoo() {
    final RoadMeasure em1 = new RoadMeasure("Road surface ABC", -4);
    final RoadMeasure em2 = new RoadMeasure("Noise barrier", -6);

    final ArrayList<RoadMeasure> lst = new ArrayList<>();
    lst.add(em1);
    lst.add(em2);

    divTable.setRowData(lst);
  }

  @Override
  public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<RoadMeasure> handler) {
    return addHandler(handler, ValueChangeEvent.getType());
  }

  public void reset() {
    divTable.deselectAll();
  }
}
