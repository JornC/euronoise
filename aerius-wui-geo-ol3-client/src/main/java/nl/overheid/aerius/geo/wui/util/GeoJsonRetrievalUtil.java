package nl.overheid.aerius.geo.wui.util;

import java.util.function.Consumer;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;

import ol.Feature;
import ol.format.GeoJson;

public final class GeoJsonRetrievalUtil {
  private GeoJsonRetrievalUtil() {}

  public static void getGeoJson(String url, Consumer<Feature[]> cons) {
    RequestBuilder bldr = new RequestBuilder(RequestBuilder.POST, url);
    bldr.setCallback(new RequestCallback() {
      @Override
      public void onResponseReceived(Request request, Response response) {
        final GeoJson geoJson = new GeoJson();

        cons.accept(geoJson.readFeatures(response.getText()));
      }

      @Override
      public void onError(Request request, Throwable exception) {
        throw new RuntimeException("Cannot load url.");
      }
    });
    try {
      bldr.send();
    } catch (RequestException e) {
      throw new RuntimeException("Cannot load url.");
    }
  }
}
