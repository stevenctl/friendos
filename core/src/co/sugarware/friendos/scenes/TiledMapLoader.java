package co.sugarware.friendos.scenes;

import co.sugarware.friendos.entities.Entity;
import co.sugarware.friendos.entities.HumanoidEnemy;
import co.sugarware.friendos.entities.Player;
import co.sugarware.friendos.entities.SimplePhysicsObject;
import co.sugarware.friendos.sprite.StaticSprite;
import co.sugarware.friendos.util.Geometry;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.*;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;

import java.util.ArrayList;
import java.util.List;

import static co.sugarware.friendos.entities.Player.RAMSEY_SPRITE;
import static co.sugarware.friendos.entities.Player.TRENT_SPRITE;
import static co.sugarware.friendos.entities.collisions.Collisions.*;
import static co.sugarware.friendos.util.Geometry.mapToWorldScale;

public class TiledMapLoader {

    private static final String COLLISIONS_LAYER = "Collisions";
    private static final String ENTITIES_LAYER = "Entities";

    static void loadCollisionBodies(TiledMap map, World world) {
        BodyDef bodyDef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1;

        // Build collisions
        bodyDef.type = BodyDef.BodyType.StaticBody;
        fixtureDef.filter.categoryBits = WORLD_CATEGORY;

        MapObjects objects = map.getLayers().get(COLLISIONS_LAYER).getObjects();
        for (MapObject object : objects) {
            Shape shape = null;
            if (object instanceof RectangleMapObject) {
                shape = Geometry.buildRectangle(((RectangleMapObject) object).getRectangle());
            } else if (object instanceof PolygonMapObject) {
                shape = Geometry.buildPolygon(((PolygonMapObject) object).getPolygon());
            } else if (object instanceof PolylineMapObject) {
                shape = Geometry.buildChainShape(((PolylineMapObject) object).getPolyline());
            } else if (object instanceof CircleMapObject) {
                shape = Geometry.buildCircle(((CircleMapObject) object).getCircle());
            }

            if (shape == null) {
                continue;
            }

            Body body = world.createBody(bodyDef);
            fixtureDef.shape = shape;
            body.createFixture(fixtureDef);

            shape.dispose();
        }
    }


    static List<Entity> loadEntities(TiledMap map) {
        // TODO(slandow) - DRY this up

        List<Entity> entities = new ArrayList<>();
        MapObjects objects = map.getLayers().get(ENTITIES_LAYER).getObjects();
        for (MapObject object : objects) {
            String type = (String) object.getProperties().get("type");
            if ("physics_object".equals(type)) {
                if (!(object instanceof TiledMapTileMapObject)) {
                    Gdx.app.getApplicationLogger().error("WORLD", "Trying to load non TiledMapTileMapObject. Type: physics_object");
                    continue;
                }
                SimplePhysicsObject physicsObject = buildSimplePhysicsObject((TiledMapTileMapObject) object);
                entities.add(physicsObject);
            } else if ("player".equals(type)) {
                RectangleMapObject rmo = validateRectangleObject(object, type);
                if (rmo == null) continue;
                String spriteName = Math.random() > 0.5f ? TRENT_SPRITE : RAMSEY_SPRITE;
                entities.add(new Player(mapToWorldScale(new Vector2(rmo.getRectangle().getX(), rmo.getRectangle().getY())), spriteName));
            } else if ("skeleton".equals(type)) {
                RectangleMapObject rmo = validateRectangleObject(object, type);
                if (rmo == null) continue;
                entities.add(new HumanoidEnemy(mapToWorldScale(new Vector2(rmo.getRectangle().getX(), rmo.getRectangle().getY())), HumanoidEnemy.SKELETON_SPRITE));
            }
        }

        return entities;
    }

    private static RectangleMapObject validateRectangleObject(MapObject object, String type) {
        if (!(object instanceof RectangleMapObject)) {
            Gdx.app.getApplicationLogger().error("WORLD", "Trying to load non TiledMapTileMapObject. Type: " + type);
            return null;
        }

        return (RectangleMapObject) object;
    }

    private static SimplePhysicsObject buildSimplePhysicsObject(TiledMapTileMapObject object) {
        StaticSprite sprite = new StaticSprite(object.getTextureRegion());

        PolygonShape shape = null;
        if (object.getTile().getObjects().getCount() > 0) {
            MapObject collisionObject = object.getTile().getObjects().get(0);
            if (collisionObject instanceof RectangleMapObject) {
                shape = Geometry.buildRectangle(((RectangleMapObject) collisionObject).getRectangle());
            }
        }

        if (shape == null) {
            shape = Geometry.buildRectangle(sprite.getRectangle());
        }

        return new SimplePhysicsObject(
                sprite,
                mapToWorldScale(new Vector2(object.getX(), object.getY())),
                shape,
                mapToWorldScale(sprite.getRectangle().getSize(new Vector2()))
        );
    }

}
