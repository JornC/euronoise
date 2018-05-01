package nl.overheid.aerius.geo.event;

import java.util.HashSet;
import java.util.Set;

import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.Event.Type;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.web.bindery.event.shared.SimpleEventBus;

public class MapEventBus extends SimpleEventBus {
  private final EventBus wrapped;
  private final Set<HandlerRegistration> registrations = new HashSet<HandlerRegistration>();
  private String scopeId;

  public MapEventBus(final EventBus wrappedBus) {
    this.wrapped = wrappedBus;
  }

  public MapEventBus(final EventBus wrappedBus, final String scopeId) {
    this.wrapped = wrappedBus;
    this.scopeId = scopeId;
  }

  @Override
  public <H> HandlerRegistration addHandler(final Type<H> type, final H handler) {
    final HandlerRegistration rtn = wrapped.addHandlerToSource(type, getScopeId(), handler);
    return doRegisterHandler(rtn);
  }

  @Override
  public <H> HandlerRegistration addHandlerToSource(final Event.Type<H> type, final Object source, final H handler) {
    final HandlerRegistration rtn = wrapped.addHandlerToSource(type, source, handler);
    return doRegisterHandler(rtn);
  }

  @Override
  public void fireEvent(final Event<?> event) {
    wrapped.fireEventFromSource(event, getScopeId());
  }

  @Override
  public void fireEventFromSource(final Event<?> event, final Object source) {
    wrapped.fireEventFromSource(event, source);
  }

  /**
   * Visible for testing.
   */
  protected int getRegistrationSize() {
    return registrations.size();
  }

  private HandlerRegistration doRegisterHandler(final HandlerRegistration registration) {
    registrations.add(registration);
    return () -> doUnregisterHandler(registration);
  }

  private void doUnregisterHandler(final HandlerRegistration registration) {
    if (registrations.contains(registration)) {
      registration.removeHandler();
      registrations.remove(registration);
    }
  }

  public void setScopeId(final String scopeId) {
    this.scopeId = scopeId;
  }

  public String getScopeId() {
    return scopeId;
  }
}