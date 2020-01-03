package co.sugarware.friendos.sprite;

import lombok.Data;
import java.util.TreeMap;

@Data
public class AnimationConfig {

    @Data
    static class Dimensions {
        private int x, y, w, h;
    }

    @Data
    static class FrameConfig {
        private Dimensions frame;
        private boolean rotated;
        private boolean trimmed;
        private Dimensions spriteSourceSize;
        private Dimensions sourceSize;
        private int duration;
    }

    @Data
    static class FrameTag {
        private String name;
        private int from;
        private int to;
        private String direction;
    }

    @Data
    static class Meta {
        private String app;
        private String version;
        private String image;
        private String format;
        private Dimensions size;
        private String scale;
        private FrameTag[] frameTags;
    }

    private TreeMap<String, FrameConfig> frames;

    private Meta meta;
}
