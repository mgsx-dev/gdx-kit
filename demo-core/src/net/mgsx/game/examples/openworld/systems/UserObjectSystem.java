package net.mgsx.game.examples.openworld.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.storage.SystemSettingsListener;
import net.mgsx.game.examples.openworld.components.ObjectMeshComponent;
import net.mgsx.game.examples.openworld.model.OpenWorldElement;
import net.mgsx.game.examples.openworld.utils.SmoothBoxShapeBuilder;
import net.mgsx.game.plugins.bullet.system.BulletWorldSystem;

@Storable(value="ow.user-objects", auto=true)
public class UserObjectSystem extends EntitySystem implements SystemSettingsListener
{
	public static class UserObject {
		transient Entity entity; // null if not on stage
		public OpenWorldElement element; // underlying data (persisted not null)
		transient boolean toRemove; // flag used to schedule a remove
	}
	
	@Inject BulletWorldSystem bulletWorld;
	
	private Array<UserObject> userObjects = new Array<UserObject>();
	
	@Editable // XXX kit bug workaround
	@Storable
	public OpenWorldElement[] persistedElements;
	
	public Array<UserObject> allUserObjects = new Array<UserObject>();
	
	/** last logical camera (POV) coordinates */
	private int lx, ly;
	
	private boolean init;
	
	private Mesh icosa1;

	
	public UserObjectSystem() {
		super(GamePipeline.LOGIC);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		
		// TODO use asset injection instead
		Model model = new G3dModelLoader(new JsonReader()).loadModel(Gdx.files.internal("openworld/icosa1.g3dj"));
		icosa1 = model.meshes.get(0);
	}
	
	@Override
	public void update(float deltaTime) {
		
		if(!init){
			
			// TODO just quad tree request ..
			for(UserObject o : allUserObjects){
				o.entity = createEntity(o, o.element); // TODO create entity
				userObjects.add(o);
			}
			
			init = true;
		}
		
		// TODO avoid bouncing (r2>r1) : 
		// TODO if distance < r1 then create
		// TODO if distance > r2 then destroy
		
		// TODO convert camera space to grid space
		// if grid space has change :
		boolean gridHasChanged = false;
		if(gridHasChanged){
			
			// invalidate
			for(UserObject o : userObjects){
				o.toRemove = true;
			}
			Array<UserObject> actives = getActiveObjects();
			for(UserObject o : actives){
				// create
				if(o.entity == null){
					Entity entity = o.entity = getEngine().createEntity();
					// TODO create components
					getEngine().addEntity(entity);
					userObjects.add(o);
				}
				// validate
				o.toRemove = false;
			}
			// destroy invalide
			for(UserObject o : userObjects){
				if(o.toRemove){
					getEngine().removeEntity(o.entity);
					o.entity = null;
					userObjects.removeValue(o, true);
				}
			}
		}
			// TODO compute camera size
			// TODO request rectangle (camera pos + camera far (circle bound rect))
			// TODO create entities for idle state
			// TODO mark to keep those actives
			
			// TODO delete all not to keep
		
	}
	
	public void appendObject(OpenWorldElement e) {
		
		UserObject o = new UserObject();
		o.element = e;
		o.entity = createEntity(o, e);
		allUserObjects.add(o);
		userObjects.add(o);
	}


	private Entity createEntity(UserObject object, OpenWorldElement element) {
		Entity newEntity = getEngine().createEntity();
		
		ObjectMeshComponent lmc = getEngine().createComponent(ObjectMeshComponent.class);
		lmc.transform.setToTranslation(element.position);
		lmc.transform.rotate(element.rotation);
		lmc.userObject = object;
		
		if(element.type == null) element.type = "box"; // XXX migration
		if(element.type.equals("box")){
			lmc.mesh = createMeshBox(element);
			// physics :
			bulletWorld.createBox(newEntity, element.dynamic ? lmc.transform.translate(0,1,0) : lmc.transform, element.size * element.geo_x, element.size * element.geo_y, element.size, element.dynamic);
		}
		else if(element.type.equals("sphere")){
			lmc.mesh = createMeshSphere(element);
			bulletWorld.createSphere(newEntity, element.dynamic ? lmc.transform.translate(0,1,0) : lmc.transform, element.size,element.dynamic);
		}
		newEntity.add(lmc);
		
		getEngine().addEntity(newEntity);
		
		return newEntity;
	}
	
	private Mesh createMeshSphere(OpenWorldElement e) {
		Mesh mesh = icosa1.copy(true);
		
		int stride = mesh.getVertexSize()/4;
		float[] vertices = new float[stride * mesh.getNumVertices()];
		mesh.getVertices(vertices);
		e.color.set(.7f, .7f, .65f, 1); // XXX force color
		for(int i=0, index=mesh.getVertexAttribute(VertexAttributes.Usage.Position).offset / 4 ; i<mesh.getNumVertices() ; i++, index+=stride){
			vertices[index+0] *= e.size;
			vertices[index+1] *= e.size;
			vertices[index+2] *= e.size;
		}
		mesh.updateVertices(0, vertices);
		return mesh;
	}
	
	private Mesh createMeshBox(OpenWorldElement e) 
	{

		//e.size = 1;
		// Matrix4 m = new Matrix4().scale(e.size * e.geo_x, e.size * e.geo_y, e.size);
		MeshBuilder builder = new MeshBuilder();
		builder.setColor(e.color);
		builder.begin(new VertexAttributes(
				VertexAttribute.Position(), 
				VertexAttribute.Normal(),
				VertexAttribute.ColorUnpacked()), GL20.GL_TRIANGLES);
		SmoothBoxShapeBuilder.build(builder, Vector3.Zero, new Vector3(e.size * e.geo_x, e.size * e.geo_y, e.size).scl(.5f));
		// BoxShapeBuilder.build(builder, m);
		Mesh mesh = builder.end();
		
		return mesh;
	}


	private Array<UserObject> getActiveObjects() {
		 // TODO request quad tree
		return userObjects;
	}

	public UserObject addElement(OpenWorldElement element, Entity entity) {
		UserObject o = new UserObject();
		o.element = element;
		o.entity = entity;
		allUserObjects.add(o);
		if(entity != null){
			userObjects.add(o);
		}
		return o;
	}


	public void removeElement(UserObject object) {
		if(object.entity != null){
			getEngine().removeEntity(object.entity);
			object.entity = null;
		}
		userObjects.removeValue(object, true);
		allUserObjects.removeValue(object, true);
	}

	@Override
	public void onSettingsLoaded() {
		for(int i=0 ; i<persistedElements.length ; i++){
			UserObject o = new UserObject();
			o.element = persistedElements[i];
			allUserObjects.add(o);
		}
	}

	@Override
	public void beforeSettingsSaved() {
		persistedElements = new OpenWorldElement[allUserObjects.size];
		for(int i=0 ; i<persistedElements.length ; i++){
			UserObject o = allUserObjects.get(i);
			if(o.element.dynamic && o.entity != null){
				ObjectMeshComponent omc = ObjectMeshComponent.components.get(o.entity);
				omc.transform.getTranslation(o.element.position);
				omc.transform.getRotation(o.element.rotation);
			}
			persistedElements[i] = o.element;
		}
	}

	
	
}
