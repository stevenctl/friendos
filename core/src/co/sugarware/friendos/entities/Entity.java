package co.sugarware.friendos.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

public interface Entity extends Disposable {

    void draw(SpriteBatch sb, float delta);

    void update(float delta);

}
