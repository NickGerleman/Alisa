package model;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * Abstract class for updates streamed
 */
public abstract class JsonModel {
    public String toJson() {
        return new Gson().toJson(this);
    }

    public JsonElement toJsonTree() {
        return new Gson().toJsonTree(this);
    }
}
