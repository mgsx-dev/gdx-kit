package net.mgsx.game.core.helpers;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class RenderDebugHelper {

	public static void box(ShapeRenderer renderer, BoundingBox box){
		// TODO it works but i don't know why max Z ... same result with opposite depth.
		renderer.box(box.min.x, box.min.y, Math.max(box.min.z, box.max.z), box.getWidth(), box.getHeight(), box.getDepth());
	}
	public static void frustum(ShapeRenderer renderer, Frustum frustum){
		Vector3[] pts = frustum.planePoints;
		
		renderer.line(pts[0], pts[1]);
		renderer.line(pts[1], pts[2]);
		renderer.line(pts[2], pts[3]);
		renderer.line(pts[3], pts[0]);
		
		renderer.line(pts[4], pts[5]);
		renderer.line(pts[5], pts[6]);
		renderer.line(pts[6], pts[7]);
		renderer.line(pts[7], pts[4]);
		
		renderer.line(pts[0], pts[4]);
		renderer.line(pts[1], pts[5]);
		renderer.line(pts[2], pts[6]);
		renderer.line(pts[3], pts[7]);	
	}
}
