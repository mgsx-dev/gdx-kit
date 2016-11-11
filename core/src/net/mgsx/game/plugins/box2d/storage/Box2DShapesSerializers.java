package net.mgsx.game.plugins.box2d.storage;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializer;
import com.badlogic.gdx.utils.JsonValue;

public class Box2DShapesSerializers
{

	public static Serializer<Shape> shape(){
		return new Serializer<Shape>() {
			
			@Override
			public void write(Json json, Shape object, Class knownType) {
				// TODO log unhandled shape ?
			}
			
			@Override
			public Shape read(Json json, JsonValue jsonData, Class type) {
				Shape.Type shapeType = json.readValue("type", Shape.Type.class, jsonData);
				switch(shapeType){
				case Chain:
					return json.readValue(ChainShape.class, jsonData);
				case Circle:
					return json.readValue(CircleShape.class, jsonData);
				case Edge:
					return json.readValue(EdgeShape.class, jsonData);
				case Polygon:
					return json.readValue(PolygonShape.class, jsonData);
				default:
					break;}
				return null;
			}
		};
	}
	public static Serializer<PolygonShape> polygon(){
		return new Serializer<PolygonShape>() {
			
			@Override
			public void write(Json json, PolygonShape object, Class knownType) {
				json.writeObjectStart();
				json.writeValue("type", object.getType());
				json.writeArrayStart("vertex");
				Vector2 vertex = new Vector2();
				for(int i=0 ; i<object.getVertexCount() ; i++){
					object.getVertex(i, vertex);
					json.writeValue(vertex);
				}
				json.writeArrayEnd();
				json.writeObjectEnd();
			}
			
			@Override
			public PolygonShape read(Json json, JsonValue jsonData, Class type) {
				PolygonShape shape = new PolygonShape();
				shape.set(json.readValue("vertex", Vector2[].class, jsonData));
				return shape;
			}
		};
	}
	public static Serializer<ChainShape> chain(){
		return new Serializer<ChainShape>() {
			
			@Override
			public void write(Json json, ChainShape object, Class knownType) {
				json.writeObjectStart();
				json.writeValue("type", object.getType());
				json.writeValue("loop", object.isLooped());
				json.writeArrayStart("vertex");
				Vector2 vertex = new Vector2();
				int max = object.getVertexCount() + (object.isLooped() ? -1 : 0);
				for(int i=0 ; i<max ; i++){
					object.getVertex(i, vertex);
					json.writeValue(vertex);
				}
				json.writeArrayEnd();
				json.writeObjectEnd();
			}
			
			@Override
			public ChainShape read(Json json, JsonValue jsonData, Class type) {
				ChainShape shape = new ChainShape();
				boolean loop = jsonData.getBoolean("loop");
				if(loop)
					shape.createLoop(json.readValue("vertex", Vector2[].class, jsonData));
				else
					shape.createChain(json.readValue("vertex", Vector2[].class, jsonData));
				return shape;
			}
		};
	}
	public static Serializer<CircleShape> circle(){
		return new Serializer<CircleShape>() {
			
			@Override
			public void write(Json json, CircleShape object, Class knownType) {
				json.writeObjectStart();
				json.writeValue("type", object.getType());
				json.writeValue("position", object.getPosition());
				json.writeValue("radius", object.getRadius());
				json.writeObjectEnd();
			}
			
			@Override
			public CircleShape read(Json json, JsonValue jsonData, Class type) {
				CircleShape shape = new CircleShape();
				shape.setPosition(json.readValue("position", Vector2.class, jsonData));
				shape.setRadius(json.readValue("radius", float.class, jsonData));
				return shape;
			}
		};
	}
	public static Serializer<EdgeShape> edge(){
		return new Serializer<EdgeShape>() {
			
			@Override
			public void write(Json json, EdgeShape object, Class knownType) {
				json.writeObjectStart();
				json.writeValue("type", object.getType());
				Vector2 vec = new Vector2();
				
				if(object.hasVertex0()){
					object.getVertex0(vec);
					json.writeValue("vertex0", vec);
				}
				
				object.getVertex1(vec);
				json.writeValue("vertex1", vec);
				
				object.getVertex2(vec);
				json.writeValue("vertex2", vec);
				
				if(object.hasVertex3()){
					object.getVertex3(vec);
					json.writeValue("vertex3", vec);
				}
					
				json.writeObjectEnd();
			}
			
			@Override
			public EdgeShape read(Json json, JsonValue jsonData, Class type) {
				EdgeShape shape = new EdgeShape();
				shape.setVertex0(json.readValue("vertex0", Vector2.class, jsonData));
				shape.set(
						json.readValue("vertex1", Vector2.class, jsonData), 
						json.readValue("vertex2", Vector2.class, jsonData));
				shape.setVertex3(json.readValue("vertex3", Vector2.class, jsonData));
				// TODO ? shape.setHasVertex0(hasVertex0);
				return shape;
			}
		};
	}
}
