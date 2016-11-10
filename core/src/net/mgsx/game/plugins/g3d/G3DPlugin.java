package net.mgsx.game.plugins.g3d;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;

import net.mgsx.game.core.GameEngine;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.components.Movable;
import net.mgsx.game.core.plugins.Plugin;
import net.mgsx.game.plugins.g3d.components.G3DModel;
import net.mgsx.game.plugins.g3d.components.TextureAnimationComponent;
import net.mgsx.game.plugins.g3d.systems.G3DAnimationSystem;
import net.mgsx.game.plugins.g3d.systems.G3DCullingSystem;
import net.mgsx.game.plugins.g3d.systems.G3DRendererSystem;
import net.mgsx.game.plugins.g3d.systems.G3DTextureAnimationSystem;
import net.mgsx.game.plugins.g3d.systems.G3DTransformSystem;

@PluginDef(components={G3DModel.class, TextureAnimationComponent.class})

public class G3DPlugin implements Plugin
{
	@Override
	public void initialize(final GameEngine engine) 
	{
		// systems
		
		engine.entityEngine.addSystem(new G3DAnimationSystem());
		engine.entityEngine.addSystem(new G3DTransformSystem());
		engine.entityEngine.addSystem(new G3DCullingSystem(engine.gameCamera));
		engine.entityEngine.addSystem(new G3DRendererSystem(engine));
		engine.entityEngine.addSystem(new G3DTextureAnimationSystem());
       
       
		// TODO just a patch for movable ... and blending ...
		engine.entityEngine.addEntityListener(Family.one(G3DModel.class).get(), new EntityListener() {
			
			@Override
			public void entityRemoved(Entity entity) {
					entity.remove(Movable.class);
			}
			
			@Override
			public void entityAdded(Entity entity) {
				G3DModel model = entity.getComponent(G3DModel.class);
				model.applyBlending();
			}
		});
		

		
	}
}
