package net.mgsx.game.examples.gpu.systems;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.GamePipeline;

public class GPUQuickFoliageSystem extends EntitySystem
{
	/** from org.lwjgl.opengl.GL32 */
	public static final int GL_GEOMETRY_SHADER = 0x8DD9;
	
	/**
	 *  Accepted by the &lt;pname&gt; parameter of ProgramParameteriEXT and
	 *  GetProgramiv:
	 */
	public static final int GL_GEOMETRY_VERTICES_OUT = 0x8DDA,
			GL_GEOMETRY_INPUT_TYPE = 0x8DDB,
			GL_GEOMETRY_OUTPUT_TYPE = 0x8DDC;

	
	protected EditorScreen screen;
	
	private Mesh mesh;
	
	private ShaderProgram shader;
	
	private CameraInputController control;
	
	public GPUQuickFoliageSystem(EditorScreen screen) {
		super(GamePipeline.RENDER);
		this.screen = screen;
		
	}

	public void addedToEngine(Engine engine) 
	{
		int s = 50;
		int w = s, h = s;
		float [] vertices = new float[w * h * 4];
		
		float dist = 10.0f;
		float rnd = 0.03f;
		for(int y=0 ; y<h ; y++){
			for(int x=0 ; x<w ; x++){
				int p = (y * w + x) * 4;
				vertices[p + 0] = dist * (x / (float)w + rnd * (MathUtils.random() * 2 - 1));
				vertices[p + 1] = 0;
				vertices[p + 2] = dist * (y / (float)h + rnd * (MathUtils.random() * 2 - 1));
				vertices[p + 3] = MathUtils.random();
			}
		}
		
		short [] indices = new short[w * h];
		for(int i=0 ; i<w*h ; i++) indices[i] = (short)i;
		
		mesh = new Mesh(true, vertices.length, indices.length, VertexAttribute.Position(), new VertexAttribute(VertexAttributes.Usage.Generic, 1, "a_weight"));
				// new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"));
		mesh.setVertices(vertices);
		mesh.setIndices(indices);
		shader = new ShaderProgram(Gdx.files.internal("gpu/quick-foliage.vs"), Gdx.files.internal("gpu/quick-foliage.fs"));
		
		int gs = loadShader(GL_GEOMETRY_SHADER, Gdx.files.internal("gpu/quick-foliage.gs").readString());
		
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
		
		
	}
	
	float time = 0;
	Vector2 vp = new Vector2();
	Vector2 wp = new Vector2();
	@Override
	public void update(float deltaTime) 
	{
		time += deltaTime;
		
		if(control == null){
			// control = new CameraInputController(screen.getGameCamera());
		}
		//control.update();
		
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glDisable(GL20.GL_BLEND);
		//Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
		
//		float z = 2.5f;
//		float speed = 0.1f;
//		screen.getGameCamera().position.set(MathUtils.cos(time * speed) * z, 0, MathUtils.sin(time * speed) * z);
		screen.getGameCamera().lookAt(Vector3.Zero);
//		//screen.getGameCamera().rotate(Vector3.Y, deltaTime * 10);
//		//screen.getGameCamera().translate(0, 0, -z);
//		screen.getGameCamera().update();
		
		shader.begin();
		
		shader.setUniformMatrix("u_projTrans", screen.getGameCamera().combined);
		// shader.setUniformMatrix("u_projTransInv", screen.getGameCamera().invProjectionView);
		shader.setUniformf("time", time);
		
		shader.setUniformf("u_viewport", vp.set(screen.getGameCamera().viewportWidth, screen.getGameCamera().viewportHeight));
		
		shader.setUniformf("u_world", wp.set(screen.stage.getViewport().getWorldWidth(), screen.stage.getViewport().getWorldHeight()));
		
		mesh.render(shader, GL20.GL_POINTS);
		
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
