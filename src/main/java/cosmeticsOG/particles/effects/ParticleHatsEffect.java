package cosmeticsOG.particles.effects;

import net.trueog.utilitiesog.UtilitiesOG;
import cosmeticsOG.util.ResourceUtil;

public class ParticleHatsEffect extends CommunityEffect {

    public ParticleHatsEffect() {

        super(ResourceUtil.getImage("particlehats.png"), "particlehats", UtilitiesOG.trueogColorize("&6ParticleHats"),
                UtilitiesOG.trueogColorize("&7Medius_Echo"));

    }

}
