package net.mgsx.game.examples.openworld.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.utils.Pool.Poolable;

public class LandMeshComponent implements Component, Poolable
{
	
	public final static ComponentMapper<LandMeshComponent> components = ComponentMapper.getFor(LandMeshComponent.class);
	
	public Mesh mesh;

	@Override
	public void reset() {
		if(mesh != null) {
			mesh.dispose();
			mesh = null;
		}
	}
}
