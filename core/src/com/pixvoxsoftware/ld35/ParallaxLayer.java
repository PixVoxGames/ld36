package com.pixvoxsoftware.ld35;

import com.badlogic.gdx.graphics.Texture;

public class ParallaxLayer{

    private Texture texture;
    private float speed;
    private float x, y;

    public ParallaxLayer(Texture texture, float speed) {
        this.texture = texture;
        this.texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.ClampToEdge);;
        this.speed = speed;
        x = 0;
        y = 0;
    }

    public Texture getTexture() {
        return texture;
    }

    public float getSpeed() {
        return speed;
    }

    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }
}