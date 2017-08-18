package net.mgsx.game.examples.rts;

import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.plugins.Plugin;
import net.mgsx.game.examples.rts.systems.BulletSystem;
import net.mgsx.game.examples.rts.systems.OrbitSystem;
import net.mgsx.game.examples.rts.systems.PlanetInfoRenderSystem;
import net.mgsx.game.examples.rts.systems.PlanetRenderSystem;
import net.mgsx.game.examples.rts.systems.PlanetSystem;
import net.mgsx.game.examples.rts.systems.RoadRenderSystem;
import net.mgsx.game.examples.rts.systems.RtsAISystem;
import net.mgsx.game.examples.rts.systems.RtsRenderSystem;
import net.mgsx.game.examples.rts.systems.SunRenderSystem;

public class RtsPlugin implements Plugin
{

	@Override
	public void initialize(GameScreen engine) 
	{
		engine.entityEngine.addSystem(new RtsAISystem());
		engine.entityEngine.addSystem(new BulletSystem());
		engine.entityEngine.addSystem(new PlanetSystem());
		engine.entityEngine.addSystem(new OrbitSystem());
		
		
		
		engine.entityEngine.addSystem(new SunRenderSystem(engine));
		engine.entityEngine.addSystem(new PlanetRenderSystem(engine));
		engine.entityEngine.addSystem(new PlanetInfoRenderSystem(engine));
		engine.entityEngine.addSystem(new RtsRenderSystem(engine));
		engine.entityEngine.addSystem(new RoadRenderSystem(engine));
		
	}

}
