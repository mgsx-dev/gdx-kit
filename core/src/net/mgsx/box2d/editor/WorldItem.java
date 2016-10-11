package net.mgsx.box2d.editor;

import net.mgsx.box2d.editor.Box2DPresets.Items;
import net.mgsx.box2d.editor.Box2DPresets.JointItem;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class WorldItem 
{
	public EditorSettings settings = new EditorSettings();
	public World world;
	
	public Items items = new Items();
	public Items selection = new Items();
	public void addAll(Items items) {
		this.items.addAll(items);
	}
	public void destroy(Items items) 
	{
		for(JointItem joint : items.joints)
			world.destroyJoint(joint.joint);
		for(BodyItem body : items.bodies)
			world.destroyBody(body.body);
		this.items.joints.removeAll(items.joints, true);
		this.items.bodies.removeAll(items.bodies, true);
	}
	public Body queryFirstBody(Vector2 pos, Vector2 scl) 
	{
		final Array<Body> bodies = new Array<Body>();
		QueryCallback callback = new QueryCallback() {
			@Override
			public boolean reportFixture(Fixture fixture) {
				Body body = fixture.getBody();
				bodies.add(body);
				return false;
			}
		};
		world.QueryAABB(callback, pos.x - scl.x, pos.y - scl.y, pos.x + scl.x, pos.y + scl.y);
		
		return bodies.size > 0 ? bodies.get(0) : null;
	}
	public Body queryFirstBody(Vector2 pos) 
	{
		final Array<Body> bodies = new Array<Body>();
		QueryCallback callback = new QueryCallback() {
			@Override
			public boolean reportFixture(Fixture fixture) {
				Body body = fixture.getBody();
				bodies.add(body);
				return false;
			}
		};
		world.QueryAABB(callback, pos.x, pos.y, pos.x, pos.y);
		
		return bodies.size > 0 ? bodies.get(0) : null;
	}
}
