package stream;

import model.JsonModel;

/**
 * Created by nick on 4/11/15.
 */
public class LocationUpdate extends JsonModel {
    private String type = "location";
    private final int bot;
    private final double latitude;
    private final double longitude;

    public LocationUpdate(int botId, double latitude, double longitude) {
        this.bot = botId;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
