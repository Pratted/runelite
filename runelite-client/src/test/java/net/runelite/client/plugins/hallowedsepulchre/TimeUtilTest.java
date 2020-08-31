package net.runelite.client.plugins.hallowedsepulchre;

import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.assertTrue;

public class TimeUtilTest {

    @Test
    public void eric() {
        Rectangle rectangle = new Rectangle(2200, 6048, 128, 128);

        assertTrue(rectangle.contains(2240, 6100));
    }
//
//
//    private static int X = CollisionDataFlag.Flag.BLOCK_MOVEMENT_FLOOR.getFlag();
//
//    private static ImmutableMap<Point, Integer> grid5x5Empty;
//
//
//
//    @Test
//    public void eric() {
//
//        int[][] flags = {
//                //   Y ------->
//                {X, X, X, X, X},
//                {X, X, X, X, X},
//                {X, 0, 0, 0, X},
//                {X, X, X, X, X},
//                {X, X, X, X, X}
//        };
//
//        Set<Point> points = TimeUtil.getPoints1TickAway(flags, 2, 2, true);
//
//        Set<Point> expected = ImmutableSet.of(
//                new Point(2,1),
//                new Point(2,3)
//        );
//
//        assertEquals(expected, points);
//    }
//
//    @Test
//    public void getTiles1TickAway_allTiles() {
//        int[][] flags = {
//                {0, 0, 0, 0, 0},
//                {0, 0, 0, 0, 0},
//                {0, 0, 0, 0, 0},
//                {0, 0, 0, 0, 0},
//                {0, 0, 0, 0, 0}
//        };
//
//        Set<Point> points = TimeUtil.getPoints1TickAway(flags, 2, 2, true);
//
//        Set<Point> expected = ImmutableSet.of(
//                new Point(2,1),
//                new Point(2,3)
//        );
//
//        assertEquals(expected, points);
//    }
//
//    @Test
//    public void solve_allTiles() {
//        int[][] flags = {
//                {0, X, 0, 0, 0},
//                {0, 0, 0, 0, 0},
//                {0, 0, 0, 0, 0},
//                {0, 0, 0, 0, 0},
//                {0, 0, 0, 0, 0},
//
//        };
//
//        final Map<Point, Integer> points = TimeUtil.bfs(flags, 2, 2, 5);
//
//        final Point x0y0 = new Point(0, 0);
//
//        assertEquals(points.get(x0y0),2);
//    }
//
//    @Test
//    public void solve_2TicksAway() {
//        int[][] flags = {
//                //     P
//                {0, 0, 0, 0, 0, 0}
//        };
//
//        final Map<Point, Integer> points = TimeUtil.bfs(flags, 0, 2, 5);
//
//        final Point x0y5 = new Point(0, 5);
//
//        assertEquals(points.get(x0y5),2);
//    }
}
