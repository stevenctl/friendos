package co.sugarware.friendos;

import co.sugarware.friendos.scenes.PlayableScene;
import co.sugarware.friendos.scenes.Scene;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

public class GdxGame extends ApplicationAdapter {

    private Scene scene;

    public void create() {
        Gdx.graphics.setWindowedMode(960, 480);
        scene = new PlayableScene();
    }

    public void render() {
        scene.update(Gdx.graphics.getDeltaTime());
        scene.draw(Gdx.graphics.getDeltaTime());
    }

    public void dispose() {
        scene.dispose();
    }

}
