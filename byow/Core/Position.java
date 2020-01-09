package byow.Core;

import java.io.Serializable;

public class Position implements Serializable {

    int xxCoord;
    int yyCoord;

    public Position(int x, int y) {
        this.xxCoord = x;
        this.yyCoord = y;
    }

}
