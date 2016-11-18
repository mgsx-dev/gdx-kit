package net.mgsx.game.examples.platformer.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;

import net.mgsx.game.core.helpers.FamilyHelper;
import net.mgsx.game.examples.platformer.components.FallingPlatform;
import net.mgsx.game.examples.platformer.components.FallingPlatform.State;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.components.Box2DFixtureModel;
import net.mgsx.game.plugins.box2d.listeners.Box2DAdapter;
import net.mgsx.game.plugins.box2d.listeners.Box2DListener;
import net.mgsx.game.plugins.box2d.systems.AbstractBox2DSystem;

public class FallingPlatformSystem extends AbstractBox2DSystem
{
	private ImmutableArray<Entity> fallingPlatforms;
	
	public FallingPlatformSystem() {
		super(FamilyHelper.all(FallingPlatform.class));
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		fallingPlatforms = getEngine().getEntitiesFor(Family.all(FallingPlatform.class, Box2DBodyModel.class).get());
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		// TODO this is a state machine and should be component based :
		// the state machine add and remove component depending on events and states
		
		for(Entity entity : fallingPlatforms){
			Box2DBodyModel physics = Box2DBodyModel.components.get(entity);
			FallingPlatform fallingPlatform = FallingPlatform.components.get(entity);
			switch(fallingPlatform.state){
			case INIT:
				physics.body.setActive(true);
				physics.body.setTransform(fallingPlatform.position, fallingPlatform.angle);
				physics.body.setType(BodyType.StaticBody);
				fallingPlatform.state = State.RIGID;
				break;
			case RIGID:
				break;
			case COLLAPSING:
				fallingPlatform.timeout -= deltaTime;
				if(fallingPlatform.timeout < 0){
					physics.body.setType(BodyType.DynamicBody);
					fallingPlatform.state = State.FALLING;
				}
				break;
			case FALLING:
				break;
			case DEAD:
				fallingPlatform.timeout -= deltaTime;
				physics.body.setActive(false);
				if(fallingPlatform.timeout < 0){
					fallingPlatform.state = State.INIT;
				}
				break;
			default:
				break;
			
			}
			
		}
	}
	
	@Override
	protected void registerListener(Entity entity, final Box2DBodyModel physics) {
		final FallingPlatform falling = FallingPlatform.components.get(entity);
		Box2DListener listener = new Box2DAdapter() {
			@Override
			public void endContact(Contact contact, Fixture self, Fixture other) {
			}
			
			@Override
			public void beginContact(Contact contact, Fixture self, Fixture other) {
				if(falling.state == State.FALLING){
					if(other.getBody().getType() != BodyType.DynamicBody){
						falling.state = State.DEAD;
						falling.timeout = falling.regenerationDelay;
					}
				}else if(falling.state == State.RIGID){
					falling.position.set(physics.body.getPosition());
					falling.angle = physics.body.getAngle();
					falling.timeout = falling.collapseTime;
					falling.state = State.COLLAPSING;
				}
				
			}
		};
		// register listener on all no sensor fixtures
		if(physics.body != null)
		for(Box2DFixtureModel fix : physics.fixtures)
			if(!fix.fixture.isSensor())
				fix.fixture.setUserData(listener);
	}

	
}
