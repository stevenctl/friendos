package co.sugarware.friendos.sprite;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

public interface Sprite extends Disposable {

    void update(float delta);

    TextureRegion getFrame();

}
