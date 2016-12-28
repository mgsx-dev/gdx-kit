package net.mgsx.game.examples.platformer.tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;

import net.mgsx.game.core.annotations.Asset;
import net.mgsx.game.plugins.btree.BTreePlugin.EntityLeafTask;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;
import net.mgsx.game.plugins.core.components.Transform2DComponent;
import net.mgsx.game.plugins.particle2d.components.Particle2DComponent;

@TaskAlias("particleAttach")
public class ParticleAttachTask extends EntityLeafTask
{
	@TaskAttribute
	@Asset(ParticleEffect.class)
	public String particle;
	
	private Entity particleEntity;
	
	@Override
	public void start() {
		particleEntity = getEngine().createEntity();
		Particle2DComponent p = getEngine().createComponent(Particle2DComponent.class);
		p.autoRemove = true;
		p.reference = particle;
		particleEntity.add(p);
		getEngine().addEntity(particleEntity);
	}
	
	@Override
	public com.badlogic.gdx.ai.btree.Task.Status execute() {
		Particle2DComponent p = Particle2DComponent.components.get(particleEntity);
		if(p != null && p.effect != null){
			
			Transform2DComponent transform = Transform2DComponent.components.get(getEntity());
			p.effect.setPosition(transform.position.x, transform.position.y); // TODO offset

			if(p.effect.isComplete()) return Status.SUCCEEDED;
		}
		return Status.RUNNING;
	}
	
	@Override
	public void end() {
		Particle2DComponent p = Particle2DComponent.components.get(particleEntity);
		if(p != null && p.effect != null){
			p.effect.allowCompletion();
		}
	}
}
