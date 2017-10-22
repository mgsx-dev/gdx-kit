package net.mgsx.game.examples.shmup.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;

import net.mgsx.game.blueprint.model.Graph;
import net.mgsx.game.blueprint.model.Graph.CopyStrategy;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.examples.shmup.blueprint.StateNode;
import net.mgsx.game.examples.shmup.editors.ShmupBlueprintEditor;

@Storable("shmup.fsm")
@EditableComponent(autoClone=true)
public class Enemy implements Component, Poolable
{
	public final static ComponentMapper<Enemy> components = ComponentMapper.getFor(Enemy.class);
	
	@Editable(editor=ShmupBlueprintEditor.class)
	public Graph fsm = new Graph(CopyStrategy.FROM_SRC);
	
	public float life = 10;
	
	// TODO emitter position (relative) for emit logic
	
	public transient Array<StateNode> current = new Array<StateNode>();
	
	public void replace(StateNode origin, StateNode target){
		int i = current.indexOf(origin, true);
		current.removeIndex(i);
		if(target != null){
			current.insert(i, target);
		}
	}

	@Override
	public void reset() {
		fsm = new Graph(CopyStrategy.FROM_SRC);
		current.clear();
	}
}
