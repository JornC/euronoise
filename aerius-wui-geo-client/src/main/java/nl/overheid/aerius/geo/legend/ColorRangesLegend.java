package nl.overheid.aerius.geo.legend;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Data class for legend with a list of legend names with for each name a colors.
 */
public class ColorRangesLegend implements Legend, Serializable {
  private static final long serialVersionUID = 179088884735859758L;

  private String[] legendNames;
  private String[] colors;
  private boolean hexagon;

  // Needed for GWT.
  public ColorRangesLegend() {}

  public ColorRangesLegend(final String[] legendNames, final String[] colors) {
    this(legendNames, colors, false);
  }

  public ColorRangesLegend(final String[] legendNames, final String[] colors, final boolean hexagon) {
    this.hexagon = hexagon;
    assert legendNames.length == colors.length : "Legend names list different size as colors list for " + this;
    this.legendNames = legendNames;
    this.colors = colors;
  }

  public String[] getColors() {
    return colors;
  }

  public String[] getLegendNames() {
    return legendNames;
  }

  /**
   * Returns if the legend items should be displayed as a hexagon picture.
   *
   * @return true if should be shown as hexagon
   */
  public boolean isHexagon() {
    return hexagon;
  }

  public void setColors(final String[] colors) {
    this.colors = colors;
  }

  public void setLegendNames(final String[] legendNames) {
    this.legendNames = legendNames;
  }

  /**
   * Get the amount of legend items.
   *
   * @return size
   */
  public int size() {
    return legendNames.length;
  }

  @Override
  public String toString() {
    return "ColorRangesLegend [legendNames=" + Arrays.toString(legendNames) + ", colors=" + Arrays.toString(colors) + ",hexagon:"
        + (hexagon ? "true" : "false") + "]" + super.toString();
  }
}

