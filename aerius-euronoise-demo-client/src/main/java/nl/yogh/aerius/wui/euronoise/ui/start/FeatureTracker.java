package nl.yogh.aerius.wui.euronoise.ui.start;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;

import nl.overheid.aerius.geo.wui.util.SelectFeatureEvent;
import nl.yogh.aerius.wui.domain.NoiseSegment;
import nl.yogh.aerius.wui.euronoise.event.MeasureSelectedEvent;
import nl.yogh.aerius.wui.resources.R;
import nl.yogh.gwt.wui.widget.EventComposite;
import ol.Feature;

public class FeatureTracker extends EventComposite {
  private static final FeatureTrackerUiBinder UI_BINDER = GWT.create(FeatureTrackerUiBinder.class);

  interface FeatureTrackerUiBinder extends UiBinder<Widget, FeatureTracker> {}

  interface FeatureTrackerEventBinder extends EventBinder<FeatureTracker> {}

  private final FeatureTrackerEventBinder EVENT_BINDER = GWT.create(FeatureTrackerEventBinder.class);

  public interface CustomStyle extends CssResource {
    String icon();

    String hwn();

    String own();

    String image();
  }

  @UiField FlowPanel distributionPanel;

  @UiField CustomStyle style;

  private final List<NoiseSegment> noiseSetWithMeasure = new ArrayList<>();
  private final List<NoiseSegment> noiseSet = new ArrayList<>();

  public FeatureTracker() {
    initWidget(UI_BINDER.createAndBindUi(this));
  }

  @EventHandler
  public void onFeatureSelect(final SelectFeatureEvent e) {
    noiseSetWithMeasure.clear();
    noiseSet.clear();

    // "A10_zonder": 59.92, "A10_metMa": 56.65, "OWN": 61.58, "Railverk": 64.42, "Industrie": 54.88
    final Feature feature = e.getValue();

    final NoiseSegment rail = new NoiseSegment("Rail", "#ffca0b", feature.get("Railverk"), getRailIcon());
    final NoiseSegment a10Without = new NoiseSegment("A10", "#ff8936", feature.get("A10_zonder"), getRoadIcon("A10"));
    final NoiseSegment a10With = new NoiseSegment("A10", "#ff8936", feature.get("A10_metMa"), getRoadIcon("A10"));
    final NoiseSegment otherRoad = new NoiseSegment("Other roads", "#65bafd", feature.get("OWN"), getOwnRoadIcon("Other..."));
    final NoiseSegment industry = new NoiseSegment("Industry", "#9f06bb", feature.get("Industrie"), getIndustryIcon());

    noiseSet.add(rail);
    noiseSet.add(a10Without);
    noiseSet.add(otherRoad);
    noiseSet.add(industry);

    noiseSetWithMeasure.add(rail);
    noiseSetWithMeasure.add(a10With);
    noiseSetWithMeasure.add(otherRoad);
    noiseSetWithMeasure.add(industry);

    draw(noiseSet);
  }

  private Widget getIndustryIcon() {
    final SimplePanel panel = new SimplePanel();

    panel.addStyleName(style.image());
    panel.getElement().getStyle().setProperty("WebkitMaskImage", "url(" + R.images().sourceIndustry().getSafeUri().asString() + ")");

    return panel;
  }

  private Widget getRailIcon() {
    final SimplePanel panel = new SimplePanel();

    panel.addStyleName(style.image());
    panel.getElement().getStyle().setProperty("WebkitMaskImage", "url(" + R.images().sourceTrain().getSafeUri().asString() + ")");

    return panel;
  }

  private Widget getRoadIcon(String name) {
    final Label panel = new Label(name);

    panel.addStyleName(style.hwn());

    return panel;
  }

  private Widget getOwnRoadIcon(String name) {
    final Label panel = new Label(name);

    panel.addStyleName(style.own());

    return panel;
  }

  private void draw(final List<NoiseSegment> set) {
    if (distributionPanel.getWidgetCount() == 0) {
      drawFresh(set);
    } else {
      update(set);
    }
  }

  private void update(List<NoiseSegment> set) {
    final double sum = set.stream().mapToDouble(v -> v.getValue()).sum();

    for (int i = 0; i < distributionPanel.getWidgetCount(); i++) {
      NoiseSegment segment = set.get(i);
      distributionPanel.getWidget(i).getElement().getStyle().setWidth((segment.getValue() / sum) * 100, Unit.PCT);
      distributionPanel.getWidget(i).setTitle(segment.getName() + ": " + formatFixed(segment.getValue()) + " Lden");
    }
  }

  private void drawFresh(final List<NoiseSegment> set) {
    distributionPanel.clear();
    final double sum = set.stream().mapToDouble(v -> v.getValue()).sum();

    for (final NoiseSegment segment : set) {
      final SimplePanel panel = new SimplePanel();
      panel.getElement().getStyle().setWidth((segment.getValue() / sum) * 100, Unit.PCT);
      panel.getElement().getStyle().setProperty("borderBottom", "8px solid " + segment.getColor());
      panel.setTitle(segment.getName() + ": " + formatFixed(segment.getValue()) + " Lden");
      panel.addStyleName(R.css().flex());
      panel.addStyleName(R.css().alignCenter());
      panel.addStyleName(R.css().justify());

      segment.getIcon().addStyleName(style.icon());
      panel.setWidget(segment.getIcon());

      distributionPanel.add(panel);
    }
  }

  private String formatFixed(final double value) {
    return String.valueOf(((int) (10 * Math.log10(value) * 100)) / 100D);
  }

  @EventHandler
  public void onMeasureSelected(final MeasureSelectedEvent e) {
    if (e.getValue() == null || e.getValue().isEmpty()) {
      draw(noiseSet);
    } else {
      draw(noiseSetWithMeasure);
    }
  }

  @Override
  public void setEventBus(final EventBus eventBus) {
    EVENT_BINDER.bindEventHandlers(this, eventBus);
  }
}
