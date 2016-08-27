package com.pixvoxgames.ld36;

public abstract class Entity {

    public abstract boolean onPickup();
    public abstract void onLookAt();
    public abstract String getHint();

    public abstract boolean isPickeable();
}
