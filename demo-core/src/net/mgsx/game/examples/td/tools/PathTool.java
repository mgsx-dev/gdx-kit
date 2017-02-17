package net.mgsx.game.examples.td.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.tools.DrawTool;
import net.mgsx.game.plugins.spline.components.PathComponent;
import net.mgsx.game.plugins.spline.components.SplineDebugComponent;

@Editable
public class PathTool extends DrawTool
{
	@Editable
	public float distanceMin = 1;
	
	private Array<Vector2> points = new Array<Vector2>();
	
	public PathTool(String name, EditorScreen editor) {
		super(name, editor);
	}

	@Override
	protected void drawingStart(Vector2 position) {
		points.clear();
		points.add(position.cpy());
	}

	@Override
	protected void drawing(Vector2 position) {
		if(position.dst(points.peek()) > distanceMin){
			points.add(position.cpy());
		}
	}

	@Override
	protected void drawingEnd(Vector2 position) {
		points.add(position.cpy());
		complete(points);
		points.clear();
	}
	
	protected void complete(Array<Vector2> points)
	{
		Entity entity = getEngine().createEntity();
		PathComponent path = getEngine().createComponent(PathComponent.class);
		path.path = new CatmullRomSpline<Vector3>(controlPoints(points), false);
		entity.add(path);
		entity.add(getEngine().createComponent(SplineDebugComponent.class));
		getEngine().addEntity(entity);
	}
	
	protected static Vector3 [] controlPoints(Array<Vector2> vectors){
		Vector3[] points = new Vector3[vectors.size + 2];
		points[0] = new Vector3(vectors.first(), 0);
		for(int i=0 ; i<vectors.size ; i++) points[i+1] = new Vector3(vectors.get(i), 0);
		points[points.length-1] = new Vector3(vectors.peek(), 0);
		return points;
	}
	protected static Vector2 [] controlPoints2D(Array<Vector2> vectors){
		Vector2[] points = new Vector2[vectors.size + 2];
		points[0] = new Vector2(vectors.first());
		for(int i=0 ; i<vectors.size ; i++) points[i+1] = new Vector2(vectors.get(i));
		points[points.length-1] = new Vector2(vectors.peek());
		return points;
	}
	

}
