package net.mgsx.game.examples.gpu.systems;

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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.components.Hidden;
import net.mgsx.game.examples.gpu.components.FoliageComponent;
import net.mgsx.game.examples.gpu.components.TesslationComponent;
import net.mgsx.game.examples.gpu.utils.ShaderProgramEx;
import net.mgsx.game.plugins.g3d.components.G3DModel;

@EditableSystem
public class GPUFoliageSystem extends IteratingSystem
{
	/** from org.lwjgl.opengl.GL32 */
	public static final int GL_GEOMETRY_SHADER = 0x8DD9;
	public static final int GL_PATCHES = 0xE;
	
	@Editable public float strength = 1;
	@Editable public float thickness = .5f;
	@Editable public float limit = .5f;
	
	@Editable public float tess_in = 1;
	@Editable public float tess_out = 1;
	@Editable public float tess_offset = 1;
	
	protected EditorScreen screen;
	private ShaderProgramEx shaderLine, shaderTri;

	public GPUFoliageSystem(EditorScreen screen) {
		super(Family.all(G3DModel.class, TesslationComponent.class, FoliageComponent.class).get(), GamePipeline.RENDER);
		this.screen = screen;
	}
	
	@Editable
	public void loadShader()
	{
		
		ShaderProgramEx shader;
		
		shader = new ShaderProgramEx(
				Gdx.files.internal("gpu/foliage-wire.vs"), 
				Gdx.files.internal("gpu/foliage-wire.tcs"), 
				Gdx.files.internal("gpu/foliage-wire.tes"), 
				Gdx.files.internal("gpu/foliage-wire.gs"), 
				Gdx.files.internal("gpu/foliage-wire.fs"));
		
		if(!shader.isCompiled()){
			System.err.println(shader.getLog());
		}
		else{
			for(String s : shader.getAttributes())
				System.out.println(s);
			
			if(this.shaderLine != null) this.shaderLine.dispose();
			this.shaderLine = shader;
		}
		
		shader = new ShaderProgramEx(
				Gdx.files.internal("gpu/foliage-tri.vs"), 
				Gdx.files.internal("gpu/foliage-tri.tcs"), 
				Gdx.files.internal("gpu/foliage-tri.tes"), 
				Gdx.files.internal("gpu/foliage-tri.gs"), 
				Gdx.files.internal("gpu/foliage-tri.fs"));
		
		if(!shader.isCompiled()){
			System.err.println(shader.getLog());
		}
		else{
			for(String s : shader.getAttributes())
				System.out.println(s);
			
			if(this.shaderTri != null) this.shaderTri.dispose();
			this.shaderTri = shader;
		}
		
	}
	
	@Override
	public void addedToEngine(Engine engine) 
	{
		super.addedToEngine(engine);
		
		loadShader();
		
		engine.addEntityListener(getFamily(), new EntityListener() {
			
			@Override
			public void entityRemoved(Entity entity) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void entityAdded(Entity entity) {
				G3DModel model = G3DModel.components.get(entity);
				TesslationComponent tess = TesslationComponent.components.get(entity);
				
				Mesh base = model.modelInstance.model.meshes.get(0);
				
				Mesh mesh = base.copy(true, false, null); // new int[]{VertexAttributes.Usage.Position, VertexAttributes.Usage.Normal});
				tess.transform = model.modelInstance.nodes.get(0).localTransform.cpy();
				tess.mesh = mesh;
				
				entity.add(getEngine().createComponent(Hidden.class));
				
			}
		});
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		TesslationComponent tess = TesslationComponent.components.get(entity);
		G3DModel model = G3DModel.components.get(entity);
		tess.transform.set(model.modelInstance.nodes.get(0).localTransform);
		
		FoliageComponent foliage = FoliageComponent.components.get(entity);
		
		ShaderProgramEx shader;
		if(foliage.asLine){
			shader = shaderLine;
		}else{
			shader = shaderTri;
		}
		
		
		shader.begin();
		
		if(foliage.asLine){
			
		}else{
			shader.setUniformf("u_time", time);
			shader.setUniformf("u_thickness", thickness);
		}
		
		shader.setUniformf("u_strength", strength);
		
		
		shader.setUniformf("u_tess_in", tess_in);
		shader.setUniformf("u_tess_out", tess_out);
		shader.setUniformf("u_offset", tess_offset);

		
		shader.setUniformMatrix("u_projTrans", screen.getGameCamera().combined.cpy().mul(model.modelInstance.transform.cpy().mul(tess.transform)));
		
		
		
		tess.mesh.bind(shader);
		tess.mesh.render(shader, GL_PATCHES, 0, tess.mesh.getMaxIndices(), false);
		tess.mesh.unbind(shader);
		
		int error = Gdx.gl.glGetError();
		if(error != GL30.GL_NO_ERROR){
			throw new GdxRuntimeException(String.valueOf(error));
		}
		
		shader.end();
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
		
		super.update(deltaTime);
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

		fetchAttributes(program);
		
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

		
		fetchAttributes(program);
	}

	private void fetchAttributes (int program) {
		IntBuffer params = BufferUtils.newIntBuffer(1);
		IntBuffer type = BufferUtils.newIntBuffer(1);

		params.clear();
		Gdx.gl20.glGetProgramiv(program, GL20.GL_ACTIVE_ATTRIBUTES, params);
		int numAttributes = params.get(0);

		for (int i = 0; i < numAttributes; i++) {
			params.clear();
			params.put(0, 1);
			type.clear();
			String name = Gdx.gl20.glGetActiveAttrib(program, i, params, type);
			int location = Gdx.gl20.glGetAttribLocation(program, name);
			int t = type.get(0);
			int s = params.get(0);
			s = 0;
		}
	}
}
