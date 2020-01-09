package byow.Core;

import java.io.Serializable;

public class SubGrid implements Serializable {

    Position position;
    SubGrid top;
    SubGrid right;

    SubGrid(int x, int y) {
        position = new Position(x, y);
        top = null;
        right = null;
    }
}
