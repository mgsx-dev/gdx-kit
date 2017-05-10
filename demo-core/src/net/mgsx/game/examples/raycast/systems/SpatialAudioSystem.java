package net.mgsx.game.examples.raycast.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ObjectMap;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.examples.raycast.components.AudioSource;
import net.mgsx.game.plugins.core.components.Transform2DComponent;
import net.mgsx.pd.Pd;

public class SpatialAudioSystem extends IteratingSystem
{

	@Inject
	public CompassLocalSystem local;
	
	private ObjectMap<Entity, Integer> sourceMap = new ObjectMap<Entity, Integer>();
	
	public SpatialAudioSystem() {
		super(Family.all(Transform2DComponent.class, AudioSource.class).get(), GamePipeline.LOGIC);
	}
	
	@Override
	public void addedToEngine(Engine engine) 
	{
		super.addedToEngine(engine);
		
		// PdPatch patch = Pd.audio.open(Gdx.files.internal("spatial/core.pd"));
		
		engine.addEntityListener(getFamily(), new EntityListener() {
			
			@Override
			public void entityRemoved(Entity entity) {
				Integer id = sourceMap.get(entity);
				if(id != null){
					Pd.audio.sendList("source", id, 0, 0, 0);
				}
			}
			
			@Override
			public void entityAdded(Entity entity) {
				AudioSource source = AudioSource.components.get(entity);
				sourceMap.put(entity, source.id);
				send(entity);
			}
		});
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		send(entity);
	}
	
	private void send(Entity entity)
	{
		Transform2DComponent transform = Transform2DComponent.components.get(entity);
		AudioSource source = AudioSource.components.get(entity);
		
		float dx = transform.position.x - local.x;
		float dy = transform.position.y - local.y;
		
		float distance = (float)Math.sqrt(dx*dx + dy*dy);
		float azimuth = (MathUtils.atan2(dy, dx) - local.azymuth) * MathUtils.radiansToDegrees + 180;
		
		Pd.audio.sendList("source", source.id, source.intensity, azimuth, distance);
	}

}
