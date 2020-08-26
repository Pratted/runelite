package net.runelite.client.plugins.hallowedsepulchre;

import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

import java.util.*;
import java.util.stream.Collectors;

public class TimeUtil {



    public static Map<LocalPoint, Integer> bfs(final Client client) {

        // Collision plane data is only available in an instanced region.
        if (client.isInInstancedRegion()) {

            final int plane = client.getPlane();
            final CollisionData[] collisionMap = client.getCollisionMaps();

            if (collisionMap != null && collisionMap.length >= client.getPlane()) {
                final int[][] flags = collisionMap[plane].getFlags();

                int x = client.getLocalPlayer().getLocalLocation().getSceneX();
                int y = client.getLocalPlayer().getLocalLocation().getSceneY();

                return bfs(flags, x, y, 5).entrySet()
                        .stream()
                        .collect(Collectors.toMap(m -> LocalPoint.fromScene(m.getKey().getX(), m.getKey().getY()), Map.Entry::getValue));
            }
        }

        return Collections.emptyMap();
    }

    public static Map<Point, Integer> bfs(final int[][] flags, int x, int y, int maxTicks) {
        final Map<Point, Integer> solved = new HashMap<>();

        Set<Point> startingTiles = Collections.singleton(new Point(x, y));
        int ticks = 0;

        while (ticks < maxTicks) {

            // expand our search outwards
            final Set<Point> nextPoints = expand(flags, startingTiles, solved, ticks);

            // The next iteration will use all of the points we've just found.
            startingTiles = nextPoints;

            ticks++;
        }

        return solved;
    }


    public static Set<Point> expand(final int[][] flags, final Set<Point> startingTiles, final Map<Point, Integer> solved, int score) {

        final Set<Point> reachableTiles = new HashSet<>();

        for (final Point tile : startingTiles) {

            // avoid overlapping subproblems.
            if (solved.containsKey(tile)) {
                continue;
            }

            reachableTiles.addAll(getPoints1TickAway(flags, tile.getX(), tile.getY(), true));

            // cache result
            solved.put(tile, score);
        }

        return reachableTiles;
    }

    public static Set<WorldPoint> getTiles1TickAway(final Client client) {

        // Collision plane data is only available in an instanced region.
        if (client.isInInstancedRegion()) {
            final int plane = client.getPlane();
            final CollisionData[] collisionMap = client.getCollisionMaps();

            if (collisionMap != null && collisionMap.length >= client.getPlane()) {
                final int[][] flags = collisionMap[plane].getFlags();

                int x = client.getLocalPlayer().getLocalLocation().getSceneX();
                int y = client.getLocalPlayer().getLocalLocation().getSceneY();

                return getPoints1TickAway(flags, x, y, true).stream()
                        .map(point -> WorldPoint.fromScene(client, point.getX(), point.getY(), client.getPlane()))
                        .collect(Collectors.toSet());
            }
        }

        return Collections.emptySet();
    }

    public static Set<Point> getPoints1TickAway(int[][] flags, int x, int y, boolean hasRunEnergy) {

        // The player (P) may reach any of the following tiles (T) in 1 tick.
        //        TTTTT
        //        TTTTT
        //        TTPTT
        //        TTTTT
        //        TTTTT
        final Set<Point> reachablePoints = new HashSet<>();

        // U=Up, D=down, L=left, R=right

        // Since these are x,y coords, use Left/Right as suffix.
        Point L2U2 = new Point(x-2, y+2);
        Point L1U2 = new Point(x-1, y+2);
        Point U2 = new Point(x, y+2);
        Point R1U2 = new Point(x+1, y+2);
        Point R2U2 = new Point(x+2, y+2);

        Point L2U1 = new Point(x-2, y+1);
        Point L1U1 = new Point(x-1, y+1);
        Point U1 = new Point(x, y+1);
        Point R1U1 = new Point(x+1, y+1);
        Point R2U1 = new Point(x+2, y+1);

        Point L2 = new Point(x-2, y);
        Point L1 = new Point(x-1, y);
        Point R1 = new Point(x+1, y);
        Point R2 = new Point(x+2, y);

        Point L2D1 = new Point(x-2, y-1);
        Point L1D1 = new Point(x-1, y-1);
        Point D1 = new Point(x, y-1);
        Point R1D1 = new Point(x+1, y-1);
        Point R2D1 = new Point(x+2, y-1);

        Point L2D2 = new Point(x-2, y-2);
        Point L1D2 = new Point(x-1, y-2);
        Point D2 = new Point(x, y-2);
        Point R1D2 = new Point(x+1, y-2);
        Point R2D2 = new Point(x+2, y-2);

        // T
        // P
        if (isReachable(flags, U1))
        {
            reachablePoints.add(U1);
        }

        // .T
        // P.
        if (isReachable(flags, R1U1))
        {
            reachablePoints.add(R1U1);
        }

        // PT
        if (isReachable(flags, R1))
        {
            reachablePoints.add(R1);
        }

        // P.
        // .T
        if (isReachable(flags, R1D1))
        {
            reachablePoints.add(R1D1);
        }

        // P
        // T
        if (isReachable(flags, D1))
        {
            reachablePoints.add(D1);
        }

        // .P
        // T.
        if (isReachable(flags, L1D1))
        {
            reachablePoints.add(L1D1);
        }

        // TP
        if (isReachable(flags, L1))
        {
            reachablePoints.add(L1);
        }

        // T.
        // .P
        if (isReachable(flags, L1U1))
        {
            reachablePoints.add(L1U1);
        }

        // All points after this are 2+ tiles away so the player will not be
        // able to reach them if they do not have run energy.
        if (!hasRunEnergy) {
            return reachablePoints;
        }

        // T
        // O
        // P
        if (isReachable(flags, U2) && isReachable(flags, U1)) {
            reachablePoints.add(U2);
        }

        // OT
        // OO
        // P
        if (isReachable(flags, U2) && isReachable(flags, R1U2) &&
                isReachable(flags,U1) && isReachable(flags, R1U1))
        {
            reachablePoints.add(R1U2);
        }

        // OOT
        // OOO
        // POO
        if (isReachable(flags, U2) && isReachable(flags, R1U2) && isReachable(flags, R2U2) &&
                isReachable(flags,U1) && isReachable(flags, R1U1) && isReachable(flags, R2U1) &&
                isReachable(flags, R1) && isReachable(flags, R2))
        {
            reachablePoints.add(R2U2);
        }

        // .OT
        // PO
        if  (isReachable(flags, R1U1) && isReachable(flags, R2U1) &&
                isReachable(flags, R1))
        {
            reachablePoints.add(R2U1);
        }

        // POT
        if (isReachable(flags, R1) && isReachable(flags, R2)) {
            reachablePoints.add(R2);
        }

        // PO
        //  OT
        if (isReachable(flags, R1) &&
                isReachable(flags, R1D1) && isReachable(flags, R2D1))
        {
            reachablePoints.add(R2D1);
        }

        // POO
        // OOO
        // OOT
        if (isReachable(flags, R1) && isReachable(flags, R2) &&
                isReachable(flags, D1) && isReachable(flags, R1D1) && isReachable(flags, R2D1) &&
                isReachable(flags, D2) && isReachable(flags, R1D2) && isReachable(flags, R2D2))
        {
            reachablePoints.add(R2D2);
        }

        // P
        // OO
        // .T
        if (isReachable(flags, D1) && isReachable(flags, R1D1) &&
                 isReachable(flags, R1D2))
        {
            reachablePoints.add(R1D2);
        }

        // P
        // O
        // T
        if (isReachable(flags, D1) && isReachable(flags, D2)) {
            reachablePoints.add(D2);
        }

        // .P
        // OO
        // T.
        if (isReachable(flags, L1D1) && isReachable(flags, D1) &&
                isReachable(flags, L1D2))
        {
            reachablePoints.add(L1D2);
        }

        // OOP
        // OOO
        // TOO
        if (isReachable(flags, L2) && isReachable(flags, L1) &&
            isReachable(flags, L2D1) && isReachable(flags, L1D1) && isReachable(flags, D1) &&
                isReachable(flags, L2D2) && isReachable(flags, L1D2) && isReachable(flags, D2))
        {
            reachablePoints.add(L2D2);
        }

        // .OP
        // TO.
        if (isReachable(flags, L1) &&
                isReachable(flags, L2D1) && isReachable(flags, L1D1))
        {
            reachablePoints.add(L2D1);
        }

        // TOP
        if (isReachable(flags, L2) && isReachable(flags, L1))
        {
            reachablePoints.add(L2);
        }

        // TO
        // .OP
        if (isReachable(flags, L2U1) && isReachable(flags, L1U1) &&
                isReachable(flags, L1))
        {
            reachablePoints.add(L2U1);
        }

        // TOO
        // OOO
        // OOP
        if (isReachable(flags, L2U2) && isReachable(flags, L1U2) && isReachable(flags, U2) &&
                isReachable(flags, L2U1) && isReachable(flags, L1U1) && isReachable(flags, U1) &&
                isReachable(flags, L2) && isReachable(flags, L1))
        {
            reachablePoints.add(L2U2);
        }

        // T
        // OO
        //  P
        if (isReachable(flags, L1U2) &&
                isReachable(flags, L1U1) && isReachable(flags, U1))
        {
            reachablePoints.add(L1U2);
        }

        return reachablePoints;
    }



    public static boolean isReachable(int[][] flags, Point point) {
        return true;
//        return point.getX() >= 0 && point.getX() < flags.length &&
//                point.getY() >= 0 && point.getY() < flags[0].length &&
//                !CollisionDataFlag.Flag.hasMovementBlocking(flags[point.getX()][point.getY()]);
    }
}
