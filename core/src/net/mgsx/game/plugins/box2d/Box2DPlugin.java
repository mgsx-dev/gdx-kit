package net.mgsx.game.plugins.box2d;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
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

import net.mgsx.game.core.Editor;
import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.commands.CommandHistory;
import net.mgsx.game.core.components.Movable;
import net.mgsx.game.core.components.Transform2DComponent;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.core.storage.ContextualSerializer;
import net.mgsx.game.core.storage.Storage;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.components.Box2DJointModel;
import net.mgsx.game.plugins.box2d.components.WorldItem;
import net.mgsx.game.plugins.box2d.editors.Box2DBodyEditorPlugin;
import net.mgsx.game.plugins.box2d.editors.Box2DJointEditorPlugin;
import net.mgsx.game.plugins.box2d.listeners.Box2DListener;
import net.mgsx.game.plugins.box2d.storage.Box2DJointSerializer;
import net.mgsx.game.plugins.box2d.storage.Box2DModelSerializer;
import net.mgsx.game.plugins.box2d.storage.Box2DShapesSerializers;
import net.mgsx.game.plugins.box2d.tools.BodyMove;
import net.mgsx.game.plugins.box2d.tools.Box2DBodySelector;
import net.mgsx.game.plugins.box2d.tools.Box2DJointMovable;
import net.mgsx.game.plugins.box2d.tools.Box2DParticleTool;

public class Box2DPlugin extends EditorPlugin 
{

	public WorldItem worldItem;
	
	// TODO future of Box2D package :
	
	// process all entities in step method
	
	// global setting for physics and physic template ...
	
	// lot of tools !
	
	// debug rendering
	
	// ...
	private Box2DDebugRenderer box2dRenderer;
	
	@Override
	public void initialize(final Editor editor) 
	{
		Storage.register(Box2DBodyModel.class, "box2d");
		Storage.register(Box2DJointModel.class, "box2d.joint");
		
		box2dRenderer = new Box2DDebugRenderer();
		CommandHistory commandHistory = new CommandHistory(); // XXX fake
		worldItem = new WorldItem(commandHistory);
		worldItem.editor = editor;
		worldItem.initialize();
		
		worldItem.world.setContactListener(new ContactListener() {
			
			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
				// TODO Auto-generated method stub
				
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
		
		
		editor.entityEngine.addEntityListener(Family.all(Box2DJointModel.class).get(), new EntityListener() {
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
		
		
		// TODO activation create a body
		//editor.addTool(new AddBox2DTool(editor, worldItem));
		
		editor.addSerializer(Box2DBodyModel.class, new Box2DModelSerializer(worldItem));
		editor.addSerializer(Box2DJointModel.class, new Box2DJointSerializer(worldItem));
		editor.addSerializer(PolygonShape.class, Box2DShapesSerializers.polygon());
		editor.addSerializer(ChainShape.class, Box2DShapesSerializers.chain());
		editor.addSerializer(CircleShape.class, Box2DShapesSerializers.circle());
		editor.addSerializer(EdgeShape.class, Box2DShapesSerializers.edge());
		editor.addSerializer(Shape.class, Box2DShapesSerializers.shape());
		
		Storage.register(new Box2DJointSerializer(worldItem));
		
		Storage.register(new ContextualSerializer<Body>(Body.class) {
			@Override
			public void write(Json json, Body object, Class knownType) {
				Entity entity = (Entity)object.getUserData();
				int entityIndex = editor.entityEngine.getEntities().indexOf(entity, true);
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
		
		editor.addGlobalEditor("Box2D", new Box2DEditorPlugin(worldItem));
		
		editor.addTool(new Box2DParticleTool(editor));
		
		editor.addSelector(new Box2DBodySelector(editor, worldItem));
		// TODO entity with model Box2D (at least a body) open all the tools ...
		
		// TODO abstraction for auto/add component !
		editor.entityEngine.addEntityListener(Family.one(Box2DBodyModel.class).get(), new EntityListener() {
			
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
				object.body.setUserData(entity);
				
				entity.add(new Movable(new BodyMove(object.body)));
				
				// TODO bah !
				Transform2DComponent transform = entity.getComponent(Transform2DComponent.class);
				
				if(entity.getComponent(Transform2DComponent.class) == null) {
//					transform = new Transform2DComponent();
//					
//					transform.origin.set(object.body.getPosition());
//					entity.add(transform); 
					
				// XXX auto attach again !? : body x/y
				}else{ // case of loading ...
					object.body.setTransform(transform.position, transform.angle);
				}
			}
		});
		
		editor.entityEngine.addSystem(new EntitySystem(GamePipeline.PHYSICS) {
			
			@Override
			public void update(float deltaTime) {
				worldItem.update();
			}
		});
		editor.entityEngine.addSystem(new IteratingSystem(Family.all(Box2DBodyModel.class, Transform2DComponent.class).get(), GamePipeline.AFTER_PHYSICS) {
			@Override
			public void processEntity(Entity entity, float deltaTime) {
				Box2DBodyModel physic = entity.getComponent(Box2DBodyModel.class);
				if(physic.body != null){
					Transform2DComponent t = entity.getComponent(Transform2DComponent.class);
					t.position.set(physic.body.getPosition());
					t.angle = physic.body.getAngle();
				}
			}
		});
		editor.entityEngine.addSystem(new EntitySystem(GamePipeline.LOGIC) {
			
			@Override
			public void update(float deltaTime) {
				for(Entity e : editor.entityEngine.getEntitiesFor(Family.one(Box2DBodyModel.class).get())){
					Box2DBodyModel bodyItem = e.getComponent(Box2DBodyModel.class);
					if(bodyItem.behavior != null) bodyItem.behavior.act();
				}
			}
		});
	
		editor.entityEngine.addSystem(new EntitySystem(GamePipeline.RENDER_OVER) {
			
			@Override
			public void update(float deltaTime) {
				box2dRenderer.render(worldItem.world, editor.camera.combined);
			}
		});

		// TODO type should be configured in editor (activation function !)
		editor.registerPlugin(Box2DBodyModel.class, new Box2DBodyEditorPlugin());
		editor.registerPlugin(Box2DJointModel.class, new Box2DJointEditorPlugin());
		
	}
}
