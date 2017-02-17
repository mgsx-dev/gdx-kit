package net.mgsx.game.examples.td.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.examples.td.components.PathFollower;
import net.mgsx.game.plugins.core.components.Transform2DComponent;
import net.mgsx.game.plugins.spline.components.PathComponent;
import net.mgsx.game.plugins.spline.components.SplineDebugComponent;

public class FollowPathTool extends PathTool
{

	public FollowPathTool(EditorScreen editor) {
		super("Follow Path", editor);
	}
	
	@Override
	protected void complete(Array<Vector2> points) 
	{
		
		Entity entity = editor.currentEntity();
		
		// shift entity start point
		Transform2DComponent transform = Transform2DComponent.components.get(entity);
		if(transform != null){
			//points.insert(0, points.first().cpy());
			points.insert(0, transform.position.cpy());
		}
		
		// TODO spline are 3D but follower is 2D ... need to simplify things
		
		// add path
		PathComponent path = getEngine().createComponent(PathComponent.class);
		path.path = new CatmullRomSpline<Vector3>(controlPoints(points), false);
		entity.add(path);
		
		// add debug path
		entity.add(getEngine().createComponent(SplineDebugComponent.class));
		
		// add path following
		PathFollower follow = getEngine().createComponent(PathFollower.class);
		follow.speed = 0.5f; // XXX derived from move speed (logic)
		follow.path = new CatmullRomSpline<Vector2>(controlPoints2D(points), false);
		follow.t = 0;
		follow.length = follow.path.approxLength(100); // XXX
		
		follow.loop = false;
		follow.wrap = false;
		
		entity.add(follow);
		
	}

}
