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

import nl.yogh.aerius.wui.domain.RoadOverload;
import nl.yogh.gwt.wui.widget.EventComposite;
import nl.yogh.gwt.wui.widget.table.SimpleInteractiveClickDivTable;
import nl.yogh.gwt.wui.widget.table.TextColumn;

public class RoadsOverloadTable extends EventComposite implements HasValueChangeHandlers<RoadOverload> {
  private static final RoadsOverloadTableUiBinder UI_BINDER = GWT.create(RoadsOverloadTableUiBinder.class);

  interface RoadsOverloadTableUiBinder extends UiBinder<Widget, RoadsOverloadTable> {}

  @UiField SimpleInteractiveClickDivTable<RoadOverload> divTable;

  @UiField(provided = true) TextColumn<RoadOverload> valueColumn;

  public RoadsOverloadTable() {
    valueColumn = new TextColumn<RoadOverload>() {
      @Override
      public String getValue(final RoadOverload object) {
        return String.valueOf((int) Math.round(object.getValue()));
      }
    };

    initWidget(UI_BINDER.createAndBindUi(this));
    divTable.setSingleSelectionModel();
    divTable.getSelectionModel().addSelectionChangeHandler(e -> {
      ValueChangeEvent.fire(RoadsOverloadTable.this, ((SingleSelectionModel<RoadOverload>) divTable.getSelectionModel()).getSelectedObject());
    });

    voodoo();
  }

  private void voodoo() {
    final RoadOverload em1 = new RoadOverload("Places exceeding GPP", 15);
    final RoadOverload em2 = new RoadOverload("Highest exceedance", 72);

    final ArrayList<RoadOverload> lst = new ArrayList<>();
    lst.add(em1);
    lst.add(em2);

    divTable.setRowData(lst);
  }

  @Override
  public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<RoadOverload> handler) {
    return addHandler(handler, ValueChangeEvent.getType());
  }
}
