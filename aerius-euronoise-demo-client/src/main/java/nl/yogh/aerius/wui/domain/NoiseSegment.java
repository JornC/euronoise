package nl.yogh.aerius.wui.domain;

public class NoiseSegment {
  private String name;
  private double value;
  private String color;

  public NoiseSegment(final String name, final String color, final double value) {
    this.name = name;
    this.color = color;
    this.value = Math.pow(10, value / 10);
  }

  public double getValue() {
    return value;
  }

  public void setValue(final double value) {
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getColor() {
    return color;
  }

  public void setColor(final String color) {
    this.color = color;
  }
}
