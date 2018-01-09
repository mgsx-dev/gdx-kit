package net.mgsx.game.examples.openworld.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.helpers.shaders.ShaderInfo;
import net.mgsx.game.core.helpers.shaders.ShaderProgramManaged;
import net.mgsx.game.core.helpers.shaders.Uniform;
import net.mgsx.game.core.helpers.systems.TransactionSystem;
import net.mgsx.game.examples.openworld.utils.MotionBlur;
import net.mgsx.game.plugins.graphics.model.FBOModel;

@Storable("ow.gfx")
@EditableSystem
public class GFXSystem extends TransactionSystem
{
	@ShaderInfo(vs="shaders/edge.vs", fs="shaders/edge.fs", inject=false, configs={"HQ"})
	public static class EdgeShader extends ShaderProgramManaged{
		@Editable @Uniform("size") public Vector3 size = new Vector3(1,1,1);
		@Editable @Uniform("stroke") public Color stroke = new Color(Color.WHITE);
		@Editable @Uniform("fill") public Color fill = new Color(Color.BLACK);
		@Editable @Uniform public float boost = 1;
		@Editable @Uniform public float blend = 1;
	}
	
	public GFXSystem() {
		super(GamePipeline.AFTER_CULLING, new AfterSystem(GamePipeline.HUD - 1) {});
	}

	@Inject FBOModel fboModel;

	private FrameBuffer fbo;
	private Batch batch = new SpriteBatch();
	
	@Editable public EdgeShader edgeShader;
	
	@Editable public int downsample = 1;
	
	@Editable public boolean edgeFX = true;
	
	@Editable public float motionBlurFactor = 1;
	@Editable public float motionBlurSpread = 1;
	@Editable public float motionBlurRecovery = .1f;
	
	@Inject OpenWorldCameraTrauma cameraTrauma;
	
	private float motionBlurFalloff;
	
	private boolean filter = true;
	
	@Editable public void setFilter(boolean value){
		this.filter = value;
		if(fbo != null){
			TextureFilter tf = filter ? TextureFilter.Linear : TextureFilter.Nearest;
			fbo.getColorBufferTexture().setFilter(tf, tf);
		}
	}
	
	@Editable public boolean isFilter(){return filter;}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		edgeShader = new EdgeShader();
	}
	
	@Override
	protected boolean updateBefore(float deltaTime) 
	{
		int downsample = Math.max(this.downsample, 1);
		int fboW =  Gdx.graphics.getWidth() / downsample;
		int fboH = Gdx.graphics.getHeight() / downsample;
		if(fbo == null || fbo.getWidth() != fboW || fbo.getHeight() != fboH){
			if(fbo != null) fbo.dispose();
			fbo = new FrameBuffer(Format.RGBA8888, fboW, fboH, true);
			TextureFilter tf = filter ? TextureFilter.Linear : TextureFilter.Nearest;
			fbo.getColorBufferTexture().setFilter(tf, tf);
		}
		
		if(motionBlurFalloff < cameraTrauma.trauma){
			motionBlurFalloff = cameraTrauma.trauma;
		}
		motionBlurFalloff = MathUtils.lerp(motionBlurFalloff, 0, deltaTime * motionBlurRecovery);
		
		MotionBlur.single.factor = Math.min(motionBlurFactor, motionBlurFalloff + 0.6f); // TODO min blur
		MotionBlur.single.spread = motionBlurSpread;
		
		if(edgeFX){
			fboModel.push(fbo);
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	protected void updateAfter(float deltaTime) 
	{
		fboModel.pop();
		
		MotionBlur.single.begin(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_COLOR_BUFFER_BIT);
		
		// Gdx.gl.glCullFace(GL20.GL_FRONT);
		Gdx.gl.glDisable(GL20.GL_CULL_FACE);
		
		// Gdx.gl.glEnable(GL20.GL_CULL_FACE);
		
		// render FBO
		batch.setShader(edgeShader.program());
		batch.disableBlending();
		batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		batch.begin();
		
		edgeShader.size.x = Gdx.graphics.getWidth();
		edgeShader.size.y = Gdx.graphics.getHeight();
		
		edgeShader.begin();
		batch.draw(fbo.getColorBufferTexture(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, 1, 1);
		// edgeShader.end();
		
		batch.end();
		
		MotionBlur.single.end(null);
	}
	
	
}
