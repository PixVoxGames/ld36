package com.pixvoxsoftware.ld35.controllers;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.pixvoxsoftware.ld35.WorldConstants;
import com.pixvoxsoftware.ld35.entities.Entity;

public class EntityController {
    public static EntityController instance = new EntityController();

    public void act(Entity entity) {
        if (entity.physicsBody != null) {
            Vector2 position = entity.physicsBody.getTransform().getPosition();
            Sprite sprite = entity.getSprite();
            sprite.setPosition(position.x - sprite.getWidth() / 2, position.y - sprite.getHeight() / 2);
        }
    };

    public boolean onKeyPressed(Entity entity, int keycode) {
        return false;
    }
    public boolean onKeyReleased(Entity entity, int keycode) {
        return false;
    }
}
