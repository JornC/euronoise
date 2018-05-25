package nl.yogh.aerius.wui.euronoise.ui.start;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;

import nl.overheid.aerius.geo.wui.util.SelectFeatureEvent;
import nl.yogh.aerius.wui.domain.RoadEmission;
import nl.yogh.gwt.wui.widget.EventComposite;
import nl.yogh.gwt.wui.widget.table.SimpleInteractiveClickDivTable;
import nl.yogh.gwt.wui.widget.table.TextColumn;
import ol.Feature;

public class RoadsDataTable extends EventComposite implements HasValueChangeHandlers<RoadEmission> {
  public static final String OWN = "Other roads";

  private static final RoadsDataTableUiBinder UI_BINDER = GWT.create(RoadsDataTableUiBinder.class);

  interface RoadsDataTableUiBinder extends UiBinder<Widget, RoadsDataTable> {}

  interface RoadsDataTableEventBinder extends EventBinder<RoadsDataTable> {}

  private final RoadsDataTableEventBinder EVENT_BINDER = GWT.create(RoadsDataTableEventBinder.class);

  @UiField SimpleInteractiveClickDivTable<RoadEmission> divTable;

  @UiField(provided = true) TextColumn<RoadEmission> emissionColumn;

  public RoadsDataTable() {
    emissionColumn = new TextColumn<RoadEmission>() {
      @Override
      public String getValue(final RoadEmission object) {
        return getEmission(object);
      }
    };

    initWidget(UI_BINDER.createAndBindUi(this));
    divTable.setSingleSelectionModel();
    divTable.setKeyProvider(v -> v.getName(), true);
    divTable.setReplacementFunction((v, r) -> ((Label) r.getWidget(1)).setText(getEmission(v)));
    divTable.getSelectionModel().addSelectionChangeHandler(e -> {
      ValueChangeEvent.fire(RoadsDataTable.this, ((SingleSelectionModel<RoadEmission>) divTable.getSelectionModel()).getSelectedObject());
    });

    voodoo(70, 68);
  }

  private String getEmission(RoadEmission object) {
    return String.valueOf((int) Math.round(object.getEmission() * 100) / 100);
  }

  @EventHandler
  public void onFeatureSelectEvent(final SelectFeatureEvent e) {
    final Feature feature = e.getValue();

    final double a10Zonder = feature.get("A10_zonder");
    final double own = feature.get("OWN");

    voodoo(a10Zonder, own);
  }

  private void voodoo(final double i, final double j) {
    final RoadEmission em1 = new RoadEmission("A10", i);
    final RoadEmission em2 = new RoadEmission(OWN, j);

    final ArrayList<RoadEmission> lst = new ArrayList<>();
    lst.add(em1);
    lst.add(em2);

    divTable.setRowData(lst);
  }

  @Override
  public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<RoadEmission> handler) {
    return addHandler(handler, ValueChangeEvent.getType());
  }

  @Override
  public void setEventBus(final EventBus eventBus) {
    super.setEventBus(eventBus);
    EVENT_BINDER.bindEventHandlers(this, eventBus);
  }

  public void reset() {
    divTable.deselectAll();
  }
}
