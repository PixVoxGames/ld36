package com.pixvoxsoftware.ld35;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.pixvoxsoftware.ld35.entities.Entity;

public class GroundCheckContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Entity entityA = (Entity) contact.getFixtureA().getUserData();
        Entity entityB = (Entity) contact.getFixtureB().getUserData();

        if (entityA.isGround() && contact.getFixtureB().isSensor()) {
            entityB.groundedChecks += 1;
        } else if (entityB.isGround() && contact.getFixtureA().isSensor()) {
            entityA.groundedChecks += 1;
        }
    }

    @Override
    public void endContact(Contact contact) {
        Entity entityA = (Entity) contact.getFixtureA().getUserData();
        Entity entityB = (Entity) contact.getFixtureB().getUserData();

        if (entityA.isGround() && contact.getFixtureB().isSensor()) {
            entityB.groundedChecks -= 1;
        } else if (entityB.isGround() && contact.getFixtureA().isSensor()) {
            entityA.groundedChecks -= 1;
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
