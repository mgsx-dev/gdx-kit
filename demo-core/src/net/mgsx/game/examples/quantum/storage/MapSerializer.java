package net.mgsx.game.examples.quantum.storage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntMap.Entry;

import net.mgsx.game.examples.quantum.components.Link;
import net.mgsx.game.examples.quantum.components.Planet;
import net.mgsx.game.examples.quantum.systems.QuantumSimulation;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public strictfp class MapSerializer {

	public static void writeMap(Engine engine, FileHandle mapFile) throws IOException
	{
		DataOutputStream out = new DataOutputStream(mapFile.write(false));
		
		QuantumSimulation sim = engine.getSystem(QuantumSimulation.class);
		
		out.writeLong(sim.seed);
		
		ImmutableArray<Entity> planets = engine.getEntitiesFor(Family.all(Planet.class).get());
		
		out.writeInt(planets.size());
		for(Entity entity : planets){
			
			writePlanet(entity, out);
			
		}
		
		out.writeInt(sim.nextId);
		
		IntMap<Array<Integer>> links = new IntMap<Array<Integer>>();
		
		for(Entity entity : engine.getEntitiesFor(Family.all(Link.class).get())){
			Link link = Link.components.get(entity);
			Planet source = Planet.components.get(link.source);
			Planet target = Planet.components.get(link.target);
			Array<Integer> targets = links.get(source.id);
			if(targets == null){
				links.put(source.id, targets = new Array<Integer>());
			}
			targets.add(target.id);
		}
		
		for(Entity entity : engine.getEntitiesFor(Family.all(Planet.class).get())){
			Planet planet = Planet.components.get(entity);
			Array<Integer> targets = links.get(planet.id);
			if(targets == null){
				out.writeInt(0);
			}else{
				out.writeInt(targets.size);
				for(int index : targets){
					out.writeInt(index);
				}
			}
		}
		
		writeString(out, sim.author);
		writeString(out, sim.name);
		writeString(out, sim.description);
		
		out.close();
	}
	public static void readMap(Engine engine, FileHandle mapFile) throws IOException
	{
		QuantumSimulation sim = engine.getSystem(QuantumSimulation.class);
	
		DataInputStream in = new DataInputStream(mapFile.read());
		
		IntMap<Entity> planets = new IntMap<Entity>();
		
		sim.seed = in.readLong();
		int planetCount = in.readInt();
		for(int i=0 ; i<planetCount ; i++){
			Entity entity = readPlanet(engine, in);
			Planet planet = Planet.components.get(entity);
			planets.put(planet.id, entity);
			
		}
		sim.nextId = in.readInt();
		
		for (Entry<Entity> planetEntry : planets.entries()) {
			int linkCount = in.readInt();
			Entity planetEntity = planetEntry.value; 
			for (int j = 0; j < linkCount ; j++){
				int destinationId = in.readInt();
				
				// create link entity
				Entity entity = engine.createEntity();
				
				Link link = engine.createComponent(Link.class);
				link.source = planetEntity;
				link.target = planets.get(destinationId);
				entity.add(link);
				
				engine.addEntity(entity);
			}
		}
		try {
			sim.author = readString(in);
			sim.name = readString(in);
			sim.description = readString(in);
		} catch (IOException ex) {
			sim.author = "";
			sim.description = "";
			sim.name = "";
		}
		in.close();
	}
	
	public static String readString (DataInputStream in) throws IOException {
		int n = in.readInt();
		byte[] chars = new byte[n];
		in.readFully(chars);
		return new String(chars);
	}
	
	public static void writeString (DataOutputStream out, String string) throws IOException {
		byte[] chars = string.getBytes();
		out.writeInt(chars.length);
		out.write(chars);
	}
	
	public static void writePlanet(Entity entity, DataOutputStream out) throws IOException {
		
		Planet planet = Planet.components.get(entity);
		Transform2DComponent transform = Transform2DComponent.components.get(entity);
		
		out.writeInt(planet.id);
		out.writeFloat(transform.position.x); // XXX scale
		out.writeFloat(transform.position.y); // XXX scale
		
		out.writeInt(planet.owner);
		out.writeFloat(planet.radius);
		out.writeFloat(planet.strength);
		out.writeFloat(planet.health);
		out.writeFloat(planet.speed);
		out.writeInt(planet.resources);
		out.writeInt(planet.full_resources);
		out.writeBoolean(planet.is_start_planet);
		out.writeInt(planet.last_idx);
		out.writeBoolean(planet.is_regrowing);
		out.writeInt(planet.chain_planet);

		out.writeInt(0); // no trees
		out.writeInt(0); // no creatures
	}

	
	public static Entity readPlanet(Engine engine, DataInputStream in) throws IOException {
		
		Entity entity = engine.createEntity();
		
		Planet planet = engine.createComponent(Planet.class);
		
		planet.id = in.readInt();
		
		engine.addEntity(entity);
		
		Transform2DComponent transform = engine.createComponent(Transform2DComponent.class);
		transform.position.x = in.readFloat(); // XXX scale
		transform.position.y = in.readFloat(); // XXX scale
		
		entity.add(transform);

		
		planet.owner = in.readInt();
		planet.radius = in.readFloat();
		planet.strength = in.readFloat();
		planet.health = in.readFloat();
		planet.speed = in.readFloat();
		planet.resources = in.readInt();
		planet.full_resources = in.readInt();
		planet.is_start_planet = in.readBoolean();
		planet.last_idx = in.readInt();
		planet.is_regrowing = in.readBoolean();
		planet.chain_planet = in.readInt();

		entity.add(planet);
		
		int n = in.readInt();
		if(n > 0) throw new GdxRuntimeException("unsupported tree");

		n = in.readInt();
		if(n > 0) throw new GdxRuntimeException("unsupported creatures");
		
		return entity;
	}
}
