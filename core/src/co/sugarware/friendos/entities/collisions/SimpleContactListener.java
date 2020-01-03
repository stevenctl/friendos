package co.sugarware.friendos.entities.collisions;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

public class SimpleContactListener implements ContactListener {

    private int contacts;

    public void beginContact(Contact contact) {
        contacts++;
    }

    public void endContact(Contact contact) {
        contacts--;
    }

    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    public void postSolve(Contact contact, ContactImpulse impulse) {
    }

    public boolean hasContact() {
        return contacts > 0;
    }
}
