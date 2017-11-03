package net.mgsx.game.examples.shmup.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader.Config;
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.plugins.g3d.systems.G3DRendererSystem;

@EditableSystem
public class ShmupRenderSystem extends EntitySystem
{
	public @Editable Vector2 dotsFactor = new Vector2(1, 1);
	public @Editable Vector2 dotsRes = new Vector2(5, 5);
	
	public ShmupRenderSystem() {
		super(GamePipeline.BEFORE_RENDER);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		patchShader();
	}
	
	@Editable
	public void patchShader(){
		getEngine().getSystem(G3DRendererSystem.class).setShaderProvider(new BaseShaderProvider() {
			@Override
			protected Shader createShader(Renderable renderable) {
				Shader shader = new DefaultShader(renderable, new Config(Gdx.files.internal("shaders/sketch.vs").readString(), Gdx.files.internal("shaders/sketch.fs").readString())){
					private int dotsFactorLocation, dotsResLocation;
					@Override
					public void init(ShaderProgram program, Renderable renderable) {
						dotsFactorLocation = register("u_dotsFactor");
						dotsResLocation = register("u_dotsRes");
						super.init(program, renderable);
					}
					@Override
					public void begin(Camera camera, RenderContext context) {
						super.begin(camera, context);
						set(dotsFactorLocation, dotsFactor);
						set(dotsResLocation, dotsRes);
					}
				};
				
				return shader;
			}
		});
	}
}
