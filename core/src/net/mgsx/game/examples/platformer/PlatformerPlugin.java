package net.mgsx.game.examples.platformer;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.Plugin;
import net.mgsx.game.examples.platformer.components.BeeComponent;
import net.mgsx.game.examples.platformer.components.BonusComponent;
import net.mgsx.game.examples.platformer.components.CavernComponent;
import net.mgsx.game.examples.platformer.components.ClimbZone;
import net.mgsx.game.examples.platformer.components.EatState;
import net.mgsx.game.examples.platformer.components.EnemyComponent;
import net.mgsx.game.examples.platformer.components.EnvComponent;
import net.mgsx.game.examples.platformer.components.FallingPlatform;
import net.mgsx.game.examples.platformer.components.FlyingState;
import net.mgsx.game.examples.platformer.components.LianaZone;
import net.mgsx.game.examples.platformer.components.LiquidComponent;
import net.mgsx.game.examples.platformer.components.MagnetComponent;
import net.mgsx.game.examples.platformer.components.OneWay;
import net.mgsx.game.examples.platformer.components.PlatformComponent;
import net.mgsx.game.examples.platformer.components.PlayerComponent;
import net.mgsx.game.examples.platformer.components.PulleyComponent;
import net.mgsx.game.examples.platformer.components.SpeedWalk;
import net.mgsx.game.examples.platformer.components.SpiderComponent;
import net.mgsx.game.examples.platformer.components.TreeComponent;
import net.mgsx.game.examples.platformer.components.WalkingComponent;
import net.mgsx.game.examples.platformer.components.WaterZone;
import net.mgsx.game.examples.platformer.systems.CavernSystem;
import net.mgsx.game.examples.platformer.systems.EnvSystem;
import net.mgsx.game.examples.platformer.systems.FallingPlatformSystem;
import net.mgsx.game.examples.platformer.systems.GravityWalkSystem;
import net.mgsx.game.examples.platformer.systems.LiquidSystem;
import net.mgsx.game.examples.platformer.systems.MagnetSystem;
import net.mgsx.game.examples.platformer.systems.OneWaySystem;
import net.mgsx.game.examples.platformer.systems.PixelEffectBindSystem;
import net.mgsx.game.examples.platformer.systems.PlatformerHUDSystem;
import net.mgsx.game.examples.platformer.systems.PlatformerPostProcessing;
import net.mgsx.game.examples.platformer.systems.PulleySystem;
import net.mgsx.game.examples.platformer.systems.SpeedWalkSystem;
import net.mgsx.game.examples.platformer.systems.SpiderSystem;
import net.mgsx.game.examples.platformer.systems.WalkingSystem;
import net.mgsx.game.examples.platformer.systems.input.KeyboardControllerSystem;
import net.mgsx.game.examples.platformer.systems.states.EatSystem;
import net.mgsx.game.examples.platformer.systems.states.FlyingControlSystem;
import net.mgsx.game.examples.platformer.systems.states.FlyingSystem;
import net.mgsx.game.plugins.DefaultPlugin;
import net.mgsx.game.plugins.boundary.components.BoundaryComponent;
import net.mgsx.game.plugins.boundary.systems.AbstractBoundaryLogicSystem;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.g3d.components.G3DModel;
import net.mgsx.game.plugins.spline.components.PathComponent;

/**
 * 
 * Game core plugin
 * 
 * @author mgsx
 *
 */
@PluginDef(components={
		BeeComponent.class,
		BonusComponent.class,
		CavernComponent.class,
		ClimbZone.class,
		EnemyComponent.class,
		EnvComponent.class,
		LianaZone.class,
		PlatformComponent.class,
		PlayerComponent.class,
		PulleyComponent.class,
		SpiderComponent.class,
		TreeComponent.class,
		WaterZone.class,
		FlyingState.class,
		EatState.class,
		SpeedWalk.class,
		OneWay.class,
		LiquidComponent.class,
		MagnetComponent.class,
		WalkingComponent.class,
		FallingPlatform.class
})
public class PlatformerPlugin implements Plugin, DefaultPlugin
{
	public GameScreen engine;
	
	@Override
	public void initialize(GameScreen engine) 
	{
		this.engine = engine;
		
		PlatformerPostProcessing effects = new PlatformerPostProcessing(engine);
		
		engine.entityEngine.addSystem(effects);
		engine.entityEngine.addSystem(new PixelEffectBindSystem(effects));
		engine.entityEngine.addSystem(new SpeedWalkSystem());
		engine.entityEngine.addSystem(new OneWaySystem());
		engine.entityEngine.addSystem(new LiquidSystem());
		engine.entityEngine.addSystem(new MagnetSystem());
		engine.entityEngine.addSystem(new WalkingSystem());
		engine.entityEngine.addSystem(new FallingPlatformSystem());
		engine.entityEngine.addSystem(new GravityWalkSystem());
		engine.entityEngine.addSystem(new PlatformerHUDSystem(engine.assets));
		
		// add a processor for player
		// TODO could be automated with a generic component and abstract behavior attached to it ?
		engine.entityEngine.addSystem(new IteratingSystem(Family.all(PlayerComponent.class, Box2DBodyModel.class, G3DModel.class).get(), GamePipeline.LOGIC) {
			@Override
			protected void processEntity(Entity entity, float deltaTime) 
			{
				// TODO do the update here !
				PlayerComponent pc = entity.getComponent(PlayerComponent.class);
				pc.update(deltaTime);
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
				Vector3 derivative = sc.path.derivativeAt(new Vector3(), pc.time);
				float dLength = derivative.len();
				if(dLength < 0.1f) dLength = 0.1f;
				if(dLength > 0)
					pc.time += 0.4f * deltaTime * pc.speed / dLength; // / derivative.len();
				else
					pc.time += deltaTime * pc.speed * 0.1f;
				if(pc.time > 1){ pc.time = 1 ; pc.speed = -0.5f; }
				if(pc.time < 0){ pc.time = 0 ; pc.speed = 0.5f; }
				Vector3 position = sc.path.valueAt(new Vector3(), pc.time);
				// update body position
				
				// bc.body.setAwake(true);
				
				//bc.body.setTransform(0, 0, 0);
				bc.body.setLinearVelocity((position.x - bc.body.getPosition().x)/deltaTime, (position.y - bc.body.getPosition().y)/deltaTime);
				//bc.body.setTransform(position.x, position.y, 0);
			}
			
		});
		
		// TODO how to generalize ? state machine ? just logic ?
		engine.entityEngine.addSystem(new IteratingSystem(Family.all(BonusComponent.class, BoundaryComponent.class).get(), GamePipeline.LOGIC) {
			@Override
			protected void processEntity(Entity entity, float deltaTime) {
				if(BoundaryComponent.components.get(entity).justOutside)
					entity.getComponent(BonusComponent.class).exit();
				else if(BoundaryComponent.components.get(entity).justInside)
					entity.getComponent(BonusComponent.class).enter();
			}
		});

		
		engine.entityEngine.addSystem(new PulleySystem());
		engine.entityEngine.addSystem(new CavernSystem(engine));
		engine.entityEngine.addSystem(new EnvSystem(engine));
		engine.entityEngine.addSystem(new SpiderSystem());
		engine.entityEngine.addSystem(AbstractBoundaryLogicSystem.create(EnemyComponent.class));
		
		engine.entityEngine.addSystem(new FlyingSystem());
		engine.entityEngine.addSystem(new EatSystem());
		
		engine.entityEngine.addSystem(new KeyboardControllerSystem());
		engine.entityEngine.addSystem(new FlyingControlSystem());
		
		
	}

	

}
