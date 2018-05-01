package nl.yogh.aerius.wui.resources;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.DataResource.MimeType;

/**
 * Image Resource interface to all images.
 */
public interface ImageResources extends ClientBundle {
  @Source("images/an-maplayers-legend.svg")
  @MimeType("image/svg+xml")
  DataResource layers();
}
