package net.mgsx.game.plugins.box2d.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.components.Box2DFixtureModel;

//TODO should own the world ?
public class Box2DWorldSystem extends EntitySystem {
	
	
	private final Box2DWorldContext worldContext;
	
	public Box2DWorldSystem(Box2DWorldContext worldContext) {
		super(GamePipeline.PHYSICS);
		this.worldContext = worldContext;
	}
	
	@Override
	public void addedToEngine(Engine engine) 
	{
		super.addedToEngine(engine);
		
		worldContext.world.setContactListener(new Box2DWorldContactListener());
	}
	
	public Box2DWorldContext getWorldContext() {
		return worldContext;
	}

	@Override
	public void update(float deltaTime) {
		worldContext.world.setGravity(worldContext.settings.world.gravity);
		worldContext.update();
		
		
		
		// update physics
		if(worldContext.settings.world.runSimulation)
		{
			worldContext.world.step(
					worldContext.settings.world.timeStep, 
					worldContext.settings.world.velocityIterations, 
					worldContext.settings.world.positionIterations);
			
			for(Runnable runnable : worldContext.scheduled){
				runnable.run();
			}
			worldContext.scheduled.clear();
			
			for(Box2DBodyModel body : worldContext.scheduledForDeletion)
			{
				body.dispose();
			}
			worldContext.scheduledForDeletion.clear();
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
}