package net.mgsx.game.examples.openworld.systems;

import java.nio.IntBuffer;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.glutils.FrameBufferCubemap;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.BufferUtils;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.PostInitializationListener;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.helpers.ShaderProgramHelper;
import net.mgsx.game.plugins.camera.model.POVModel;
import net.mgsx.game.plugins.graphics.model.FBOModel;

@Storable(value="ow.sky")
@EditableSystem
public class OpenWorldSkySystem extends EntitySystem implements PostInitializationListener
{
	@Inject OpenWorldLandRenderSystem landRenderer;
	@Inject OpenWorldEnvSystem environment;
	@Inject WeatherSystem weather;
	@Inject POVModel pov;
	@Inject FBOModel fboModel;
	
	@Editable public boolean debugFaces = false;
	
	@Editable public int cubeMapSize = 2048;
	
	@Editable public boolean realtime = false;
	
	@Editable public float cloudDarkness = 3f;
	@Editable public float parallax = 1f;
	
	private boolean cubeMapDirty;
	private FrameBufferCubemap fboCubeMap;
	private ShaderProgram bgShader;
	private PerspectiveCamera fboCam;
	
	private ShaderProgram skyShader;
	private ShapeRenderer skyRenderer;
	
	private ShapeRenderer renderer;
	
	private float cloudTime; // TODO should trace offset, since direction can change (need shader modifictions)
	
	public OpenWorldSkySystem() {
		super(GamePipeline.RENDER);
	}
	
	public Cubemap getCubeMap() {
		return fboCubeMap.getColorBufferTexture();
	}
	
	@Editable
	public void genEnv(){
		cubeMapDirty = true;
		
		skyShader = ShaderProgramHelper.reload(skyShader,
				Gdx.files.internal("shaders/sky.vert"),
				Gdx.files.internal("shaders/sky.frag"));
		
		if(skyRenderer != null) skyRenderer.dispose();
		skyRenderer = new ShapeRenderer(36, skyShader);
		
		// TODO utils in libGDX ?
		IntBuffer params = BufferUtils.newIntBuffer(16);
		Gdx.gl.glGetIntegerv(GL20.GL_MAX_CUBE_MAP_TEXTURE_SIZE, params);
		int maxCubeMapSize = params.get() / 4; // TODO why : because it is the max size for 1 byte format, we use 4 bytes format ... ?
		// TODO it is recommanded to use texture proxy in order to get precise limit, it is due to internal format, just divide by 4 could not be the right way !
		
		if(cubeMapSize > maxCubeMapSize) {
			cubeMapSize = maxCubeMapSize;
			Gdx.app.log("WARNING", "cubemap size too big : max " + maxCubeMapSize);
		}
		
		if(fboCubeMap == null || fboCubeMap.getWidth() != cubeMapSize || fboCubeMap.getHeight() != cubeMapSize){
			if(fboCubeMap != null) fboCubeMap.dispose();
			fboCubeMap = new FrameBufferCubemap(Format.RGBA8888, cubeMapSize, cubeMapSize, true);
			fboCam = new PerspectiveCamera(90, cubeMapSize, cubeMapSize);
		}
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		
		cubeMapDirty = true;
//		for(int i=0 ; i<6 ; i++)
//			Gdx.gl30.glFramebufferTextureLayer(GL30.GL_DRAW_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0 + i, cubeMap.glTarget, 0, 1);
	
		bgShader = ShaderProgramHelper.reload(bgShader,
			Gdx.files.internal("shaders/sky-bg.vert"),
			Gdx.files.internal("shaders/sky-bg.frag"));
		
//		skyShader = new ShaderProgram(
//				Gdx.files.internal("shaders/sky.vert"),
//				Gdx.files.internal("shaders/sky.frag"));
	
		renderer = new ShapeRenderer(36, bgShader);
		// skyRenderer = new ShapeRenderer(36, skyShader);
	}
	
	@Override
	public void onPostInitialization() {
		genEnv();
	}
	
	private Matrix4 backup = new Matrix4();
	@Editable public Vector2 cloudDirection = new Vector2(Vector2.X);
	
	Color [] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.CYAN, Color.PURPLE};

	@Override
	public void update(float deltaTime) 
	{
		cloudTime += deltaTime * weather.windSpeed / 400f; // map from km/h to texCoord
		
		if(cubeMapDirty || realtime) {
			
			prepareSky();
			
			fboCubeMap.begin();
			
			backup.set(pov.camera.combined);
			
			while(fboCubeMap.nextSide()) {
				
				if(debugFaces){
					
					Color color = colors[fboCubeMap.getSide().index];
					Gdx.gl.glClearColor(color.r, color.g, color.b, color.a);
					Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
				} else{
					Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
				}
				
				// TODO render geometries
				fboCam.position.set(pov.camera.position);
				//fboCam.position.y = 3; // XXX offset
				fboCam.near = pov.camera.far/2;
				fboCam.far = 3000;
				
				fboCubeMap.getSide().getUp(fboCam.up);
				fboCubeMap.getSide().getDirection(fboCam.direction);
				fboCam.update();
				
				pov.camera.combined.set(fboCam.combined);
				
				landRenderer.renderLow();
				
				if(!debugFaces) {
					renderSky();
				}
				
			}
			
			pov.camera.combined.set(backup);
			
			fboCubeMap.end();
			
			cubeMapDirty = false;
			
			fboModel.bind();
		}
		
		bgShader.begin();
		bgShader.setUniformi("u_texture", 0);
		bgShader.end();
		
		boolean parallaxEffect = parallax != 1 && pov.camera instanceof PerspectiveCamera;
		float oldFOV = 0;
		if(parallaxEffect){
			oldFOV = ((PerspectiveCamera)pov.camera).fieldOfView;
			((PerspectiveCamera)pov.camera).fieldOfView *= parallax;
			pov.camera.update();
		}
		
		Gdx.gl.glDisable(GL20.GL_CULL_FACE); // XXX issue with render box !
		
		// render background
		float s = pov.camera.far / 2; // TODO not really that ...
		renderer.setTransformMatrix(renderer.getTransformMatrix().setToTranslation(pov.camera.position));
		renderer.setProjectionMatrix(pov.camera.combined);
		renderer.begin(ShapeType.Filled);
		fboCubeMap.getColorBufferTexture().bind();
		renderer.box(
				-s, 
				-s, 
				-s, s*2, s*2, -s*2);
		renderer.end();
		
		if(parallaxEffect){
			((PerspectiveCamera)pov.camera).fieldOfView = oldFOV;
			pov.camera.update();
		}
		
		Gdx.gl.glEnable(GL20.GL_CULL_FACE); // XXX issue with render box !

	}
	
	private void prepareSky() {
		skyShader.begin();
		skyShader.setUniformf("u_sunDirection", environment.sunDirection);
		skyShader.setUniformf("u_fogColor", environment.fogColor);
		skyShader.setUniformf("u_cloudTime", cloudTime);
		skyShader.setUniformf("u_cloudRate", MathUtils.lerp(5, .5f, weather.cloudRate));
		skyShader.setUniformf("u_cloudDarkness", cloudDarkness);
		skyShader.setUniformf("u_cloudDirection", cloudDirection.set(Vector2.Y).rotate(-weather.windAngle));
		skyShader.end();
	}
	
	private void renderSky() {
		
		
		// simple "infinite" quad
		float s = 2000; // pov.camera.far/2; //(3000 + 50) / 2; //pov.camera.far + 1; // TODO not really that ...
		
		skyRenderer.setTransformMatrix(skyRenderer.getTransformMatrix().setToTranslation(pov.camera.position));
		skyRenderer.setProjectionMatrix(pov.camera.combined);
		skyRenderer.begin(ShapeType.Filled);
		
		Gdx.gl.glDisable(GL20.GL_CULL_FACE); // XXX issue with render box !
		
		skyRenderer.box(
				-s, 
				-s, 
				-s, s*2, s*2, -s*2);
		skyRenderer.end();
		
		Gdx.gl.glEnable(GL20.GL_CULL_FACE); // XXX issue with render box !
		
	}
}
