package co.sugarware.friendos.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

import java.util.HashMap;
import java.util.Map;

public class SimpleKeyboardController implements InputProcessor {

    private final Controllable controllable;
    private final Map<Integer, Control> keyMapping;

    private static final Map<Integer, Control> ALTERNATE_KEY_MAPPING = new HashMap<Integer, Control>() {{
        put(Input.Keys.RIGHT, Control.Right);
        put(Input.Keys.LEFT, Control.Left);
        put(Input.Keys.UP, Control.Jump);
    }};


    private static final Map<Integer, Control> DEFAULT_KEY_MAPPING = new HashMap<Integer, Control>() {{
        put(Input.Keys.D, Control.Right);
        put(Input.Keys.A, Control.Left);
        put(Input.Keys.W, Control.Jump);
    }};

    @SuppressWarnings("unchecked")
    private static final Map<Integer, Control>[] schemes = new Map[]{
            DEFAULT_KEY_MAPPING,
            ALTERNATE_KEY_MAPPING
    };

    public SimpleKeyboardController(Controllable controllable, int scheme) {
        this.controllable = controllable;
        this.keyMapping = schemes[scheme];
    }

    public SimpleKeyboardController(Controllable controllable) {
        this(controllable, 0);
    }

    public boolean keyDown(int keycode) {
        Control control = keyMapping.get(keycode);
        if (control == null) {
            return false;
        }

        return controllable.controlStart(control);
    }

    public boolean keyUp(int keycode) {
        Control control = keyMapping.get(keycode);
        if (control == null) {
            return false;
        }

        return controllable.controlStop(control);
    }


    public boolean keyTyped(char character) {
        return false;
    }

    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    public boolean scrolled(int amount) {
        return false;
    }
}
