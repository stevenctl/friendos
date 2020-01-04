package co.sugarware.friendos.entities;

import co.sugarware.friendos.entities.collisions.SimpleContactListener;
import co.sugarware.friendos.sprite.AnimatedSprite;
import co.sugarware.friendos.input.Control;
import co.sugarware.friendos.input.Controllable;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import static co.sugarware.friendos.entities.collisions.Collisions.PLAYER_CATEGORY;
import static co.sugarware.friendos.entities.collisions.Collisions.PLAYER_MASK;

public class Player implements Entity, PhysicsObject, Controllable {

    // rendering
    public static final String RAMSEY_SPRITE = "sprites/ramsey.json";
    public static final String TRENT_SPRITE = "sprites/trent.json";

    private static final int WIDTH = 20 / 2, HEIGHT = 20 / 2;
    private final AnimatedSprite sprite;

    // physics parameters
    private static final float DENSITY = 1.0f, RESTITUTION = 0.0f, DAMPING = .5f;

    // movement
    private static final float WALK_ACCEL = 10.0f, MAX_WALK_SPEED = 30.0f, JUMP_FORCE = 50;

    // box2d physics objects
    private Body body;
    private Vector2 startPosition;
    private final Vector2 impulse = new Vector2(); // reusable, just clear both dimensions before using
    private SimpleContactListener floorSensor;
    private SimpleContactListener leftSensor;
    private SimpleContactListener rightSensor;

    // walking state
    private boolean right = false, left = false;
    private boolean facingLeft;

    // jumping state
    private static final float EXTENDED_JUMP_SECONDS = 0.2f;
    private boolean jumping = false;
    private float extendedJumpTimer;
    private float climbCooldown; // used to prevent sticking to the wall when climbing

    public Player(Vector2 startPosition, String sprite) {
        this.startPosition = startPosition;
        this.sprite = new AnimatedSprite(sprite);
    }

    public void setupPhysics(World world) {

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(startPosition);

        bodyDef.linearDamping = DAMPING;
        body = world.createBody(bodyDef);
        body.setUserData(this);
        body.setFixedRotation(true);

        // Hitbox
        float hitboxWidth = WIDTH / 4f;
        float hitboxHeight = HEIGHT * (3f / 8f);

        // Main collision fixture
        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(hitboxWidth, hitboxHeight, new Vector2(0, -1), 0);
        fixtureDef.filter.categoryBits = PLAYER_CATEGORY;
        fixtureDef.filter.maskBits = PLAYER_MASK;
        fixtureDef.shape = shape;
        fixtureDef.density = DENSITY;
        fixtureDef.restitution = RESTITUTION;
        body.createFixture(fixtureDef);

        /* Sensors */
        fixtureDef.isSensor = true;

        // Floor Sensor
        shape.setAsBox(hitboxWidth - 0.2f, 0.5f, new Vector2(0, -hitboxHeight - 0.5F), 0);
        floorSensor = new SimpleContactListener();
        body.createFixture(fixtureDef).setUserData(floorSensor);

        // Side Sensors
        shape.setAsBox(0.1f, hitboxHeight, new Vector2(-hitboxWidth - 0.05f, 0), 0);
        leftSensor = new SimpleContactListener();
        body.createFixture(fixtureDef).setUserData(leftSensor);

        shape.setAsBox(0.1f, hitboxHeight, new Vector2(hitboxWidth + 0.05f, 0), 0);
        rightSensor = new SimpleContactListener();
        body.createFixture(fixtureDef).setUserData(rightSensor);


        shape.dispose();
    }

    public Vector2 getPosition() {
        if (body == null) {
            return null;
        }

        return body.getPosition();
    }

    private boolean onFloor() {
        return floorSensor.hasContact();
    }

    private boolean onRightWall() {
        return right && rightSensor.hasContact();
    }

    private boolean onLeftWall() {
        return left && leftSensor.hasContact();
    }

    private void beginJump() {
        if (!(onLeftWall() || onRightWall() || onFloor())) {
            return;
        }

        jumping = true;
        extendedJumpTimer = EXTENDED_JUMP_SECONDS;
    }

    private void die() {
        this.body.setTransform(startPosition, 0);
    }

    public void update(float delta) {
        // Walking logic
        if (climbCooldown > 0) {
            climbCooldown -= delta;
        } else if (left || right) {
            impulse.set(right ? WALK_ACCEL : -WALK_ACCEL, 0).scl(body.getMass());
            impulse.scl(body.getMass());
            body.applyLinearImpulse(impulse, this.body.getPosition(), true);
        } else if (!jumping) {
            // when neither key is press, dampen horizontal motion more strongly
            body.setLinearVelocity(body.getLinearVelocity().scl(0.75f, 1));
        }

        // Jumping logic
        if (jumping) {
            if (extendedJumpTimer > 0) {
                // Wall jump
                boolean onWall = onLeftWall() || onRightWall();
                boolean firstJumpFrame = extendedJumpTimer == EXTENDED_JUMP_SECONDS;
                float xMult = 1;
                if (firstJumpFrame && onWall) {
                    climbCooldown = EXTENDED_JUMP_SECONDS;
                    xMult = -2;
                }

                impulse.set(xMult * body.getLinearVelocity().x, JUMP_FORCE);
                body.setLinearVelocity(impulse);
            } else {
                jumping = false;
            }
            extendedJumpTimer -= delta;
        }

        // Snappy-Jump/Falling
        float vVel = body.getLinearVelocity().y;
        if (vVel < 0) {
            body.setGravityScale(1.75f);
        } else {
            body.setGravityScale(1.0f);
        }

        // Fall Death
        if (getPosition().y < 0) {
            die();
        }

        // Walk speed limiting
        float hVel = body.getLinearVelocity().x;
        if (hVel > MAX_WALK_SPEED) {
            hVel = MAX_WALK_SPEED;
        } else if (hVel < -MAX_WALK_SPEED) {
            hVel = -MAX_WALK_SPEED;
        }
        body.setLinearVelocity(hVel, body.getLinearVelocity().y);
    }

    public void draw(SpriteBatch sb, float delta) {
        Vector2 pos = getPosition();
        if (pos == null) {
            return;
        }

        if (body.getLinearVelocity().x < -2) {
            facingLeft = true;
        } else if (body.getLinearVelocity().x > 2) {
            facingLeft = false;
        }

        // Animation logic
        if ((onLeftWall() || onRightWall()) && !onFloor()) {
            sprite.setTag("hang"); // wall hang
            facingLeft = onRightWall();
        } else if (Math.abs(body.getLinearVelocity().y) > 1) {
            sprite.setTag("jump"); // jump
        } else if (Math.abs(body.getLinearVelocity().x) > 2) {
            sprite.setTag("run"); // move
        } else {
            sprite.setTag("idle"); // idle
        }

        float x = pos.x - WIDTH / 2f;
        float y = pos.y - HEIGHT / 2f;
        if (facingLeft) {
            x += WIDTH;
        }
        sprite.update(delta);

        sb.draw(sprite.getFrame(), x, y, facingLeft ? -WIDTH : WIDTH, HEIGHT);
    }

    public boolean controlStart(Control control) {
        switch (control) {
            case Right:
                right = true;
                return true;
            case Left:
                left = true;
                return true;
            case Jump:
                beginJump();
        }
        return false;
    }

    public boolean controlStop(Control control) {
        switch (control) {
            case Right:
                right = false;
                return true;
            case Left:
                left = false;
                return true;
            case Jump:
                jumping = false;
        }
        return false;
    }

    public void dispose() {
        sprite.dispose();
    }

}
