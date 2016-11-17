package net.mgsx.game.plugins.box2d;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.commands.CommandHistory;
import net.mgsx.game.core.components.Movable;
import net.mgsx.game.core.plugins.Plugin;
import net.mgsx.game.core.storage.ContextualSerializer;
import net.mgsx.game.core.storage.Storage;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.components.Box2DJointModel;
import net.mgsx.game.plugins.box2d.components.WorldItem;
import net.mgsx.game.plugins.box2d.listeners.Box2DListener;
import net.mgsx.game.plugins.box2d.storage.Box2DJointSerializer;
import net.mgsx.game.plugins.box2d.storage.Box2DModelSerializer;
import net.mgsx.game.plugins.box2d.storage.Box2DShapesSerializers;
import net.mgsx.game.plugins.box2d.systems.Box2DWorldSystem;
import net.mgsx.game.plugins.box2d.tools.Box2DJointMovable;

@PluginDef(components={Box2DBodyModel.class, Box2DJointModel.class})
public class Box2DPlugin implements Plugin
{
	public static WorldItem worldItem;

	@Override
	public void initialize(final GameScreen engine) 
	{
		engine.entityEngine.addSystem(new Box2DWorldSystem());
		
		CommandHistory commandHistory = new CommandHistory(); // XXX fake
		worldItem = new WorldItem(commandHistory);
		worldItem.initialize();
		
		worldItem.world.setContactListener(new ContactListener() {
			
			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
				Fixture fixtureA = contact.getFixtureA();
				Fixture fixtureB = contact.getFixtureB();
				
				Object dataA = fixtureA.getUserData();
				Object dataB = fixtureB.getUserData();
				
				if(dataA instanceof Box2DListener){
					((Box2DListener) dataA).preSolve(contact, fixtureA, fixtureB, oldManifold);
				}
				if(dataB instanceof Box2DListener){
					((Box2DListener) dataB).preSolve(contact, fixtureB, fixtureA, oldManifold);
				}
			}
			
			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
				Fixture fixtureA = contact.getFixtureA();
				Fixture fixtureB = contact.getFixtureB();
				
				Object dataA = fixtureA.getUserData();
				Object dataB = fixtureB.getUserData();
				
				if(dataA instanceof Box2DListener){
					((Box2DListener) dataA).postSolve(contact, fixtureA, fixtureB, impulse);
				}
				if(dataB instanceof Box2DListener){
					((Box2DListener) dataB).postSolve(contact, fixtureB, fixtureA, impulse);
				}
			}
			
			@Override
			public void endContact(Contact contact) {
				Fixture fixtureA = contact.getFixtureA();
				Fixture fixtureB = contact.getFixtureB();
				
				Object dataA = fixtureA.getUserData();
				Object dataB = fixtureB.getUserData();
				
				if(dataA instanceof Box2DListener){
					((Box2DListener) dataA).endContact(contact, fixtureA, fixtureB);
				}
				if(dataB instanceof Box2DListener){
					((Box2DListener) dataB).endContact(contact, fixtureB, fixtureA);
				}
			}
			
			@Override
			public void beginContact(Contact contact) 
			{
				Fixture fixtureA = contact.getFixtureA();
				Fixture fixtureB = contact.getFixtureB();
				
				Object dataA = fixtureA.getUserData();
				Object dataB = fixtureB.getUserData();
				
				if(dataA instanceof Box2DListener){
					((Box2DListener) dataA).beginContact(contact, fixtureA, fixtureB);
				}
				if(dataB instanceof Box2DListener){
					((Box2DListener) dataB).beginContact(contact, fixtureB, fixtureA);
				}
			}
		});
		
		
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
		
	}


}
