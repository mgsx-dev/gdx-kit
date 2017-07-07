package net.mgsx.game.examples.openworld.systems;

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
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;

@EditableSystem
public class OpenWorldSkySystem extends EntitySystem
{
	@Inject OpenWorldLandRenderSystem landRenderer;
	
	@Editable public boolean debugFaces = false;
	
	private final int cubeMapSize = 1024;
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
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		
		fboCam = new PerspectiveCamera(90, cubeMapSize, cubeMapSize);
		
		fboCubeMap = new FrameBufferCubemap(Format.RGBA8888, cubeMapSize, cubeMapSize, true);

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
	
	@Override
	public void update(float deltaTime) 
	{
		if(cubeMapDirty) {
			
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
				
				landRenderer.update(0);
				
				if(!debugFaces) {
					renderSky();
				}
				
			}
			
			screen.camera.combined.set(backup);
			
			fboCubeMap.end();
			
			cubeMapDirty = false;
		}
		
		Vector3 vOffset = screen.camera.position.cpy().scl(1);
		
		// render background
		Gdx.gl.glCullFace(GL20.GL_CCW);
		float s = screen.camera.far / 2; // TODO not really that ...
		renderer.setTransformMatrix(renderer.getTransformMatrix().idt());
		renderer.setProjectionMatrix(screen.camera.combined);
		renderer.begin(ShapeType.Filled);
		fboCubeMap.getColorBufferTexture().bind();
		bgShader.setUniformi("u_texture", 0);
		renderer.box(
				vOffset.x-s, 
				vOffset.y-s, 
				vOffset.z-s, s*2, s*2, -s*2);
		renderer.end();
		
		Gdx.gl.glCullFace(GL20.GL_CCW);
		
	}
	
	private void renderSky() {
		
		// simple "infinite" quad
		Vector3 vOffset = Vector3.Zero;
		float s = screen.camera.far / 2; // TODO not really that ...
		
		skyRenderer.setProjectionMatrix(screen.camera.combined);
		skyRenderer.begin(ShapeType.Filled);
		
		// bgShader.setUniformi("u_texture", 0);
		
		skyRenderer.box(
				vOffset.x-s, 
				vOffset.y-s, 
				vOffset.z-s, s*2, s*2, -s*2);
		skyRenderer.end();
		
	}
}
