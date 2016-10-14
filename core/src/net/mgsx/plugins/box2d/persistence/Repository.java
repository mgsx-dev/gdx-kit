package net.mgsx.plugins.box2d.persistence;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.Shape.Type;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import net.mgsx.plugins.box2d.model.BodyItem;
import net.mgsx.plugins.box2d.model.EditorSettings;
import net.mgsx.plugins.box2d.model.FixtureItem;
import net.mgsx.plugins.box2d.model.JointItem;
import net.mgsx.plugins.box2d.model.WorldItem;

public class Repository 
{
	public static class Data
	{
		public String version = "0.1";
		public EditorSettings settings;
		public Array<BodyData> bodies;
		public Array<JointData> joints;
	}
	public static class BodyData{
		public String id;
		public String name;
		public BodyDef def;
		public Array<FixtureData> fixtures;
	}
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
	
	public static class JointData
	{
		public String id;
		public String name;
		public JointDef def;
		public String bodyA, bodyB;
	}
	
	public static FixtureData exportShape(FixtureItem fix)
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

	public static void save(FileHandle file, WorldItem worldItem){
		Data data = new Data();
		data.settings = worldItem.settings;
		data.bodies = new Array<BodyData>();
		data.joints = new Array<JointData>();
		int id = 0;
		
		final Map<BodyItem, String> bodiesID = new HashMap<BodyItem, String>();
		
		for(BodyItem bodyItem : worldItem.items.bodies){
			BodyData bd = new BodyData();
			bd.def = bodyItem.def;
			bd.name = bodyItem.id;
			bd.id = String.valueOf(id++);
			data.bodies.add(bd);
			
			bodiesID.put(bodyItem, bd.id);
			
			bd.fixtures = new Array<FixtureData>();
			for(FixtureItem fix : bodyItem.fixtures){
				
				FixtureData fd = exportShape(fix);
				fd.id = String.valueOf(id++);
				bd.fixtures.add(fd);
			}
		}
		
		for(JointItem jointItem : worldItem.items.joints)
		{
			JointData jd = new JointData();
			jd.def = jointItem.def;
			jd.id = String.valueOf(id++);
			jd.name = jointItem.id;
			jd.bodyA = bodiesID.get((BodyItem)jointItem.joint.getBodyA().getUserData());
			jd.bodyB = bodiesID.get((BodyItem)jointItem.joint.getBodyB().getUserData());
			data.joints.add(jd);
		}
		
		createMapper().toJson(data, file);
	}
	private static class IgnoreSerializer<T> implements Json.Serializer<T>{
		@SuppressWarnings("rawtypes")
		@Override
		public void write(Json json, T object, Class knownType) 
		{
			json.writeValue(null);
		}
		@SuppressWarnings("rawtypes")
		@Override
		public T read(Json json, JsonValue jsonData, Class type) {
			return null;
		}
	}
	private static Json createMapper(){
		Json json = new Json();
		
		// ignore all Box2D body (native binding) included in JointDef
		json.setSerializer(Body.class, new IgnoreSerializer<Body>()); 
		
		// ignore all Box2D shapes (native binding) included in FixtureDef
		json.setSerializer(Shape.class, new IgnoreSerializer<Shape>()); 
		json.setSerializer(ChainShape.class, new IgnoreSerializer<ChainShape>()); 
		json.setSerializer(CircleShape.class, new IgnoreSerializer<CircleShape>()); 
		json.setSerializer(EdgeShape.class, new IgnoreSerializer<EdgeShape>()); 
		json.setSerializer(PolygonShape.class, new IgnoreSerializer<PolygonShape>());
		
		return json;
	}
	public static void load(FileHandle file, WorldItem worldItem)
	{
		// need to recreate world here ...
		
		worldItem.dispose();
		
		final Map<String, BodyItem> bodies = new HashMap<String, BodyItem>();
		
		Data data = createMapper().fromJson(Data.class, file);
		worldItem.settings = data.settings;
		worldItem.world = new World(worldItem.settings.gravity, true); // TODO doSleep is option ?
		
		Vector2 offset = new Vector2();
		
		for(BodyData bodyData : data.bodies){
			Body body = worldItem.world.createBody(bodyData.def);
			BodyItem bodyItem = new BodyItem(bodyData.name, bodyData.def, body);
			worldItem.items.bodies.add(bodyItem);
			bodies.put(bodyData.id, bodyItem);
			
			for(FixtureData fix : bodyData.fixtures){
				importShape(fix, offset);
				bodyItem.fixtures.add(new FixtureItem(fix.name, fix.def, body.createFixture(fix.def)));
			}
			
		}
		
		for(JointData jd : data.joints){
			jd.def.bodyA = bodies.get(jd.bodyA).body;
			jd.def.bodyB = bodies.get(jd.bodyB).body;
			Joint joint = worldItem.world.createJoint(jd.def);
			worldItem.items.joints.add(new JointItem(jd.name, jd.def, joint));
		}
	}
}
