package net.mgsx.game.examples.openworld.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.PostInitializationListener;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.helpers.ShaderProgramHelper;
import net.mgsx.game.core.helpers.shaders.ShaderInfo;
import net.mgsx.game.core.helpers.shaders.ShaderProgramManaged;
import net.mgsx.game.core.helpers.shaders.Uniform;
import net.mgsx.game.examples.openworld.components.LandMeshComponent;
import net.mgsx.game.examples.openworld.components.ObjectMeshComponent;
import net.mgsx.game.examples.openworld.components.TreesComponent;
import net.mgsx.game.examples.openworld.model.OpenWorldRuntimeSettings;
import net.mgsx.game.plugins.camera.model.POVModel;

@Storable(value="ow.water")
@EditableSystem
public class OpenWorldWaterRenderSystem extends EntitySystem implements PostInitializationListener
{
	@Inject OpenWorldLandRenderSystem landerRendering;
	@Inject OpenWorldEnvSystem environment;
	@Inject OpenWorldSkySystem sky;
	@Inject POVModel pov;
	
	@Editable public int mirrorSize = 1024;

	private ShaderProgram reflectionShader;
	
	private ImmediateModeRenderer renderer;
	
	@ShaderInfo(vs="shaders/water.vert", fs="shaders/water.frag", inject=false)
	public static class WaterShader extends ShaderProgramManaged
	{
		@Uniform transient float time;
		@Editable public float speed = 1f;
		@Uniform @Editable public float frequency = 10;
		@Uniform @Editable public float amplitude = 0.005f;
		@Uniform @Editable public float transparency = 0.3f; // TODO rename diffusion ?
		@Uniform transient Cubemap texture;
		@Uniform transient Vector3 camPos = new Vector3();
		
		@Uniform(only="mirror") transient Texture mirrorTexture;
		@Uniform(only="mirror") transient Vector2 window = new Vector2();
		
		@Uniform(only="translucent") @Editable public Color translucency = new Color(.5f, .5f, 1f, .5f);
	}
	
	@Editable public WaterShader waterShader = new WaterShader();

	
	private FrameBuffer mirrorBuffer;

	public OpenWorldWaterRenderSystem() {
		super(GamePipeline.RENDER_TRANSPARENT);
	}
	
	@Override
	public void onPostInitialization() 
	{
		if(OpenWorldRuntimeSettings.highQuality){
			loadShader();
			setProcessing(true);
		}else{
			setProcessing(false);
		}
	}
	
	@Editable
	public void loadShader(){
		
		ShaderProgram.prependVertexCode = ShaderProgram.prependFragmentCode = "#define CLIP_PLANE\n";
		reflectionShader = ShaderProgramHelper.reload(reflectionShader,
				Gdx.files.internal("shaders/land.vert"),
				Gdx.files.internal("shaders/land.frag"));
		ShaderProgram.prependVertexCode = ShaderProgram.prependFragmentCode = "";

		if(mirrorBuffer != null) mirrorBuffer.dispose();
		mirrorBuffer = new FrameBuffer(Format.RGBA8888, mirrorSize, mirrorSize, true);
		
		mirrorCamera = new PerspectiveCamera();
	}
	private Matrix4 backup = new Matrix4(), transform = new Matrix4();
	private PerspectiveCamera mirrorCamera;
	
	@Editable public boolean lands = true;
	@Editable public boolean trees = true;
	@Editable public boolean objects = true;
	
	@Override
	public void update(float deltaTime) {
		
		final boolean mirror = waterShader.isEnabled("mirror");
		final boolean translucent = waterShader.isEnabled("translucent");
		
		if(mirror){
			
			// render lands and trees from a mirrored view
			
			Gdx.gl.glEnable(GL20.GL_CULL_FACE);
			Gdx.gl.glCullFace(GL20.GL_FRONT);
			 
			// TODO setup camera
			backup.set(pov.camera.combined);
			
			mirrorCamera.position.set(pov.camera.position);
			mirrorCamera.position.y = 2 * environment.waterLevel - mirrorCamera.position.y;

			mirrorCamera.direction.set(pov.camera.direction);
			mirrorCamera.direction.y = -pov.camera.direction.y;
			mirrorCamera.near = pov.camera.near;
			mirrorCamera.far = pov.camera.far;
			mirrorCamera.up.set(pov.camera.up);

			mirrorCamera.viewportWidth = pov.camera.viewportWidth;
			mirrorCamera.viewportHeight = pov.camera.viewportHeight;
			mirrorCamera.fieldOfView = ((PerspectiveCamera)pov.camera).fieldOfView;
			
			mirrorCamera.update();
			
			pov.camera.combined.set(mirrorCamera.combined);
			
			// bind FBO
			mirrorBuffer.begin();
			Gdx.gl.glClearColor(0, 0, 0, 0);
			Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_COLOR_BUFFER_BIT);
			
			reflectionShader.begin();
			reflectionShader.setUniformMatrix("u_projTrans", pov.camera.combined);
			reflectionShader.setUniformMatrix("u_worldTrans", transform.idt());
			reflectionShader.setUniformf("u_sunDirection", environment.sunDirection);
			reflectionShader.setUniformf("u_fogColor", environment.fogColor);
			reflectionShader.setUniformf("u_clip", environment.waterLevel - 0.1f); // XXX offset for deformations.
			if(lands){
				for(Entity entity : getEngine().getEntitiesFor(Family.all(LandMeshComponent.class).get())){
					LandMeshComponent lmc = LandMeshComponent.components.get(entity);
					lmc.mesh.render(reflectionShader, GL20.GL_TRIANGLES);
				}
			}
			if(trees){
				for(Entity entity : getEngine().getEntitiesFor(Family.all(TreesComponent.class).get())){
					TreesComponent tmc = TreesComponent.components.get(entity);
					tmc.mesh.render(reflectionShader, GL20.GL_TRIANGLES);
				}
			}
			if(objects){
				for(Entity entity : getEngine().getEntitiesFor(Family.all(ObjectMeshComponent.class).get())){
					ObjectMeshComponent omc = ObjectMeshComponent.components.get(entity);
					transform.set(pov.camera.combined).mul(omc.getWorldTransform());
					reflectionShader.setUniformMatrix("u_worldTrans", omc.getWorldTransform());
					reflectionShader.setUniformMatrix("u_projTrans", transform);
					omc.getMeshToRender().render(reflectionShader, GL20.GL_TRIANGLES);
				}
			}
			reflectionShader.end();
			
			// unbind FBO
			mirrorBuffer.end();
			
			pov.camera.combined.set(backup);
			
			Gdx.gl.glCullFace(GL20.GL_BACK);
			Gdx.gl.glDisable(GL20.GL_CULL_FACE);
		}
		
		waterShader.time += deltaTime * waterShader.speed;
		waterShader.camPos.set(pov.camera.position);
		waterShader.texture = sky.getCubeMap();
		
		if(mirror){
			waterShader.mirrorTexture = mirrorBuffer.getColorBufferTexture();
			waterShader.window.set(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		}
		
		if(waterShader.begin()){
			if(renderer != null) renderer.dispose();
			renderer = new ImmediateModeRenderer20(4, false, false, 0, waterShader.program());
		}
		
		renderer.begin(pov.camera.combined, GL20.GL_TRIANGLE_STRIP);
		
		float s = pov.camera.far;
		
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

		if(translucent){
			Gdx.gl.glDisable(GL20.GL_CULL_FACE);
			Gdx.gl.glEnable(GL20.GL_BLEND);
			Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		}
		
		float x = pov.camera.position.x;
		float y = environment.waterLevel;
		float z = pov.camera.position.z;
		
		renderer.vertex(x-s, y, z-s);
		renderer.vertex(x+s, y, z-s);
		renderer.vertex(x-s, y, z+s);
		renderer.vertex(x+s, y, z+s);
		
		renderer.end();
		
		waterShader.end();
		
		if(translucent){
			Gdx.gl.glDisable(GL20.GL_CULL_FACE);
			Gdx.gl.glDisable(GL20.GL_BLEND);
		}
		Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
	}
	
}
