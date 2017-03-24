package net.mgsx.game.plugins.ui;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.Kit;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.plugins.editor.systems.EditorSystem;

/**
 * System handling stage view in world coordinates.
 * Widget bounds are expressed in world unit (same units as {@link Transform2DComponent} and physics).
 * 
 * @author mgsx
 *
 */
@EditableSystem
public class WidgetSystem extends IteratingSystem
{
	@Inject EditorSystem editor;
	
	@Editable
	public transient boolean debug = false;
	
	private Stage stage;
	public ScreenViewport viewport;
	private ObjectMap<Entity, Actor> actors = new ObjectMap<Entity, Actor>();

	public Skin skin;

	@Editable
	public float unitsPerPixel = 0.05f; // arbitrary convert from screen ~1000 to world ~50
	
	public WidgetSystem() {
		super(Family.all(WidgetComponent.class).get(), GamePipeline.RENDER);
		viewport = new ScreenViewport();
		stage = new Stage(viewport);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		
		// TODO when variable will be injected before added to system :
		// skin = editor.getSkin();
		skin = new Skin(Gdx.files.classpath("uiskin.json"));
		
		Kit.inputs.addProcessor(stage);
		
		engine.addEntityListener(getFamily(), new EntityListener() {
			
			@Override
			public void entityRemoved(Entity entity) {
				actors.remove(entity).remove();
			}
			
			@Override
			public void entityAdded(Entity entity) {
				WidgetComponent widget = WidgetComponent.components.get(entity);
				if(widget.widget == null){
					widget.widget = widget.factory.createActor(getEngine(), entity, skin);
				}
				actors.put(entity, widget.widget);
				stage.addActor(widget.widget);
			}
		});
	}
	
	@Override
	public void removedFromEngine(Engine engine) {
		Kit.inputs.removeProcessor(stage);
		super.removedFromEngine(engine);
	}
	
	private void syncCamera(){
		
		// TODO copied from EditorCamera ...
		Camera camera = editor.getEditorCamera();
		OrthographicCamera orthographicCamera = (OrthographicCamera)viewport.getCamera();
		if(camera instanceof OrthographicCamera){
			OrthographicCamera currentCamera = (OrthographicCamera)camera;
			orthographicCamera.setToOrtho(false, // FIXME true or false ?
					Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			orthographicCamera.position.set(currentCamera.position);
			orthographicCamera.update(true);
		}else if(camera instanceof PerspectiveCamera){
			PerspectiveCamera perspectiveCamera = (PerspectiveCamera)camera;
			orthographicCamera.zoom = perspectiveCamera.position.z;
			orthographicCamera.update(true);
		}
	}
	
	@Override
	public void update(float deltaTime) {
		stage.getRoot().setTransform(true);
		
		Vector3 base = editor.getEditorCamera().position;
		
		viewport.setUnitsPerPixel(unitsPerPixel);
		viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		syncCamera();
		viewport.getCamera().position.set(base.x * unitsPerPixel * Gdx.graphics.getHeight() /1.33333f, base.y * unitsPerPixel  * ( Gdx.graphics.getHeight() /1.33333f), 0);
		viewport.getCamera().update(true);
		stage.setDebugAll(debug);
		super.update(deltaTime);
		stage.act();
		stage.draw();
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		WidgetComponent slider = WidgetComponent.components.get(entity);
		slider.widget.setBounds(slider.bounds.x, slider.bounds.y, slider.bounds.width, slider.bounds.height);
	}

}
