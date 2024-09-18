package cosmeticsOG.particles.effects;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

import cosmeticsOG.particles.properties.ParticleLocation;
import cosmeticsOG.particles.properties.ParticleTracking;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

public abstract class CommunityEffect extends PixelEffect {

	private final TextComponent displayName;
	private final TextComponent credit;

	public CommunityEffect (BufferedImage image, String name, TextComponent displayName, TextComponent credit) {

		super(image, name);

		this.displayName = displayName;
		this.credit = credit.append(Component.text("&8by "));

	}

	@Override
	public String getName() {

		return name;

	}

	@Override
	public String getDisplayName() {

		return displayName.content();

	}

	@Override
	public String getDescription() {

		return credit.content();

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