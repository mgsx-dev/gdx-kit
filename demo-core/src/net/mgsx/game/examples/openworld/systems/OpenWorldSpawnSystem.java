package net.mgsx.game.examples.openworld.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.examples.openworld.components.ObjectMeshComponent;
import net.mgsx.game.examples.openworld.components.SpawnComponent;
import net.mgsx.game.examples.openworld.model.OpenWorldElement;
import net.mgsx.game.examples.openworld.model.SpawnGenerator;
import net.mgsx.game.examples.openworld.systems.UserObjectSystem.UserObject;
import net.mgsx.game.examples.openworld.utils.VirtualGrid;

/**
 * Logic system responsible of spawning element/entities and
 * manage their lifecycle.
 * 
 * @author mgsx
 *
 */
public class OpenWorldSpawnSystem extends EntitySystem
{
	public static class SpawnChunk {
		public Array<Entity> entities = new Array<Entity>();
		public Array<OpenWorldElement> elements = new Array<OpenWorldElement>();
	}
	
	@Inject OpenWorldCameraSystem cameraSystem;
	@Inject UserObjectSystem userObject;
	@Inject OpenWorldGeneratorSystem generatorSystem;
	
	private VirtualGrid<SpawnChunk> spawnGrid;

	private int spawnGridSize = 3; // XXX 10; // 10 active chunks forming a minimal 10x10=100 chunks
	private int spawnGridMargin = 1; // XXX = 10; // extra 10 chunks forming a maximal 30x30=900 chunks
	private float spawnGridScale = 10; // 10m chunk size forming a 100m X 100m living grid, 300m X 300m memory
	
	private SpawnGenerator spawnGenerator;
	
	public OpenWorldSpawnSystem() {
		super(GamePipeline.LOGIC);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		
		engine.addEntityListener(Family.all(SpawnComponent.class).get(), new EntityListener() {
			@Override
			public void entityRemoved(Entity entity) {
				SpawnComponent spawn = SpawnComponent.components.get(entity);
				spawn.chunk.entities.removeValue(entity, true);
				// we remove linked element only if active. That is not
				// removed because of exit state.
				if(spawn.active){
					ObjectMeshComponent omc = ObjectMeshComponent.components.get(entity);
					spawn.chunk.elements.removeValue(omc.userObject.element, true);
				}
			}
			
			@Override
			public void entityAdded(Entity entity) {
				// noop
			}
		});
		
		spawnGrid = new VirtualGrid<SpawnChunk>() {

			@Override
			protected void dispose(SpawnChunk cell) {
				// remove remaining entities
				for(Entity entity : cell.entities){
					getEngine().removeEntity(entity);
				}
			}

			@Override
			protected SpawnChunk create(float worldX, float worldY) {
				SpawnChunk chunk = new SpawnChunk();
				spawn(chunk, worldX, worldY);
				return chunk;
			}

			@Override
			protected void update(SpawnChunk cell, int distance) {
				// maybe change LOD
			}

			@Override
			protected void enter(SpawnChunk cell) 
			{
				// we materialize elements
				for(OpenWorldElement element : cell.elements){
					
					// It is a user object, but just a transient one, we don't want to save it until player gets hand on it.
					UserObject uo = userObject.appendObject(element, false);
					
					cell.entities.add(uo.entity);
					
					// Keep track of it.
					SpawnComponent sp = getEngine().createComponent(SpawnComponent.class);
					sp.chunk = cell;
					sp.active = true;
					uo.entity.add(sp);
				}
			}

			@Override
			protected void exit(SpawnChunk cell) {
				// remove entity but keep element definition
				for(Entity entity : cell.entities){
					SpawnComponent spawn = SpawnComponent.components.get(entity);
					spawn.active = false;
					getEngine().removeEntity(entity);
				}
				cell.entities.clear();
			}
		};
		
		spawnGrid.resize(spawnGridSize, spawnGridSize, spawnGridMargin, spawnGridMargin, spawnGridScale);
		
		spawnGenerator = new SpawnGenerator();
	}
	
	private void spawn(SpawnChunk chunk, float worldX, float worldY)
	{
		// TODO clusters and voronoi offset.
		float centerX = worldX + spawnGridScale/2;
		float centerY = worldY + spawnGridScale/2;
		
		OpenWorldElement element = spawnGenerator.generate();
		
		// TODO how to handle dynamics ? they should not be handled by chunk !
		// so should be removed from elements when materialized
		// another system should keep track of these kind of objects.
		element.dynamic = false;
		
		float altitude = generatorSystem.getAltitude(centerX, centerY);

		// TODO static has no auto offset but it's nice to have them planted into the groud though!
		element.position.set(worldX, altitude, worldY);
		
		chunk.elements.add(element);
	}
	
	@Override
	public void update(float deltaTime) 
	{
		Camera camera = cameraSystem.getCamera();
		if(camera == null) return;
		
		// update grid
		spawnGrid.update(camera.position.x, camera.position.z);
	}
}
