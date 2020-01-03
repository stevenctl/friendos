package co.sugarware.friendos.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public interface PhysicsObject {

    Vector2 getPosition();

    void setupPhysics(World world);

}
