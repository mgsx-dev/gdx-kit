package net.mgsx.plugins.box2d;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;

import net.mgsx.core.Editor;
import net.mgsx.core.GamePipeline;
import net.mgsx.core.commands.CommandHistory;
import net.mgsx.core.components.Attach;
import net.mgsx.core.components.Movable;
import net.mgsx.core.plugins.EditorPlugin;
import net.mgsx.core.storage.Storage;
import net.mgsx.core.tools.Tool;
import net.mgsx.plugins.box2d.model.Box2DBodyModel;
import net.mgsx.plugins.box2dold.model.WorldItem;

public class Box2DPlugin extends EditorPlugin 
{

	private WorldItem worldItem;
	
	// TODO future of Box2D package :
	
	// process all entities in step method
	
	// global setting for physics and physic template ...
	
	// lot of tools !
	
	// debug rendering
	
	// ...
	private Box2DDebugRenderer box2dRenderer;
	private ShapeRenderer renderer;
	
	@Override
	public void initialize(final Editor editor) 
	{
		Storage.register(Box2DBodyModel.class, "box2d");
		
		renderer = new ShapeRenderer();
		box2dRenderer = new Box2DDebugRenderer();
		CommandHistory commandHistory = new CommandHistory(); // XXX fake
		worldItem = new WorldItem(commandHistory);
		worldItem.editor = editor;
		worldItem.initialize();
		
		// TODO activation create a body
		//editor.addTool(new AddBox2DTool(editor, worldItem));
		
		editor.addSerializer(Box2DBodyModel.class, new Box2DModelSerializer(worldItem.world));
		editor.addSerializer(PolygonShape.class, Box2DShapesSerializers.polygon());
		editor.addSerializer(ChainShape.class, Box2DShapesSerializers.chain());
		editor.addSerializer(CircleShape.class, Box2DShapesSerializers.circle());
		editor.addSerializer(EdgeShape.class, Box2DShapesSerializers.edge());
		editor.addSerializer(Shape.class, Box2DShapesSerializers.shape());
		
		editor.addGlobalEditor("Box2D", new Box2DEditorPlugin(worldItem));
		
		
		editor.addSelector(new Box2DBodySelector(editor, worldItem));
		// TODO entity with model Box2D (at least a body) open all the tools ...
		
		
		editor.entityEngine.addEntityListener(Family.one(Box2DBodyModel.class).get(), new EntityListener() {
			
			@Override
			public void entityRemoved(Entity entity) {
				Box2DBodyModel model = entity.getComponent(Box2DBodyModel.class);
				model.body.setUserData(null);
				worldItem.world.destroyBody(model.body);
			}
			
			@Override
			public void entityAdded(Entity entity) {
				Box2DBodyModel object = entity.getComponent(Box2DBodyModel.class);
				object.body.setUserData(entity);
				
				// TODO why auto attach ? conceptually OK but ...
				Movable oldMovable = entity.getComponent(Movable.class);
				Movable newMovable = new Movable(new BodyMove(object.body));
				if(oldMovable != null){
					entity.add(new Attach(entity, newMovable, entity, oldMovable));
				}
				entity.add(newMovable);
			}
		});
		
		// TODO it's not the right place, we should have a behavior component
		editor.entityEngine.addSystem(new EntitySystem(GamePipeline.PHYSICS) {
			
			@Override
			public void update(float deltaTime) {
				worldItem.update(); // TODO move box 2D code here ?
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
				box2dRenderer.render(worldItem.world, editor.perspectiveCamera.combined);
				Vector2 s = Tool.pixelSize(editor.orthographicCamera).scl(3);
				renderer.setProjectionMatrix(editor.orthographicCamera.combined);
				renderer.begin(ShapeType.Line);
				for(Entity e : editor.entityEngine.getEntitiesFor(Family.one(Box2DBodyModel.class).get())){
					Box2DBodyModel item = e.getComponent(Box2DBodyModel.class);
					if(item.body != null)
					renderer.rect(item.body.getPosition().x-s.x, item.body.getPosition().y-s.y, 2*s.x, 2*s.y);
				}
				renderer.end();
			}
		});

		editor.registerPlugin(Box2DBodyModel.class, new Box2DBodyEditorPlugin());
		
	}
}
