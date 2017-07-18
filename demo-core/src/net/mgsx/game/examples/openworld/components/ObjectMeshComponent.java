package net.mgsx.game.examples.openworld.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Pool.Poolable;

import net.mgsx.game.examples.openworld.systems.UserObjectSystem.UserObject;

public class ObjectMeshComponent implements Component, Poolable
{
	
	public final static ComponentMapper<ObjectMeshComponent> components = ComponentMapper.getFor(ObjectMeshComponent.class);
	
	public Matrix4 transform = new Matrix4();
	
	public Mesh mesh;

	public UserObject userObject;

	@Override
	public void reset() {
		if(mesh != null) {
			mesh.dispose();
			mesh = null;
		}
	}
}
