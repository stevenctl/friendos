package co.sugarware.friendos.entities;

import co.sugarware.friendos.input.Controllable;
import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;
import java.util.Map;

public class Player extends Humanoid {

    public static final String RAMSEY_SPRITE = "sprites/ramsey.json";
    public static final String TRENT_SPRITE = "sprites/trent.json";
    public static final String NAT_SPRITE = "sprites/nat.json";
    public static final Map<String, String> SPRITES = new HashMap<>();

    static {
        SPRITES.put("ramsey", RAMSEY_SPRITE);
        SPRITES.put("trent", TRENT_SPRITE);
        SPRITES.put("nat", NAT_SPRITE);
    }


    public Player(Vector2 startPosition, String sprite) {
        super(startPosition, sprite);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (jumping) {
            if (extendedJumpTimer > 0) {
                // Wall jump
                boolean onWall = (onLeftWall() || onRightWall()) && !onFloor();
                boolean firstJumpFrame = extendedJumpTimer == EXTENDED_JUMP_SECONDS - delta;
                if (onWall) {
                    System.out.println(extendedJumpTimer);
                }
                if (firstJumpFrame && onWall) {
                    System.out.println(onLeftWall());
                    System.out.println(onRightWall());
                    System.out.println("----");
                    walkCooldown = EXTENDED_JUMP_SECONDS;
                    float dir = onLeftWall() ? 1 : -1;
                    impulse.set(2 * dir * WALK_ACCEL, body.getLinearVelocity().y);
                    body.setLinearVelocity(impulse);
                }
            }
        }
    }

    @Override
    protected boolean canJump() {
        return onFloor() || onLeftWall() || onRightWall();
    }

}