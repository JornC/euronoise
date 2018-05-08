package nl.yogh.aerius.wui.domain;

import com.google.gwt.user.client.ui.HasName;

public class RoadOverload implements HasName {
  private double emission;
  private String name;

  public RoadOverload() {}

  public RoadOverload(final String name, final double emission) {
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

}
