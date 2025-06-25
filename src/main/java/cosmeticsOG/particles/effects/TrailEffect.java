package cosmeticsOG.particles.effects;

import cosmeticsOG.locale.Message;
import cosmeticsOG.particles.Effect;
import cosmeticsOG.particles.Hat;
import cosmeticsOG.particles.properties.ParticleLocation;
import cosmeticsOG.particles.properties.ParticleTracking;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class TrailEffect extends Effect {

    @Override
    public String getName() {
        return "trail";
    }

    @Override
    public String getDisplayName() {
        return Message.TYPE_TRAIL_NAME.getValue();
    }

    @Override
    public String getDescription() {
        return Message.TYPE_TRAIL_DESCRIPTION.getValue();
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
        return Arrays.asList(ParticleTracking.TRACK_NOTHING);
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
        // Nothing to do :o
    }

    @Override
    public void display(int ticks, Entity entity, Hat hat) {
        if (hat.canDisplay(ticks)) {
            Location location = entity.getLocation();
            double o = 0.3f;
            double x = getRandomPosition(o);
            double y = getRandomPosition(o);
            double z = getRandomPosition(o);

            location.add(x, y, z).add(hat.getTotalOffset());
            displayParticle(location, hat, 0);
        }
    }

    private double getRandomPosition(double offset) {
        return ((Math.random() * 2) - 1) * offset;
    }
}
