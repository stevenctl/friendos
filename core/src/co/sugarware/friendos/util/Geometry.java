package co.sugarware.friendos.util;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class Geometry {

    private static final int MAP_TO_WORLD_RATIO = 2;

    public static Vector2 mapToWorldScale(Vector2 pixelCoords) {
        return pixelCoords.scl(1f / MAP_TO_WORLD_RATIO);
    }

    public static PolygonShape buildRectangle(Rectangle rectangle) {
        PolygonShape polygon = new PolygonShape();
        Vector2 size = new Vector2((rectangle.x + rectangle.width * 0.5f) / MAP_TO_WORLD_RATIO,
                (rectangle.y + rectangle.height * 0.5f) / MAP_TO_WORLD_RATIO);
        polygon.setAsBox(rectangle.width * 0.5f / MAP_TO_WORLD_RATIO, rectangle.height * 0.5f / MAP_TO_WORLD_RATIO, size, 0.0f);
        return polygon;
    }

    public static CircleShape buildCircle(Circle circle) {
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(circle.radius / MAP_TO_WORLD_RATIO);
        circleShape.setPosition(new Vector2(circle.x / MAP_TO_WORLD_RATIO, circle.y / MAP_TO_WORLD_RATIO));
        return circleShape;
    }

    public static PolygonShape buildPolygon(Polygon polygon) {
        PolygonShape polygonShape = new PolygonShape();
        float[] vertices = polygon.getTransformedVertices();
        float[] worldVertices = new float[vertices.length];
        int i = 0;
        while (i < vertices.length) {
            worldVertices[i] = vertices[i] / MAP_TO_WORLD_RATIO;
            ++i;
        }
        polygonShape.set(worldVertices);
        return polygonShape;
    }

    public static ChainShape buildChainShape(Polyline polyline) {
        float[] vertices = polyline.getTransformedVertices();
        Vector2[] worldVertices = new Vector2[vertices.length / 2];
        int i = 0;
        while (i < vertices.length / 2) {
            worldVertices[i] = new Vector2();
            worldVertices[i].x = vertices[i * 2] / MAP_TO_WORLD_RATIO;
            worldVertices[i].y = vertices[i * 2 + 1] / MAP_TO_WORLD_RATIO;
            ++i;
        }
        ChainShape chain = new ChainShape();
        chain.createChain(worldVertices);
        return chain;
    }
}
