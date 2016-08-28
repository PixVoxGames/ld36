package com.pixvoxsoftware.ld35.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.pixvoxsoftware.ld35.GameWorld;
import com.pixvoxsoftware.ld35.WorldConstants;
import com.pixvoxsoftware.ld35.controllers.EntityController;

public abstract class Entity {
    protected boolean visible = true;
    protected EntityController controller;
    protected Sprite sprite;
    protected boolean killed = false;

    private Rectangle bounds = new Rectangle();

    public GameWorld world;
    public Body physicsBody;

    public int groundedChecks = 0;
    private boolean selected = false;

    public boolean isKilled() {
        return killed;
    }

    public void kill() {
        killed = true;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public void setController(EntityController controller) {
        this.controller = controller;
    }

    public EntityController getController() {
        return controller;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public GameWorld getWorld() {
        return world;
    }

    public void setWorld(GameWorld world) {
        this.world = world;
    }

    public void setPosition(float x, float y) {
        physicsBody.setTransform(x, y, 0);
    }

    public Vector2 getPosition() {
        return physicsBody.getTransform().getPosition();
    }

    public float getX() {
        return physicsBody.getTransform().getPosition().x;
    }

    public float getY() {
        return physicsBody.getTransform().getPosition().y;
    }

    public void createPhysicsBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        physicsBody = world.physicsWorld.createBody(bodyDef);

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(sprite.getWidth() / 2, sprite.getHeight() / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.density = 0.000001f;
        fixtureDef.friction = 0;
        fixtureDef.restitution = 0;
        fixtureDef.filter.maskBits = getCollisionMask();
        fixtureDef.filter.categoryBits = getCategory();

        Fixture fixture = physicsBody.createFixture(fixtureDef);
        fixture.setUserData(this);
        polygonShape.dispose();

        PolygonShape footShape = new PolygonShape();
        Vector2 offset = new Vector2(0, (-sprite.getHeight() / 2 - 2f / WorldConstants.PIXELS_PER_METER));
        footShape.setAsBox(0.9f * sprite.getWidth() / 2, 2 / WorldConstants.PIXELS_PER_METER, offset, 0);

        FixtureDef footFixtureDef = new FixtureDef();
        footFixtureDef.shape = footShape;
        footFixtureDef.density = 0;
        footFixtureDef.friction = 0;
        footFixtureDef.restitution = 0;
        footFixtureDef.isSensor = true;

        fixture = physicsBody.createFixture(footFixtureDef);
        fixture.setUserData(this);

        footShape.dispose();


        physicsBody.setUserData(this);
    }

    public boolean isOnGround() {
        return groundedChecks > 0;
    }

    public Rectangle getBoundingRectangle() {
        bounds.set(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
        return bounds;
    }

    public abstract boolean isGround();
    public abstract short getCategory();
    public abstract short getCollisionMask();
    public abstract int renderPass();

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public abstract String getName();
}
