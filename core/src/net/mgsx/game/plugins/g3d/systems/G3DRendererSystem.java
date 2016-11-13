package net.mgsx.game.plugins.g3d.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.components.Hidden;
import net.mgsx.game.core.helpers.FilesShader;
import net.mgsx.game.plugins.g3d.components.G3DModel;

@EditableSystem("G3D Rendering")
public class G3DRendererSystem extends IteratingSystem 
{
	// TODO should be in editor code !
	public static enum ShaderType{
		DEFAULT, VERTEX, FRAGMENT, TOON
	}
	
	@Editable
	public ShaderType shader = ShaderType.DEFAULT;
	
	@Editable
	public Color ambient = new Color(0.4f, 0.4f, 0.4f, 1f);
	
	@Editable
	public Color diffuse = new Color(0.8f, 0.8f, 0.8f, 1f);

	@Editable
	public Quaternion direction = new Quaternion().setFromAxisRad(-1f, -0.8f, -0.2f, 0);

	
	
	
	private ModelBatch modelBatch;
	public Environment environment;
	
	public DirectionalLight light;
	
	public ShaderProvider [] shaderProviders;

	private GameScreen engine;


	public G3DRendererSystem(GameScreen engine) {
		super(Family.all(G3DModel.class).exclude(Hidden.class).get(), GamePipeline.RENDER);
		this.engine = engine;
		
		// TODO env should be configurable ... in some way but it's not 1-1 mapping !
		
		FileHandle vs = Gdx.files.classpath("net/mgsx/game/plugins/g3d/shaders/pixel-vertex.glsl");
		FileHandle fs = Gdx.files.classpath("net/mgsx/game/plugins/g3d/shaders/pixel-fragment.glsl");
		
		shaderProviders = new ShaderProvider[ShaderType.values().length];
		shaderProviders[ShaderType.DEFAULT.ordinal()] = new DefaultShaderProvider();
		shaderProviders[ShaderType.VERTEX.ordinal()] = new DefaultShaderProvider();
		shaderProviders[ShaderType.FRAGMENT.ordinal()] = new FilesShader(vs, fs);
		shaderProviders[ShaderType.TOON.ordinal()] = new FilesShader(
				Gdx.files.local("../core/src/net/mgsx/game/plugins/g3d/shaders/platform-vertex.glsl"),
				Gdx.files.local("../core/src/net/mgsx/game/plugins/g3d/shaders/platform-fragment.glsl")); // TODO toon !
		
		
		ShaderProvider switchableProvider = new ShaderProvider() {
			@Override
			public Shader getShader(Renderable renderable) {
				return shaderProviders[shader.ordinal()].getShader(renderable);
			}
			@Override
			public void dispose() {
			}
		};
		
		modelBatch = new ModelBatch(switchableProvider);
		
		light = new DirectionalLight().set(diffuse, direction.transform(new Vector3(0,0,1)) );
		
		environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, ambient));
        environment.add(light);

	}

	@Override
	public void update(float deltaTime) {
		
		// update environnement TODO editor specific ?
		((ColorAttribute)environment.get(ColorAttribute.AmbientLight)).color.set(ambient);
		light.color.set(diffuse);
		light.direction.set(direction.transform(new Vector3(0,0,1)));
		
		modelBatch.begin(engine.camera);
		super.update(deltaTime);
	    modelBatch.end();
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		G3DModel model = G3DModel.components.get(entity);
		if(model.inFrustum){
			modelBatch.render(model.modelInstance, environment);
		}
	}
}