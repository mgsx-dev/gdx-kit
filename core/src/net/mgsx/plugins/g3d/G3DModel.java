package net.mgsx.plugins.g3d;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Vector3;

public class G3DModel implements Component
{
	public ModelInstance modelInstance;
	public Vector3 origin = new Vector3(0,0,0); // TODO introduce a pre matrix ... ?
	
	// public Node node = new Node();
	
	
}
