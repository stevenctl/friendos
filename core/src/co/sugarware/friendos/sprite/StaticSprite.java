package co.sugarware.friendos.sprite;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class StaticSprite implements Sprite {

    private TextureRegion textureRegion;

    public StaticSprite(TextureRegion textureRegion) {
        this.textureRegion = textureRegion;
    }

    public TextureRegion getFrame() {
        return textureRegion;
    }

    public void update(float delta) {
    }

    public void dispose() {
    }

    public Rectangle getRectangle() {
        return new Rectangle(0, 0, textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
    }
}
