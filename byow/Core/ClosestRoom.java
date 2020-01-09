package byow.Core;

import java.io.Serializable;

public class ClosestRoom implements Serializable {

    private static final Position UL_EDGE = new Position(0, 59);
    private static final Position UR_EDGE = new Position(59, 59);
    private static final Position LR_EDGE = new Position(50, 0);
    private static final Position LL_EDGE = new Position(0, 0);

    // Sees if the given room has a smaller distance to the given than the current best room.
    // If so, updates the best room.
    static Room closestUL(Room r, Room best) {
        if (distance(r.ul, UL_EDGE) < distance(best.ul, UL_EDGE)) {
            return r;
        }
        return best;
    }

    static Room closestLR(Room r, Room best) {
        if (distance(r.lr, LR_EDGE) < distance(best.lr, LR_EDGE)) {
            return r;
        }
        return best;
    }

    static Room closestUR(Room r, Room best) {
        if (distance(r.ur, UR_EDGE) < distance(best.ur, UR_EDGE)) {
            return r;
        }
        return best;
    }

    static Room closestLL(Room r, Room best) {
        if (distance(r.ll, LL_EDGE) < distance(best.ll, LL_EDGE)) {
            return r;
        }
        return best;
    }

    private static double distance(Position p1, Position edge) {
        return Math.pow((edge.xxCoord - p1.xxCoord), 2) + Math.pow((edge.yyCoord - p1.yyCoord), 2);
    }
}
