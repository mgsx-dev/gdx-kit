package net.mgsx.game.examples.openworld.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionWorld;
import com.badlogic.gdx.utils.Pool.Poolable;

import net.mgsx.game.examples.openworld.model.OpenWorldPool;

public class TreesComponent implements Component, Poolable
{
	
	public final static ComponentMapper<TreesComponent> components = ComponentMapper.getFor(TreesComponent.class);
	public Mesh mesh;
	
	public btCollisionObject collisionObject;
	public btCollisionWorld world;
	
	@Override
	public void reset() {
		OpenWorldPool.treesMeshPool.free(mesh);
		mesh = null;
		if(collisionObject != null){
			world.removeCollisionObject(collisionObject);
			collisionObject = null;
			world = null;
		}
	}
}
