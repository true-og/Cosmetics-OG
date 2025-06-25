package cosmeticsOG.particles.effects;

import cosmeticsOG.particles.Effect;
import cosmeticsOG.particles.properties.ParticleLocation;
import cosmeticsOG.particles.properties.ParticleTracking;
import java.util.Arrays;
import java.util.List;

// TODO: [Future] Finish Animated Effect

public class AnimatedEffect extends Effect {

    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getDisplayName() {
        return "";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public int getParticlesSupported() {
        return 0;
    }

    @Override
    public ParticleLocation getDefaultLocation() {
        return ParticleLocation.CHEST;
    }

    @Override
    public List<ParticleTracking> getSupportedTrackingMethods() {
        return Arrays.asList(ParticleTracking.values());
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
    public void build() {}
}
