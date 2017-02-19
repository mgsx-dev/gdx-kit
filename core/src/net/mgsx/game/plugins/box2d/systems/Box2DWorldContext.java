package net.mgsx.game.plugins.box2d.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointEdge;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.components.Box2DJointModel;
import net.mgsx.game.plugins.box2d.helper.WorldProvider;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

// TODO it is more an EditorContext (ctx) ...
public class Box2DWorldContext 
{
	final public Box2DEditorSettings settings = new Box2DEditorSettings();
	public World world;
	public EditorScreen editor;
	public WorldProvider provider;
	
	final ObjectSet<Body> bodiesToDelete = new ObjectSet<Body>();
	final ObjectSet<Joint> jointsToDelete = new ObjectSet<Joint>();
	
	public Array<Runnable> scheduled= new Array<Runnable>();
	
	public Box2DWorldContext() {
		super();
	}
	
	public void initialize() {
		if(world == null){
			world = new World(new Vector2(), true); // TODO settings for doSleep 
			provider = new WorldProvider(world);
		}
	}
	
	public Box2DBodyModel currentBody(String defaultName, float x, float y) 
	{
		Entity entity = editor.currentEntity();
		Box2DBodyModel item = entity == null ? null : entity.getComponent(Box2DBodyModel.class);
		if(item == null){
			
			BodyDef def = settings.body();
			// XXX ???
			if(entity != null && entity.getComponent(Transform2DComponent.class) != null){
				x = entity.getComponent(Transform2DComponent.class).position.x;
				y = entity.getComponent(Transform2DComponent.class).position.y;
				
			}
			def.position.set(x, y);
			Body body = world.createBody(def);
			item = editor.entityEngine.createComponent(Box2DBodyModel.class);
			item.context = this;
			item.entity = entity;
			item.id = defaultName;
			item.def = def;
			item.body = body;
			item.body.setUserData(entity);
			// XXX item = new Box2DBodyModel(this, entity, defaultName, def, body);
			entity.add(item);
		}
		return item;
	}
	
	public void remove(Joint joint){
		// TODO remove gears before joints ?
		if(world.isLocked()){
			jointsToDelete.add(joint);
		}else{
			world.destroyJoint(joint);
		}
	}
	
	public void remove(Body body){
		// delete joints before.
		for(JointEdge jointEdge : body.getJointList()){
			//body.getWorld().isLocked()
			
			Object data = jointEdge.joint.getUserData();
			if(data instanceof Entity){
				Entity jointEntity = (Entity)data;
				jointEntity.remove(Box2DJointModel.class);
			}
			
		}
		if(world.isLocked()){
			bodiesToDelete.add(body);
		}else{
			world.destroyBody(body);
		}
	}
	
	public void schedule(Runnable runnable) 
	{
		scheduled.add(runnable);
	}

	
}
