package nl.overheid.aerius.geo.wui.util;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;

import nl.yogh.gwt.wui.util.GWTAtomicInteger;
import ol.Feature;
import ol.format.GeoJson;
import ol.source.Vector;

public class MapRedrawRequestCallback implements RequestCallback {
  private final int id;
  private final GWTAtomicInteger counter;
  private final Vector vectorSource;

  public MapRedrawRequestCallback(final Vector vectorSource, final int id, final GWTAtomicInteger counter) {
    this.vectorSource = vectorSource;
    this.id = id;
    this.counter = counter;
  }

  @Override
  public void onResponseReceived(final Request request, final Response response) {
    if (counter.get() > id) {
      return;
    }

    final GeoJson geoJson = new GeoJson();
    final Feature[] features = geoJson.readFeatures(response.getText());

    vectorSource.addFeatures(features);
  }

  @Override
  public void onError(final com.google.gwt.http.client.Request request, final Throwable exception) {
    Window.alert(exception.getMessage());
  }
}