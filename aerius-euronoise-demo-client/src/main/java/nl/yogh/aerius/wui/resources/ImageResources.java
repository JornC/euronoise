package nl.yogh.aerius.wui.resources;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.DataResource.MimeType;
import com.google.gwt.resources.client.ImageResource;

/**
 * Image Resource interface to all images.
 */
public interface ImageResources extends ClientBundle {
  @Source("images/layers.svg")
  @MimeType("image/svg+xml")
  DataResource layers();

  @Source("images/sources.svg")
  @MimeType("image/svg+xml")
  DataResource sources();

  @Source("images/logo-omgevingswet.svg")
  @MimeType("image/svg+xml")
  DataResource logo();

  @Source("images/marker-1-not-affected.svg")
  @MimeType("image/svg+xml")
  DataResource marker1NotAffected();

  @Source("images/marker-2-affected.svg")
  @MimeType("image/svg+xml")
  DataResource marker2Affected();

  @Source("images/marker-3-selected.svg")
  @MimeType("image/svg+xml")
  DataResource marker3Selected();

  @Source("images/source-air-traffic.svg")
  @MimeType("image/svg+xml")
  DataResource sourceAirTraffic();

  @Source("images/source-industry.svg")
  @MimeType("image/svg+xml")
  DataResource sourceIndustry();

  @Source("images/source-road.svg")
  @MimeType("image/svg+xml")
  DataResource sourceRoad();

  @Source("images/source-train.svg")
  @MimeType("image/svg+xml")
  DataResource sourceTrain();

  @Source("images/waiting-animation.gif")
  ImageResource waitingAnimation();
}
