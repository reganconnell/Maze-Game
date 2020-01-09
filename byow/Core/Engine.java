package byow.Core;

import java.awt.*;
import java.io.Serializable;
import edu.princeton.cs.introcs.StdDraw;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.Random;

public class Engine implements Serializable {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 50;
    public static final int HEIGHT = 50;
    public static final int UPPER_RIGHT_BORDER = 42;
    public static final int LOWER_LEFT_BORDER = 8;

    public static final TETile LOCKED_DOOR = new TETile('█', Color.red, Color.red,
            "locked door");

    private TETile[][] world;
    private TETile[][] displayedWorld;
    private int seedIndex;
    private long seed;
    private Random random;
    private ArrayList<TETile> theme;
    private String themeType;

    Room bestULRoom;
    Room bestURRoom;
    Room bestLRRoom;
    Room bestLLRoom;

    boolean urReached;
    boolean ulReached;
    boolean lrReached;
    boolean llReached;


    String pastInput;
    int avatarLocX;
    int avatarLocY;
    int xxDoorLoc;
    int yyDoorLoc;

    boolean interactingWithKeyboard = false;
    boolean hiddenWorld = false;

    double mouseLocY;
    double mouseLocX;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */

    public void interactWithKeyboard() {
        /*If interactingWithKeyboard is true, that means interactWithKeyboard
         * has just been called by interactWithInputString after L.*/
        if (interactingWithKeyboard) {
            interactWithAvatar();
        }

        interactingWithKeyboard = true;
        pastInput = "";

        /*At start screen,
         * if N
         *     returns so can go to drawSeedScreen
         *if L
         *     loads past input, then calls
         *     interactWithUserInput
         *if Q
         *     system exits*/
        drawStartScreen();

        /*Reach if N inputted at start screen.
         * Will draw start screen, then return so
         * will reach interactWithWorld call next.*/
        drawSeedScreen();

        /*Reached after seed screen created. Instructions to move or quit
        will always be inputted at this point.*/
        buildWorldWithNS();
    }

    /*Draws start screen where user has option to start new game (n),
    load a past game (l), or quit (q)*/
    private void drawStartScreen() {
        StdDraw.clear();
        StdDraw.setCanvasSize(500, 500);
        Font header = new Font("Arial", Font.BOLD, 30);
        Font subHeader = new Font("Arial", Font.ITALIC, 15);
        StdDraw.setFont(header);
        StdDraw.text(.5, .8, "CS61B: Runner");
        StdDraw.setFont(subHeader);
        StdDraw.text(.5, .5, "New Game (N)");
        StdDraw.text(.5, .4, "New Game with Hidden World (H)");
        StdDraw.text(.5, .3, "Load Game (L)");
        StdDraw.text(.5, .2, "Quit (Q)");
        StdDraw.show();

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                switch (c) {
                    case 'L': case 'l':
                        interactWithInputString(loadInput());
                        return;
                    case 'n' : case'N':
                        pastInput += Character.toString(c);
                        return;
                    case 'h' : case 'H':
                        pastInput += 'n';
                        hiddenWorld = true;
                        return;
                    case 'q': case 'Q':
                        System.exit(0);
                }
            }
        }
    }

    private void drawSeedScreen() {
        StdDraw.clear();
        StdDraw.setCanvasSize(500, 500);
        Font header = new Font("Arial", Font.BOLD, 15);
        StdDraw.setFont(header);
        StdDraw.text(.5, .5, "Input integers to generate your world, then 's'.");
        return;
    }

    /*Called after seed screen generated*/
    private void buildWorldWithNS() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                pastInput += Character.toString(c);
                if (c == 's' || c == 'S') {
                    interactWithInputString(pastInput);
                }
            }
        }
    }

    /*Called after world generated so user can input
     * avatar instructions, as well as q to quit*/
    private void interactWithAvatar() {
        boolean colonInputtedLast = false;
        while (true) {

            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();

                if (c == ':') {
                    colonInputtedLast = true;
                } else if (c == 'Q' || c == 'q') {
                    if (colonInputtedLast) {
                        saveInput();
                        System.exit(0);
                    }
                }
                if (c == 'w' || c == 'W') { /*up*/
                    pastInput += Character.toString(c);
                    colonInputtedLast = false;
                    if (world[avatarLocX][avatarLocY + 1] == theme.get(0)) {
                        world[avatarLocX][avatarLocY + 1] = Tileset.AVATAR;
                        world[avatarLocX][avatarLocY] = theme.get(0);
                        displayedWorld[avatarLocX][avatarLocY + 1] = Tileset.AVATAR;
                        displayedWorld[avatarLocX][avatarLocY] = theme.get(0);
                        avatarLocY++;
                        updateDisplayedWorld(avatarLocX, avatarLocY);
                    } else if (world[avatarLocX][avatarLocY + 1] == LOCKED_DOOR) {
                        world[avatarLocX][avatarLocY + 1] = Tileset.AVATAR;
                        endGame();
                        System.exit(0);
                        return;
                    }
                } else if (c == 'a' || c == 'A') { /*left*/
                    pastInput += Character.toString(c);
                    colonInputtedLast = false;
                    if (world[avatarLocX - 1][avatarLocY] == theme.get(0)) {
                        world[avatarLocX - 1][avatarLocY] = Tileset.AVATAR;
                        world[avatarLocX][avatarLocY] = theme.get(0);
                        displayedWorld[avatarLocX - 1][avatarLocY] = Tileset.AVATAR;
                        displayedWorld[avatarLocX][avatarLocY] = theme.get(0);
                        avatarLocX--;
                        updateDisplayedWorld(avatarLocX, avatarLocY);
                    } else if (world[avatarLocX - 1][avatarLocY] == LOCKED_DOOR) {
                        world[avatarLocX - 1][avatarLocY] = Tileset.AVATAR;
                        endGame();
                        System.exit(0);
                        return;
                    }
                } else if (c == 's' || c == 'S') { /*down*/
                    pastInput += Character.toString(c);
                    colonInputtedLast = false;
                    if (world[avatarLocX][avatarLocY - 1] == theme.get(0)) {
                        world[avatarLocX][avatarLocY - 1] = Tileset.AVATAR;
                        world[avatarLocX][avatarLocY] = theme.get(0);
                        displayedWorld[avatarLocX][avatarLocY - 1] = Tileset.AVATAR;
                        displayedWorld[avatarLocX][avatarLocY] = theme.get(0);
                        avatarLocY--;
                        updateDisplayedWorld(avatarLocX, avatarLocY);
                    } else if (world[avatarLocX][avatarLocY - 1] == LOCKED_DOOR) {
                        world[avatarLocX][avatarLocY - 1] = Tileset.AVATAR;
                        endGame();
                        System.exit(0);
                        return;
                    }
                } else if (c == 'd' || c == 'D') { /*down*/
                    pastInput += Character.toString(c);
                    colonInputtedLast = false;
                    if (world[avatarLocX + 1][avatarLocY] == theme.get(0)) {
                        world[avatarLocX + 1][avatarLocY] = Tileset.AVATAR;
                        world[avatarLocX][avatarLocY] = theme.get(0);
                        displayedWorld[avatarLocX + 1][avatarLocY] = Tileset.AVATAR;
                        displayedWorld[avatarLocX][avatarLocY] = theme.get(0);
                        avatarLocX++;
                        updateDisplayedWorld(avatarLocX, avatarLocY);
                    } else if (world[avatarLocX + 1][avatarLocY] == LOCKED_DOOR) {
                        world[avatarLocX + 1][avatarLocY] = Tileset.AVATAR;
                        endGame();
                        System.exit(0);
                        return;
                    }
                }
//                 TODO: if statements will dictate which world is displayed. just fill in the conditions with whatever works
            }
            if (!hiddenWorld) {
                ter.renderFrame(world, mouseTile()); //comment out for submission
            } else if (hiddenWorld) {
                ter.renderFrame(displayedWorld, mouseTile());
            }
        }
    }

    private String mouseTile(){
        int xVal = (int) Math.floor(StdDraw.mouseX());
        int yVal = (int) Math.floor(StdDraw.mouseY());
        return world[xVal][yVal].description();

    }

    private void endGame() {


        StdDraw.clear();
        StdDraw.setCanvasSize(500, 500);
        Font header = new Font("Arial", Font.BOLD, 30);
        Font subHeader = new Font("Arial", Font.ITALIC, 15);
        StdDraw.setFont(header);
        StdDraw.text(.5, .8, "Congratulations!");
        StdDraw.setFont(subHeader);
        StdDraw.text(.5, .5, "You won the game.");
        StdDraw.show();
        StdDraw.pause(1000);
        return;
    }

    private String loadInput() {
        File f = new File("./save_data.txt");
        if (f.exists()) {
            try {
                FileInputStream fs = new FileInputStream(f);
                ObjectInputStream os = new ObjectInputStream(fs);
                return (String) os.readObject();
            } catch (FileNotFoundException e) {
                System.out.println("file not found");
                System.exit(0);
            } catch (IOException e) {
                System.out.println(e);
                System.exit(0);
            } catch (ClassNotFoundException e) {
                System.out.println("class not found");
                System.exit(0);
            }
        }

        /* In the case no Engine has been saved yet, we return a blank string. */
        return "";
    }

    private void saveInput() {

        File f = new File("./save_data.txt");
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream fs = new FileOutputStream(f);
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(pastInput);
        }  catch (FileNotFoundException e) {
            System.out.println("file not found");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(0);
        }
    }


    /*Autograder tests this.*/
    public TETile[][] interactWithInputString(String input) {


        //ter.initialize(WIDTH, HEIGHT); //comment out for submission
        /*Check for valid input.*/
        if (input.length() == 0) {
            throw new IllegalArgumentException("Must enter a non-empty string.");
        }

        /*Convert input string to array of chars for easier processing.*/
        char[] charInput = input.toCharArray();

        /*Check if input starts with N, in which case
         * normally generate world.*/
        if (charInput[0] == 'N' || charInput[0] == 'n') {
            pastInput = input;
            /*Generate world map.*/
            getSeed(input);
            random = new Random(seed);
            makeTheme();
            initializeWorld();
            initializeDisplayedWorld();
            generateWorld();
            buildWalls();

            /*Place avatar in map.*/
            generateAvatar();

            createClosedDoor();

            updateDisplayedWorld(avatarLocX, avatarLocY);

            /*Once world and avatar generated, process any instructions after s
             * if they exist.*/
            processInputAfterSeedOrLoad(charInput);
        }

        /*Check if input starts with L, in which case
         * call loadInput to load last saved game.
         * Then, attach old input to input after L, and
         * call interactWithInputString on this combined input.*/
        if (charInput[0] == 'l' || charInput[0] == 'L') {

            /*Combine last input and current index.*/
            char[] lastInput = loadInput().toCharArray();
            char [] totalInput = new char[lastInput.length + charInput.length - 3];
            int index = 0;
            for (char i : lastInput) {
                if (i != ':' && i != 'q' && i != 'Q') {
                    totalInput[index] = i;
                    index++;
                }
            }
            for (char i : charInput) {
                if (i != 'l' && i != 'L') {
                    totalInput[index] = i;
                    index++;
                }
            }
            String totalInputAsString = new String(totalInput);
            interactWithInputString(totalInputAsString);
        }


        // TODO: if statements will dictate which world is displayed. just fill in the conditions with whatever works
        if (!hiddenWorld) {
            ter.renderFrame(world, mouseTile()); //comment out for submission
        } else if (hiddenWorld) {
            ter.renderFrame(displayedWorld, mouseTile());
        }

        if (interactingWithKeyboard) {
            interactWithAvatar();
        }

        return world;
    }

    //puts an avatar in the first grass spot found.
    private void generateAvatar() {
        int x = RandomUtils.uniform(random, WIDTH);
        int y = RandomUtils.uniform(random, HEIGHT);
        while (true) {
            if (world[x][y] == theme.get(0)) {
                world[x][y] = Tileset.AVATAR;
                displayedWorld[x][y] = Tileset.AVATAR;
                avatarLocX = x;
                avatarLocY = y;
                return;
            }
            x = RandomUtils.uniform(random, WIDTH);
            y = RandomUtils.uniform(random, HEIGHT);
        }
    }

    private void processInputAfterSeedOrLoad(char[] charInput) {
        /*These chars will always be either W, A, S, D, or :Q.
         * If they aren't, skip them.*/

        /*This variable will help catch if :q exist in the
        input consecutively.*/
        boolean colonInputtedLast = false;

        /*keeps track of whether seed s or move s. When false until
         * seed s encountered. Then turned true, so following s's will
         * lead to an avatar move.*/
        boolean moveS = false;

        for (char i : charInput) {

            if (i == ':') {
                colonInputtedLast = true;
            } else if (i == 'q' || i == 'Q') { /*Check if :q inputted consecutively.*/
                if (colonInputtedLast) {
                    /*Save and Quit world.*/
                    saveInput();
                    System.exit(0);
                }
            } else if (checkNotSeedOrLoad(i)) { /*Check if not a valid move command or quit.*/
                colonInputtedLast = false;
                continue;
            } else if (i == 'w' || i == 'W') { /*up*/
                colonInputtedLast = false;
                if (world[avatarLocX][avatarLocY + 1] == theme.get(0)) {
                    world[avatarLocX][avatarLocY + 1] = Tileset.AVATAR;
                    world[avatarLocX][avatarLocY] = theme.get(0);
                    displayedWorld[avatarLocX][avatarLocY + 1] = Tileset.AVATAR;
                    displayedWorld[avatarLocX][avatarLocY] = theme.get(0);
                    avatarLocY++;
                    updateDisplayedWorld(avatarLocX, avatarLocY);

                }
            } else if (i == 'a' || i == 'A') { /*left*/
                colonInputtedLast = false;
                if (world[avatarLocX - 1][avatarLocY] == theme.get(0)) {
                    world[avatarLocX - 1][avatarLocY] = Tileset.AVATAR;
                    world[avatarLocX][avatarLocY] = theme.get(0);
                    displayedWorld[avatarLocX - 1][avatarLocY] = Tileset.AVATAR;
                    displayedWorld[avatarLocX][avatarLocY] = theme.get(0);
                    avatarLocX--;
                    updateDisplayedWorld(avatarLocX, avatarLocY);
                }
            } else if (i == 's' || i == 'S') { /*down*/
                if (!moveS) {
                    moveS = true;
                    continue;
                } else {
                    colonInputtedLast = false;
                    if (world[avatarLocX][avatarLocY - 1] == theme.get(0)) {
                        world[avatarLocX][avatarLocY - 1] = Tileset.AVATAR;
                        world[avatarLocX][avatarLocY] = theme.get(0);
                        displayedWorld[avatarLocX][avatarLocY - 1] = Tileset.AVATAR;
                        displayedWorld[avatarLocX][avatarLocY] = theme.get(0);
                        avatarLocY --;
                        updateDisplayedWorld(avatarLocX, avatarLocY);
                    }
                }
            } else if (i == 'd' || i == 'D') { /*right*/
                colonInputtedLast = false;
                if (world[avatarLocX + 1][avatarLocY] == theme.get(0)) {
                    world[avatarLocX + 1][avatarLocY] = Tileset.AVATAR;
                    world[avatarLocX][avatarLocY] = theme.get(0);
                    displayedWorld[avatarLocX + 1][avatarLocY] = Tileset.AVATAR;
                    displayedWorld[avatarLocX][avatarLocY] = theme.get(0);
                    avatarLocX ++;
                    updateDisplayedWorld(avatarLocX, avatarLocY);
                }
            }
            //If reached here, character didn't instruct a move. Next char will be checked.
        }
    }

    /*This function will generate a boolean that will tell processInputAfterSeedOrLoad
     * whether to process or skip a char from the user's input.*/
    private boolean checkNotSeedOrLoad(char i) {
        boolean checkL = (i == 'l') || (i == 'L');
        boolean checkN = (i == 'N') || (i == 'n');
        boolean checkInt = Character.isDigit(i);
        return checkL || checkN || checkInt;
    }

    public static void main(String[] args) {
        Engine x = new Engine();
        //x.interactWithKeyboard();
        //x.interactWithInputString("L");
        x.interactWithKeyboard();
//        x.interactWithInputString("n1123s");
//        x.interactWithInputString("L");
    }
















    public String getTheme() {
        return this.themeType;
    }




    private void initializeWorld() {
        ter.initialize(WIDTH, HEIGHT);
        world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = theme.get(2);
            }
        }
    }

    private void initializeDisplayedWorld() {
        ter.initialize(WIDTH, HEIGHT);
        displayedWorld = new TETile[WIDTH][HEIGHT];
        createBlackScreen();
    }

    private void createBlackScreen() {
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                displayedWorld[x][y] = Tileset.NOTHING;
            }
        }
    }

    private void updateDisplayedWorld(int x, int y) {
        createBlackScreen();
        displayedWorld[x][y] = Tileset.AVATAR;
        for (int i = x - 4; i <= x + 4; i++) {
            for (int j = y - 4; j <= y + 4; j++) {
                if (!outOfBounds(i, j)) {
                    if (i == xxDoorLoc && j == yyDoorLoc) {
                        displayedWorld[i][j] = LOCKED_DOOR;
                    } else if (world[i][j] == theme.get(0)) {
                        displayedWorld[i][j] = theme.get(0);
                    } else if (world[i][j] == theme.get(1)) {
                        displayedWorld[i][j] = theme.get(1);
                    } else if (world[i][j] == theme.get(2)) {
                        displayedWorld[i][j] = theme.get(2);
                    }
                }
            }
        }
    }

    private boolean outOfBounds(int i, int j) {
        // Checks if coordinates are out of bounds.
        if (i > WIDTH - 1 || j > HEIGHT - 1) {
            return true;
        } else if (i < 0 || j < 0) {
            return true;
        }
        return false;
    }

    private void getSeed(String input) {
        seedIndex = 1;
        seed = 0;
        char curr = input.charAt(seedIndex);
        while (curr != 'S' && curr != 's') {
            seed = seed * 10 + (curr - '0');
            seedIndex += 1;
            curr = input.charAt(seedIndex);
        }
    }

    private void generateWorld() {
        Room firstRoom = roomHelper();
        bestULRoom = firstRoom;
        bestURRoom = firstRoom;
        bestLLRoom = firstRoom;
        bestLRRoom = firstRoom;

        urReached = false;
        ulReached = false;
        lrReached = false;
        llReached = false;

        // Traverses the graph to the upper right edge until it has been reached.
        while (!urReached || !ulReached || !lrReached || !llReached) {
            if (!urReached) {
                generateRoom(bestURRoom.ul, bestURRoom.lr);
                double prob = RandomUtils.uniform(random);
                if (prob < 0.5) {
                    generateUpHallway(bestURRoom, "right");
                } else {
                    generateRightHallway(bestURRoom, "up");
                }
            }
            if (!ulReached) {
                generateRoom(bestULRoom.ul, bestULRoom.lr);
                double prob = RandomUtils.uniform(random);
                if (prob < 0.5) {
                    generateUpHallway(bestULRoom, "left");
                } else {
                    generateLeftHallway(bestULRoom, "up");
                }
            }
            if (!lrReached) {
                generateRoom(bestLRRoom.ul, bestLRRoom.lr);
                double prob = RandomUtils.uniform(random);
                if (prob < 0.5) {
                    generateDownHallway(bestLRRoom, "right");
                } else {
                    generateRightHallway(bestLRRoom, "down");
                }
            }
            if (!llReached) {
                generateRoom(bestLLRoom.ul, bestLLRoom.lr);
                double prob = RandomUtils.uniform(random);
                if (prob < 0.5) {
                    generateLeftHallway(bestLLRoom, "down");
                } else {
                    generateDownHallway(bestLLRoom, "left");
                }
            }
        }
    }


    private void generateUpHallway(Room room, String direction) {
        Position ul = room.ul;
        int xxOffset = room.lr.xxCoord - ul.xxCoord;
        if (xxOffset > 0) {
            xxOffset = RandomUtils.uniform(random, xxOffset + 1);
        }
        int xxPos = xxOffset + ul.xxCoord;
        int yyPos = ul.yyCoord;
        Position start = new Position(xxPos, yyPos);
        int hallwayLength = drawUpHallway(start);

        double prob = RandomUtils.uniform(random);

        int yyLowerCoord = yyPos + hallwayLength;

        if (prob < 0.6) {
            // Generates a new room.
            int xxLeftOffset = RandomUtils.uniform(random, -3, 1);
            int xxRightOffset = RandomUtils.uniform(random, 0, 4);
            int xxLeftCoord = xxPos + xxLeftOffset;
            int xxRightCoord = xxPos + xxRightOffset;
            int yyOffset = RandomUtils.uniform(random, 7);
            int yyUpperCoord = yyOffset + yyLowerCoord;
            Position ulPos = new Position(xxLeftCoord, yyUpperCoord);
            Position lrPos = new Position(xxRightCoord, yyLowerCoord);
            generateRoom(ulPos, lrPos);
        } else {
            Position p = new Position(xxPos, yyLowerCoord);

            if (direction.equals("right")) {
                drawRightHallway(p);
            } else if (direction.equals("left")) {
                drawLeftHallway(p);
            }
        }
    }

    // Draws the hallway starting at Position p and returns the hallway length.
    private int drawUpHallway(Position p) {
        int xxPos = p.xxCoord;
        int yyPos = p.yyCoord;
        int hallwayLength = RandomUtils.uniform(random, 4, 10);
        for (int i = 1; i <= hallwayLength; i++) {
            boundaryCheck(xxPos, yyPos + i);
            if (edgeReached(xxPos, yyPos + i)) {
                continue;
            }
            world[xxPos][yyPos + i] = theme.get(0);
        }
        return hallwayLength;
    }

    private void generateRightHallway(Room room, String direction) {
        Position lr = room.lr;
        int yyOffset = room.ul.yyCoord - lr.yyCoord;
        if (yyOffset > 0) {
            yyOffset = RandomUtils.uniform(random, yyOffset + 1);
        }
        int yyPos = yyOffset + lr.yyCoord;
        int xxPos = lr.xxCoord;
        Position start = new Position(xxPos, yyPos);

        int hallwayLength = drawRightHallway(start);

        int xxLowerCoord = xxPos + hallwayLength;

        double prob = RandomUtils.uniform(random);

        if (prob < 0.6) {
            int yyLowerOffset = RandomUtils.uniform(random, -3, 1);
            int yyUpperOffset = RandomUtils.uniform(random, 0, 4);
            int yyLowerCoord = yyPos + yyLowerOffset;
            int yyUpperCoord = yyPos + yyUpperOffset;
            int xxOffset = RandomUtils.uniform(random, 7);
            int xxUpperCoord = xxOffset + xxLowerCoord;
            Position ulPos = new Position(xxLowerCoord, yyUpperCoord);
            Position lrPos = new Position(xxUpperCoord, yyLowerCoord);
            generateRoom(ulPos, lrPos);
        } else if (prob > 0.85) {
            Position p = new Position(xxLowerCoord, yyPos);
            if (direction.equals("up")) {
                drawUpHallway(p);
            } else if (direction.equals("down")) {
                drawDownHallway(p);
            }
        }
    }

    private int drawRightHallway(Position p) {
        int xxPos = p.xxCoord;
        int yyPos = p.yyCoord;
        int hallwayLength = RandomUtils.uniform(random, 4, 10);
        for (int i = 1; i <= hallwayLength; i++) {
            boundaryCheck(xxPos + i, yyPos);
            if (edgeReached(xxPos + i, yyPos)) {
                continue;
            }
            world[xxPos + i][yyPos] = theme.get(0);
        }
        return hallwayLength;
    }


    private void generateLeftHallway(Room room, String direction) {
        Position lr = room.lr;
        int yyOffset = room.ul.yyCoord - lr.yyCoord;
        if (yyOffset > 0) {
            yyOffset = RandomUtils.uniform(random, yyOffset + 1);
        }
        int yyPos = yyOffset + lr.yyCoord;
        int xxPos = room.ul.xxCoord;
        Position start = new Position(xxPos, yyPos);

        int hallwayLength = drawLeftHallway(start);

        int xxUpperCoord = xxPos - hallwayLength;

        double prob = RandomUtils.uniform(random);

        if (prob < 0.6) {
            int yyLowerOffset = RandomUtils.uniform(random, -3, 1);
            int yyUpperOffset = RandomUtils.uniform(random, 0, 4);
            int yyLowerCoord = yyPos + yyLowerOffset;
            int yyUpperCoord = yyPos + yyUpperOffset;
            int xxOffset = RandomUtils.uniform(random, 7);
            int xxLowerCoord = xxUpperCoord - xxOffset;
            Position ulPos = new Position(xxLowerCoord, yyUpperCoord);
            Position lrPos = new Position(xxUpperCoord, yyLowerCoord);
            generateRoom(ulPos, lrPos);
        } else if (prob > 0.85) {
            Position p = new Position(xxUpperCoord, yyPos);
            if (direction.equals("up")) {
                drawUpHallway(p);
            } else if (direction.equals("down")) {
                drawDownHallway(p);
            }
        }
    }

    private void generateDownHallway(Room room, String direction) {
        Position ul = room.ul;
        int xxOffset = room.lr.xxCoord - ul.xxCoord;
        if (xxOffset > 0) {
            xxOffset = RandomUtils.uniform(random, xxOffset + 1);
        }
        int xxPos = xxOffset + ul.xxCoord;
        int yyPos = room.lr.yyCoord;

        Position start = new Position(xxPos, yyPos);
        int hallwayLength = drawDownHallway(start);

        double prob = RandomUtils.uniform(random);

        int yyUpperCoord = yyPos - hallwayLength;

        if (prob < 0.6) {
            // Generates a new room.
            int xxLeftOffset = RandomUtils.uniform(random, -3, 1);
            int xxRightOffset = RandomUtils.uniform(random, 0, 4);
            int xxLeftCoord = xxPos + xxLeftOffset;
            int xxRightCoord = xxPos + xxRightOffset;
            int yyOffset = RandomUtils.uniform(random, 7);
            int yyLowerCoord = yyUpperCoord - yyOffset;
            Position ulPos = new Position(xxLeftCoord, yyUpperCoord);
            Position lrPos = new Position(xxRightCoord, yyLowerCoord);
            generateRoom(ulPos, lrPos);
        } else if (prob > 0.75) {
            Position p = new Position(xxPos, yyUpperCoord);
            if (direction.equals("right")) {
                drawRightHallway(p);
            } else if (direction.equals("left")) {
                drawLeftHallway(p);
            }
        }
    }

    private int drawDownHallway(Position p) {
        int xxPos = p.xxCoord;
        int yyPos = p.yyCoord;
        int hallwayLength = RandomUtils.uniform(random, 4, 10);
        for (int i = 1; i <= hallwayLength; i++) {
            boundaryCheck(xxPos, yyPos - i);
            if (edgeReached(xxPos, yyPos - i)) {
                continue;
            }
            world[xxPos][yyPos - i] = theme.get(0);
        }
        return hallwayLength;
    }

    private int drawLeftHallway(Position p) {
        int xxPos = p.xxCoord;
        int yyPos = p.yyCoord;
        int hallwayLength = RandomUtils.uniform(random, 4, 10);
        for (int i = 1; i <= hallwayLength; i++) {
            boundaryCheck(xxPos - i, yyPos);
            if (edgeReached(xxPos - i, yyPos)) {
                continue;
            }
            world[xxPos - i][yyPos] = theme.get(0);
        }
        return hallwayLength;
    }


    // Returns a Room that complies with the given boundaries.
    private Room roomHelper() {
        int xxULCoord = RandomUtils.uniform(random, 1, WIDTH - 7);
        int xxOffset = RandomUtils.uniform(random, 7);
        int xxLRCoord = xxULCoord + xxOffset;
        int yyLRCoord = RandomUtils.uniform(random, 1, HEIGHT - 7);
        int yyOffset = RandomUtils.uniform(random, 7);
        int yyULCoord = yyLRCoord + yyOffset;
        Position ulPos = new Position(xxULCoord, yyULCoord);
        Position lrPos = new Position(xxLRCoord, yyLRCoord);
        return new Room(ulPos, lrPos);
    }

    // Generates a room (walls not included) that goes from
    // the given upper left position to the lower right.
    private void generateRoom(Position ulPos, Position lrPos) {
        Room r = new Room(ulPos, lrPos);
        for (int i = ulPos.xxCoord; i <= lrPos.xxCoord; i++) {
            for (int j = lrPos.yyCoord; j <= ulPos.yyCoord; j++) {
                boundaryCheck(i, j);
                if (edgeReached(i, j)) {
                    r = null;
                    break;
                }
                world[i][j] = theme.get(0);
            }
        }
        if (r != null) {
            checkClosestRoom(r);
        }
    }

    private boolean edgeReached(int i, int j) {
        if (i <= 0 || i >= 49 || j <= 0 || j >= 49) {
            return true;
        }
        return false;
    }

    private void boundaryCheck(int i, int j) {
        // Checks if boundary is crossed.
        if (i >= UPPER_RIGHT_BORDER && j >= UPPER_RIGHT_BORDER) {
            urReached = true;
        } else if (i >= UPPER_RIGHT_BORDER && j <= LOWER_LEFT_BORDER) {
            lrReached = true;
        } else if (i <= LOWER_LEFT_BORDER && j >= UPPER_RIGHT_BORDER) {
            ulReached = true;
        } else if (i <= LOWER_LEFT_BORDER && j <= LOWER_LEFT_BORDER) {
            llReached = true;
        }

    }

    // Checks if the given room is a new closest room to an edge.
    private void checkClosestRoom(Room r) {
        bestULRoom = ClosestRoom.closestUL(r, bestULRoom);
        bestURRoom = ClosestRoom.closestUR(r, bestURRoom);
        bestLRRoom = ClosestRoom.closestLR(r, bestLRRoom);
        bestLLRoom = ClosestRoom.closestLL(r, bestLLRoom);
    }

    private void buildWalls() {
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                /*Check if the current tile is a floor
                 * tile. If it is, no need to add a wall
                 * and the loop continues to the next
                 * tile.*/
                if (world[x][y] == theme.get(0)) {
                    continue;
                }
                if (checkNeighbors(x, y)) {
                    world[x][y] = theme.get(1);
                }
                /*Check if the non-floor tile has floor neighbors.
                 * If it does, change it to a WALL tile.*/
            }
        }
    }

    /*Helps the buildWalls method by checking a tile for
    floor neighbors.*/
    private boolean checkNeighbors(int x, int y) {
        /*Check leftNeighbor exists and is a floor,
         * then return true.*/
        if ((x - 1) > 0 && (world[x - 1][y] == theme.get(0))) {
            return true;
        }
        if ((x + 1) < WIDTH && (world[x + 1][y] == theme.get(0))) {
            return true;
        }
        if ((y + 1) < HEIGHT && (world[x][y + 1] == theme.get(0))) {
            return true;
        }
        if ((y - 1) > 0 && (world[x][y - 1] == theme.get(0))) {
            return true;
        }
        return false;
    }

    private void makeTheme() {
        Theme t = new Theme();
        double prob = RandomUtils.uniform(random);
        if (prob < 0.25) {
            theme = t.THEMES.get("desert");
            themeType = "desert";
        } else if (prob >= 0.25 && prob < 0.5) {
            theme = t.THEMES.get("island");
            themeType = "island";
        } else if (prob >= 0.5 && prob < 0.75) {
            theme = t.THEMES.get("jungle");
            themeType = "jungle";
        } else {
            theme = t.THEMES.get("mountain");
            themeType = "mountain";
        }
    }

    private void createClosedDoor() {
        int x = RandomUtils.uniform(random, WIDTH);
        int y = RandomUtils.uniform(random, HEIGHT);
        while (true) {
            if (!outOfBounds(x, y + 1) && !outOfBounds(x + 1, y)
                    && !outOfBounds(x, y - 1) && !outOfBounds(x - 1, y)) {
                if (world[x][y] == theme.get(1)) {
                    if ((world[x-1][y] == theme.get(1)) && (world[x+1][y] == theme.get(1))) {
                        if (world[x][y+1] == theme.get(0)) {
                            if (world[x][y-1] == theme.get(2)) {
                                world[x][y] = LOCKED_DOOR;
                                break;
                            }
                        } else if (world[x][y-1] == theme.get(0)) {
                            if (world[x][y+1] == theme.get(2)) {
                                world[x][y] = LOCKED_DOOR;
                                break;
                            }
                        }
                    } else if ((world[x][y-1] == theme.get(1)) && (world[x][y+1] == theme.get(1))) {
                        if (world[x+1][y] == theme.get(0)) {
                            if (world[x-1][y] == theme.get(2)) {
                                world[x][y] = LOCKED_DOOR;
                                break;
                            }
                        } else if (world[x-1][y] == theme.get(0)) {
                            if (world[x+1][y] == theme.get(2)) {
                                world[x][y] = LOCKED_DOOR;
                                break;
                            }
                        }
                    }
                }
            }
            x = RandomUtils.uniform(random, WIDTH);
            y = RandomUtils.uniform(random, WIDTH);
        }
        xxDoorLoc = x;
        yyDoorLoc = y;
    }
}
