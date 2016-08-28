package com.pixvoxsoftware.ld35;

import com.badlogic.gdx.graphics.OrthographicCamera;

import java.util.ArrayList;

public class ParallaxBackground {

    private ArrayList<ParallaxLayer> layers;
    private float cachedX, cachedY;

    public ParallaxBackground(final OrthographicCamera camera) {
        layers = new ArrayList<>();
        cachedX = camera.position.x;
        cachedY = camera.position.y;
    }

    public void addLayer(ParallaxLayer layer) {
        layers.add(layer);
    }

    public ArrayList<ParallaxLayer> getLayers() {
        return layers;
    }

    public void update(final OrthographicCamera camera) {
        float deltaX = camera.position.x - cachedX,
              deltaY = camera.position.y - cachedY;

        for (ParallaxLayer layer : layers) {
            layer.setX(((deltaX - layer.getX()) * layer.getSpeed()/ layer.getTexture().getWidth()) % 1f);
//            layer.setY(((layer.getY() - deltaY) * layer.getSpeed() * 0.2f/ layer.getTexture().getWidth()) % 1f);
        }
    }
}
