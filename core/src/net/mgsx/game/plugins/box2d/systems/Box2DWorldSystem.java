package net.mgsx.game.plugins.box2d.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.plugins.box2d.Box2DPlugin;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.components.Box2DFixtureModel;

//TODO should own the world !
public class Box2DWorldSystem extends EntitySystem {
	public Box2DWorldSystem() {
		super(GamePipeline.PHYSICS);
	}
	
	@Override
	public void addedToEngine(Engine engine) 
	{
		super.addedToEngine(engine);
		
		Box2DPlugin.worldItem.world.setContactListener(new Box2DWorldContactListener());
	}

	@Override
	public void update(float deltaTime) {
		Box2DPlugin.worldItem.world.setGravity(Box2DPlugin.worldItem.settings.gravity);
		Box2DPlugin.worldItem.update();
		
		
		
		// update physics
		if(Box2DPlugin.worldItem.settings.runSimulation)
		{
			Box2DPlugin.worldItem.world.step(
					Box2DPlugin.worldItem.settings.timeStep, 
					Box2DPlugin.worldItem.settings.velocityIterations, 
					Box2DPlugin.worldItem.settings.positionIterations);
			
			for(Runnable runnable : Box2DPlugin.worldItem.scheduled){
				runnable.run();
			}
			Box2DPlugin.worldItem.scheduled.clear();
			
			for(Box2DBodyModel body : Box2DPlugin.worldItem.scheduledForDeletion)
			{
				body.dispose();
			}
			Box2DPlugin.worldItem.scheduledForDeletion.clear();
		}
		
	}
	
	public Box2DBodyModel create(Entity entity) {
		Box2DBodyModel model = getEngine().createComponent(Box2DBodyModel.class);
		model.context = Box2DPlugin.worldItem;
		model.def = new BodyDef();
		model.entity = entity;
		model.id = "";
		return model;
	}

	public Body createBody(Box2DBodyModel model) {
		Body body = model.body = Box2DPlugin.worldItem.world.createBody(model.def);
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