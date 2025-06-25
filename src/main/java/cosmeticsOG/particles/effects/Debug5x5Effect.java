package cosmeticsOG.particles.effects;

import cosmeticsOG.particles.properties.ParticleLocation;
import cosmeticsOG.particles.properties.ParticleTracking;
import cosmeticsOG.util.ResourceUtil;
import java.util.Arrays;
import java.util.List;

public class Debug5x5Effect extends PixelEffect {

    public Debug5x5Effect() {
        super(ResourceUtil.getImage("debug_5x5.png"), "debug_5x5");
    }

    @Override
    public String getName() {
        return "debug_5x5";
    }

    @Override
    public String getDisplayName() {
        return "Debug 5x5";
    }

    @Override
    public String getDescription() {
        return "&cYou're not supposed to see this";
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
}
