package net.mgsx.game.plugins.p3d;

import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;

import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.plugins.Plugin;
import net.mgsx.game.core.storage.AssetSerializer;
import net.mgsx.game.plugins.p3d.systems.Particle3DRenderSystem;
import net.mgsx.game.plugins.p3d.systems.Particle3DSystem;

public class Particle3DPlugin implements Plugin{

	@Override
	public void initialize(final GameScreen engine) {
		engine.entityEngine.addSystem(new Particle3DSystem(engine));
		engine.entityEngine.addSystem(new Particle3DRenderSystem(engine));
		
		engine.registry.addSerializer(ParticleEffect.class, new AssetSerializer<ParticleEffect>(ParticleEffect.class));
	}

}
