package net.mgsx.game.examples.gpu.systems;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.examples.gpu.components.SpikeComponent;
import net.mgsx.game.plugins.g3d.components.G3DModel;

@EditableSystem
public class GPUSpikeSystem extends IteratingSystem
{
	/** from org.lwjgl.opengl.GL32 */
	public static final int GL_GEOMETRY_SHADER = 0x8DD9;

	@Editable public float strength = 1;
	@Editable public float thickness = .5f;
	
	protected EditorScreen screen;
	private ShaderProgram shader;

	public GPUSpikeSystem(EditorScreen screen) {
		super(Family.all(G3DModel.class, SpikeComponent.class).get(), GamePipeline.RENDER);
		this.screen = screen;
	}
	
	@Override
	public void addedToEngine(Engine engine) 
	{
		super.addedToEngine(engine);
		
		shader = new ShaderProgram(Gdx.files.internal("gpu/spike.vs"), Gdx.files.internal("gpu/spike.fs"));
		
		int gs = loadShader(GL_GEOMETRY_SHADER, Gdx.files.internal("gpu/spike.gs").readString());
		
		int program = 0;
		try{
			Field f = ShaderProgram.class.getDeclaredField("program");
			f.setAccessible(true);
			program = f.getInt(shader);
		}catch(Exception e){
			throw new GdxRuntimeException(e);
		}
		// XXX int program = (Integer)ReflectionHelper.get(shader, ReflectionHelper.field(ShaderProgram.class, "program"));
		
		// int gs = Gdx.gl.glCreateShader(0x8DD9);
		
		
		linkProgram(program, gs);
		int e = Gdx.gl.glGetError();
		if(e != GL30.GL_NO_ERROR) throw new GdxRuntimeException("error");
		
		engine.addEntityListener(getFamily(), new EntityListener() {
			
			@Override
			public void entityRemoved(Entity entity) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void entityAdded(Entity entity) {
				G3DModel model = G3DModel.components.get(entity);
				SpikeComponent spike = SpikeComponent.components.get(entity);
				
				Mesh base = model.modelInstance.model.meshes.get(0);
				
				Mesh mesh = base.copy(true, false, new int[]{VertexAttributes.Usage.Position, VertexAttributes.Usage.Normal});
				spike.transform = model.modelInstance.nodes.get(0).localTransform.cpy();
				spike.mesh = mesh;
			}
		});
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		SpikeComponent spike = SpikeComponent.components.get(entity);
		G3DModel model = G3DModel.components.get(entity);
		spike.transform.set(model.modelInstance.nodes.get(0).localTransform);
		shader.setUniformMatrix("u_projTrans", screen.getGameCamera().combined.cpy().mul(model.modelInstance.transform.cpy().mul(spike.transform)));
		spike.mesh.render(shader, GL20.GL_TRIANGLES);
	}
	
	float time = 0;
	Vector2 vp = new Vector2();
	Vector2 wp = new Vector2();
	@Override
	public void update(float deltaTime) 
	{
		time += deltaTime;
		
		Gdx.gl.glDepthMask(true);
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glDisable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
		
		screen.getGameCamera().lookAt(Vector3.Zero);
		
		shader.begin();
		
	//	shader.setUniformf("time", time);
		
		shader.setUniformf("u_strength", strength);
		shader.setUniformf("u_thickness", thickness);
		
	//	shader.setUniformf("u_viewport", vp.set(screen.getGameCamera().viewportWidth, screen.getGameCamera().viewportHeight));
		
	//	shader.setUniformf("u_world", wp.set(screen.stage.getViewport().getWorldWidth(), screen.stage.getViewport().getWorldHeight()));
		
		super.update(deltaTime);
		
		shader.end();
	}
	
	private int loadShader (int type, String source) {
		GL20 gl = Gdx.gl20;
		IntBuffer intbuf = BufferUtils.newIntBuffer(1);

		int shader = gl.glCreateShader(type);

		gl.glShaderSource(shader, source);
		gl.glCompileShader(shader);
		gl.glGetShaderiv(shader, GL20.GL_COMPILE_STATUS, intbuf);

		int compiled = intbuf.get(0);
		if (compiled == 0) {
			String infoLog = gl.glGetShaderInfoLog(shader);
			throw new GdxRuntimeException(infoLog);
		}
		
		return shader;
	}
	
	private void linkProgram (int program, int geoShader) {
		GL20 gl = Gdx.gl20;

		gl.glAttachShader(program, geoShader);
		gl.glLinkProgram(program);

		ByteBuffer tmp = ByteBuffer.allocateDirect(4);
		tmp.order(ByteOrder.nativeOrder());
		IntBuffer intbuf = tmp.asIntBuffer();

		gl.glGetProgramiv(program, GL20.GL_LINK_STATUS, intbuf);
		int linked = intbuf.get(0);
		if (linked == 0) {
			throw new GdxRuntimeException(Gdx.gl20.glGetProgramInfoLog(program));
		}

	}

}
