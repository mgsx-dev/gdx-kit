package net.mgsx.game.plugins.camera.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.EnumType;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.plugins.camera.components.ActiveCamera;
import net.mgsx.game.plugins.camera.components.CameraComponent;
import net.mgsx.game.plugins.camera.components.ViewportComponent;
import net.mgsx.game.plugins.editor.systems.DebugRenderSystem;
import net.mgsx.game.plugins.editor.systems.EditorSystem;

@EditableSystem
public class ViewportDebugSystem extends EntitySystem
{
	@Inject protected DebugRenderSystem debug;
	@Inject protected EditorSystem editor;

	@Editable
	public boolean editorCameraOnly = true;
	
	@Editable
	public int offset = 0;
	
	@Editable(type=EnumType.UNIT)
	public float alpha = 0.5f;
	
	private Vector3 base = new Vector3();
	private Vector3 p1 = new Vector3();
	private Vector3 p2 = new Vector3();
	private Vector3 p3 = new Vector3();
	private Vector3 p4 = new Vector3();
	
	private ShapeRenderer renderer;
	
	private ImmutableArray<Entity> cameras;
	
	public ViewportDebugSystem() {
		super(GamePipeline.RENDER_DEBUG);
		renderer = new ShapeRenderer();
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		cameras = engine.getEntitiesFor(Family.all(CameraComponent.class, ActiveCamera.class, ViewportComponent.class).get());
	}
	
	@Override
	public void update(float deltaTime) 
	{
		// only display viewport in editor camera mode.
		// in game mode, rectangle would cover all screen, so not very useful.
		if((editor.isEditorCamera() || !editorCameraOnly) && cameras.size() > 0)
		{
			Entity entity = cameras.first();
			
			CameraComponent camera = CameraComponent.components.get(entity);
			
			ViewportComponent viewport = ViewportComponent.components.get(entity);
			
			camera.camera.project(base.setZero());
			camera.camera.unproject(p1.set(offset,offset,base.z));
			camera.camera.unproject(p2.set(Gdx.graphics.getWidth()-offset,Gdx.graphics.getHeight()-offset,base.z));
			camera.camera.unproject(p3.set(-viewport.viewport.getLeftGutterWidth(), -viewport.viewport.getTopGutterHeight(),base.z));
			camera.camera.unproject(p4.set(Gdx.graphics.getWidth()+viewport.viewport.getRightGutterWidth(),
					Gdx.graphics.getHeight() + viewport.viewport.getBottomGutterHeight(),base.z));
			
			Gdx.gl.glEnable(GL20.GL_BLEND);
			
			renderer.setProjectionMatrix(editor.isEditorCamera() ? editor.getEditorCamera().combined : camera.camera.combined);
			renderer.begin(ShapeType.Filled);
			
			renderer.setColor(0,0,0, alpha);
			renderer.rect(p3.x, p3.y, p4.x - p3.x, p4.y - p3.y);
			
			renderer.setColor(1,1,1, alpha);
			renderer.rect(p1.x, p1.y, p2.x - p1.x, p2.y - p1.y);
			
			renderer.end();
			
			Gdx.gl.glDisable(GL20.GL_BLEND);
		}
		
	}
}
