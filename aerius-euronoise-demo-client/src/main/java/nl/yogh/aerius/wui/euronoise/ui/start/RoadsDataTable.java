package nl.yogh.aerius.wui.euronoise.ui.start;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;

import nl.yogh.aerius.wui.domain.RoadEmission;
import nl.yogh.gwt.wui.widget.EventComposite;
import nl.yogh.gwt.wui.widget.table.SimpleInteractiveClickDivTable;
import nl.yogh.gwt.wui.widget.table.TextColumn;

public class RoadsDataTable extends EventComposite implements HasValueChangeHandlers<RoadEmission> {
  public static final String OWN = "Other roads";

  private static final RoadsDataTableUiBinder UI_BINDER = GWT.create(RoadsDataTableUiBinder.class);

  interface RoadsDataTableUiBinder extends UiBinder<Widget, RoadsDataTable> {}

  @UiField SimpleInteractiveClickDivTable<RoadEmission> divTable;

  @UiField(provided = true) TextColumn<RoadEmission> emissionColumn;

  public RoadsDataTable() {
    emissionColumn = new TextColumn<RoadEmission>() {
      @Override
      public String getValue(final RoadEmission object) {
        return String.valueOf((int) Math.round(object.getEmission()));
      }
    };

    initWidget(UI_BINDER.createAndBindUi(this));
    divTable.setSingleSelectionModel();
    divTable.getSelectionModel().addSelectionChangeHandler(e -> {
      ValueChangeEvent.fire(RoadsDataTable.this, ((SingleSelectionModel<RoadEmission>) divTable.getSelectionModel()).getSelectedObject());
    });

    voodoo();
  }

  private void voodoo() {
    final RoadEmission em1 = new RoadEmission("A10", 70);
    final RoadEmission em2 = new RoadEmission(OWN, 68);

    final ArrayList<RoadEmission> lst = new ArrayList<>();
    lst.add(em1);
    lst.add(em2);

    divTable.setRowData(lst);
  }

  @Override
  public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<RoadEmission> handler) {
    return addHandler(handler, ValueChangeEvent.getType());
  }
}
