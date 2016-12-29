package net.mgsx.game.examples.platformer;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.Plugin;
import net.mgsx.game.examples.platformer.ai.MortarSystem;
import net.mgsx.game.examples.platformer.ai.PatrolSystem;
import net.mgsx.game.examples.platformer.animations.CavernComponent;
import net.mgsx.game.examples.platformer.animations.CavernSystem;
import net.mgsx.game.examples.platformer.animations.Character2DModelSystem;
import net.mgsx.game.examples.platformer.animations.EnvComponent;
import net.mgsx.game.examples.platformer.animations.EnvSystem;
import net.mgsx.game.examples.platformer.animations.FlyingAnimationSystem;
import net.mgsx.game.examples.platformer.animations.PlatformComponent;
import net.mgsx.game.examples.platformer.animations.PlatformerCameraSystem;
import net.mgsx.game.examples.platformer.animations.TubeWorldSystem;
import net.mgsx.game.examples.platformer.animations.WalkingAnimationSystem;
import net.mgsx.game.examples.platformer.animations.WalkingComponent;
import net.mgsx.game.examples.platformer.audio.CristalSoundSystem;
import net.mgsx.game.examples.platformer.audio.PlatformerMusicSystem;
import net.mgsx.game.examples.platformer.audio.WaterSoundSystem;
import net.mgsx.game.examples.platformer.inputs.JoystickControllerSystem;
import net.mgsx.game.examples.platformer.inputs.KeyboardControllerSystem;
import net.mgsx.game.examples.platformer.inputs.WalkingSystem;
import net.mgsx.game.examples.platformer.logic.BonusComponent;
import net.mgsx.game.examples.platformer.logic.EnemyComponent;
import net.mgsx.game.examples.platformer.logic.PlayerComponent;
import net.mgsx.game.examples.platformer.logic.PlayerSensorSystem;
import net.mgsx.game.examples.platformer.logic.SecretRenderSystem;
import net.mgsx.game.examples.platformer.logic.SpiderComponent;
import net.mgsx.game.examples.platformer.logic.SpiderSystem;
import net.mgsx.game.examples.platformer.logic.TreeComponent;
import net.mgsx.game.examples.platformer.logic.TreeSystem;
import net.mgsx.game.examples.platformer.physics.FallingPlatform;
import net.mgsx.game.examples.platformer.physics.FallingPlatformSystem;
import net.mgsx.game.examples.platformer.physics.GravityWalkSystem;
import net.mgsx.game.examples.platformer.physics.LiquidComponent;
import net.mgsx.game.examples.platformer.physics.LiquidSystem;
import net.mgsx.game.examples.platformer.physics.MagnetComponent;
import net.mgsx.game.examples.platformer.physics.MagnetSystem;
import net.mgsx.game.examples.platformer.physics.OneWay;
import net.mgsx.game.examples.platformer.physics.OneWaySystem;
import net.mgsx.game.examples.platformer.physics.PlayerPhysicSensorListener;
import net.mgsx.game.examples.platformer.physics.PulleyComponent;
import net.mgsx.game.examples.platformer.physics.PulleySystem;
import net.mgsx.game.examples.platformer.physics.SpeedWalk;
import net.mgsx.game.examples.platformer.physics.SpeedWalkSystem;
import net.mgsx.game.examples.platformer.rendering.PixelEffectBindSystem;
import net.mgsx.game.examples.platformer.rendering.PlatformerPostProcessing;
import net.mgsx.game.examples.platformer.rendering.PlatformerRenderSystem;
import net.mgsx.game.examples.platformer.sensors.ClimbZone;
import net.mgsx.game.examples.platformer.sensors.LianaZone;
import net.mgsx.game.examples.platformer.sensors.WaterZone;
import net.mgsx.game.examples.platformer.states.EatState;
import net.mgsx.game.examples.platformer.states.EatSystem;
import net.mgsx.game.examples.platformer.states.FlyingControlSystem;
import net.mgsx.game.examples.platformer.states.FlyingState;
import net.mgsx.game.examples.platformer.ui.PlatformerHUDSystem;
import net.mgsx.game.plugins.DefaultPlugin;
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
		engine.entityEngine.addSystem(new PlatformerCameraSystem());
		engine.entityEngine.addSystem(new CristalSoundSystem());
		engine.entityEngine.addSystem(new PlatformerMusicSystem(engine));
		
		engine.entityEngine.addSystem(new WaterSoundSystem(engine));
		engine.entityEngine.addSystem(new JoystickControllerSystem());
		
		// TODO HUD should not be in level screen
		engine.entityEngine.addSystem(new PlatformerHUDSystem(engine.assets));
		
		engine.entityEngine.addSystem(new PlatformerRenderSystem());
		
		engine.entityEngine.addSystem(new Character2DModelSystem());
		engine.entityEngine.addSystem(new WalkingAnimationSystem());
		engine.entityEngine.addSystem(new PatrolSystem());
		engine.entityEngine.addSystem(new TubeWorldSystem());
		engine.entityEngine.addSystem(new MortarSystem(engine));
		engine.entityEngine.addSystem(new PlayerSensorSystem());
		
		engine.entityEngine.addSystem(new SecretRenderSystem(engine));

		engine.entityEngine.addSystem(new PlayerPhysicSensorListener());
		
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
		
		
		engine.entityEngine.addSystem(new PulleySystem());
		engine.entityEngine.addSystem(new CavernSystem(engine));
		engine.entityEngine.addSystem(new EnvSystem(engine));
		engine.entityEngine.addSystem(new SpiderSystem());
		engine.entityEngine.addSystem(AbstractBoundaryLogicSystem.create(EnemyComponent.class));
		
		engine.entityEngine.addSystem(new FlyingAnimationSystem());
		engine.entityEngine.addSystem(new EatSystem());
		
		engine.entityEngine.addSystem(new KeyboardControllerSystem());
		engine.entityEngine.addSystem(new FlyingControlSystem());
		engine.entityEngine.addSystem(new TreeSystem());
		
		
	}

	

}
