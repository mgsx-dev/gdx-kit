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
		
		
		// TODO update timeout
		for(Entity entity : fallingPlatforms){
			Box2DBodyModel physics = Box2DBodyModel.components.get(entity);
			FallingPlatform fallingPlatform = FallingPlatform.components.get(entity);
			if(fallingPlatform.isFalling)
				fallingPlatform.timeout += deltaTime;
			if(fallingPlatform.timeout > 5){
				fallingPlatform.timeout = 0;
				physics.body.setActive(true);
				physics.body.setTransform(fallingPlatform.position, fallingPlatform.angle);
				physics.body.setType(BodyType.StaticBody);
				fallingPlatform.isFalling = false;
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
				if(falling.isFalling){
					if(other.getBody().getType() != BodyType.DynamicBody){
						physics.context.schedule(new Runnable() {
							@Override
							public void run() {
								physics.body.setActive(false);
							}
						});
					}
				}else{
					falling.position.set(physics.body.getPosition());
					falling.angle = physics.body.getAngle();
					physics.context.schedule(new Runnable() {
						@Override
						public void run() {
							physics.body.setType(BodyType.DynamicBody);
						}
					});
					
					falling.isFalling = true;
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
