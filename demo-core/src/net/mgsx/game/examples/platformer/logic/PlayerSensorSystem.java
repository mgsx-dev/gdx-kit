package net.mgsx.game.examples.platformer.logic;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.platformer.inputs.PlayerController;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class PlayerSensorSystem extends IteratingSystem
{
	private ImmutableArray<Entity> players;
	
	private Vector2 position = new Vector2();
	
	public PlayerSensorSystem() {
		super(Family.all(PlayerSensor.class, Transform2DComponent.class).get(), GamePipeline.LOGIC);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		players = engine.getEntitiesFor(Family.all(PlayerController.class, Transform2DComponent.class).get());
	}
	
	@Override
	public void update(float deltaTime) {
		position.setZero();
		if(players.size() > 0){
			float factor = 1.f / (float)players.size();
			for(Entity player : players){
				Transform2DComponent transform = Transform2DComponent.components.get(player);
				position.mulAdd(transform.position, factor);
			}
		}
		super.update(deltaTime);
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Transform2DComponent transform = Transform2DComponent.components.get(entity);
		PlayerSensor sensor = PlayerSensor.components.get(entity);
		sensor.distance = position.dst(transform.position);
		sensor.exists = players.size() > 0;
	}
}
