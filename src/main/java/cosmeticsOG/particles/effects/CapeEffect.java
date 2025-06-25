package cosmeticsOG.particles.effects;

import cosmeticsOG.locale.Message;
import cosmeticsOG.particles.Effect;
import cosmeticsOG.particles.properties.ParticleLocation;
import cosmeticsOG.particles.properties.ParticleTracking;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.util.Vector;

public class CapeEffect extends Effect {

    @Override
    public String getName() {
        return "cape";
    }

    @Override
    public String getDisplayName() {
        return Message.TYPE_CAPE_NAME.getValue();
    }

    @Override
    public String getDescription() {
        return Message.TYPE_CAPE_DESCRIPTION.getValue();
    }

    @Override
    public int getParticlesSupported() {
        return 1;
    }

    @Override
    public ParticleLocation getDefaultLocation() {
        return ParticleLocation.FEET;
    }

    @Override
    public List<ParticleTracking> getSupportedTrackingMethods() {
        return Arrays.asList(ParticleTracking.TRACK_NOTHING, ParticleTracking.TRACK_BODY_ROTATION);
    }

    @Override
    public ParticleTracking getDefaultTrackingMethod() {
        return ParticleTracking.TRACK_BODY_ROTATION;
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
        double zoffset = 0.1;
        double xoffset = 0.02;
        double[] xpoints = new double[] {-0.32, -0.16, 0, 0.16, 0.32};

        List<Vector> points = new ArrayList<Vector>();

        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 6; y++) {
                points.add(new Vector(xpoints[x] - (x * xoffset), (-y * 0.18), -0.28 - (y * zoffset)));
            }
        }

        List<List<Vector>> frames = createEmptyFrames();
        frames.add(points);

        setFrames(frames);
    }
}
