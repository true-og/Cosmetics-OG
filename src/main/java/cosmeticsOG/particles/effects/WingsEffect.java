package cosmeticsOG.particles.effects;

import cosmeticsOG.locale.Message;
import cosmeticsOG.particles.properties.ParticleLocation;
import cosmeticsOG.particles.properties.ParticleTracking;
import cosmeticsOG.util.ResourceUtil;
import java.util.Arrays;
import java.util.List;

public class WingsEffect extends PixelEffect {

    public WingsEffect() {
        super(ResourceUtil.getImage("wings.png"), "wings");
    }

    @Override
    public String getName() {
        return "wings";
    }

    @Override
    public String getDisplayName() {
        return Message.TYPE_WINGS_NAME.getValue();
    }

    @Override
    public String getDescription() {
        return Message.TYPE_WINGS_DESCRIPTION.getValue();
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
