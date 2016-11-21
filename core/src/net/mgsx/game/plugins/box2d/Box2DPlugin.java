package net.mgsx.game.plugins.box2d;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.components.Movable;
import net.mgsx.game.core.plugins.Plugin;
import net.mgsx.game.core.storage.ContextualSerializer;
import net.mgsx.game.core.storage.Storage;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.components.Box2DJointModel;
import net.mgsx.game.plugins.box2d.storage.Box2DJointSerializer;
import net.mgsx.game.plugins.box2d.storage.Box2DModelSerializer;
import net.mgsx.game.plugins.box2d.storage.Box2DShapesSerializers;
import net.mgsx.game.plugins.box2d.systems.Box2DMasterSystem;
import net.mgsx.game.plugins.box2d.systems.Box2DSlaveSystem;
import net.mgsx.game.plugins.box2d.systems.Box2DWorldContext;
import net.mgsx.game.plugins.box2d.systems.Box2DWorldSystem;
import net.mgsx.game.plugins.box2d.tools.BodyMove;
import net.mgsx.game.plugins.box2d.tools.Box2DJointMovable;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

@PluginDef(components={Box2DBodyModel.class, Box2DJointModel.class})
public class Box2DPlugin implements Plugin
{
	

	@Override
	public void initialize(final GameScreen engine) 
	{
		Box2DWorldContext worldItem = new Box2DWorldContext();
		worldItem.initialize();
		
		engine.entityEngine.addSystem(new Box2DWorldSystem(worldItem));
		engine.entityEngine.addSystem(new Box2DMasterSystem());
		engine.entityEngine.addSystem(new Box2DSlaveSystem());
		
		engine.entityEngine.addEntityListener(Family.all(Box2DJointModel.class).get(), new EntityListener() {
			@Override
			public void entityRemoved(Entity entity) {
				Box2DJointModel jm = (Box2DJointModel)entity.remove(Box2DJointModel.class);
				if(jm != null){
					jm.destroy();
				}
				entity.remove(Movable.class);
			}
			
			@Override
			public void entityAdded(Entity entity) {
				entity.add(new Movable(new Box2DJointMovable()));
				entity.getComponent(Box2DJointModel.class).joint.setUserData(entity);
			}
		});
		
		engine.addSerializer(Box2DBodyModel.class, new Box2DModelSerializer(worldItem));
		engine.addSerializer(Box2DJointModel.class, new Box2DJointSerializer(worldItem));
		engine.addSerializer(PolygonShape.class, Box2DShapesSerializers.polygon());
		engine.addSerializer(ChainShape.class, Box2DShapesSerializers.chain());
		engine.addSerializer(CircleShape.class, Box2DShapesSerializers.circle());
		engine.addSerializer(EdgeShape.class, Box2DShapesSerializers.edge());
		engine.addSerializer(Shape.class, Box2DShapesSerializers.shape());
		
		// TODO doublon ?
		Storage.register(new Box2DJointSerializer(worldItem));
		
		Storage.register(new ContextualSerializer<Body>(Body.class) {
			@Override
			public void write(Json json, Body object, Class knownType) {
				Entity entity = (Entity)object.getUserData();
				int entityIndex = engine.entityEngine.getEntities().indexOf(entity, true);
				json.writeValue(entityIndex);
			}

			@Override
			public Body read(Json json, JsonValue jsonData, Class type) {
				int entityIndex = jsonData.asInt();
				Entity entity = findEntity(entityIndex);
				Box2DBodyModel bodyModel = entity.getComponent(Box2DBodyModel.class);
				return bodyModel.body;
			}
		});
		
		// TODO abstraction for auto/add component !
		engine.entityEngine.addEntityListener(Family.one(Box2DBodyModel.class).get(), new EntityListener() {
			
			@Override
			public void entityRemoved(Entity entity) {
				Box2DBodyModel model = (Box2DBodyModel)entity.remove(Box2DBodyModel.class);
				if(model != null){
					model.context.scheduleRemove(entity, model);
					entity.remove(Movable.class); // because of body reference
				}
			}
			
			@Override
			public void entityAdded(Entity entity) {
				Box2DBodyModel object = entity.getComponent(Box2DBodyModel.class);
				if(object != null && object.body != null){
					object.body.setUserData(entity);
				
					entity.add(new Movable(new BodyMove(object.body)));
				
					// TODO bah !
					Transform2DComponent transform = entity.getComponent(Transform2DComponent.class);
					
					if(entity.getComponent(Transform2DComponent.class) == null) {
					// XXX auto attach again !? : body x/y
					}else{ // case of loading ...
						object.body.setTransform(transform.position, transform.angle);
					}
				}
			}
		});
	}


}
