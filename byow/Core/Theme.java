package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Theme {

    public HashMap<String, ArrayList<TETile>> THEMES;

    public static final TETile AVATAR = new TETile('@', Color.white, Color.black, "you");
    public static final TETile WALL = new TETile('#', new Color(216, 128, 128), Color.darkGray,
            "wall");
    public static final TETile FLOOR = new TETile('·', new Color(128, 192, 128), Color.black,
            "floor");
    public static final TETile NOTHING = new TETile(' ', Color.black, Color.black, "nothing");
    public static final TETile GRASS = new TETile('"', Color.green, Color.black, "grass");
    public static final TETile WATER = new TETile('≈', Color.blue, Color.black, "water");
    public static final TETile FLOWER = new TETile('❀', Color.magenta, Color.pink, "flower");
    public static final TETile LOCKED_DOOR = new TETile('█', Color.orange, Color.black,
            "locked door");
    public static final TETile UNLOCKED_DOOR = new TETile('▢', Color.orange, Color.black,
            "unlocked door");
    public static final TETile SAND = new TETile('▒', Color.yellow, Color.black, "sand");
    public static final TETile MOUNTAIN = new TETile('▲', Color.gray, Color.black, "mountain");
    public static final TETile TREE = new TETile('♠', Color.green, Color.black, "tree");

    // 0th index is the walking tile, 1st index is the wall tile, and 2nd index is the outside tile.
    public Theme() {
        THEMES = new HashMap<>();

        ArrayList<TETile> jungle = new ArrayList<>();
        jungle.add(GRASS);
        jungle.add(FLOWER);
        jungle.add(TREE);
        THEMES.put("jungle", jungle);

        ArrayList<TETile> island = new ArrayList<>();
        island.add(GRASS);
        island.add(SAND);
        island.add(WATER);
        THEMES.put("island", island);

        ArrayList<TETile> desert = new ArrayList<>();
        desert.add(SAND);
        desert.add(WATER);
        desert.add(GRASS);
        THEMES.put("desert", desert);

        ArrayList<TETile> mountain = new ArrayList<>();
        mountain.add(MOUNTAIN);
        mountain.add(TREE);
        mountain.add(GRASS);
        THEMES.put("mountain", mountain);
    }


}
