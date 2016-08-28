package com.pixvoxsoftware.ld35.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.pixvoxsoftware.ld35.AnimatedSprite;
import com.pixvoxsoftware.ld35.GameWorld;
import com.pixvoxsoftware.ld35.WorldConstants;
import com.pixvoxsoftware.ld35.controllers.PlayerController;

public class Player extends Entity {

    public final static float DIRECTION_RIGHT = 1f;
    public final static float DIRECTION_LEFT = -1f;
    private Entity consumedSoul = null;
    public boolean isJumping = false;

    public enum State {
        IDLE,
        MOVE,
    }

    private float direction;
    private State state;


    public Player(GameWorld world, float x, float y) {
        this.world = world;
        sprite = new AnimatedSprite(Gdx.files.internal("gg_w.png"), 12, 0.05f);
        state = State.IDLE;
        direction = 0f;
        createPhysicsBody();
        controller = new PlayerController();
        setPosition(x, y);
    }

    public State getState() {
        return state;
    }

    public float getDirection() {
        return direction;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setDirection(float direction) {
        this.direction = direction;
    }

    public void setConsumedSoul(Entity consumedSoul) {
        this.consumedSoul = consumedSoul;
    }

    public Entity getConsumedSoul() {
        return consumedSoul;
    }

    @Override
    public boolean isGround() {
        return false;
    }

    @Override
    public short getCategory() {
        return WorldConstants.SOUL_CATEGORY;
    }

    @Override
    public void createPhysicsBody() {
        super.createPhysicsBody();
        physicsBody.setFixedRotation(true);
//        physicsBody.setGravityScale(0);
    }

    @Override
    public short getCollisionMask() {
        return WorldConstants.OBSTACLE_CATEGORY | WorldConstants.SOUL_CATEGORY;
    }

    @Override
    public int renderPass() {
        return 1;
    }

    @Override
    public String getName() {
        return "PlayerSpawn";
    }

    @Override
    public Vector2 getPosition() {
        if (consumedSoul != null) {
            return consumedSoul.getPosition();
        } else {
            return super.getPosition();
        }
    }
}
