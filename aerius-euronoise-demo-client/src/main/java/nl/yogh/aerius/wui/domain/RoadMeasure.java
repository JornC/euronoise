package nl.yogh.aerius.wui.domain;

import com.google.gwt.user.client.ui.HasName;

public class RoadMeasure implements HasName {
  private double emission;
  private String name;
  private String type;

  public RoadMeasure() {}

  public RoadMeasure(String type, final String name, final double emission) {
    this.setType(type);
    this.name = name;
    this.emission = emission;
  }

  @Override
  public void setName(final String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

  public double getValue() {
    return emission;
  }

  public void setEmission(final double emission) {
    this.emission = emission;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

}
