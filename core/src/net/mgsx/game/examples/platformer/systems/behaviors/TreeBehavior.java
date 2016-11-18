package net.mgsx.game.examples.platformer.systems.behaviors;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;

import net.mgsx.game.core.components.Initializable;
import net.mgsx.game.core.components.LogicBehavior;
import net.mgsx.game.core.helpers.EntityHelper;
import net.mgsx.game.examples.platformer.components.BonusComponent;
import net.mgsx.game.examples.platformer.components.PlayerComponent;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.listeners.Box2DAdapter;
import net.mgsx.game.plugins.core.components.Transform2DComponent;
import net.mgsx.game.plugins.g3d.components.G3DModel;

public class TreeBehavior implements LogicBehavior, Initializable
{
	private Box2DBodyModel body;
	private G3DModel model;
	private int sensorContact = 0;
	
	protected Engine manager;
	protected Entity entity;
	
	private boolean playerEnter = false;
	private boolean playerExit = false;
	
	@Override
	public void initialize(Engine manager, Entity entity)
	{
		this.manager = manager;
		this.entity = entity;
		body = entity.getComponent(Box2DBodyModel.class);
		model = entity.getComponent(G3DModel.class);
		body.fixtures.get(1).fixture.setUserData(new Box2DAdapter() { // XXX sensor
			
			@Override
			public void endContact(Contact contact, Fixture self, Fixture other) {
				Entity otherEntity = (Entity)other.getBody().getUserData();
				if(otherEntity.getComponent(PlayerComponent.class) != null){
					sensorContact--;
					if(sensorContact == 0) playerExit = true;
				}
			}
			
			@Override
			public void beginContact(Contact contact, Fixture self, Fixture other) {
				Entity otherEntity = (Entity)other.getBody().getUserData();
				if(otherEntity.getComponent(PlayerComponent.class) != null){
					if(sensorContact == 0) playerEnter = true;
					sensorContact++;
				}
			}
		});
	}
	
	private void onPlayerEnter()
	{
		// TODO launch bonus !
		// how to do that ? we need a factory or duplicate an entity ?
		
		// XXX find template !
		Entity template = manager.getEntitiesFor(Family.one(BonusComponent.class).get()).first();
		
		Entity newEntity = EntityHelper.clone(manager, template);
		Transform2DComponent t = EntityHelper.getOrCreate(manager, newEntity, Transform2DComponent.class);
		t.position.set(body.body.getPosition());
		t.position.x += 3.2f; // TODO how to set it ? 
		t.position.y += 1;
		newEntity.add(t);
		newEntity.getComponent(Box2DBodyModel.class).body.setType(BodyType.DynamicBody);
		
		model.animationController.allowSameAnimation = true;
		model.animationController.animate(model.modelInstance.animations.get(0).id, -1, 0.6f, null, 1f);
	}
	
	private void onPlayerExit()
	{
		model.animationController.animate(model.modelInstance.animations.get(0).id, -1, 0.0001f, null, 1f);
	}
	
	@Override
	public void update(float deltaTime) 
	{
		// XXX hack to not create bodies inside listeners !
		if(playerEnter) onPlayerEnter();
		else if(playerExit) onPlayerExit();
		playerEnter = playerExit = false;
	}

	@Override
	public void enter() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exit() {
		// TODO Auto-generated method stub
		
	}



}
