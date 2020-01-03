package co.sugarware.friendos.sprite;

import co.sugarware.friendos.Util;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Json;

import java.util.HashMap;
import java.util.Map;

public class AnimatedSprite implements Sprite {

    private static final String PINGPONG = "pingpong";
    private static final String FORWARD = "forward";

    private final String imagePath;
    private AnimationConfig.FrameConfig[] frameConfigs;
    private Map<String, AnimationConfig.FrameTag> frameTags;

    private Texture texture;
    private TextureRegion[] frames;

    private String lastTag;
    private String currentTag;
    private int currentFrame;

    private float delay;
    private boolean forward;


    private AnimatedSprite(
            AnimationConfig.FrameConfig[] frameConfigs,
            Map<String, AnimationConfig.FrameTag> frameTags,
            String imagePath
    ) {
        this.frameConfigs = frameConfigs;
        this.frameTags = frameTags;
        this.imagePath = imagePath;
    }

    public AnimatedSprite(String configPath) {
        AnimationConfig config = new Json().fromJson(AnimationConfig.class, Gdx.files.internal(configPath));

        frameTags = new HashMap<>();
        for (AnimationConfig.FrameTag tag : config.getMeta().getFrameTags()) {
            frameTags.put(tag.getName(), tag);
        }
        currentTag = frameTags.values().iterator().next().getName();
        lastTag = currentTag;

        frameConfigs = new AnimationConfig.FrameConfig[config.getFrames().size()];
        int i = 0;


        for (AnimationConfig.FrameConfig frameCfg : config.getFrames().values()) {
            frameConfigs[i] = frameCfg;
            i++;
        }

        imagePath = Util.truncatePath(configPath) + "/" + config.getMeta().getImage();

        loadFrames();
    }

    private void loadFrames() {
        texture = new Texture(Gdx.files.local(imagePath));
        frames = new TextureRegion[frameConfigs.length];

        for (int i = 0; i < frameConfigs.length; i++) {
            AnimationConfig.FrameConfig cfg = frameConfigs[i];
            frames[i] = new TextureRegion(
                    texture,
                    cfg.getFrame().getX(),
                    cfg.getFrame().getY(),
                    cfg.getFrame().getW(),
                    cfg.getFrame().getH()
            );
        }

    }

    public void setTag(String tagName) {
        lastTag = currentTag;
        currentTag = tagName;
    }

    public TextureRegion getFrame() {
        return frames[currentFrame];
    }

    @Override
    public void update(float delta) {
        // animation switch resets frames
        if (!lastTag.equals(currentTag)) {
            forward = true;
            currentFrame = frameTags.get(currentTag).getFrom();
            return;
        }

        delay -= delta;
        if (delay <= 0) {
            tickAnimation();

            // set delay in nanoseconds
            resetDelay();
        }

    }

    private void resetDelay() {
        delay = frameConfigs[currentFrame].getDuration() / 1000f;
    }

    private void tickAnimation() {
        switch (frameTags.get(currentTag).getDirection()) {
            case FORWARD:
                updateFrameForward();
                break;
            case PINGPONG:
                updateFramePingpong();
                break;
        }
    }

    private void updateFrameForward() {
        currentFrame++;
        if (currentFrame > frameTags.get(currentTag).getTo()) {
            currentFrame = frameTags.get(currentTag).getFrom();
        }
    }

    private void updateFramePingpong() {
        currentFrame += forward ? 1 : -1;
        if (forward && currentFrame > frameTags.get(currentTag).getTo()) {
            currentFrame -= 2;
            forward = false;
        }
        if (!forward && currentFrame < frameTags.get(currentTag).getFrom()) {
            currentFrame += 2;
            forward = true;
        }
    }


    @Override
    public void dispose() {
        texture.dispose();
    }


}
