package nl.yogh.aerius.wui.euronoise;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.assistedinject.GinFactoryModuleBuilder;
import com.google.gwt.place.shared.PlaceHistoryHandler.Historian;

import nl.overheid.aerius.geo.epsg.EPSG;
import nl.overheid.aerius.geo.epsg.EPSGRDNew;
import nl.overheid.aerius.geo.epsg.RDNewReceptorGridSettings;
import nl.overheid.aerius.geo.epsg.ReceptorGridSettings;
import nl.yogh.aerius.wui.euronoise.history.EuroNoisePlaceHistoryMapper;
import nl.yogh.aerius.wui.euronoise.place.StartPlace;
import nl.yogh.gwt.wui.activity.ActivityMapper;
import nl.yogh.gwt.wui.history.HTML5Historian;
import nl.yogh.gwt.wui.history.PlaceHistoryMapper;
import nl.yogh.gwt.wui.place.ApplicationPlace;
import nl.yogh.gwt.wui.place.DefaultPlace;

public class EuroNoiseClientModule extends AbstractGinModule {
  @Override
  protected void configure() {
    bind(ApplicationPlace.class).annotatedWith(DefaultPlace.class).to(StartPlace.class);
    bind(Historian.class).to(HTML5Historian.class);

    // Geo bindings
    bind(EPSG.class).to(EPSGRDNew.class);
    bind(ReceptorGridSettings.class).to(RDNewReceptorGridSettings.class);

    // Bind components
    bind(ActivityMapper.class).to(EuroNoiseActivityMapper.class);
    bind(PlaceHistoryMapper.class).to(EuroNoisePlaceHistoryMapper.class);

    install(new GinFactoryModuleBuilder().build(EuroNoiseActivityFactory.class));
  }
}
