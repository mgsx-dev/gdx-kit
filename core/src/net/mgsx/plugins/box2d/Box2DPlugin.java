package net.mgsx.plugins.box2d;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.sun.java.swing.plaf.gtk.GTKConstants.ShadowType;

import net.mgsx.core.CommandHistory;
import net.mgsx.core.Editor;
import net.mgsx.core.plugins.Plugin;
import net.mgsx.core.tools.Tool;
import net.mgsx.plugins.box2d.model.BodyItem;
import net.mgsx.plugins.box2d.model.WorldItem;

public class Box2DPlugin extends Plugin 
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
		renderer = new ShapeRenderer();
		box2dRenderer = new Box2DDebugRenderer();
		CommandHistory commandHistory = new CommandHistory(); // XXX fake
		worldItem = new WorldItem(commandHistory);
		worldItem.editor = editor;
		worldItem.initialize();
		
		// TODO activation create a body
		editor.addTool(new AddBox2DTool(editor, worldItem));
		editor.addGlobalTool(new SelectBodyTool(editor, worldItem));
		// TODO entity with model Box2D (at least a body) open all the tools ...
		
		
		editor.entityEngine.addSystem(new EntitySystem() {
			
			@Override
			public void update(float deltaTime) {
				for(Entity e : editor.entityEngine.getEntitiesFor(Family.one(BodyItem.class).get())){
					BodyItem bodyItem = e.getComponent(BodyItem.class);
					if(bodyItem.behavior != null) bodyItem.behavior.act();
				}
				worldItem.update();
			}
		});
	
		editor.entityEngine.addSystem(new EntitySystem() {
			
			@Override
			public void update(float deltaTime) {
				box2dRenderer.render(worldItem.world, editor.orthographicCamera.combined);
				Vector2 s = Tool.pixelSize(editor.orthographicCamera).scl(3);
				renderer.setProjectionMatrix(editor.orthographicCamera.combined);
				renderer.begin(ShapeType.Line);
				for(Entity e : editor.entityEngine.getEntitiesFor(Family.one(BodyItem.class).get())){
					BodyItem item = e.getComponent(BodyItem.class);
					if(item.body != null)
					renderer.rect(item.body.getPosition().x-s.x, item.body.getPosition().y-s.y, 2*s.x, 2*s.y);
				}
				renderer.end();
			}
		});

		editor.registerPlugin(BodyItem.class, new Box2DEditorPlugin(editor, worldItem));
		
	}
}
