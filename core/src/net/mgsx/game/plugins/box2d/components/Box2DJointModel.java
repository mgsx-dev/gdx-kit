package net.mgsx.game.plugins.box2d.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool.Poolable;

import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.components.ContextualDuplicable;
import net.mgsx.game.core.storage.EntityGroup;
import net.mgsx.game.plugins.box2d.systems.Box2DWorldContext;

@Storable("box2d.joint")
public class Box2DJointModel implements Component, ContextualDuplicable, Poolable, Disposable
{
	
	public final static ComponentMapper<Box2DJointModel> components = ComponentMapper.getFor(Box2DJointModel.class);
	
	public String id;
	public JointDef def;
	// references to linked entities (used when added to engine)
	public int bodyA, bodyB;
	public Joint joint;
	public Box2DWorldContext context;
	
	public Box2DJointModel() {
	}
	public Box2DJointModel(String id, JointDef def, Joint joint) {
		super();
		this.id = id;
		this.def = def;
		this.joint = joint;
	}
	@Override
	public Component duplicate(Engine engine, EntityGroup sourceGroup, EntityGroup cloneGroup) 
	{
		Box2DJointModel clone = engine.createComponent(Box2DJointModel.class);
		clone.id = id;
		clone.def = def;
		clone.context = context;
		// case of template, we dereference entities
		if(def.bodyA == null || def.bodyB == null)
		{
			clone.def.bodyA = Box2DBodyModel.components.get(cloneGroup.get(bodyA)).body;
			clone.def.bodyB = Box2DBodyModel.components.get(cloneGroup.get(bodyB)).body;
		}
		return clone;
	}
	@Override
	public void reset() {
		dispose();
	}
	@Override
	public void dispose() {
		if(joint != null){
			context.remove(joint);
			joint = null;
		}
		context = null;
		def = null;
		id = null;
	}
	
}