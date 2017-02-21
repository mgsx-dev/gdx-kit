package net.mgsx.game.plugins.spline.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Path;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.tools.MultiClickTool;
import net.mgsx.game.plugins.spline.components.PathComponent;
import net.mgsx.game.plugins.spline.components.SplineDebugComponent;

public abstract class AbstractPathTool extends MultiClickTool
{
	private Entity entity;
	private int minDots;
	
	public AbstractPathTool(String name, EditorScreen editor, int minDots) {
		super(name, editor);
		this.minDots = minDots;
	}
	
	@Override
	protected void activate() {
		super.activate();
	}
	
	@Override
	protected void desactivate() 
	{
		entity = null;
		super.desactivate();
	}

	@Override
	protected void onNewDot(Vector2 worldPosition) 
	{
		if(dots.size < minDots) return;
		if(entity == null){
			PathComponent path = getEngine().createComponent(PathComponent.class);
			entity = currentEntity().add(path).add(getEngine().createComponent(SplineDebugComponent.class));
		}
		
		computePath();
		
		SplineDebugComponent debug = SplineDebugComponent.components.get(entity);
		debug.vertices = null;
	}
	
	
	private void computePath(){
		
		Vector3 [] controls = new Vector3[dots.size];
		for(int i=0 ; i<controls.length ; i++) controls[i] = new Vector3(dots.get(i), 0);
		
		PathComponent path = PathComponent.components.get(entity);
		path.path = createPath(controls);
	}
	
	
	abstract protected Path<Vector3> createPath(Vector3[] controls);
	
	@Override
	protected void complete() 
	{
		entity = null;
	}

}
