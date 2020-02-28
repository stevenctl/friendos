package co.sugarware.friendos.scenes;

import co.sugarware.friendos.entities.Entity;
import co.sugarware.friendos.entities.PhysicsObject;
import co.sugarware.friendos.entities.Player;
import co.sugarware.friendos.entities.collisions.FixtureDelegateContactListener;
import co.sugarware.friendos.input.SimpleKeyboardController;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlayableScene implements Scene {

    // physics config
    private static final float GRAVITY = -100f;

    // viewport config
    private static final float CAMERA_HEIGHT = 100;
    private static final float CAMERA_WIDTH = CAMERA_HEIGHT * ((Gdx.graphics.getWidth() / 2f) / (float) Gdx.graphics.getHeight());

    // game state
    private List<Entity> entities;
    private List<Player> players;
    private World world;

    // map
    private TiledMap map;

    // rendering
    private Matrix4 identity = new Matrix4();
    private OrthographicCamera[] cameras;
    private FrameBuffer[] fbos;
    private SpriteBatch sb;
    private TiledMapRenderer mapRenderer;
    private Box2DDebugRenderer debugRenderer;

    public PlayableScene() {
        entities = new ArrayList<>();

        world = new World(new Vector2(0, GRAVITY), false);
        world.setContactListener(new FixtureDelegateContactListener());

        map = new TmxMapLoader().load("tilemaps/test.tmx");
        TiledMapLoader.loadCollisionBodies(map, world);
        entities.addAll(TiledMapLoader.loadEntities(map));
        setupPlayers();

        mapRenderer = new OrthogonalTiledMapRenderer(map, 0.5f);
        sb = new SpriteBatch();

        debugRenderer = new Box2DDebugRenderer();
        debugRenderer.setDrawBodies(true);
        debugRenderer.setDrawVelocities(false);

        for (Entity e : entities) {
            if (e instanceof PhysicsObject) {
                ((PhysicsObject) e).setupPhysics(world);
            }
        }
    }

    private void setupPlayers() {
        // setup each player
        players = new ArrayList<>();
        Set<Entity> extras = new HashSet<Entity>();
        for (Entity e : entities) {
            if (e instanceof Player) {
                if (players.size() < 2) {
                    players.add((Player) e);
                } else {
                    extras.add(e);
                }
            }
        }

        for(Entity e : extras) {
            e.dispose();
            entities.remove(e);
        }
        Gdx.app.getApplicationLogger().error("WORLD", "Removed " + extras.size() + " extra players. ");


        cameras = new OrthographicCamera[players.size()];
        fbos = new FrameBuffer[players.size()];
        InputMultiplexer input = new InputMultiplexer();
        Gdx.input.setInputProcessor(input);

        for (int i = 0; i < players.size(); i++) {
            // register inputs
            input.addProcessor(new SimpleKeyboardController(players.get(i), i));

            // setup camera
            OrthographicCamera cam = new OrthographicCamera(CAMERA_WIDTH, CAMERA_HEIGHT);
            cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
            cameras[i] = cam;

            // setup split-screen fbo
            fbos[i] = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth() / players.size(), Gdx.graphics.getHeight(), false);
        }

    }

    @Override
    public void update(float delta) {
        world.step(delta, 20, 20);
        for (Entity e : entities) {
            e.update(delta);
        }

        for (int i = 0; i < players.size(); i++) {
            OrthographicCamera cam = cameras[i];
            Player player = players.get(i);

            cam.position.set(
                    Math.max(cam.viewportWidth / 2, player.getPosition().x),
                    Math.max(cam.viewportHeight / 2, player.getPosition().y),
                    0
            );
            cam.update();
        }
    }


    @Override
    public void draw(float delta) {
        // Render each camera to a separate frame buffer
        for (int i = 0; i < cameras.length; i++) {
            FrameBuffer fbo = fbos[i];
            OrthographicCamera cam = cameras[i];

            fbo.begin();

            Gdx.gl.glClearColor(i + 1, i, i - 1, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            mapRenderer.setView(cam);
            mapRenderer.render();

            sb.setProjectionMatrix(cam.combined);
            sb.begin();
            for (Entity e : entities) {
                e.draw(sb, delta);
            }
            sb.end();

            debugRenderer.render(world, cam.combined);
            fbo.end();
        }

        // render each frame buffer as a split-screen
        sb.setProjectionMatrix(identity);
        sb.begin();
        for (int i = 0; i < fbos.length; i++) {
            sb.draw(
                    fbos[i].getColorBufferTexture(),
                    -1 + i, 1f, 1, -2
            );
        }
        sb.end();

    }

    @Override
    public void dispose() {
        for (Entity e : entities) {
            e.dispose();
        }
        sb.dispose();
        debugRenderer.dispose();
        world.dispose();
    }

}
