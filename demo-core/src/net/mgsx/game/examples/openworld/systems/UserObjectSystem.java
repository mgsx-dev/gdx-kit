package net.mgsx.game.examples.openworld.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

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
	
	public UserObjectSystem() {
		super(GamePipeline.LOGIC);
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


	private Entity createEntity(UserObject object, OpenWorldElement element) {
		Entity newEntity = getEngine().createEntity();
		
		ObjectMeshComponent lmc = getEngine().createComponent(ObjectMeshComponent.class);
		lmc.transform.setToTranslation(element.position);
		lmc.transform.rotate(element.rotation);
		lmc.userObject = object;
		lmc.mesh = createMesh(element);
		newEntity.add(lmc);
		
		// physics :
		bulletWorld.createBox(newEntity, lmc.transform, element.size * element.geo_x, element.size * element.geo_y, element.size, false);
		
		getEngine().addEntity(newEntity);
		
		return newEntity;
	}
	
	private Mesh createMesh(OpenWorldElement e) 
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
		for(int i=0 ; i<persistedElements.length ; i++) persistedElements[i] = allUserObjects.get(i).element;
	}
	
}
