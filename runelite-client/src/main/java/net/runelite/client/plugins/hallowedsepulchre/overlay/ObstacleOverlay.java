package net.runelite.client.plugins.hallowedsepulchre.overlay;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.plugins.hallowedsepulchre.HallowedSepulchreConfig;
import net.runelite.client.plugins.hallowedsepulchre.HallowedSepulchrePlugin;
import net.runelite.client.plugins.hallowedsepulchre.model.HallowedSepulchreFloor;
import net.runelite.client.plugins.hallowedsepulchre.model.StrangeTile;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.ProgressPieComponent;

import javax.inject.Inject;
import java.awt.*;
import java.util.*;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

@Slf4j
public class ObstacleOverlay extends Overlay {

    private final Client client;
    private final HallowedSepulchrePlugin plugin;
    private final HallowedSepulchreConfig config;

    @Inject
    private ObstacleOverlay(
            Client client,
            HallowedSepulchrePlugin plugin,
            HallowedSepulchreConfig config) {
        super(plugin);

        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);

        this.client = client;
        this.plugin = plugin;
        this.config = config;

        getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Current Lap Time Overlay"));
    }

    @Override
    public Dimension render(Graphics2D graphics) {

        // We only want to show HS information if the player is in the HS or in the lobby.
        if (plugin.getActiveSession().isPresent()) {

            if (plugin.getActiveSession().get().getCurrentFloor().isPresent()) {
                final HallowedSepulchreFloor currentFloor = plugin.getActiveSession().get().getCurrentFloor().get();

                final Color outlineColor = config.highlightColor();

                // Skilling Obstacles
                if (config.highlightSepulchreSkilling()) {
                    currentFloor.getSkillingObstacles().forEach(skillingObstacle -> {
                        final Color color = currentFloor.hasTimeExpired() ?
                                Color.ORANGE :
                                outlineColor;

                        highlight(graphics, skillingObstacle, color, 2500);
                    });
                }

                if (config.highlightSepulchreSkilling()) {
                    currentFloor.getSkillChallenges().forEach(skillingChallenge -> {
                        skillingChallenge.getObstacles().forEach(skillingObstacle -> {
                            if (currentFloor.hasTimeExpired()) {
                                // highlight(graphics, skillingObstacle, Color.ORANGE);
                            } else if (!skillingChallenge.canComplete(client) && !skillingChallenge.isCompleted()) {
                                // highlight(graphics, skillingObstacle, Color.ORANGE);
                            } else {
                                // highlight(graphics, skillingObstacle, outlineColor);
                            }
                        });
                    });
                }

                // Staircases / Platforms / Gate
                if (config.highlightSepulchreStaircases()) {
                    currentFloor.getProgressionObstacles().forEach(staircase -> {
                        highlight(graphics, staircase, outlineColor, 2500);
                    });
                }

                // Statues
                if (config.highlightSepulchreStatues()) {
                    currentFloor.getStatues().forEach(statue -> {
                        if (statue.isAnimating()) {
                            highlight(graphics, statue.getGameObject(), outlineColor);
                        }
                    });
                }

                // Projectiles
                if (config.highlightSepulchreProjectiles()) {
                    currentFloor.getProjectiles().forEach(projectile -> {
                        highlightProjectile(graphics, projectile, outlineColor, 5000);
                    });
                }

                // Strange tiles
                if (config.highlightStrangeTiles()) {
                    currentFloor.getActiveStrangeTiles().forEach(strangeTile -> {
//                        Polygon poly = Perspective.getCanvasTilePoly(client, strangeTile.getLocalLocation());
//                        if (poly == null) {
//                            return;
//                        }
//
//                        Color blue = new Color(0, 50, 255, 100);
//                        Color yellow = new Color(255, 255, 10, 100);
//
//                        final Color color = (strangeTile.getColor().equals(StrangeTile.Color.BLUE)) ?
//                                blue :
//                                yellow;
//
//                        OverlayUtil.renderPolygon(graphics, poly, color);
//
//                        if (config.showStrangeTileTimers()) {
//                            final Point position = Perspective.localToCanvas(client, strangeTile.getLocalLocation(), client.getPlane());
//                            // Color color = new Color(255, 0, 0, 101);
//
//                            double progress = 1 - strangeTile.getProgress();
//
//                            int diameter = (int) ( poly.getBounds().getWidth() * .6);
//                            ProgressPieComponent pie = new ProgressPieComponent();
//                            pie.setDiameter(diameter);
//                            pie.setFill(color);
//                            pie.setBorderColor(Color.BLACK);
//                            pie.setPosition(position);
//                            pie.setProgress(progress);
//
//                            pie.render(graphics);
//                        }
                    });
                }
            }
        }

        return null;
    }

    private void highlight(final Graphics2D graphics, final TileObject gameObject, final Color color) {
        highlight(graphics, gameObject, color, 5000);
    }

    private void highlightProjectile(final Graphics2D graphics, final NPC projectile, final Color color, int maxDistance) {
        if (projectile.getComposition() != null) {
            // Swords are larger than 1 tile so this size ensure the actual projectile is highlighted.
            final int size = projectile.getComposition().getSize();
            final LocalPoint location = projectile.getLocalLocation();

            final Polygon outline = Perspective.getCanvasTileAreaPoly(client, location, size);
            if (outline != null) {
                OverlayUtil.renderPolygon(graphics, outline, color);
            }
        }
    }

    private void highlight(final Graphics2D graphics, final TileObject tileObject, final Color color, int maxDistance) {
        final Color fill = setAlpha(color, 50);
        final Color original = graphics.getColor();
        final LocalPoint location = client.getLocalPlayer().getLocalLocation();

        if (tileObject.getPlane() == client.getPlane() && location.distanceTo(tileObject.getLocalLocation()) < maxDistance) {
            Optional.ofNullable(tileObject.getClickbox()).ifPresent(clickbox -> {
                graphics.setColor(color);
                graphics.draw(clickbox);
                graphics.setColor(fill);
                graphics.fill(clickbox);
                graphics.setColor(original);
            });
        }
    }


    private Color setAlpha(final Color color, final int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }
}
