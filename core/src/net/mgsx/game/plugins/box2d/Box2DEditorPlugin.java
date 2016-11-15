package net.mgsx.game.plugins.box2d;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.components.Movable;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.components.Box2DJointModel;
import net.mgsx.game.plugins.box2d.editors.Box2DBodyEditorPlugin;
import net.mgsx.game.plugins.box2d.editors.Box2DJointEditorPlugin;
import net.mgsx.game.plugins.box2d.editors.Box2DWorldEditorPlugin;
import net.mgsx.game.plugins.box2d.tools.BodyMove;
import net.mgsx.game.plugins.box2d.tools.Box2DBodySelector;
import net.mgsx.game.plugins.box2d.tools.Box2DParticleTool;
import net.mgsx.game.plugins.core.components.SlavePhysics;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

@PluginDef(dependencies={Box2DPlugin.class})
public class Box2DEditorPlugin extends EditorPlugin 
{

	
	// TODO future of Box2D package :
	
	// process all entities in step method
	
	// global setting for physics and physic template ...
	
	// lot of tools !
	
	// debug rendering
	
	// ...
	private Box2DDebugRenderer box2dRenderer;
	
	@Override
	public void initialize(final EditorScreen editor) 
	{
		Box2DPlugin.worldItem.editor = editor;
		
		box2dRenderer = new Box2DDebugRenderer();
		
		
		editor.registry.addGlobalEditor("Box2D", new Box2DWorldEditorPlugin(Box2DPlugin.worldItem));
		
		editor.addTool(new Box2DParticleTool(editor));
		
		editor.addSelector(new Box2DBodySelector(editor, Box2DPlugin.worldItem));
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
				Box2DPlugin.worldItem.world.setGravity(Box2DPlugin.worldItem.settings.gravity);
				Box2DPlugin.worldItem.update();
			}
		});
		editor.entityEngine.addSystem(new IteratingSystem(Family.all(Box2DBodyModel.class, Transform2DComponent.class).exclude(SlavePhysics.class).get(), GamePipeline.AFTER_PHYSICS) {
			@Override
			public void processEntity(Entity entity, float deltaTime) {
				Box2DBodyModel physic = entity.getComponent(Box2DBodyModel.class);
				Transform2DComponent t = entity.getComponent(Transform2DComponent.class);
				if(physic.body != null){
					t.position.set(physic.body.getPosition());
					t.angle = physic.body.getAngle();
				}
			}
		});
		editor.entityEngine.addSystem(new IteratingSystem(Family.all(Box2DBodyModel.class, Transform2DComponent.class, SlavePhysics.class).get(), GamePipeline.AFTER_LOGIC) {
			@Override
			public void processEntity(Entity entity, float deltaTime) {
				Box2DBodyModel physic = entity.getComponent(Box2DBodyModel.class);
				Transform2DComponent t = entity.getComponent(Transform2DComponent.class);
				if(physic.body != null){
					physic.body.setLinearVelocity(t.position.cpy().sub(physic.body.getPosition()));
					float a1 = t.angle * MathUtils.degreesToRadians;
					float a2 = physic.body.getAngle();
					float delta = a2 - a1; // TODO not good !
					physic.body.setAngularVelocity(delta);
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
				box2dRenderer.render(Box2DPlugin.worldItem.world, editor.getRenderCamera().combined);
			}
		});

		// TODO type should be configured in editor (activation function !)
		editor.registry.registerPlugin(Box2DBodyModel.class, new Box2DBodyEditorPlugin());
		editor.registry.registerPlugin(Box2DJointModel.class, new Box2DJointEditorPlugin());
		
	}
}
