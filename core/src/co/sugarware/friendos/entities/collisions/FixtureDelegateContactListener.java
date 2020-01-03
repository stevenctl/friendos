package co.sugarware.friendos.entities.collisions;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

// Delegates contact events to contact listeners on the fixture's user data if applicable
public class FixtureDelegateContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Object a = contact.getFixtureA().getUserData();
        if (a instanceof ContactListener) {
            ((ContactListener) a).beginContact(contact);
        }

        Object b = contact.getFixtureB().getUserData();
        if (b instanceof ContactListener) {
            ((ContactListener) b).beginContact(contact);
        }
    }

    @Override
    public void endContact(Contact contact) {
        Object a = contact.getFixtureA().getUserData();
        if (a instanceof ContactListener) {
            ((ContactListener) a).endContact(contact);
        }

        Object b = contact.getFixtureB().getUserData();
        if (b instanceof ContactListener) {
            ((ContactListener) b).endContact(contact);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        Object a = contact.getFixtureA().getUserData();
        if (a instanceof ContactListener) {
            ((ContactListener) a).preSolve(contact, oldManifold);
        }

        Object b = contact.getFixtureB().getUserData();
        if (b instanceof ContactListener) {
            ((ContactListener) b).preSolve(contact, oldManifold);
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        Object a = contact.getFixtureA().getUserData();
        if (a instanceof ContactListener) {
            ((ContactListener) a).postSolve(contact, impulse);
        }

        Object b = contact.getFixtureB().getUserData();
        if (b instanceof ContactListener) {
            ((ContactListener) b).postSolve(contact, impulse);
        }
    }
}
