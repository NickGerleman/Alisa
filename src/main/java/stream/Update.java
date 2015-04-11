package stream;

import com.google.gson.Gson;

/**
 * Abstract class for updates streamed
 */
public abstract class Update {
    public String toJson() {
        return new Gson().toJson(this);
    }
}
