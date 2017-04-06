package net.mgsx.game.plugins.bullet.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Pool.Poolable;

import net.mgsx.game.core.annotations.EditableComponent;

@EditableComponent(autoTool=false)
public class BulletComponent implements Component, Poolable
{
	
	public final static ComponentMapper<BulletComponent> components = ComponentMapper.getFor(BulletComponent.class);
	
	public transient btCollisionObject object;

	public transient btCollisionShape shape;

	public transient btCollisionWorld world;
	
	@Override
	public void reset() 
	{
		if(object instanceof btRigidBody){
			if(world instanceof btDynamicsWorld){
				((btDynamicsWorld) world).removeRigidBody((btRigidBody) object);
			}else{
				world.removeCollisionObject(object);
			}
		}else{
			world.removeCollisionObject(object);
		}
		shape.dispose();
		shape = null;
		object.dispose();
		object = null;
		world = null;
		
	}
	
}
