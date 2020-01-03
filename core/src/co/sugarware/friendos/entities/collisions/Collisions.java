package co.sugarware.friendos.entities.collisions;

public class Collisions {

    public static final short WORLD_CATEGORY = 0b00000010;
    public static final short PLAYER_CATEGORY = 0b00000001;
    public static final short ENTITY_CATEGORY = 0b00000100;

    public static final short PLAYER_MASK = (short) (PLAYER_CATEGORY | WORLD_CATEGORY | ENTITY_CATEGORY);
    public static final short ENTITY_MASK = (short) (ENTITY_CATEGORY | WORLD_CATEGORY | PLAYER_CATEGORY);


}
