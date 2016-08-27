package com.pixvoxgames.ld36;

import java.util.HashSet;
import java.util.Set;

public class RailPoint {

    public static final float RADIUS = 8;  // Editor const

    private float mX, mY;
    private Set<RailPoint> mIncidentPoints;

    public RailPoint(float x, float y) {
        mX = x;
        mY = y;
        mIncidentPoints= new HashSet<RailPoint>();
    }

    public float getX() {
        return mX;
    }

    public float getY() {
        return mY;
    }

    public Set<RailPoint> getIncidentPoints() {
        return mIncidentPoints;
    }

    public void addIncidentPoint(RailPoint point) {
        mIncidentPoints.add(point);
    }

    public boolean isHovered(float x, float y) {
        return Math.abs(mX - x) <= RADIUS &&
                Math.abs(mY - y) <= RADIUS;
    }
}
