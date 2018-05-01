package nl.yogh.aerius.wui.euronoise.history;

import com.google.gwt.core.client.GWT;

import nl.yogh.aerius.wui.euronoise.place.StartPlace;
import nl.yogh.gwt.wui.history.PlaceHistoryMapper;
import nl.yogh.gwt.wui.place.ApplicationPlace;

public class EuroNoisePlaceHistoryMapper implements PlaceHistoryMapper {
  private static final String START = "start";

  private static final String SEPERATOR = ":";

  @Override
  public String getToken(final ApplicationPlace value) {
    String token = "";

    if (value instanceof StartPlace) {
      token = START + SEPERATOR + new StartPlace.Tokenizer().getToken((StartPlace) value);
      // } else if (value instanceof PullRequestPlace) {
      // token = PULL_REQUESTS + SEPERATOR + new PullRequestPlace.Tokenizer().getToken((PullRequestPlace) value);
      // } else if (value instanceof LogPlace) {
      // token = LOGS + SEPERATOR + new LogPlace.Tokenizer().getToken((LogPlace) value);
      // } else if (value instanceof DockerPlace) {
      // token = DOCKER + SEPERATOR + new DockerPlace.Tokenizer().getToken((DockerPlace) value);
    }

    return token;
  }

  @Override
  public ApplicationPlace getPlace(final String token) {
    final String[] tokens = token.split(SEPERATOR, 2);

    ApplicationPlace place;

    GWT.log("Token split: " + tokens[0] + " >> " + tokens[1]);

    switch (tokens[0]) {
    case START:
      place = new StartPlace.Tokenizer().getPlace(tokens[1]);
      break;
    default:
      place = null;
    }

    return place;
  }
}
