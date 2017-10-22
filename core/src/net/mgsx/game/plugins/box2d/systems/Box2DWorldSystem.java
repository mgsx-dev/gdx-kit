package net.mgsx.game.plugins.box2d.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.components.Box2DFixtureModel;
import net.mgsx.game.plugins.box2d.listeners.Box2DListener;

//TODO should own the world ?

@Storable("box2d.world")
@EditableSystem
public class Box2DWorldSystem extends EntitySystem {
	
	@Editable
	public boolean runSimulation = true;
	
	@Editable
	public Vector2 gravity = new Vector2(0, -9.807f); // earth gravity
	
	@Editable
	public float timeStep = 1.f / 30.f;
	
	@Editable
	public int velocityIterations = 8;
	
	@Editable
	public int positionIterations = 3;

	private transient Array<Box2DListener> globalListeners = new Array<Box2DListener>();
	
	private final Box2DWorldContext worldContext;
	
	public Box2DWorldSystem(Box2DWorldContext worldContext) {
		super(GamePipeline.PHYSICS);
		this.worldContext = worldContext;
	}
	
	@Override
	public void addedToEngine(Engine engine) 
	{
		super.addedToEngine(engine);
		
		worldContext.world.setContactListener(new Box2DWorldContactListener(globalListeners));
	}
	
	public Box2DWorldContext getWorldContext() {
		return worldContext;
	}

	@Override
	public void update(float deltaTime) {
		worldContext.world.setGravity(gravity);
		
		// update physics
		if(runSimulation)
		{
			worldContext.world.step(timeStep, velocityIterations, positionIterations);
			
			for(Runnable runnable : worldContext.scheduled){
				runnable.run();
			}
			worldContext.scheduled.clear();
			
			for(Joint joint : worldContext.jointsToDelete)
			{
				worldContext.world.destroyJoint(joint);
			}
			for(Body body : worldContext.bodiesToDelete)
			{
				worldContext.world.destroyBody(body);
			}
		}
		
	}
	
	public Box2DBodyModel create(Entity entity) {
		Box2DBodyModel model = getEngine().createComponent(Box2DBodyModel.class);
		model.context = worldContext;
		model.def = new BodyDef();
		model.entity = entity;
		model.id = "";
		return model;
	}

	public Body createBody(Box2DBodyModel model) {
		Body body = model.body = worldContext.world.createBody(model.def);
		model.body.setUserData(model.entity);
		return body;
	}

	public Box2DFixtureModel createFixture(Box2DBodyModel physics) 
	{
		Box2DFixtureModel fix = new Box2DFixtureModel();
		fix.def = new FixtureDef();
		fix.id = "";
		physics.fixtures.add(fix);
		return fix;
	}

	public Fixture addFixture(Box2DBodyModel model, Box2DFixtureModel fix) {
		return fix.fixture = model.body.createFixture(fix.def);
	}

	public void addListener(Box2DListener listener) {
		globalListeners.add(listener);
	}
	public void removeListener(Box2DListener listener) {
		globalListeners.removeValue(listener, true);
	}
}