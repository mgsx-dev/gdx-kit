package net.mgsx.game.plugins.btree;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.utils.Pool.Poolable;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.components.Duplicable;

@Storable("btree")
@EditableComponent(autoTool=false)
public class BTreeModel implements Component, Duplicable, Poolable
{
	
	public final static ComponentMapper<BTreeModel> components = ComponentMapper.getFor(BTreeModel.class);
	
	public String libraryName;
	public BehaviorTree<EntityBlackboard> tree;
	
	@Editable
	public boolean enabled = false; // XXX
	
	@Editable
	public boolean remove = false;
	
	@Override
	public Component duplicate(Engine engine) {
		BTreeModel clone = engine.createComponent(BTreeModel.class);
		clone.tree = engine.getSystem(BTreeSystem.class).createBehaviorTree(libraryName); 
		clone.libraryName = libraryName;
		return clone;
	}
	
	@Override
	public void reset() {
		// TODO release tree ?
		enabled = false;
	}

}
