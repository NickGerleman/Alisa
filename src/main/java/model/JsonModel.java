package model;

import com.google.gson.Gson;

/**
 * Abstract class for updates streamed
 */
public abstract class JsonModel {
    public String toJson() {
        return new Gson().toJson(this);
    }
}
