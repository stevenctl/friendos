package co.sugarware.friendos.entities;

import co.sugarware.friendos.sprite.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import static co.sugarware.friendos.entities.collisions.Collisions.ENTITY_CATEGORY;
import static co.sugarware.friendos.entities.collisions.Collisions.ENTITY_MASK;


public class SimplePhysicsObject implements Entity, PhysicsObject {

    private static final float DENSITY = 1.0f;

    private boolean rotation;

    private final Vector2 startPosition;
    private final Vector2 size;

    private Sprite sprite;
    private Body body;
    private Shape shape;


    public SimplePhysicsObject(Sprite sprite, Vector2 startingPosition, Shape shape, Vector2 size, boolean rotation) {
        this.sprite = sprite;
        this.startPosition = startingPosition;
        this.size = size;
        this.rotation = rotation;
        this.shape = shape;
    }

    public SimplePhysicsObject(Sprite sprite, Vector2 startingPosition, Shape shape, Vector2 size) {
        this(sprite, startingPosition, shape, size, false);
    }


    public Vector2 getPosition() {
        if (body == null) {
            return null;
        }

        return body.getPosition();
    }

    public void setupPhysics(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.fixedRotation = !rotation;
        bodyDef.position.set(startPosition);

        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();

        fixtureDef.density = DENSITY;
        fixtureDef.filter.categoryBits = ENTITY_CATEGORY;
        fixtureDef.filter.maskBits = ENTITY_MASK;
        fixtureDef.shape = shape;
        shape.dispose();

        body.createFixture(fixtureDef);
        body.setBullet(true);
    }

    public void update(float delta) {
        sprite.update(delta);
        body.setLinearVelocity(body.getLinearVelocity().scl(0.75f, 1));
    }

    public void draw(SpriteBatch sb, float delta) {
        Vector2 pos = body.getPosition();
        float x = pos.x - shape.getRadius() / 2f;
        float y = pos.y - shape.getRadius() / 2f;

        sb.draw(sprite.getFrame(), x, y, 0, 0, size.x, size.y, 1f, 1f, body.getTransform().getRotation());
    }

    public void dispose() {

    }


}
