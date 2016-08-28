package com.pixvoxsoftware.ld35;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.pixvoxsoftware.ld35.entities.Entity;

public class FollowCamera extends OrthographicCamera {
    private Entity target;
    private float minX, maxX, minY, maxY;

    public FollowCamera(float w, float h, Entity target) {
        super(w, h);
        this.target = target;
        minX = Float.MIN_VALUE;
        minY = Float.MIN_VALUE;
        maxX = Float.MAX_VALUE;
        maxY = Float.MAX_VALUE;
    }

    public Entity getTarget() {
        return target;
    }

    public void setTarget(Entity target) {
        this.target = target;
    }

    @Override
    public void update() {
        super.update();
        if (target != null) {
            // because it's a rectangle, we need to lookAt his center
            // z-coordinate doesn't matter, because of orthographic projection
            Sprite sprite = target.getSprite();
            float x = sprite.getX() + sprite.getWidth() / 2;
            float y = sprite.getY() + sprite.getHeight() / 2;

            float speed = 7f;
            float dt = Gdx.graphics.getDeltaTime();
            position.x += (x - position.x) * speed * dt;
            position.y += (y - position.y) * speed * dt;

            position.x = MathUtils.clamp(position.x, minX, maxX);
            position.y = MathUtils.clamp(position.y, minY, maxY);
        }
    }

    public void setBounds(float minX, float minY, float maxX, float maxY) {
        this.minX = minX + this.viewportWidth / 2;
        this.maxX = maxX - this.viewportWidth / 2;
        this.minY = minY + this.viewportHeight / 2;
        this.maxY = maxY - this.viewportHeight / 2;
    }
}
