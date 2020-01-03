package co.sugarware.friendos.scenes;

import com.badlogic.gdx.utils.Disposable;

public interface Scene extends Disposable {

    void update(float delta);

    void draw(float delta);


}
