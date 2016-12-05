package net.mgsx.game.examples.platformer.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.examples.platformer.components.WaterZone;
import net.mgsx.game.plugins.core.components.Transform2DComponent;
import net.mgsx.pd.Pd;
import net.mgsx.pd.patch.PdPatch;

public class WaterSoundSystem extends IteratingSystem
{
	private static final String pdWater = "water-level";

	private GameScreen game;
	
	private Vector3 position = new Vector3();
	private float center;
	private int count;
	
	private PdPatch patch;
	
	public WaterSoundSystem(GameScreen game) {
		super(Family.all(WaterZone.class, Transform2DComponent.class).get(), GamePipeline.AFTER_LOGIC); // audio done just after logic to minimize latencies.
		this.game = game;
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		patch = Pd.audio.open(Gdx.files.internal("engine.pd")); // TODO from assets !
	}
	
	@Override
	public void removedFromEngine(Engine engine) {
		// TODO patch.dispose();
		super.removedFromEngine(engine);
	}
	
	@Override
	public void update(float deltaTime) 
	{
		if(patch == null) return;
		count = 0;
		center = -1000000f;
		
		super.update(deltaTime);
		
		float level = 0;
		if(count > 0){
			//float average = center / count;
			float range = center / 50.f;
			level = 1.0f - MathUtils.clamp(Math.abs(range), 0, 1);
			level = level * level;
		}
		Pd.audio.sendFloat(pdWater, level);
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		Transform2DComponent transform = Transform2DComponent.components.get(entity);
		
		Camera camera = game.getCullingCamera();
		
		position.set(transform.position, 0).sub(camera.position);
		
		if(Math.abs(position.x) < Math.abs(center)) center = position.x;
		count++;
		
	}

}
