package net.mgsx.game.examples.platformer.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.helpers.FilesShader;
import net.mgsx.game.plugins.g3d.systems.G3DRendererSystem;

@EditableSystem
public class PlatformerRenderSystem extends EntitySystem
{

	public static enum ShaderType{
		DEFAULT, VERTEX, FRAGMENT, TOON, SHADOW
	}
	
	private ShaderProvider [] shaderProviders;
	
	private ShaderType shader = ShaderType.DEFAULT;

	@Editable
	public void reloadShaders(){
		ShaderProvider sp = shaderProviders[shader.ordinal()];
		if(sp instanceof FilesShader){
			shaderProviders[shader.ordinal()] = ((FilesShader) sp).reload();
		}
	}
	
	@Editable
	public void setShader(ShaderType shader) {
		this.shader = shader;
		getEngine().getSystem(G3DRendererSystem.class).setShaderProvider(shaderProviders[shader.ordinal()]);
	}
	@Editable
	public ShaderType getShader() {
		return shader;
	}
	
	public PlatformerRenderSystem() 
	{
		super(GamePipeline.BEFORE_RENDER);
		
		FileHandle vs = Gdx.files.internal("shaders/pixel-vertex.glsl");
		FileHandle fs = Gdx.files.internal("shaders/pixel-fragment.glsl");
		
		shaderProviders = new ShaderProvider[ShaderType.values().length];
		shaderProviders[ShaderType.DEFAULT.ordinal()] = new DefaultShaderProvider();
		shaderProviders[ShaderType.VERTEX.ordinal()] = new DefaultShaderProvider();
		shaderProviders[ShaderType.FRAGMENT.ordinal()] = new FilesShader(vs, fs);
		shaderProviders[ShaderType.TOON.ordinal()] = new FilesShader(
				Gdx.files.internal("shaders/platform-vertex.glsl"),
				Gdx.files.internal("shaders/platform-fragment.glsl")); // TODO toon !
		shaderProviders[ShaderType.SHADOW.ordinal()] = new FilesShader(
				Gdx.files.internal("shaders/shadow-vertex.glsl"),
				Gdx.files.internal("shaders/shadow-fragment.glsl")); // TODO toon !
	}
}
