package net.mgsx.game.examples.platformer.core;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializer;
import com.badlogic.gdx.utils.JsonValue;

import net.mgsx.game.core.GameEngine;
import net.mgsx.game.core.helpers.EntityHelper;
import net.mgsx.game.core.plugins.Plugin;
import net.mgsx.game.core.storage.Storage;

/**
 * 
 * Game core plugin
 * 
 * @author mgsx
 *
 */
public class PlatformerPlugin implements Plugin
{

	@Override
	public void initialize(GameEngine engine) 
	{
		Storage.register(PlayerComponent.class, "example.platformer.player");
		engine.addSerializer(PlayerComponent.class, new Serializer<PlayerComponent>() {

			@Override
			public void write(Json json, PlayerComponent object, Class knownType) {
				// write nothing
				json.writeObjectStart();
				json.writeObjectEnd();
			}

			@Override
			public PlayerComponent read(Json json, JsonValue jsonData, Class type) {
				// read nothing but create it.
				return null;
			}
		});
		
		// TODO maybe treat all "Initializable" components by calling initialize(entity) ?
		engine.entityEngine.addEntityListener(Family.one(PlayerComponent.class).get(), new EntityListener() {
			@Override
			public void entityRemoved(Entity entity) {
				entity.remove(PlayerComponent.class);
			}
			
			@Override
			public void entityAdded(Entity entity) {
				entity.getComponent(PlayerComponent.class).initialize(entity);
			}
		});
		
		// add a processor for player
		// TODO could be automated with a generic component and abstract behavior attached to it ?
		engine.entityEngine.addSystem(new EntityHelper.SingleComponentIteratingSystem<PlayerComponent>(PlayerComponent.class) {
			@Override
			protected void processEntity(Entity entity, PlayerComponent component, float deltaTime) {
				component.update(deltaTime);
			}
		});
		
	}

}
