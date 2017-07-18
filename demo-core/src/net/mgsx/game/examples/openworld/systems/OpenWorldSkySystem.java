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
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.annotations.Storable;

@Storable(value="ow.sky")
@EditableSystem
public class OpenWorldSkySystem extends EntitySystem
{
	@Inject OpenWorldLandRenderSystem landRenderer;
	@Inject OpenWorldEnvSystem environment;
	
	@Editable public boolean debugFaces = false;
	
	@Editable public int cubeMapSize = 2048;
	
	@Editable public boolean realtime = false;
	
	@Editable public float cloudSpeed = 0.1f;
	@Editable public float cloudRate = 2f;
	@Editable public float cloudDarkness = 3f;
	@Editable public float parallax = 1f;
	
	private boolean cubeMapDirty;
	private FrameBufferCubemap fboCubeMap;
	private ShaderProgram bgShader;
	private PerspectiveCamera fboCam;
	
	private ShaderProgram skyShader;
	private ShapeRenderer skyRenderer;
	
	private ShapeRenderer renderer;
	private GameScreen screen;
	
	public OpenWorldSkySystem(GameScreen screen) {
		super(GamePipeline.RENDER);
		this.screen = screen;
	}
	
	public Cubemap getCubeMap() {
		return fboCubeMap.getColorBufferTexture();
	}
	
	@Editable
	public void genEnv(){
		cubeMapDirty = true;
		
		// XXX
		if(skyShader != null) skyShader.dispose();
		skyShader = new ShaderProgram(
				Gdx.files.internal("shaders/sky.vert"),
				Gdx.files.internal("shaders/sky.frag"));
		
		if(!skyShader.isCompiled()){
			throw new GdxRuntimeException(skyShader.getLog());
		}
		
		if(skyRenderer != null) skyRenderer.dispose();
		skyRenderer = new ShapeRenderer(36, skyShader);
		
		// TODO utils in libGDX ?
		IntBuffer params = BufferUtils.newIntBuffer(16);
		Gdx.gl.glGetIntegerv(GL20.GL_MAX_CUBE_MAP_TEXTURE_SIZE, params);
		int maxCubeMapSize = params.get() / 4; // TODO why : because it is the max size for 1 byte format, we use 4 bytes format ... ?
		
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
	
		bgShader = new ShaderProgram(
			Gdx.files.internal("shaders/sky-bg.vert"),
			Gdx.files.internal("shaders/sky-bg.frag"));
		
//		skyShader = new ShaderProgram(
//				Gdx.files.internal("shaders/sky.vert"),
//				Gdx.files.internal("shaders/sky.frag"));
	
		renderer = new ShapeRenderer(36, bgShader);
		// skyRenderer = new ShapeRenderer(36, skyShader);
		
		genEnv();
	}
	
	private Matrix4 backup = new Matrix4();
	@Editable public Vector2 cloudDirection = new Vector2(Vector2.X);
	
	@Override
	public void update(float deltaTime) 
	{
		if(cubeMapDirty || realtime) {
			
			prepareSky();
			
			fboCubeMap.begin();
			
			Color [] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.CYAN, Color.PURPLE};
			
			backup.set(screen.camera.combined);
			
			while(fboCubeMap.nextSide()) {
				
				if(debugFaces){
					
					Color color = colors[fboCubeMap.getSide().index];
					Gdx.gl.glClearColor(color.r, color.g, color.b, color.a);
					Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
				} else{
					Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
				}
				
				// TODO render geometries
				fboCam.position.set(screen.camera.position);
				fboCam.position.y = 0; // XXX offset
				fboCam.near = 10f;
				fboCam.far = 3000;
				
				fboCubeMap.getSide().getUp(fboCam.up);
				fboCubeMap.getSide().getDirection(fboCam.direction);
				fboCam.update();
				
				screen.camera.combined.set(fboCam.combined);
				
				landRenderer.renderLow();
				
				if(!debugFaces) {
					renderSky();
				}
				
			}
			
			screen.camera.combined.set(backup);
			
			fboCubeMap.end();
			
			cubeMapDirty = false;
		}
		
		bgShader.begin();
		bgShader.setUniformi("u_texture", 0);
		bgShader.end();
		
		boolean parallaxEffect = parallax != 1 && screen.camera instanceof PerspectiveCamera;
		float oldFOV = 0;
		if(parallaxEffect){
			oldFOV = ((PerspectiveCamera)screen.camera).fieldOfView;
			((PerspectiveCamera)screen.camera).fieldOfView *= parallax;
			screen.camera.update();
		}
		
		// render background
		float s = screen.camera.far / 2; // TODO not really that ...
		renderer.setTransformMatrix(renderer.getTransformMatrix().setToTranslation(screen.camera.position));
		renderer.setProjectionMatrix(screen.camera.combined);
		renderer.begin(ShapeType.Filled);
		fboCubeMap.getColorBufferTexture().bind();
		renderer.box(
				-s, 
				-s, 
				-s, s*2, s*2, -s*2);
		renderer.end();
		
		if(parallaxEffect){
			((PerspectiveCamera)screen.camera).fieldOfView = oldFOV;
			screen.camera.update();
		}

	}
	
	private void prepareSky() {
		skyShader.begin();
		skyShader.setUniformf("u_sunDirection", environment.sunDirection);
		skyShader.setUniformf("u_fogColor", environment.fogColor);
		skyShader.setUniformf("u_cloudTime", environment.time * cloudSpeed);
		skyShader.setUniformf("u_cloudRate", cloudRate);
		skyShader.setUniformf("u_cloudDarkness", cloudDarkness);
		skyShader.setUniformf("u_cloudDirection", cloudDirection);
		skyShader.end();
	}
	
	private void renderSky() {
		
		// simple "infinite" quad
		float s = screen.camera.far / 2; // TODO not really that ...
		
		skyRenderer.setTransformMatrix(skyRenderer.getTransformMatrix().setToTranslation(screen.camera.position));
		skyRenderer.setProjectionMatrix(screen.camera.combined);
		skyRenderer.begin(ShapeType.Filled);
		
		// bgShader.setUniformi("u_texture", 0);
		
		skyRenderer.box(
				-s, 
				-s, 
				-s, s*2, s*2, -s*2);
		skyRenderer.end();
		
	}
}
