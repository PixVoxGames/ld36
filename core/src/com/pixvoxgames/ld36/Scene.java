package com.pixvoxgames.ld36;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;

public class Scene implements Json.Serializable{

    static final String BACKGROUND_FIELD = "background";

    private String mBackground;
    private ArrayList<Entity> mEntities;
    private ArrayList<RailPoint> mRailPoints;

    public String getBackground() {
        return mBackground;
    }

    public void setBackground(String background) {
        mBackground = background;
    }

    public ArrayList<Entity> getEntities() {
        return mEntities;
    }

    public ArrayList<RailPoint> getRailPoints() {
        return mRailPoints;
    }

    public Scene() {
        mEntities = new ArrayList<Entity>();
        mRailPoints = new ArrayList<RailPoint>();
    }

    @Override
    public void write(Json json) {
        json.writeValue(BACKGROUND_FIELD, mBackground);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        mBackground = jsonData.get(BACKGROUND_FIELD).asString();
    }
}
