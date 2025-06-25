package cosmeticsOG.particles.effects;

import cosmeticsOG.locale.Message;
import cosmeticsOG.particles.Effect;
import cosmeticsOG.particles.properties.ParticleLocation;
import cosmeticsOG.particles.properties.ParticleTracking;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.util.Vector;

public class VortexEffect extends Effect {

    @Override
    public String getName() {
        return "vortex";
    }

    @Override
    public String getDisplayName() {
        return Message.TYPE_VORTEX_NAME.getValue();
    }

    @Override
    public String getDescription() {
        return Message.TYPE_VORTEX_DESCRIPTION.getValue();
    }

    @Override
    public int getParticlesSupported() {
        return 2;
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
        return ParticleTracking.TRACK_NOTHING;
    }

    @Override
    public boolean supportsAnimation() {
        return true;
    }

    @Override
    public boolean isCustom() {
        return false;
    }

    @Override
    public void build() {
        List<Vector> frame1 = new ArrayList<Vector>();
        List<Vector> frame2 = new ArrayList<Vector>();

        double radius = 5;

        for (double y = 0; y <= 3; y += 0.1) {
            radius = y / 3;
            double x = radius * Math.cos(3 * y);
            double z = radius * Math.sin(3 * y);
            double yy = 3 - y;

            frame1.add(new Vector(x, yy, z));
            frame2.add(new Vector(-x, yy, -z));
        }

        List<List<Vector>> frames = createEmptyFrames();
        frames.add(frame1);
        frames.add(frame2);

        setFrames(frames);
    }
}
