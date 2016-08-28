package com.pixvoxsoftware.ld35;

public class WorldConstants {
    public static final float PIXELS_PER_METER = 64;

    public static final float PLAYER_MAX_Y_VELOCITY = 3f;
    public static final float PLAYER_MAX_X_VELOCITY = 4f;

    public static final short OBSTACLE_CATEGORY = 0x001;
    public static final short SOUL_CATEGORY = 0x002;
    public static final short ENTITY_CATEGORY = 0x004;
    public static final short ANY_CATEGORY = 0xfff;

    public static final int MAX_RENDER_PASSES = 4;

    public static final float MIN_CONSUMING_DISTANCE = 0.5f;
    public static final float PLAYER_CONSUMING_SPEED = 60f;
    public static final float MIN_DISTANCE_TO_MERLIN = 1f;
    public static final float MIN_VELOCITY_TO_ALARM = 0.01f;
}
