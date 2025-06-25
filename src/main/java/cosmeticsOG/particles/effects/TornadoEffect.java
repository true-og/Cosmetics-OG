package cosmeticsOG.particles.effects;

import cosmeticsOG.locale.Message;
import cosmeticsOG.particles.Effect;
import cosmeticsOG.particles.properties.ParticleLocation;
import cosmeticsOG.particles.properties.ParticleTracking;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.util.Vector;

public class TornadoEffect extends Effect {

    @Override
    public String getName() {
        return "tornado";
    }

    @Override
    public String getDisplayName() {
        return Message.TYPE_TORNADO_NAME.getValue();
    }

    @Override
    public String getDescription() {
        return Message.TYPE_TORNADO_DESCRIPTION.getValue();
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
        return Arrays.asList(ParticleTracking.TRACK_NOTHING);
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
        double points = 16;
        double dist = 360.0f / points;

        List<Vector> frame1 = new ArrayList<Vector>();
        List<Vector> frame2 = new ArrayList<Vector>();

        for (double i = 0; i < 360; i += dist) {
            double angle = (i * Math.PI / 180);
            double x = 0.5 * Math.cos(angle);
            double z = 0.5 * Math.sin(angle);
            ;

            frame1.add(new Vector(x, 0, z));
        }

        for (double i = 0; i < 360; i += dist) {
            double angle = ((i + 50) * Math.PI / 180);
            double x = 0.25 * Math.cos(angle);
            double z = 0.25 * Math.sin(angle);
            ;

            frame2.add(new Vector(x, -0.1, z));
        }

        List<List<Vector>> frames = createEmptyFrames();
        frames.add(frame1);
        frames.add(frame2);

        setFrames(frames);
    }
}
