package net.mgsx.game.core;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.plugins.camera.components.CameraComponent;
import net.mgsx.game.plugins.camera.components.RenderingComponent;
import net.mgsx.game.plugins.camera.systems.CameraSystem;

public class EditorCamera 
{
	private Engine entityEngine;

	private boolean orthoMode;
	private Entity gameCamera, editorCamera;

	private OrthographicCamera orthographicCamera;
	private PerspectiveCamera perspectiveCamera;

	
	
	public EditorCamera(Engine entityEngine) {
		super();
		this.entityEngine = entityEngine;
		createCamera();
	}

	private void createCamera()
	{
		gameCamera = entityEngine.getSystem(CameraSystem.class).getRenderCamera();
		
		editorCamera = entityEngine.createEntity();
		
		CameraComponent camera = entityEngine.createComponent(CameraComponent.class);
		RenderingComponent render = entityEngine.createComponent(RenderingComponent.class);
		
		PerspectiveCamera pc = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		pc.position.set(0, 0, 10);
		pc.up.set(0,1,0);
		pc.lookAt(0,0,0);
		pc.near = 1f;
		pc.far = 3000f;
		pc.update();
		camera.camera = pc;
		
		perspectiveCamera = pc;
		
		orthographicCamera = new OrthographicCamera();
		
		editorCamera.add(camera);
		editorCamera.add(render);
		
		entityEngine.addEntity(editorCamera);
	}
	
	public void zoom(float rate) {
		
		rate *= Tool.pixelSize(perspectiveCamera).x * Gdx.graphics.getWidth(); // 100 world unit per pixel TODO pixelSize !!
		
		if(orthoMode){
			perspectiveCamera.position.set(orthographicCamera.position);
			perspectiveCamera.translate(0, 0, -rate);
			perspectiveCamera.update(false);
			syncOrtho(true);
			
		}else{
			perspectiveCamera.translate(0, 0, -rate);
		}
	}
	
	public void fov(float rate) 
	{
		rate *= 360; // degree scale
		
		syncPerspective(false);
		
		// translate camera according to FOV changes (keep sprite plan unchanged !)
		
		float oldFOV = perspectiveCamera.fieldOfView * MathUtils.degreesToRadians * 0.5f;
		
		float hWorld = (float)Math.tan(oldFOV) * perspectiveCamera.position.z;
		
		perspectiveCamera.fieldOfView += rate;
		perspectiveCamera.update(true);
		
		float newFOV = perspectiveCamera.fieldOfView * MathUtils.degreesToRadians * 0.5f;
		
		float distWorld = hWorld / (float)Math.tan(newFOV);
		
		float deltaZ = distWorld - perspectiveCamera.position.z;
		perspectiveCamera.translate(0, 0, deltaZ);
		perspectiveCamera.update(false);
		
		
		syncOrtho(false);
	}
	
	private void syncPerspective(boolean force)
	{
		if(orthoMode || force){
			perspectiveCamera.position.set(orthographicCamera.position);
			perspectiveCamera.update(true);
		}
	}
	private void syncOrtho(boolean force)
	{
		if(!orthoMode || force)
		{
			// sync sprite plan for ortho (working !)
			Vector3 objectDepth = perspectiveCamera.project(new Vector3());
			
			Vector3 a = perspectiveCamera.unproject(new Vector3(0, 0, objectDepth.z));
			Vector3 b = perspectiveCamera.unproject(new Vector3(1, 1, objectDepth.z));
			b.sub(a);
			
			float w = Math.abs(b.x) * Gdx.graphics.getWidth();
			float h = Math.abs(b.y) * Gdx.graphics.getHeight();
			
			orthographicCamera.setToOrtho(false, w, h);
			orthographicCamera.position.set(perspectiveCamera.position);
			
			orthographicCamera.update(true);
		}
	}

	public void switchCamera()
	{
		CameraComponent camera = CameraComponent.components.get(editorCamera);
		if(!orthoMode)
		{
			syncOrtho(true);
			
			
			camera.camera = orthographicCamera;
			orthoMode = true;
		}
		else
		{
			syncPerspective(true);
			
			camera.camera = perspectiveCamera;
			orthoMode = false;
		}
	}
	
	public void switchCameras()
	{
		Entity currentCamera = entityEngine.getSystem(CameraSystem.class).getRenderCamera();
		currentCamera.remove(RenderingComponent.class);
		
		if(currentCamera == editorCamera)
		{
			currentCamera = gameCamera;
		}else{
			currentCamera = editorCamera;
		}
		currentCamera.add(entityEngine.createComponent(RenderingComponent.class));
	}

	public void resize(int width, int height) 
	{
		syncPerspective(false);
		
		perspectiveCamera.viewportWidth = Gdx.graphics.getWidth();
		perspectiveCamera.viewportHeight = Gdx.graphics.getHeight();
		perspectiveCamera.update(true);

		syncOrtho(false);
	}
}
