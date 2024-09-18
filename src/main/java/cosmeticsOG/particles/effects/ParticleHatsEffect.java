package cosmeticsOG.particles.effects;

import cosmeticsOG.Utils;
import cosmeticsOG.util.ResourceUtil;

public class ParticleHatsEffect extends CommunityEffect {

	public ParticleHatsEffect() {

		super(ResourceUtil.getImage("particlehats.png"), "particlehats", Utils.legacySerializerAnyCase("&6ParticleHats"), Utils.legacySerializerAnyCase("&7Medius_Echo"));

	}

}