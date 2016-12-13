package net.mgsx.game.examples.platformer.ai;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.examples.platformer.inputs.PlayerController;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.core.components.ExpiryComponent;
import net.mgsx.game.plugins.core.components.Transform2DComponent;
import net.mgsx.game.plugins.g3d.components.G3DModel;

public class MortarSystem extends IteratingSystem
{
	private GameScreen game;
	private ImmutableArray<Entity> players;
	
	public MortarSystem(GameScreen game) {
		super(Family.all(MortarComponent.class, MortarState.class, Transform2DComponent.class, G3DModel.class).get(), GamePipeline.LOGIC);
		this.game = game;
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		players = getEngine().getEntitiesFor(Family.all(PlayerController.class, Transform2DComponent.class).get());
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		MortarComponent mortar = MortarComponent.components.get(entity);
		Transform2DComponent parentTransform = Transform2DComponent.components.get(entity);
		G3DModel model = G3DModel.components.get(entity);
		if(mortar.time > 0){
			mortar.time -= deltaTime;
		}else{
			
			if(players.size() <= 0) return;
				
			Entity playerEntity = players.first();
			Transform2DComponent playerTransform = Transform2DComponent.components.get(playerEntity);
			
			if(playerTransform.position.dst(parentTransform.position) > mortar.distance) return;
			
			model.animationController.allowSameAnimation = true;
			model.animationController.animate(mortar.shootAnimation, 1, 4, null, 0.1f);
			for(Entity child : mortar.projectile.group.create(game.assets, game.entityEngine)){
				ExpiryComponent expiry = getEngine().createComponent(ExpiryComponent.class);
				expiry.time = mortar.expiry;
				child.add(expiry);
				Box2DBodyModel childPhysics = Box2DBodyModel.components.get(child);
				Transform2DComponent childTransform = Transform2DComponent.components.get(child);
				if(childPhysics != null){
					// body is null has it has not been created yet !
					childPhysics.def.type = BodyType.DynamicBody; // TODO sure ??
					if(childTransform != null){
						childTransform.position.add(parentTransform.position).add(mortar.offset);
					}
					childPhysics.def.linearVelocity.set(new Vector2(mortar.speed, 0).setAngle(mortar.angle));
					if(playerTransform.position.x < parentTransform.position.x)
						childPhysics.def.linearVelocity.x = -childPhysics.def.linearVelocity.x;
				}
			}
			mortar.time = mortar.reloadTime;
		}
	}
}
