package net.mgsx.game.examples.breaker.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.tools.RectangleTool;
import net.mgsx.game.examples.breaker.components.BallComponent;
import net.mgsx.game.examples.breaker.components.BrickComponent;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.components.Box2DFixtureModel;
import net.mgsx.game.plugins.box2d.listeners.Box2DComponentListener;
import net.mgsx.game.plugins.box2d.listeners.Box2DListener;
import net.mgsx.game.plugins.box2d.systems.Box2DWorldSystem;

public class PlayTool extends RectangleTool
{
	public Box2DListener listener = new Box2DComponentListener<BrickComponent>(BrickComponent.class){
		@Override
		protected void beginContact(Contact contact, Fixture self, Fixture other, Entity otherEntity,
				BrickComponent otherComponent) {
			if(otherComponent.solid && otherComponent.life < 0){
				getEngine().removeEntity((Entity) self.getBody().getUserData());
			}else if(!otherComponent.solid){
				otherComponent.life -= 1;
				if(otherComponent.life == 0){
					getEngine().removeEntity(otherEntity);
				}
			}

		}
	};
//	public Box2DListener listener = new Box2DComponentTrigger<BrickComponent>(BrickComponent.class) {
//
//		@Override
//		protected void enter(Contact contact, Fixture self, Fixture other, Entity otherEntity,
//				BrickComponent otherComponent, boolean b) {
//			
//			if(otherComponent.solid){
//				getEngine().removeEntity((Entity) self.getBody().getUserData());
//			}else{
//				otherComponent.life -= 1;
//				if(otherComponent.life <= 0){
//					getEngine().removeEntity(otherEntity);
//				}
//			}
//		}
//
//		@Override
//		protected void exit(Contact contact, Fixture self, Fixture other, Entity otherEntity,
//				BrickComponent otherComponent, boolean b) {
//		}
//	};
	
	
	@Inject Box2DWorldSystem world;
	
	@Editable public int count = 10;
	@Editable public float radius = .5f;
	@Editable public float period = 1f;
	
	private int remain; 
	private float timeout;
	
	Vector2 p = new Vector2();
	Vector2 v = new Vector2();
	
	@Override
	protected void create(Vector2 startPoint, Vector2 endPoint) 
	{
		p.x = (startPoint.x + endPoint.x) / 2;
		p.y = (startPoint.y + endPoint.y) / 2;
		
		v.set(endPoint).sub(startPoint);
		
		remain = count;
		timeout = 0;
		
	}
	
	@Override
	public void update(float deltaTime) 
	{
		if(remain > 0){
			timeout -= deltaTime;
			if(timeout <= 0){
				timeout += period;
				remain--;
			}else{
				return;
			}
			
			selection().clear();
			
			// Entity entity = transcientEntity();
			
			world.getWorldContext().settings.bodyDef.type = BodyType.DynamicBody;
			world.getWorldContext().settings.bodyDef.gravityScale = 0;
			world.getWorldContext().settings.bodyDef.bullet = true;
			
			Box2DBodyModel bodyItem = world.getWorldContext().currentBody("Rectangle", p.x, p.y);
			Entity entity = bodyItem.entity;
			
			CircleShape shape = new CircleShape();
			shape.setRadius(radius);
			// shape.setPosition(new Vector2(startPoint).sub(bodyItem.body.getPosition()));

			
			FixtureDef fix = world.getWorldContext().settings.fixture();
			fix.shape = shape;
			
			Fixture fixture = bodyItem.body.createFixture(fix);
			Filter filter = new Filter();
			filter.categoryBits = 2;
			filter.maskBits = 1;
			fixture.setFilterData(filter);
			fixture.setDensity(1);
			fixture.setFriction(0);
			fixture.setRestitution(0f);
			
			Box2DFixtureModel fixtureItem = new Box2DFixtureModel("Polygon", fix, fixture);
			bodyItem.fixtures.add(fixtureItem);
			fixtureItem.fixture.setUserData(listener);
			
			BallComponent ball = getEngine().createComponent(BallComponent.class);
			ball.body = bodyItem.body;
			entity.add(ball);
			
			bodyItem.body.setLinearVelocity(v.x, v.y);
			
			
			bodyItem.setListener(listener);
			
		}
		
	}
	
}
