/*
 * Copyright Dutch Ministry of Economic Affairs
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package nl.overheid.aerius.geo.wui.util;

import java.util.HashSet;

import com.github.desjardins.elemental.XMLSerializer;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.web.bindery.event.shared.EventBus;

import elemental2.dom.Node;
import nl.overheid.aerius.geo.BBox;
import nl.overheid.aerius.geo.command.InfoLocationChangeCommand;
import nl.overheid.aerius.geo.command.LayerAddedCommand;
import nl.overheid.aerius.geo.domain.IsLayer;
import nl.overheid.aerius.geo.domain.LayerInfo;
import nl.overheid.aerius.geo.domain.Point;
import nl.overheid.aerius.geo.epsg.EPSG;
import nl.overheid.aerius.geo.util.ReceptorUtil;
import nl.overheid.aerius.geo.wui.layers.InformationLayer;
import nl.overheid.aerius.geo.wui.layers.OL3Layer;
import nl.yogh.gwt.wui.util.GWTAtomicInteger;
import ol.Collection;
import ol.Coordinate;
import ol.Extent;
import ol.Feature;
import ol.Map;
import ol.MapBrowserEvent;
import ol.MapOptions;
import ol.OLFactory;
import ol.OLUtil;
import ol.View;
import ol.ViewOptions;
import ol.color.Color;
import ol.control.Control;
import ol.control.MousePosition;
import ol.control.ScaleLine;
import ol.control.Zoom;
import ol.control.ZoomSlider;
import ol.format.Wfs;
import ol.format.WfsWriteFeatureOptions;
import ol.format.filter.Filter;
import ol.format.filter.Intersects;
import ol.geom.Polygon;
import ol.layer.Image;
import ol.layer.Layer;
import ol.layer.LayerOptions;
import ol.layer.Tile;
import ol.layer.VectorLayerOptions;
import ol.proj.Projection;
import ol.proj.ProjectionOptions;
import ol.source.ImageWms;
import ol.source.ImageWmsOptions;
import ol.source.ImageWmsParams;
import ol.source.Vector;
import ol.source.Wmts;
import ol.source.WmtsOptions;
import ol.style.Fill;
import ol.style.FillOptions;
import ol.style.Stroke;
import ol.style.StrokeOptions;
import ol.style.Style;
import ol.style.StyleOptions;
import ol.tilegrid.WmtsTileGrid;
import ol.tilegrid.WmtsTileGridOptions;

/**
 * BOILERPLATE CONTAINER
 */
public final class MapUtil {
  private static final String CLICK_EVENT_NAME = "click";
  private static final double MIN_WFS_ZOOM = 11;
  protected static final int MAX_FEATURE_BATCH = 50;
  protected static final int MAX_DRAW_FEATURES = 6000;
  private static String baseMapUid;
  private static Feature previousFeature;

  private static HashSet<String> interestItems = new HashSet<>();
  private static EPSG epsg;
  private static Map map;
  private static EventBus eventBus;
  static {
    interestItems.add("pand.2087049");
    interestItems.add("pand.7461654");
    interestItems.add("pand.6831828");
    interestItems.add("pand.2466756");
  }

  private MapUtil() {}

  public static Map prepareMap(final String target, final Projection projection) {
    // create a view
    final ViewOptions viewOptions = OLFactory.createOptions();
    viewOptions.setProjection(projection);
    final View view = new View(viewOptions);

    final Coordinate centerCoordinate = OLFactory.createCoordinate(117631.26876462143D, 489868.0364256811D);

    view.setCenter(centerCoordinate);
    view.setZoom(11);

    // create the map
    final MapOptions mapOptions = OLFactory.createOptions();
    mapOptions.setTarget(target);
    mapOptions.setView(view);
    mapOptions.setControls(new Collection<Control>());

    return new ol.Map(mapOptions);
  }

  /**
   * Creates a WMTS tilegrid.
   *
   * @param projection
   *          projection of the grid
   * @return WMTS tilegrid
   */
  private static WmtsTileGrid createWmtsTileGrid(final Projection projection) {
    final WmtsTileGridOptions wmtsTileGridOptions = OLFactory.createOptions();

    final double[] resolutions = new double[14];
    final String[] matrixIds = new String[14];

    final double width = projection.getExtent().getWidth();
    final double matrixWidth = width / 256;

    for (int i = 0; i < 14; i++) {
      resolutions[i] = matrixWidth / Math.pow(2, i);
      matrixIds[i] = String.valueOf(i);
    }

    final Coordinate tileGridOrigin = projection.getExtent().getTopLeft();
    wmtsTileGridOptions.setOrigin(tileGridOrigin);
    wmtsTileGridOptions.setResolutions(resolutions);
    wmtsTileGridOptions.setMatrixIds(matrixIds);

    return new WmtsTileGrid(wmtsTileGridOptions);
  }

  private static boolean isInterest(final String id) {
    return interestItems.contains(id);
  }

  public static IsLayer<Layer>[] prepareWFSBAGLayer() {
    // create a vector layer
    final Vector overlaySource = new Vector();
    final VectorLayerOptions overlayLayerOptions = new VectorLayerOptions();
    overlayLayerOptions.setSource(overlaySource);
    final ol.layer.Vector overlayLayer = new ol.layer.Vector(overlayLayerOptions);
    overlayLayer.setStyle(getBAGHighlightStyle());

    // create a vector layer
    final Vector vectorSource = new Vector();
    final VectorLayerOptions vectorLayerOptions = new VectorLayerOptions();
    vectorLayerOptions.setSource(vectorSource);
    final ol.layer.Vector wfsLayer = new ol.layer.Vector(vectorLayerOptions);

    wfsLayer.setStyleFunction(feature -> {
      if (isInterest(feature.getId())) {
        return new Style[] { getBAGInterestStyle() };
      } else {
        return new Style[] { getBAGStyle() };
      }
    });

    // create a view
    final Wfs wfs = new Wfs();

    final GWTAtomicInteger counter = new GWTAtomicInteger(0);

    // create WFS-XML node
    final RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, "https://geodata.nationaalgeoregister.nl/bag/wfs");

    map.addMoveEndListener(e -> getFeatures(map, vectorSource, epsg, requestBuilder, wfs, counter));

    final LayerInfo info = new LayerInfo();
    info.setTitle("Actueel_ortho25_wfs");

    map.addPointerMoveListener(e -> {
      final Feature feature = map.forEachFeatureAtPixel(e.getPixel(), f -> f);

      if (feature == null) {
        nothing(map, overlaySource);
      } else {
        highlightFeature(map, overlaySource, feature);
      }
    });

    map.addClickListener(e -> {
      final Feature feature = map.forEachFeatureAtPixel(e.getPixel(), f -> f);

      if (feature != null) {
        selectFeature(feature);
      }
    });

    getFeatures(map, vectorSource, epsg, requestBuilder, wfs, counter);

    return new OL3Layer[] { wrap(wfsLayer, info), wrap(overlayLayer, info) };
  }

  public static void loadInfrastructureLayers() {
    GeoJsonRetrievalUtil.getGeoJson("res/json/HWN.geojson", f -> addLayer("HWN", f, getHwnStyle()));
    GeoJsonRetrievalUtil.getGeoJson("res/json/OWN.geojson", f -> addLayer("OWN", f, getOwnStyle()));
    GeoJsonRetrievalUtil.getGeoJson("res/json/puntbronnen.geojson", f -> addLayer("puntbronnen", f, getPuntBronStyle()));
    GeoJsonRetrievalUtil.getGeoJson("res/json/schermen.geojson", f -> addLayer("schermen", f, getSchermenStyle()));
    GeoJsonRetrievalUtil.getGeoJson("res/json/spoorbaan.geojson", f -> addLayer("spoorbaan", f, getSpoorbaanStyle()));
  }


  private static Style getHwnStyle() {
    final StrokeOptions strokeOptions = OLFactory.createOptions();
    strokeOptions.setColor(new Color(225, 113, 13, 1f));
    strokeOptions.setWidth(4);
    
    final StyleOptions options = OLFactory.createOptions();
    options.setStroke(new Stroke(strokeOptions));

    return new Style(options);
  }

  private static Style getOwnStyle() {
    final StrokeOptions strokeOptions = OLFactory.createOptions();
    strokeOptions.setColor(new Color(130, 247, 253, 1f));
    strokeOptions.setWidth(4);

    final StyleOptions options = OLFactory.createOptions();
    options.setStroke(new Stroke(strokeOptions));

    return new Style(options);
  }

  private static Style getSpoorbaanStyle() {
    final StrokeOptions strokeOptions = OLFactory.createOptions();
    strokeOptions.setColor(new Color(204, 204, 0, 1f));
    strokeOptions.setWidth(4);

    final StyleOptions options = OLFactory.createOptions();
    options.setStroke(new Stroke(strokeOptions));

    return new Style(options);
  }

  private static Style getSchermenStyle() {
    final StrokeOptions strokeOptions = OLFactory.createOptions();
    strokeOptions.setColor(new Color(236, 95, 232, 1f));
    strokeOptions.setWidth(4);

    final StyleOptions options = OLFactory.createOptions();
    options.setStroke(new Stroke(strokeOptions));

    return new Style(options);
  }

  private static Style getPuntBronStyle() {
    final StrokeOptions strokeOptions = OLFactory.createOptions();
    strokeOptions.setColor(new Color(204, 0, 52, 1f));
    strokeOptions.setWidth(4);

    final StyleOptions options = OLFactory.createOptions();
    options.setStroke(new Stroke(strokeOptions));

    return new Style(options);
  }

  private static void addLayer(String name, Feature[] f, Style style) {
    final Vector lyrSource = new Vector();
    final VectorLayerOptions lyrOptions = new VectorLayerOptions();
    lyrOptions.setSource(lyrSource);
    final ol.layer.Vector layer = new ol.layer.Vector(lyrOptions);
    layer.setStyle(style);

    lyrSource.addFeatures(f);

    final LayerInfo info = new LayerInfo();
    info.setTitle(name);

    OL3Layer lyr = wrap(layer, info);
    eventBus.fireEvent(new LayerAddedCommand(lyr));
  }

  private static void selectFeature(Feature feature) {}

  private static void nothing(final Map map, final Vector overlaySource) {
    if (overlaySource.getFeatures().length > 0) {
      overlaySource.clear(true);
      map.getTargetElement().getStyle().clearCursor();
      previousFeature = null;
    }
  }

  private static void highlightFeature(final Map map, final Vector overlaySource, final Feature f) {
    if (previousFeature != null) {
      if (previousFeature.equals(f)) {
        return;
      }
      overlaySource.removeFeature(previousFeature);
    }

    map.getTargetElement().getStyle().setCursor(Cursor.POINTER);
    overlaySource.addFeature(f);
    previousFeature = f;
  }

  private static void getFeatures(final Map map, final Vector vectorSource, final EPSG epsg, final RequestBuilder requestBuilder, final Wfs wfs,
      final GWTAtomicInteger counter) {
    if (map.getView().getZoom() < MIN_WFS_ZOOM) {
      vectorSource.clear(true);
      return;
    }

    final WfsWriteFeatureOptions getNumberOfFeaturesOptions = getWfsWriteFeatureOptions(map, epsg);

    final Filter filter = new Intersects("geometrie", Polygon.fromExtent(map.getView().calculateExtent(map.getSize())), epsg.getEpsgCode());
    getNumberOfFeaturesOptions.setFilter(filter);
    final Node wfsNode = wfs.writeGetFeature(getNumberOfFeaturesOptions);
    requestBuilder.setRequestData(new XMLSerializer().serializeToString(wfsNode));

    final int latest = counter.incrementAndGet();
    requestBuilder.setCallback(new RequestCallback() {
      @Override
      public void onResponseReceived(final Request request, final Response response) {
        final String resp = response.getText();

        final int start = resp.indexOf("\"totalFeatures\"") + 16;

        final int numberOfFeatures = Integer.parseInt(resp.substring(start, resp.indexOf(",", start)));

        final WfsWriteFeatureOptions getFeaturesOptions = getWfsWriteFeatureOptions(map, epsg);
        final Filter filter = new Intersects("geometrie", Polygon.fromExtent(map.getView().calculateExtent(map.getSize())), epsg.getEpsgCode());
        getFeaturesOptions.setFilter(filter);
        final Node wfsNode = wfs.writeGetFeature(getFeaturesOptions);
        requestBuilder.setRequestData(new XMLSerializer().serializeToString(wfsNode));
        GWT.log("Got " + numberOfFeatures + " features.");
        for (int i = 0; i < Math.min(numberOfFeatures, MAX_DRAW_FEATURES); i += MAX_FEATURE_BATCH) {
          GWT.log("Getting: " + i + " > " + (i + MAX_FEATURE_BATCH));
          getFeatures(map, getFeaturesOptions, vectorSource, epsg, requestBuilder, wfs, counter, latest, i, MAX_FEATURE_BATCH);
        }
      }

      @Override
      public void onError(final Request request, final Throwable exception) {

      }
    });

    try {
      requestBuilder.send();
    } catch (final RequestException ex) {
      Window.alert(ex.getMessage());
    }
  }

  private static WfsWriteFeatureOptions getWfsWriteFeatureOptions(final Map map, final EPSG epsg) {
    final WfsWriteFeatureOptions wfsWriteFeatureOptions = new WfsWriteFeatureOptions();
    final String[] featureTypes = { "pand" };
    wfsWriteFeatureOptions.setSrsName(epsg.getEpsgCode());
    wfsWriteFeatureOptions.setFeaturePrefix("bag");
    wfsWriteFeatureOptions.setFeatureNS("wfs");
    wfsWriteFeatureOptions.setFeatureTypes(featureTypes);
    wfsWriteFeatureOptions.setOutputFormat("application/json");

    return wfsWriteFeatureOptions;
  }

  private static void getFeatures(final Map map, final WfsWriteFeatureOptions wfsWriteFeatureOptions, final Vector vectorSource, final EPSG epsg,
      final RequestBuilder requestBuilder, final Wfs wfs, final GWTAtomicInteger counter, final int latest, final int start, final int count) {
    try {

      final Filter filter = new Intersects("geometrie", Polygon.fromExtent(map.getView().calculateExtent(map.getSize())), epsg.getEpsgCode());
      wfsWriteFeatureOptions.setFilter(filter);
      wfsWriteFeatureOptions.setCount(count);
      wfsWriteFeatureOptions.setStartIndex(start);

      final Node wfsNode = wfs.writeGetFeature(wfsWriteFeatureOptions);
      requestBuilder.setRequestData(new XMLSerializer().serializeToString(wfsNode));

      requestBuilder.setCallback(new MapRedrawRequestCallback(vectorSource, latest, counter));

      requestBuilder.send();
    } catch (final RequestException ex) {
      Window.alert(ex.getMessage());
    }
  }

  private static Style getBAGStyle() {
    final FillOptions fillOptions = OLFactory.createOptions();
    fillOptions.setColor(new Color(255, 255, 255, 0.5f));

    final StrokeOptions strokeOptions = OLFactory.createOptions();
    strokeOptions.setColor(new Color(57, 70, 70, 0.9f));
    strokeOptions.setWidth(2);

    final StyleOptions options = OLFactory.createOptions();
    options.setFill(new Fill(fillOptions));
    options.setStroke(new Stroke(strokeOptions));
    options.setZIndex(100);

    final Style style = new Style(options);

    return style;
  }

  private static Style getBAGInterestStyle() {
    final FillOptions fillOptions = OLFactory.createOptions();
    fillOptions.setColor(new Color(255, 255, 255, 0.5f));

    final StrokeOptions strokeOptions = OLFactory.createOptions();
    strokeOptions.setColor(new Color(122, 162, 214, 1f));
    strokeOptions.setWidth(4);

    final StyleOptions options = OLFactory.createOptions();
    options.setFill(new Fill(fillOptions));
    options.setStroke(new Stroke(strokeOptions));
    options.setZIndex(1000);

    final Style style = new Style(options);

    return style;
  }

  private static Style getBAGHighlightStyle() {
    final FillOptions fillOptions = OLFactory.createOptions();
    fillOptions.setColor(new Color(255, 255, 255, 0.5f));

    final StrokeOptions strokeOptions = OLFactory.createOptions();
    strokeOptions.setColor(new Color(225, 113, 13, 1f));
    strokeOptions.setWidth(4);

    final StyleOptions options = OLFactory.createOptions();
    options.setFill(new Fill(fillOptions));
    options.setStroke(new Stroke(strokeOptions));
    options.setZIndex(1000);

    final Style style = new Style(options);

    return style;
  }

  public static IsLayer<Layer> prepareBAGLayer(final Map map, final Projection projection, final EPSG epsg) {
    final ImageWmsParams imageWMSParams = OLFactory.createOptions();
    imageWMSParams.setLayers("pand");

    final ImageWmsOptions imageWMSOptions = OLFactory.createOptions();
    // https://geodata.nationaalgeoregister.nl/bag/wms?request=GetCapabilities
    imageWMSOptions.setUrl("https://geodata.nationaalgeoregister.nl/bag/wms");
    imageWMSOptions.setParams(imageWMSParams);
    imageWMSOptions.setRatio(1.5f);

    final ImageWms imageWMSSource = new ImageWms(imageWMSOptions);

    final LayerOptions layerOptions = OLFactory.createOptions();
    layerOptions.setSource(imageWMSSource);

    final Image wmsLayer = new Image(layerOptions);
    wmsLayer.setOpacity(1);

    final LayerInfo info = new LayerInfo();
    info.setTitle("BAG");

    return wrap(wmsLayer, info);
  }

  public static IsLayer<Layer> prepareBaseLayer(final Map map, final Projection projection, final EPSG epsg, EventBus eventBus) {
    MapUtil.map = map;
    MapUtil.epsg = epsg;
    MapUtil.eventBus = eventBus;
    final WmtsOptions wmtsOptions = OLFactory.createOptions();
    // https://geodata.nationaalgeoregister.nl/tiles/service/wmts?request=GetCapabilities&service=WMTS
    wmtsOptions.setUrl("https://geodata.nationaalgeoregister.nl/luchtfoto/rgb/wmts");
    wmtsOptions.setLayer("Actueel_ortho25");
    wmtsOptions.setFormat("image/jpeg");
    wmtsOptions.setMatrixSet(epsg.getEpsgCode());
    wmtsOptions.setStyle("default");
    wmtsOptions.setProjection(projection);
    wmtsOptions.setWrapX(true);
    wmtsOptions.setTileGrid(createWmtsTileGrid(projection));

    final Wmts wmtsSource = new Wmts(wmtsOptions);

    final LayerOptions wmtsLayerOptions = OLFactory.createOptions();
    wmtsLayerOptions.setSource(wmtsSource);

    final Tile wmtsLayer = new Tile(wmtsLayerOptions);
    wmtsLayer.setOpacity(1);

    final LayerInfo info = new LayerInfo();
    info.setTitle("Actueel_ortho25");

    return wrap(wmtsLayer, info);
  }

  public static void prepareControls(final Map map) {
    // add some controls
    map.addControl(new Zoom());
    map.addControl(new ZoomSlider());
    map.addControl(new ScaleLine());
    // map.addControl(new OverviewMap());
    final MousePosition mousePosition = new MousePosition();
    mousePosition.setCoordinateFormat(Coordinate.createStringXY(0));
    map.addControl(mousePosition);
  }

  public static void prepareEPSG(final EPSG epsg) {
    final BBox bnds = epsg.getBounds();
    final ProjectionOptions options = new ProjectionOptions();
    options.setCode(epsg.getEpsgCode());
    options.setUnits(epsg.getUnit());
    options.setExtent(new Extent(bnds.getMinX(), bnds.getMinY(), bnds.getMaxX(), bnds.getMaxY()));
    Projection.addProjection(new Projection(options));
  }

  public static void prepareInformationLayer(final Map map, final Projection projection, final EventBus eventBus, final ReceptorUtil receptorUtil) {
    OLUtil.observe(map, CLICK_EVENT_NAME, (final MapBrowserEvent e) -> onInfoChange(eventBus, e.getCoordinate(), receptorUtil));

    final InformationLayer layer = new InformationLayer(projection, eventBus);

    map.addLayer(layer.asLayer());
  }

  private static void onInfoChange(final EventBus eventBus, final Coordinate coordinate, final ReceptorUtil receptorUtil) {
    final Point point = new Point(coordinate.getX(), coordinate.getY());

    eventBus.fireEvent(new InfoLocationChangeCommand(receptorUtil.createReceptorIdFromPoint(point)));
  }

  public static OL3Layer wrap(final Layer layer) {
    return new OL3Layer(layer);
  }

  public static OL3Layer wrap(final Layer layer, final LayerInfo info) {
    return new OL3Layer(layer, info);
  }

  public static void setBaseMapUid(final String baseMapUid) {
    MapUtil.baseMapUid = baseMapUid;
  }

  public static String getBaseMapUid() {
    return baseMapUid;
  }
}
