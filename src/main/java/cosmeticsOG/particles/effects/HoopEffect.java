package cosmeticsOG.particles.effects;

import cosmeticsOG.locale.Message;
import cosmeticsOG.particles.Effect;
import cosmeticsOG.particles.properties.ParticleLocation;
import cosmeticsOG.particles.properties.ParticleTracking;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.util.Vector;

public class HoopEffect extends Effect {

    @Override
    public String getName() {

        return "hoop";

    }

    @Override
    public String getDisplayName() {

        return Message.TYPE_HOOP_NAME.getValue();

    }

    @Override
    public String getDescription() {

        return Message.TYPE_HOOP_DESCRIPTION.getValue();

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

        return Arrays.asList(ParticleTracking.values());

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

        double points = 30;
        double dist = 360.0f / points;
        double radius = 0.8;

        List<Vector> frame1 = new ArrayList<Vector>();
        List<Vector> frame2 = new ArrayList<Vector>();

        for (double i = 0; i < 360; i += dist) {

            double angle = (i * Math.PI / 180);
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);

            frame1.add(new Vector(x, 0, z));
            frame2.add(new Vector(-x, 0, -z));

        }

        List<List<Vector>> frames = createEmptyFrames();
        frames.add(frame1);
        frames.add(frame2);

        setFrames(frames);

    }

}
