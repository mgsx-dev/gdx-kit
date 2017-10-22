package net.mgsx.game.examples.shmup.blueprint;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

import net.mgsx.game.blueprint.annotations.Inlet;
import net.mgsx.game.blueprint.annotations.Outlet;
import net.mgsx.game.blueprint.model.Graph;
import net.mgsx.game.blueprint.model.Graph.CopyStrategy;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.examples.shmup.component.Emitter;
import net.mgsx.game.examples.shmup.component.Enemy;
import net.mgsx.game.examples.shmup.editors.ShmupBlueprintEditor;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

@Inlet
public class Emit implements StateNode {

	@Outlet public transient StateNode over;
	
	// TODO should be a KIT group which may contains a FSM ... or other behavior model
	// because it could have sprite as well or models ... and box2D collisions .... !
	
	// TODO another emit (bullet emit could be programatic though)
	
	@Editable(editor=ShmupBlueprintEditor.class)
	public Graph fsm = new Graph(CopyStrategy.FROM_SRC);
	
	@Editable
	public int count;
	
	@Editable
	public float delay;
	
	@Override
	public void update(Engine engine, Entity entity, float deltaTime)
	{
		Emitter emitter = Emitter.components.get(entity);
		if(emitter == null){
			emitter = engine.createComponent(Emitter.class);
			emitter.remains = count;
			entity.add(emitter);
		}
		
		Transform2DComponent emitterTransform = Transform2DComponent.components.get(entity);
		if(emitter.remains > 0 || count < 0){
			emitter.timeout -= deltaTime;
			if(emitter.timeout < 0){
				emitter.timeout = delay;
				emitter.remains--;
				
				Entity e = engine.createEntity();
				Transform2DComponent trans = engine.createComponent(Transform2DComponent.class);
				Enemy enemy = engine.createComponent(Enemy.class);
				
				trans.position.set(emitterTransform.position);
				
				enemy.fsm = fsm;
				for(Init init : enemy.fsm.find(Init.class)){
					enemy.current.add(init);
				}
				
				engine.addEntity(e.add(trans).add(enemy));
			}
		}
		else
		{
			entity.remove(Emitter.class);
			Enemy enemy = Enemy.components.get(entity);
			enemy.replace(this, over);
		}
		
	}
}
