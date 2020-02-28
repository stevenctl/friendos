package co.sugarware.friendos.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import static co.sugarware.friendos.entities.collisions.Collisions.PLAYER_CATEGORY;

public class HumanoidEnemy extends Humanoid implements ContactListener {

    public static final String SKELETON_SPRITE= "sprites/skeleton.json";

    public HumanoidEnemy(Vector2 startPosition, String sprite) {
        super(startPosition, sprite);
    }

    @Override
    public void setupPhysics(World world) {
        super.setupPhysics(world);
    }

    @Override
    public void beginContact(Contact contact) {
        boolean aSelf = contact.getFixtureA().getBody().getUserData() == this;
        Fixture foreign = aSelf ? contact.getFixtureB() : contact.getFixtureA();
        if (foreign.getFilterData().categoryBits == PLAYER_CATEGORY) {
            attack();
        }
    }

    private void attack() {
        // TODO
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
