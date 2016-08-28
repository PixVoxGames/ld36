package com.pixvoxsoftware.ld35.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.pixvoxsoftware.ld35.AnimatedSprite;
import com.pixvoxsoftware.ld35.Loggers;
import com.pixvoxsoftware.ld35.WorldConstants;
import com.pixvoxsoftware.ld35.entities.Entity;
import com.pixvoxsoftware.ld35.entities.Player;


public class PlayerController extends EntityController {
    private EntityController consumedSoulController;
    private Sprite playerSprite;
    private Entity consumingSoul = null;
    private boolean isSpittingSoul = false;
    private boolean isPlayingSpittingAnimation = false;

    private enum ConsumingAnimationState {
        PLAYING,
        NOT_PLAYING;
    }
    private ConsumingAnimationState consumingAnimationState = ConsumingAnimationState.NOT_PLAYING;

    @Override
    public void act(Entity entity) {
        super.act(entity);
        if (entity instanceof Player) {
            Player player = (Player) entity;
            Entity consumedSoul = player.getConsumedSoul();
            Body physicsBody;
            if (consumedSoul != null) {
                // now consumed soul sprite == player sprite
                physicsBody = consumedSoul.physicsBody;
            } else {
                physicsBody = player.physicsBody;
            }

            if (consumingSoul != null) {
                Vector2 playerPosition = player.getPosition();
                Vector2 consumingSoulPosition = consumingSoul.getPosition();
                Vector2 direction = new Vector2(consumingSoulPosition.x - playerPosition.x, consumingSoulPosition.y - consumingSoulPosition.y);
                if (consumingAnimationState == ConsumingAnimationState.PLAYING) {
                    physicsBody.applyLinearImpulse(physicsBody.getLinearVelocity().scl(-physicsBody.getMass()), physicsBody.getWorldCenter(), true);
                    AnimatedSprite sprite = (AnimatedSprite) player.getSprite();
                    if (sprite.isFinishedAnimation()) {
                        player.setSprite(playerSprite);
                        playerSprite = null;
                        consumingAnimationState = ConsumingAnimationState.NOT_PLAYING;
                        consumeSoul(player, consumingSoul);
                        consumingSoul = null;
                    }
                } else if (consumingAnimationState == ConsumingAnimationState.NOT_PLAYING && direction.len() <= WorldConstants.MIN_CONSUMING_DISTANCE) {
                    physicsBody.applyLinearImpulse(physicsBody.getLinearVelocity().scl(-physicsBody.getMass()), physicsBody.getWorldCenter(), true);
                    AnimatedSprite sprite = new AnimatedSprite(Gdx.files.internal("gg_w_consuming.png"), 15, 0.02f);
                    sprite.setNoRepeat();
                    playerSprite = player.getSprite();
                    sprite.setPosition(playerSprite.getX(), playerSprite.getY());
                    player.setSprite(sprite);
                    consumingAnimationState = ConsumingAnimationState.PLAYING;
                } else {
                    direction = direction.nor().scl(WorldConstants.PLAYER_CONSUMING_SPEED);
                    if (player.getSprite() instanceof AnimatedSprite) {
                        AnimatedSprite sprite = (AnimatedSprite) player.getSprite();
                        if (direction.x < 0) {
                            sprite.setMirroredVertically(true);
                        } else {
                            sprite.setMirroredVertically(false);
                        }
                    }
                    Vector2 impulse = direction.sub(physicsBody.getLinearVelocity()).scl(physicsBody.getMass());
                    physicsBody.applyLinearImpulse(impulse, physicsBody.getWorldCenter(), true);
                }
            } else if (isSpittingSoul) {
                if (isPlayingSpittingAnimation) {
                    AnimatedSprite sprite = (AnimatedSprite) player.getSprite();
                    if (sprite.isFinishedAnimation()) {
                        spitSoul(player);
                        isPlayingSpittingAnimation = false;
                        isSpittingSoul = false;
                    }
                }
            } else {
                if (player.getSprite() instanceof AnimatedSprite) {
                    AnimatedSprite sprite = (AnimatedSprite) player.getSprite();
                    if (player.getDirection() == Player.DIRECTION_LEFT) {
                        sprite.setMirroredVertically(true);
                    } else {
                        sprite.setMirroredVertically(false);
                    }
                }

                // movement
                if (physicsBody != null) {
                    if (player.getState() == Player.State.MOVE) {
                        float impulseX = physicsBody.getMass() * (WorldConstants.PLAYER_MAX_X_VELOCITY * player.getDirection() - physicsBody.getLinearVelocity().x);
                        physicsBody.applyLinearImpulse(new Vector2(impulseX, 0), physicsBody.getWorldCenter(), true);
                    } else {
                        physicsBody.applyLinearImpulse(new Vector2(-physicsBody.getLinearVelocity().x * physicsBody.getMass(), 0), physicsBody.getWorldCenter(), true);
                    }
                    if (player.isJumping) {
                        float impulseY = physicsBody.getMass() * (WorldConstants.PLAYER_MAX_Y_VELOCITY - physicsBody.getLinearVelocity().y);
                        physicsBody.applyLinearImpulse(new Vector2(0, impulseY), physicsBody.getWorldCenter(), true);
                    }
                }
            }
        } else {
            // it's consumed soul, what we need to do here?
        }
    }

    public boolean onTouchDown(Player player, float worldX, float worldY, int pointer, int button) {
        Entity targetEntity = null;
        for (Entity entity : player.world.getEntities()) {
            if (entity.getBoundingRectangle().contains(worldX, worldY) && !player.equals(entity)) {
                targetEntity = entity;
                break;
            }
        }
        if (targetEntity != null && player.getConsumedSoul() == null && canConsumeSoul(player, targetEntity)) {
            consumingSoul = targetEntity;
        }
        return true;
    }

    private void consumeSoul(Player player, Entity entity) {
        player.setConsumedSoul(entity);
        player.setVisible(false);
        // save controller for disable actions of consumed soul
        consumedSoulController = entity.getController();
        entity.setController(this);
        playerSprite = player.getSprite();
        player.setSprite(entity.getSprite());
        player.setConsumedSoul(entity);
        player.world.physicsWorld.destroyBody(player.physicsBody);
        player.physicsBody = null;
    }

    private void spitSoul(Player player) {
        if (player.getConsumedSoul() == null) {
            return;
        }
        Entity consumedSoul = player.getConsumedSoul();

        player.setVisible(true);
        player.setSprite(playerSprite);
        player.createPhysicsBody();
        player.setPosition(consumedSoul.getX(), consumedSoul.getY());

        if (canEntitySurviveAfterSpit(consumedSoul)) {
            consumedSoul.setController(consumedSoulController);
        } else {
            consumedSoul.kill();
        }
        player.setConsumedSoul(null);
    }

    private boolean canEntitySurviveAfterSpit(Entity e) {
        return true;
    }

    private class MyRayCastCallback implements RayCastCallback {
        private int hits = 0;
        @Override
        public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
            hits += 1;
            Loggers.game.debug("hit {}", fixture);
            return 1;
        }
    }

    private boolean canConsumeSoul(Player player, Entity entity) {
        MyRayCastCallback rayCastCallback = new MyRayCastCallback();
        Loggers.game.debug("rayCastCallback: {} -> {}", player.getPosition(), entity.getPosition());
        Vector2 pointA = player.getPosition();
        Vector2 pointB = entity.getPosition();
        entity.world.physicsWorld.rayCast(rayCastCallback, pointA, pointB);
        return rayCastCallback.hits <= 1;
    }

    private void triggerConsumedSoulAction(Player player) {
    }

    private void preSpitSoul(Player player) {
        if (player.getConsumedSoul() != null) {
            AnimatedSprite sprite = new AnimatedSprite(Gdx.files.internal("gg_w_consuming.png"), 15, 0.02f);
            sprite.setNoRepeatAndReversed();
            Vector2 position = player.getConsumedSoul().getPosition();
            sprite.setPosition(position.x - playerSprite.getWidth() / 2, position.y - playerSprite.getHeight() / 2);
            player.setSprite(sprite);
            isSpittingSoul = true;
            isPlayingSpittingAnimation = true;
            player.setVisible(true);
        }
    }

    @Override
    public boolean onKeyPressed(Entity entity, int keycode) {
        if (!(entity instanceof Player)) {
            // it's consumed soul, we'll also handle Player entity later, so just skip
            return false;
        }
        Player player = (Player) entity;
        switch (keycode) {
            case Input.Keys.E:
                preSpitSoul(player);
                return true;
            case Input.Keys.Q:
                triggerConsumedSoulAction(player);
                return true;
            case Input.Keys.W:
            case Input.Keys.SPACE:
                player.isJumping = true;
                return true;

            case Input.Keys.S:
                return true;

            case Input.Keys.A:
                // Move left
                if (player.getState() == Player.State.IDLE) {
                    player.setState(Player.State.MOVE);
                    player.setDirection(Player.DIRECTION_LEFT);
                }
                return true;

            case Input.Keys.D:
                // Move right
                if (player.getState() == Player.State.IDLE) {
                    player.setState(Player.State.MOVE);
                    player.setDirection(Player.DIRECTION_RIGHT);
                }
                return true;

            default:
                break;
        }
        return false;
    }

    @Override
    public boolean onKeyReleased(Entity entity, int keycode) {
        if (!(entity instanceof Player)) {
            // it's consumed soul, we'll also handle Player entity later, so just skip
            return false;
        }
        Player player = (Player) entity;
        switch (keycode) {
            case Input.Keys.A:
                switch (player.getState()) {
                    case MOVE:
                        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                            player.setState(Player.State.MOVE);
                            player.setDirection(Player.DIRECTION_RIGHT);
                        } else {
                            player.setState(Player.State.IDLE);
                        }
                        break;
                }
                return true;

            case Input.Keys.D:
                switch (player.getState()) {
                    case MOVE:
                        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                            player.setState(Player.State.MOVE);
                            player.setDirection(Player.DIRECTION_LEFT);
                        } else {
                            player.setState(Player.State.IDLE);
                        }
                        break;
                }
                return true;
            case Input.Keys.W:
            case Input.Keys.SPACE:
                player.isJumping = false;
                return true;
        }
        return false;
    }
}
