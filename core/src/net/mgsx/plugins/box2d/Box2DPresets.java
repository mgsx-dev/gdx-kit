package net.mgsx.plugins.box2d;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.PulleyJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;

import net.mgsx.plugins.box2d.model.BodyItem;
import net.mgsx.plugins.box2d.model.FixtureItem;
import net.mgsx.plugins.box2d.model.Items;
import net.mgsx.plugins.box2d.model.JointItem;

public class Box2DPresets 
{
	public static interface Box2DPreset{
		public void create(Items items, World world, float x, float y);
	}
	
	public static final Box2DPreset ball = new Box2DPreset() {
		@Override
		public void create(Items items, World world, float x, float y) {
			
			items.bodies.add(ball(world, 30f, x, y)); // 30cm ball
			
		}
	};
	
	public static BodyItem ball(World world, float size, float x, float y) 
	{
		CircleShape circle = new CircleShape();
		circle.setRadius(size);
		
		BodyDef def = new BodyDef();
		def.position.set(x, y);
		def.type = BodyType.DynamicBody;
		def.linearDamping = 1;
		
		FixtureDef fix = new FixtureDef();
		fix.friction = 0.9f;
		fix.restitution = 0.5f;
		fix.density = 1;
		fix.shape = circle;
		
		Body body = world.createBody(def);

		BodyItem item = new BodyItem(null, "Ball", def, body); // XXX
		item.fixtures.add(new FixtureItem("Circle", fix, body.createFixture(fix)));
	
		return item;
	};

	public static final Box2DPreset pulley = new Box2DPreset(){
		@Override
		public void create(Items items, World world, float x, float y) {
			// TODO create a pulley at human dimensions
			// with two circles body
			
			BodyItem ballA = ball(world, 30f, x-50, y-25); // 30cm ball
			BodyItem ballB = ball(world, 30f, x+50, y-25); // 40cm ball
			
			PulleyJointDef jdef = new PulleyJointDef();
			jdef.bodyA = ballA.body;
			jdef.bodyB = ballB.body;
			jdef.groundAnchorA.set(x-50, y+25);
			jdef.groundAnchorB.set(x+50, y+25);
			jdef.lengthA = 50f;
			jdef.lengthB = 50f;
			
			Joint joint = world.createJoint(jdef);
			
			items.bodies.addAll(ballA, ballB);
			items.joints.add(new JointItem("Pulley", jdef, joint));
		}
	};
	
	public static final Box2DPreset revolute = new Box2DPreset(){
		@Override
		public void create(Items items, World world, float x, float y) {
			// TODO create a pulley at human dimensions
			// with two circles body
			
			BodyItem ballA = ball(world, 30f, x-50, y-25); // 30cm ball
			BodyItem ballB = ball(world, 30f, x+50, y-25); // 40cm ball
			
			RevoluteJointDef jdef = new RevoluteJointDef();
			jdef.bodyA = ballA.body;
			jdef.bodyB = ballB.body;
			jdef.localAnchorB.set(100, 0);
			jdef.enableLimit = false;
			jdef.localAnchorA.set(30, 40);
			jdef.enableMotor = false;
			jdef.motorSpeed = 5;
			jdef.maxMotorTorque = 10;
			Joint joint = world.createJoint(jdef);
			
			items.bodies.addAll(ballA, ballB);
			items.joints.add(new JointItem("Revolute", jdef, joint));
		}
	};
	
	public static final Box2DPreset ground = new Box2DPreset(){
		@Override
		public void create(Items items, World world, float x, float y) {
			
			PolygonShape pshape = new PolygonShape();
			pshape.setAsBox(500, 50); // 1m x 10m
			
			BodyDef def = new BodyDef();
			def.type = BodyType.StaticBody;
			def.position.set(x, y-50);
			
			FixtureDef fix = new FixtureDef();
			fix.friction = 0.01f;
			fix.restitution = 0.1f;
			fix.density = 100;
			fix.shape = pshape;
			
			Body body = world.createBody(def);
			
			BodyItem bodyItem = new BodyItem(null, "Ground", def, body);
			bodyItem.fixtures.add(new FixtureItem("Polygon", fix, bodyItem.body.createFixture(fix)));


			items.bodies.add(bodyItem);
		}
	};
	
	public static final Box2DPreset rope = new Box2DPreset() {
		
		@Override
		public void create(Items items, World world, float x, float y) 
		{
			BodyItem prev = null;
			for(int i=0 ; i<2 ; i++)
			{
				BodyItem cur = ball(world, 30f, x, y-i*40);
				
				if(prev != null)
				{
					RopeJointDef def = new RopeJointDef();
					def.bodyA = prev.body;
					def.bodyB = cur.body;
//					def.localAnchorA.set(x, y-(i-1)*10);
//					def.localAnchorB.set(x, y-i*10);
					def.maxLength = 1;
					Joint joint = world.createJoint(def);
					items.joints.add(new JointItem("rope node", def, joint));
				}
				
				prev = cur;
			}
		}
	};
}
