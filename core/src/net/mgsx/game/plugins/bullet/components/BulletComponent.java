package net.mgsx.game.plugins.bullet.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Pool.Poolable;

import net.mgsx.game.core.annotations.EditableComponent;

/**
 * This component keep tracks of bullet data for an entity.
 * 
 * Physical properties are never set for objects. these properties (friction, restitution, damping) could be set
 * on component creation or in a dedicated listener.
 * 
 * @author mgsx
 *
 */
@EditableComponent(autoTool=false)
public class BulletComponent implements Component, Poolable
{
	
	public final static ComponentMapper<BulletComponent> components = ComponentMapper.getFor(BulletComponent.class);
	
	public transient btCollisionObject object;

	public transient btCollisionShape shape;

	public transient btCollisionWorld world;

	/** physical properties for dynamic body. SI based unit : volume in m³, mass in Kg, density in Kg/m³.
	 * All 3 valumes are stored here for convenience but only 2 is required (the third can be derived from others).
	 * @see https://en.wikipedia.org/wiki/International_System_of_Units */
	public transient float density, volume, mass;

	/** only for dynamic bodies */
	public transient Vector3 inertia = new Vector3();
	
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
		
		density = mass = volume = 0;
		inertia.setZero();
	}
	
}
