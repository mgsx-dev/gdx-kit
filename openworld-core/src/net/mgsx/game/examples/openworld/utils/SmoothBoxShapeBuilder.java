package net.mgsx.game.examples.openworld.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.math.Vector3;

public class SmoothBoxShapeBuilder {

	public static void build(MeshBuilder builder, Vector3 pos, Vector3 half) {
		
		BoxShapeBuilder.build(builder,
				vi(pos, half, -1, -1, -1),
				vi(pos, half, -1,  1, -1),
				vi(pos, half,  1, -1, -1),
				vi(pos, half,  1,  1, -1),
				vi(pos, half, -1, -1,  1),
				vi(pos, half, -1,  1,  1),
				vi(pos, half,  1, -1,  1),
				vi(pos, half,  1,  1,  1));
		
		
	}

	private static VertexInfo vi(Vector3 pos, Vector3 half, float dx, float dy, float dz) {
		VertexInfo vp = new VertexInfo();
		vp.setPos(vp.position.set(half).scl(dx,dy,-dz).add(pos)); // XXX -z workaround because of vertices order
		vp.setNor(vp.normal.set(dx,dy,dz).nor());
		vp.setCol(vp.color.set(Color.WHITE));
		return vp;
	}
}
