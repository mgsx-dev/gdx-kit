package net.mgsx.game.examples.shmup.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.utils.Pool.Poolable;

import net.mgsx.game.blueprint.model.Graph;
import net.mgsx.game.blueprint.model.Graph.CopyStrategy;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.examples.shmup.editors.ShmupBlueprintEditor;

// TODO emitter is not a component but a state ! which exit when emition is finished ...

// TODO links could be conditions : always check links (in graph) and trigger transition !
// OR use fork / join with parallel conditions ? ... inefficient ...
// OR have 2 node types : type action and type condition !

@EditableComponent
@Storable("shmup.emitter")
public class Emitter implements Component, Poolable
{
	public final static ComponentMapper<Emitter> components = ComponentMapper.getFor(Emitter.class);
	
	@Editable(editor=ShmupBlueprintEditor.class)
	public Graph fsm = new Graph(CopyStrategy.FROM_SRC);
	
	@Editable
	public void restart(){
		remains = count;
		timeout = 0;
	}
	
	@Editable
	public int count;
	
	@Editable
	public float delay;
	
	public transient int remains;
	public transient float timeout;

	@Override
	public void reset() {
		remains = 0;
		timeout = 0;
		count = 0;
		delay = 0;
	}
}
