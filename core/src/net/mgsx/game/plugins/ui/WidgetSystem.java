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
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.Kit;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.plugins.core.components.Transform2DComponent;
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
	
	private Stage stage;
	public ScreenViewport viewport;
	private ObjectMap<Entity, Actor> actors = new ObjectMap<Entity, Actor>();

	private Skin skin;

	@Editable
	public float unitsPerPixel = 0.05f; // arbitrary convert from screen ~1000 to world ~50
	
	public WidgetSystem(EditorScreen editor) {
		super(Family.all(SliderComponent.class).get(), GamePipeline.RENDER);
		viewport = new ScreenViewport();
		stage = new Stage(viewport);
		skin = editor.loadAssetNow("uiskin.json", Skin.class);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		
		Kit.inputs.addProcessor(stage);
		
		engine.addEntityListener(getFamily(), new EntityListener() {
			
			@Override
			public void entityRemoved(Entity entity) {
				actors.remove(entity).remove();
			}
			
			@Override
			public void entityAdded(Entity entity) {
				SliderComponent slider = SliderComponent.components.get(entity);
				slider.widget = new Slider(0, 1, .001f, false, skin);
				actors.put(entity, slider.widget);
				stage.addActor(slider.widget);
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
		stage.setDebugAll(true);
		super.update(deltaTime);
		stage.act();
		stage.draw();
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		SliderComponent slider = SliderComponent.components.get(entity);
		slider.widget.setBounds(slider.bounds.x, slider.bounds.y, slider.bounds.width, slider.bounds.height);
		slider.widget.invalidate();
		slider.widget.validate();
	}

}
