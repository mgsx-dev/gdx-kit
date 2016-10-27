package net.mgsx.game.examples.platformer.core;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.SplineTest.BlenderNURBSCurve;
import net.mgsx.game.core.GameEngine;
import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.helpers.EmptySerializer;
import net.mgsx.game.core.plugins.Plugin;
import net.mgsx.game.core.storage.Storage;
import net.mgsx.game.plugins.box2d.model.Box2DBodyModel;
import net.mgsx.game.plugins.g3d.G3DModel;
import net.mgsx.game.plugins.spline.PathComponent;

/**
 * 
 * Game core plugin
 * 
 * @author mgsx
 *
 */
public class PlatformerPlugin implements Plugin
{
	public GameEngine engine;
	public PlatformerPostProcessing ppp;
	
	@Override
	public void initialize(GameEngine engine) 
	{
		this.engine = engine;
		
		Storage.register(PlayerComponent.class, "example.platformer.player");
		engine.addSerializer(PlayerComponent.class, new EmptySerializer<PlayerComponent>());
		
		Storage.register(BonusComponent.class, "example.platformer.bonus");
		engine.addSerializer(BonusComponent.class, new EmptySerializer<BonusComponent>());
		
		Storage.register(EnemyComponent.class, "example.platformer.enemy");
		engine.addSerializer(EnemyComponent.class, new EmptySerializer<EnemyComponent>());
		
		Storage.register(TreeComponent.class, "example.platformer.tree");
		engine.addSerializer(TreeComponent.class, new EmptySerializer<TreeComponent>());
		
		Storage.register(ClimbZone.class, "example.platformer.climb-zone");
		engine.addSerializer(ClimbZone.class, new EmptySerializer<ClimbZone>());
		
		Storage.register(WaterZone.class, "example.platformer.water-zone");
		engine.addSerializer(WaterZone.class, new EmptySerializer<WaterZone>());
		
		Storage.register(LianaZone.class, "example.platformer.liana-zone");
		engine.addSerializer(LianaZone.class, new EmptySerializer<LianaZone>());
		
		Storage.register(PlatformComponent.class, "example.platformer.platform");
		engine.addSerializer(PlatformComponent.class, new EmptySerializer<PlatformComponent>());
		
		// add a processor for player
		// TODO could be automated with a generic component and abstract behavior attached to it ?
		engine.entityEngine.addSystem(new IteratingSystem(Family.all(PlayerComponent.class, Box2DBodyModel.class, G3DModel.class).get(), GamePipeline.LOGIC) {
			@Override
			protected void processEntity(Entity entity, float deltaTime) {
				PlayerComponent pc = entity.getComponent(PlayerComponent.class);
				pc.update(deltaTime);
			}
		});
		
		engine.entityEngine.addSystem(new IteratingSystem(Family.all(EnemyComponent.class, Box2DBodyModel.class, G3DModel.class).get(), GamePipeline.LOGIC) {
			@Override
			protected void processEntity(Entity entity, float deltaTime) {
				entity.getComponent(EnemyComponent.class).behavior.update(deltaTime);
				
			}
		});
		engine.entityEngine.addSystem(new IteratingSystem(Family.all(TreeComponent.class, Box2DBodyModel.class, G3DModel.class).get(), GamePipeline.LOGIC) {
			@Override
			protected void processEntity(Entity entity, float deltaTime) {
				entity.getComponent(TreeComponent.class).behavior.update(deltaTime);
			}
		});
		
		engine.entityEngine.addSystem(new IteratingSystem(Family.all(PlatformComponent.class, Box2DBodyModel.class, PathComponent.class).get(), GamePipeline.BEFORE_PHYSICS) {

			@Override
			protected void processEntity(Entity entity, float deltaTime) {
				
				// TODO put this in seprate class and cleanup hard coded things
				
				PlatformComponent pc = entity.getComponent(PlatformComponent.class);
				Box2DBodyModel bc = entity.getComponent(Box2DBodyModel.class);
				PathComponent sc = entity.getComponent(PathComponent.class);
				if(((BlenderNURBSCurve)sc.path.splines.get(0)).bs == null) return;
				Vector3 derivative =((BlenderNURBSCurve)sc.path.splines.get(0)).bs.derivativeAt(new Vector3(), pc.time);
				float dLength = derivative.len();
				if(dLength < 0.1f) dLength = 0.1f;
				if(dLength > 0)
					pc.time += 0.4f * deltaTime * pc.speed / dLength; // / derivative.len();
				else
					pc.time += deltaTime * pc.speed * 0.1f;
				if(pc.time > 1){ pc.time = 1 ; pc.speed = -0.5f; }
				if(pc.time < 0){ pc.time = 0 ; pc.speed = 0.5f; }
				Vector3 position =((BlenderNURBSCurve)sc.path.splines.get(0)).bs.valueAt(new Vector3(), pc.time);
				// update body position
				
				// bc.body.setAwake(true);
				
				//bc.body.setTransform(0, 0, 0);
				bc.body.setLinearVelocity((position.x - bc.body.getPosition().x)/deltaTime, (position.y - bc.body.getPosition().y)/deltaTime);
				//bc.body.setTransform(position.x, position.y, 0);
			}
			
		});
		
		engine.entityEngine.addSystem(new PulleySystem());
		
		ppp = new PlatformerPostProcessing(engine);
		
	}

}
