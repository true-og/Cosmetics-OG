package cosmeticsOG.particles.effects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.util.Vector;

import cosmeticsOG.locale.Message;
import cosmeticsOG.particles.Effect;
import cosmeticsOG.particles.properties.ParticleLocation;
import cosmeticsOG.particles.properties.ParticleTracking;

public class CleanTrailEffect extends Effect {

	@Override
	public String getName() {
		return "single_particle";
	}

	@Override
	public String getDisplayName() {
		return Message.TYPE_CLEAN_TRAIL_NAME.getValue();
	}

	@Override
	public String getDescription() {
		return Message.TYPE_CLEAN_TRAIL_DESCRIPTION.getValue();
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

	@Override
	public void build() 
	{
		List<Vector> l = new ArrayList<Vector>();
		l.add(new Vector(0, 0, 0));

		List<List<Vector>> frames = createEmptyFrames();
		frames.add(l);

		setFrames(frames);
	}

}
