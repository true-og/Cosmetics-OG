package cosmeticsOG.particles.effects;

import cosmeticsOG.locale.Message;
import cosmeticsOG.particles.Effect;
import cosmeticsOG.particles.Hat;
import cosmeticsOG.particles.ParticleEffect;
import cosmeticsOG.particles.properties.ParticleLocation;
import cosmeticsOG.particles.properties.ParticleTracking;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class RainbowHaloEffect extends Effect {

    private static final Color[] RAINBOW_COLORS = { Color.fromRGB(255, 82, 82), Color.fromRGB(255, 171, 64),
            Color.fromRGB(255, 238, 88), Color.fromRGB(105, 240, 174), Color.fromRGB(64, 196, 255),
            Color.fromRGB(124, 77, 255), Color.fromRGB(255, 64, 129) };
    private static final int POINT_COUNT = 18;
    private static final double RADIUS = 0.8D;
    private static final float DUST_SCALE = 0.7F;

    private final List<Vector> ringPoints = new ArrayList<>();

    @Override
    public String getName() {

        return "rainbow_halo";

    }

    @Override
    public String getDisplayName() {

        return Message.TYPE_RAINBOW_HALO_NAME.getValue();

    }

    @Override
    public String getDescription() {

        return Message.TYPE_RAINBOW_HALO_DESCRIPTION.getValue();

    }

    @Override
    public int getParticlesSupported() {

        return 1;

    }

    @Override
    public ParticleLocation getDefaultLocation() {

        return ParticleLocation.HEAD;

    }

    @Override
    public List<ParticleTracking> getSupportedTrackingMethods() {

        return Arrays.asList(ParticleTracking.values());

    }

    @Override
    public ParticleTracking getDefaultTrackingMethod() {

        return ParticleTracking.TRACK_NOTHING;

    }

    @Override
    public boolean supportsAnimation() {

        return false;

    }

    @Override
    public boolean isCustom() {

        return false;

    }

    @Override
    public void build() {

        ringPoints.clear();
        for (int i = 0; i < POINT_COUNT; i++) {

            final double angle = i * Math.PI * 2.0D / POINT_COUNT;
            ringPoints.add(new Vector(Math.cos(angle) * RADIUS, 0.0D, Math.sin(angle) * RADIUS));

        }

        final List<List<Vector>> frames = createEmptyFrames();
        frames.add(new ArrayList<>(ringPoints));
        setFrames(frames);

    }

    @Override
    public void display(int ticks, Entity entity, Hat hat) {

        if (ticks % hat.getUpdateFrequency() != 0) {

            return;

        }

        Location location = entity.getLocation();
        if (hat.getTrackingMethod() == ParticleTracking.TRACK_HEAD_MOVEMENT && entity instanceof Player) {

            location = ((Player) entity).getEyeLocation();

        }

        final double yaw = Math.toRadians(location.getYaw());
        final double cos = Math.cos(yaw);
        final double sin = Math.sin(yaw);

        final Vector offset = hat.getOffset();
        final double offsetX = ((offset.getX() * cos) - (offset.getZ() * sin));
        final double offsetZ = ((offset.getX() * sin) + (offset.getZ() * cos));

        final Vector angle = hat.getAngle();
        final double angleX = Math.toRadians(angle.getX());
        final double angleY = Math.toRadians(angle.getY());
        final double angleZ = Math.toRadians(angle.getZ());

        final World world = location.getWorld();
        final int colorStep = ticks / 4;
        final double spin = ticks * 0.05D;
        final double spinCos = Math.cos(spin);
        final double spinSin = Math.sin(spin);

        for (int i = 0; i < ringPoints.size(); i++) {

            final Vector base = ringPoints.get(i).clone();
            final double rx = (base.getX() * spinCos) - (base.getZ() * spinSin);
            final double rz = (base.getX() * spinSin) + (base.getZ() * spinCos);
            Vector v = new Vector(rx, 0.0D, rz);
            v.multiply(hat.getScale());
            v = getAngleVector(angleX, angleY, angleZ, v);

            final Location target = location.clone().add(offsetX, 0.0D, offsetZ);
            target.add(getTrackingPosition(hat, v, location, cos, sin));

            final Color color = RAINBOW_COLORS[Math.floorMod(colorStep + i, RAINBOW_COLORS.length)];
            renderer.spawnParticleColor(world, ParticleEffect.REDSTONE, target, 1, 0.0D, 0.0D, 0.0D, 0.0D, color,
                    DUST_SCALE, true);

        }

    }

}
