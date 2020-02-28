package co.sugarware.friendos.entities.collisions;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import lombok.Setter;

public class SimpleContactListener implements ContactListener {

    private int contacts;

    @Setter
    private ContactListener next;

    public void beginContact(Contact contact) {
        contacts++;
        if (next != null) {
            next.beginContact(contact);
        }
    }

    public void endContact(Contact contact) {
        contacts--;
        if (next != null) {
            next.endContact(contact);
        }
    }

    public void preSolve(Contact contact, Manifold oldManifold) {
        if (next != null) {
            next.preSolve(contact, oldManifold);
        }
    }

    public void postSolve(Contact contact, ContactImpulse impulse) {
        if (next != null) {
            next.postSolve(contact, impulse);
        }
    }

    public boolean hasContact() {
        return contacts > 0;
    }

}
