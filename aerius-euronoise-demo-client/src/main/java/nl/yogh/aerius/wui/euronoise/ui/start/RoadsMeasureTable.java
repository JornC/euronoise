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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.MultiSelectionModel;

import nl.yogh.aerius.wui.domain.RoadMeasure;
import nl.yogh.gwt.wui.widget.EventComposite;
import nl.yogh.gwt.wui.widget.table.SimpleInteractiveClickDivTable;
import nl.yogh.gwt.wui.widget.table.TextColumn;

public class RoadsMeasureTable extends EventComposite implements HasValueChangeHandlers<Set<RoadMeasure>> {
  private static final int LENGTH_ASSUMPTION = 100;

  private static final RoadsMeasureTableUiBinder UI_BINDER = GWT.create(RoadsMeasureTableUiBinder.class);

  interface RoadsMeasureTableUiBinder extends UiBinder<Widget, RoadsMeasureTable> {}

  @UiField SimpleInteractiveClickDivTable<RoadMeasure> divTable;

  @UiField(provided = true) TextColumn<RoadMeasure> emissionColumn;

  @UiField Label sumField;

  public RoadsMeasureTable() {
    emissionColumn = new TextColumn<RoadMeasure>() {
      @Override
      public String getValue(final RoadMeasure object) {
        return String.valueOf((int) Math.round(object.getValue()));
      }
    };

    initWidget(UI_BINDER.createAndBindUi(this));
    divTable.setMultiSelectionModel();
    divTable.getSelectionModel().addSelectionChangeHandler(e -> {
      Set<RoadMeasure> selectedSet = ((MultiSelectionModel<RoadMeasure>) divTable.getSelectionModel()).getSelectedSet();
      ValueChangeEvent.fire(RoadsMeasureTable.this, selectedSet);

      updateSum(selectedSet);
    });

    voodoo();
  }

  private void voodoo() {
    final RoadMeasure em1 = new RoadMeasure("barrier", "Noise Barrier (2m)", 93);
    final RoadMeasure em2 = new RoadMeasure("barrier", "Noise Barrier (3m)", 133);
    final RoadMeasure em3 = new RoadMeasure("barrier", "Noise Barrier (4m)", 172);
    final RoadMeasure em4 = new RoadMeasure("barrier", "Noise Barrier (5m)", 212);
    final RoadMeasure em6 = new RoadMeasure("surface", "Road surface (ZOAB)", 40);
    final RoadMeasure em7 = new RoadMeasure("surface", "Road surface (ZOAB dual layer)", 240);
    final RoadMeasure em8 = new RoadMeasure("surface", "Road surface (thin layered)", 130);

    final ArrayList<RoadMeasure> lst = new ArrayList<>();
    lst.add(em1);
    lst.add(em2);
    lst.add(em3);
    lst.add(em4);
    lst.add(em6);
    lst.add(em7);
    lst.add(em8);

    divTable.setRowData(lst);
  }

  private void updateSum(Set<RoadMeasure> set) {
    double sum = set.stream().mapToDouble(v -> v.getValue()).sum();

    sumField.setText(String.valueOf(((int) (sum * 100)) / 100 * LENGTH_ASSUMPTION));
  }

  @Override
  public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<Set<RoadMeasure>> handler) {
    return addHandler(handler, ValueChangeEvent.getType());
  }

  public void reset() {
    divTable.deselectAll();
  }
}
