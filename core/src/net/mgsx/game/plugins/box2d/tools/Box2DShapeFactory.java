package net.mgsx.game.plugins.box2d.tools;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.Shape.Type;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.plugins.box2d.components.Box2DFixtureModel;

public class Box2DShapeFactory 
{
	public static class FixtureData{
		public String id;
		public String name;
		public FixtureDef def;
		public ShapeData shape;
	}
	public static class ShapeData{
		public Type type;
		public Array<Vector2> vertex = new Array<Vector2>();
		public float radius;
		public boolean loop;
	}
	
	public static FixtureData exportShape(Box2DFixtureModel fix)
	{
		
			FixtureData fd = new FixtureData();
			fd.def = fix.def;

			Shape nativeShape = fix.def.shape;
			fd.shape = new ShapeData();
			fd.shape.type = nativeShape.getType();
			switch(nativeShape.getType()){
			case Polygon:
				{
					PolygonShape shape = (PolygonShape)nativeShape;
					for(int i=0 ; i<shape.getVertexCount() ; i++){
						Vector2 vertex = new Vector2();
						shape.getVertex(i, vertex);
						fd.shape.vertex.add(vertex);
					}
				}
				break;
			case Chain:
			{
				ChainShape shape = (ChainShape)nativeShape;
				for(int i=0 ; i<shape.getVertexCount() ; i++){
					Vector2 vertex = new Vector2();
					shape.getVertex(i, vertex);
					fd.shape.vertex.add(vertex);
				}
				// remove last vertex (same as first)
				if(shape.isLooped()) fd.shape.vertex.removeIndex(fd.shape.vertex.size-1);
				fd.shape.loop = shape.isLooped();
			}
			break;
			case Edge:
			{
				EdgeShape shape = (EdgeShape)nativeShape;
				Vector2 v0 = new Vector2(); shape.getVertex0(v0); fd.shape.vertex.add(v0);
				Vector2 v1 = new Vector2(); shape.getVertex1(v1); fd.shape.vertex.add(v1);
				Vector2 v2 = new Vector2(); shape.getVertex2(v2); fd.shape.vertex.add(v2);
				Vector2 v3 = new Vector2(); shape.getVertex3(v3); fd.shape.vertex.add(v3);
			}
			break;
			case Circle:
			{
				CircleShape shape = (CircleShape)nativeShape;
				fd.shape.vertex.add(shape.getPosition());
				fd.shape.radius = shape.getRadius();
			}
			break;
			}
			fd.name = fix.id;
			return fd;
	}
	
	public static void importShape(FixtureData fix, Vector2 offset)
	{
		Vector2 [] vertices = new Vector2[fix.shape.vertex.size];
		for(int i=0 ; i<vertices.length ; i++){
			vertices[i] = fix.shape.vertex.get(i).add(offset);
		}
		Shape shape = null;
		switch(fix.shape.type){
		case Chain:
			ChainShape chain = new ChainShape();
			if(fix.shape.loop){
				chain.createLoop(vertices);
			}else{
				chain.createChain(vertices);
			}
			shape = chain;
			break;
		case Circle:
			CircleShape circle = new CircleShape();
			circle.setPosition(vertices[0]);
			circle.setRadius(fix.shape.radius);
			shape = circle;
			break;
		case Edge:
			EdgeShape edge= new EdgeShape();
			if(vertices[0] != null) edge.getVertex0(vertices[0]);
			edge.getVertex1(vertices[1]);
			edge.getVertex2(vertices[2]);
			if(vertices[3] != null) edge.getVertex3(vertices[3]);
			shape = edge;
			break;
		case Polygon:
			PolygonShape poly = new PolygonShape();
			poly.set(vertices);
			shape = poly;
			break;
		}
		
		fix.def.shape = shape;
	}

}
