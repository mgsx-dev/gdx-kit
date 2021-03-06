package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.td.components.Aiming;
import net.mgsx.game.examples.td.components.Damage;
import net.mgsx.game.examples.td.components.Load;
import net.mgsx.game.examples.td.components.Poisoning;
import net.mgsx.game.examples.td.components.Shooter;
import net.mgsx.game.examples.td.components.Shot;
import net.mgsx.game.examples.td.components.SingleTarget;
import net.mgsx.game.examples.td.components.Stunning;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class ShooterSystem extends IteratingSystem
{
	public ShooterSystem() {
		super(Family.all(Shooter.class, Transform2DComponent.class).get(), GamePipeline.LOGIC);
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		// handle multiple cases :
		// - loading needs to be OK
		// - aiming need to be complete
		// - single target need a valid target (not null)
		// in all cases, a shooter create shot entities

		Load loading = Load.components.get(entity);
		if(loading != null && loading.reload > 0) return;
		
		Aiming aiming = Aiming.components.get(entity);
		if(aiming != null && !aiming.inSights) return;
		
		SingleTarget targeting = SingleTarget.components.get(entity);
		if(targeting != null && targeting.target == null) return;
		
		// all conditions are met, lets shoot then.
		Shooter shooter = Shooter.components.get(entity);
		if(targeting != null){
			for(int i=0 ; i<shooter.maxShots ; i++)
			{
				createShot(entity, targeting.target);
			}
		}
		else{
			// TODO get all targets in range (if range) and randomly choose (TODO maybe not randomly ...)
		}
		
		// update all status :
		// reload required
		if(loading != null){
			loading.reload += loading.reloadRequired;
		}
	}
	
	private Vector2 direction = new Vector2();

	private void createShot(Entity shootingEntity, Entity targetEntity) 
	{
		Shooter shooter = Shooter.components.get(shootingEntity);
		Transform2DComponent source = Transform2DComponent.components.get(shootingEntity);
		Transform2DComponent target = Transform2DComponent.components.get(targetEntity);
		
		// compute shot angle
		float angle = direction.set(target.position).sub(source.position).angle();
		
		
		Entity shotEntity = getEngine().createEntity();
		Shot shot = getEngine().createComponent(Shot.class);
		shot.start
			.set(0.2f, 0).rotate(angle) // XXX offset from tower center
			.add(source.position); // position
		shot.end.set(target.position);
		shotEntity.add(shot);
		
		shot.speed = shooter.speed;
		
		SingleTarget singleTarget = getEngine().createComponent(SingleTarget.class);
		singleTarget.target = targetEntity;
		shotEntity.add(singleTarget);
		
		// TODO maybe add more aspects (freezer, stunt, posion, ...)
		Damage towerDamage = Damage.components.get(shootingEntity);
		if(towerDamage != null){
			Damage shotDamage = getEngine().createComponent(Damage.class);
			shotDamage.amount = towerDamage.amount;
			shotEntity.add(shotDamage);
		}
		
		Stunning towerStunning = Stunning.components.get(shootingEntity);
		if(towerStunning != null)
		{
			if(MathUtils.random() < towerStunning.chance){
				Stunning shotStunning = getEngine().createComponent(Stunning.class);
				shotStunning.chance = 1; // XXX no more chance calculations, already done by shooter ...
				shotStunning.duration = towerStunning.duration;
				shotEntity.add(shotStunning);
			}
		}
		
		Poisoning poisoning = Poisoning.components.get(shootingEntity);
		if(poisoning != null)
		{
			Poisoning shotPoisoning = getEngine().createComponent(Poisoning.class);
			shotPoisoning.damageDuration = poisoning.damageDuration;
			shotPoisoning.damageSpeed = poisoning.damageSpeed;
			shotEntity.add(shotPoisoning);
		}
		
		getEngine().addEntity(shotEntity);
	}
}
