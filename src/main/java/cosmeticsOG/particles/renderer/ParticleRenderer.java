package cosmeticsOG.particles.renderer;

import cosmeticsOG.particles.ParticleEffect;
import cosmeticsOG.particles.properties.ParticleData;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;

public interface ParticleRenderer {

    public void spawnParticle(
            World world,
            ParticleEffect particle,
            Location location,
            int count,
            double offsetX,
            double offsetY,
            double offsetZ,
            double extra);

    public void spawnParticleBlockData(
            World world,
            ParticleEffect particle,
            Location location,
            int count,
            double offsetX,
            double offsetY,
            double offsetZ,
            double extra,
            ParticleData data);

    public void spawnParticleItemData(
            World world,
            ParticleEffect particle,
            Location location,
            int count,
            double offsetX,
            double offsetY,
            double offsetZ,
            double extra,
            ParticleData data);

    public void spawnParticleColor(
            World world,
            ParticleEffect particle,
            Location location,
            int count,
            double offsetX,
            double offsetY,
            double offsetZ,
            double extra,
            Color color,
            double scale,
            boolean useDustOptions);

    void spawnParticleColorTransition(
            World world,
            ParticleEffect particle,
            Location location,
            int count,
            double offsetX,
            double offsetY,
            double offsetZ,
            double extra,
            Color fromColor,
            Color toColor,
            double scale);

    <T> void spawnParticle(
            World world,
            ParticleEffect particle,
            Location location,
            int count,
            double offsetX,
            double offsetY,
            double offsetZ,
            double extra,
            T data);
}
