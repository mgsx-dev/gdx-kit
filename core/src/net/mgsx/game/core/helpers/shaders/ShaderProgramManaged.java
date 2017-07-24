package net.mgsx.game.core.helpers.shaders;

import java.nio.IntBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

import net.mgsx.game.core.Kit;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.helpers.ShaderProgramHelper;
import net.mgsx.game.core.ui.accessors.Accessor;

/**
 * Easy {@link ShaderProgram} editor wrapper.
 * 
 * How to use : 
 * <ul>
 * <li>Create a sub class with mandatory {@link ShaderInfo} annotation </li>
 * <li>Add fields with {@link Uniform} and/or {@link Editable}</li>
 * <li>Use instances in fields (system ..) with {@link Editable} annotation </li>
 * </ul>
 * 
 * Note : 
 * <ul>
 * <li>fields with only {@link Uniform} are sent to the shader in all cases.</li>
 * <li>fields with only {@link Editable} are never sent to the shader but is permit for data grouping.</li>
 * <li>fields with both {@link Editable} and {@link Uniform} are sent to the shader in edit mode and
 * are injected (as hardcoded value #DEFINE in shader code at compilation time).</li>
 * </ul>
 * 
 * Which impacts on shader code : do not set uniform varable in shader code, these are injected automatically
 * at runtime.
 * 
 * @author mgsx
 *
 */
// TODO allow #include and other features (#define switches, #version injection ...)
abstract public class ShaderProgramManaged {

	// TODO maybe register an editor globally and allow partial scaning ... ?
	@Editable(editor=ShaderProgramManagedEditor.class)
	public final transient ShaderProgramManaged control = this;
	
	private static class UniformInfo {
		String name;
		/** see https://www.khronos.org/registry/OpenGL-Refpages/gl4/html/glGetActiveUniform.xhtml */
		int type;
		/** always 1 execpt for arrays of uniforms */
		int size;
		
		int location;
		
		boolean bound;
	}
	
	private abstract static class UniformAccessor{
		protected Accessor accessor;
		protected ShaderProgram shader;
		protected UniformInfo info;
		protected boolean freezable;
		protected String name;
		
		public boolean bind(ShaderProgram shader, UniformInfo info) {
			this.shader = shader;
			this.info = info;
			init();
			return check();
		}
		protected void init(){}
		public abstract void update();
		public abstract boolean check();
		public abstract String value();
		public abstract String type();
		protected String inline(String...values){
			String s = type() + "(";
			for(int i=0 ; i<values.length ; i++){
				if(i>0) s += ", ";
				s += values[i];
			}
			s += ")";
			return s;
		}
	}
	private static class UAUndefined extends UniformAccessor{
		@Override
		public void update() {
			// NOOP
		}
		@Override
		public boolean check() {
			return false;
		}
		@Override
		public String value() {
			return "";
		}
		@Override
		public String type() {
			return "";
		}
	}
	private static class UAFloat extends UniformAccessor{
		@Override
		public void update() {
			shader.setUniformf(info.location, accessor.get(Float.class));
		}
		@Override
		public boolean check() {
			return info.type == GL20.GL_FLOAT && info.size == 1;
		}
		@Override
		public String value() {
			return String.valueOf(accessor.get(Float.class));
		}
		@Override
		public String type() {
			return "float";
		}
	}
	private static class UAVector2 extends UniformAccessor{
		Vector2 value;
		@Override
		protected void init() {
			super.init();
			value = accessor.get(Vector2.class);
		}
		@Override
		public void update() {
			shader.setUniformf(info.location, value);
		}
		@Override
		public boolean check() {
			return info.type == GL20.GL_FLOAT_VEC2 && info.size == 1;
		}
		@Override
		public String value() {
			init();
			return inline(String.valueOf(value.x), String.valueOf(value.y));
		}
		@Override
		public String type() {
			return "vec2";
		}
	}
	private static class UAVector3 extends UniformAccessor{
		Vector3 value;
		@Override
		protected void init() {
			super.init();
			value = accessor.get(Vector3.class);
		}
		@Override
		public void update() {
			shader.setUniformf(info.location, value);
		}
		@Override
		public boolean check() {
			return info.type == GL20.GL_FLOAT_VEC3 && info.size == 1;
		}
		@Override
		public String value() {
			init();
			return inline(String.valueOf(value.x), String.valueOf(value.y), String.valueOf(value.z));
		}
		@Override
		public String type() {
			return "vec3";
		}
	}
	private static class UAColor extends UniformAccessor{
		Color value;
		@Override
		protected void init() {
			super.init();
			value = accessor.get(Color.class);
		}
		@Override
		public void update() {
			shader.setUniformf(info.location, value);
		}
		@Override
		public boolean check() {
			return info.type == GL20.GL_FLOAT_VEC4 && info.size == 1;
		}
		@Override
		public String value() {
			init();
			return inline(String.valueOf(value.r), String.valueOf(value.g), String.valueOf(value.b), String.valueOf(value.a));
		}
		@Override
		public String type() {
			return "vec4";
		}
	}
	private static class UAMatrix4 extends UniformAccessor{
		Matrix4 value;
		@Override
		protected void init() {
			super.init();
			value = accessor.get(Matrix4.class);
		}
		@Override
		public void update() {
			shader.setUniformMatrix(info.location, value);
		}
		@Override
		public boolean check() {
			return info.type == GL20.GL_FLOAT_MAT4 && info.size == 1;
		}
		@Override
		public String value() {
			throw new GdxRuntimeException("inline matrix4 not supported yet");
		}
		@Override
		public String type() {
			return "mat4";
		}
	}

	@Storable
	public String vs, fs;
	
	private transient ShaderInfo shaderInfo;
	
	transient protected FileHandle vertexShader;
	transient protected FileHandle fragmentShader;
	
	transient private ShaderProgram shaderProgram;
	
	transient private IntBuffer result = BufferUtils.newIntBuffer(16);
	transient private IntBuffer type = BufferUtils.newIntBuffer(1);
	transient private boolean frozen = true;
	
	transient private Array<UniformAccessor> allUniformAccessors;
	transient private Array<UniformAccessor> activeUniformAccessors;
	
	public ShaderProgramManaged() {
		// no deep initialization here because of JSON serializer (check default)
		shaderInfo = this.getClass().getAnnotation(ShaderInfo.class);
	}
	
	public void freeze(boolean frozen){
		this.frozen = frozen;
		reload();
	}
	public boolean isFrozen(){
		return frozen;
	}
	
	public ShaderProgram program() {
		return shaderProgram;
	}
	
	public void begin(){
		if(shaderProgram == null){
			reload();
		}
		shaderProgram.begin();
		
		setUniforms();
	}
	/**
	 * send uniform to shader (shader must be bound before), called during {@link #begin()}
	 * useful if shader program is used by a Batch or a ShaderProvider.
	 */
	public void setUniforms() {
		if(shaderProgram == null){
			reload();
		}
		for(UniformAccessor ua : activeUniformAccessors){
			if(!ua.freezable) ua.init(); // XXX because value could be updated by client code !!!
			ua.update();
		}
	}
	public void end(){
		shaderProgram.end();
	}
	
	public void dumpVS(){
		if(shaderProgram != null){
			Gdx.app.log("Shader", "\n" + shaderProgram.getVertexShaderSource());
		}
	}
	
	public void dumpFS(){
		if(shaderProgram != null){
			Gdx.app.log("Shader", "\n" + shaderProgram.getFragmentShaderSource());
		}
	}
	
	public void reload()
	{
		if(shaderInfo.storable()){
			if(vs == null) vs = shaderInfo.vs();
			if(fs == null) fs = shaderInfo.fs();
		}
		
		this.vertexShader = Gdx.files.internal(shaderInfo.vs());
		this.fragmentShader = Gdx.files.internal(shaderInfo.fs());
		
		// scan once : java code won't change (TODO even with code swap for annotation only ?) 
		if(allUniformAccessors == null){
			allUniformAccessors = findAllUniformAccessors();
		}
		
		
		String preVertCode = "";
		String preFragCode = "";
		
		if(shaderInfo.inject()) {
			// do the injections
			for(UniformAccessor ua : allUniformAccessors) {
				String code;
				if(frozen && ua.freezable){
					code = "#define " + ua.name + " " + ua.value() + "\n";
				}else{
					code = "uniform " + ua.type() + " " + ua.name + ";\n";
				}
				preVertCode += code;
				preFragCode += code;
			}
			preVertCode += "#line 0\n";
			preFragCode += "#line 0\n";
		}
		
		String preVertexCodeBefore = ShaderProgram.prependVertexCode;
		String preFragmentCodeBefore = ShaderProgram.prependFragmentCode;

		ShaderProgram.prependVertexCode = preVertexCodeBefore + preVertCode;
		ShaderProgram.prependFragmentCode = preFragmentCodeBefore + preFragCode;

		shaderProgram = ShaderProgramHelper.reload(shaderProgram, vertexShader, fragmentShader);
		
		ShaderProgram.prependVertexCode = preVertexCodeBefore;
		ShaderProgram.prependFragmentCode = preFragmentCodeBefore;
		
		// Obtain the handle XXX not exposed ...
		result.clear();
		shaderProgram.begin();
		Gdx.gl20.glGetIntegerv(GL20.GL_CURRENT_PROGRAM, result);
		shaderProgram.end();
		int handle = result.get(0);
		
		// int handle = ReflectionHelper.get(shaderProgram, "program", Integer.class);
		
		// first scan program to get all uniforms
		result.clear();
		Gdx.gl20.glGetProgramiv(handle, GL20.GL_ACTIVE_UNIFORMS, result);
		int numUniforms = result.get(0);
		
		ObjectMap<String, UniformInfo> uniformInfos = new ObjectMap<String, UniformInfo>();
		
		for (int i = 0; i < numUniforms; i++) {
			result.clear();
			result.put(0, 1);
			type.clear();
			UniformInfo info = new UniformInfo();
			String name = Gdx.gl20.glGetActiveUniform(handle, i, result, type);
			info.location = Gdx.gl20.glGetUniformLocation(handle, name);
			info.name = name;
			info.size = result.get(0);
			info.type = type.get(0);
			uniformInfos.put(name, info);
		}
		
		activeUniformAccessors = new Array<UniformAccessor>();
		for(UniformAccessor ua : allUniformAccessors) {
			UniformInfo i = uniformInfos.get(ua.name);
			boolean frozenUniform = ua.freezable && frozen;
			if(!frozenUniform){
				if(i == null){
					Gdx.app.error("Shader", "missing uniform variable in GLSL code : " + ua.name);
				}else if(!ua.bind(shaderProgram, i)){
					// TODO log only ?
					throw new GdxRuntimeException("uniform misssmatch for " + ua.name);
				}else{
					i.bound = true;
					activeUniformAccessors.add(ua);
				}
			}
		}
		
		// check for GLSL uniform not bound to Java
		for(Entry<String, UniformInfo> e : uniformInfos){
			if(!e.value.bound){
				Gdx.app.error("Shader", "uniform not bound : name from program : " + e.value.name);
			}
		}
		
	}

	private Array<UniformAccessor> findAllUniformAccessors() {
		Array<UniformAccessor> all = new Array<UniformAccessor>();
		
		for(Accessor a : Kit.meta.accessorsFor(this, Uniform.class)) {
			
			Uniform config = a.config(Uniform.class);
			Editable edit = a.config(Editable.class);
			
			String uniformName = config.value();
			if(uniformName.isEmpty()){
				uniformName = "u_" + a.getName();
			}
			
			UniformAccessor ua;
			if(a.getType() == float.class){
				ua = new UAFloat();
			}
			else if(a.getType() == Vector2.class){
				ua = new UAVector2();
			}
			else if(a.getType() == Vector3.class){
				ua = new UAVector3();
			}
			else if(a.getType() == Color.class){
				ua = new UAColor();
			}
			else if(a.getType() == Matrix4.class){
				ua = new UAMatrix4();
			}
			else{
				Gdx.app.error("Shader", "missing Java/GLSL type binding for " + uniformName + " type " + a.getType().getName());
				ua = new UAUndefined();
			}
			// uniform with edit can be frozen, other one can't be
			ua.freezable = edit != null;
			ua.name = uniformName;
			ua.accessor = a;
			
			all.add(ua);
		}
		return all;
	}

	public void changeVS(FileHandle file) {
		if(shaderInfo.storable()){
			vs = file.path();
		}
		vertexShader = file;
	}
	public void changeFS(FileHandle file) {
		if(shaderInfo.storable()){
			fs = file.path();
		}
		fragmentShader = file;
	}
	
}
