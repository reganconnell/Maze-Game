package byow.Core;

import java.io.Serializable;

public class Room implements Serializable {

    Position ul;
    Position lr;
    Position ur;
    Position ll;

    public Room(Position ulPos, Position lrPos) {
        this.ul = ulPos;
        this.lr = lrPos;
        this.ur = new Position(lrPos.xxCoord, ulPos.yyCoord);
        this.ll = new Position(ulPos.xxCoord, lrPos.yyCoord);
    }


}
