package net.mgsx.plugins.box2d.commands;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointDef;

import net.mgsx.core.Command;
import net.mgsx.plugins.box2d.Box2DPresets.Box2DPreset;
import net.mgsx.plugins.box2d.model.BodyItem;
import net.mgsx.plugins.box2d.model.FixtureItem;
import net.mgsx.plugins.box2d.model.Items;
import net.mgsx.plugins.box2d.model.JointItem;
import net.mgsx.plugins.box2d.model.WorldItem;

public class Box2DCommands {

	public static Command preset(final WorldItem worldItem, final Box2DPreset preset, final float x, final float y){
		return new Command(){
			private Items items;
			@Override
			public void commit() 
			{
				items = new Items();
				preset.create(items, worldItem.world, x, y);
				worldItem.addAll(items);
			}
			@Override
			public void rollback() {
				worldItem.destroy(items);
			}
		};
	}
	
	// TODO maybe create a special Vector2 mutator ... ?, same for float ... and Vec3
	public static Command moveBody(final BodyItem body, final Vector2 value){
		return new Command(){
			Vector2 position;
			@Override
			public void commit() {
				position = body.body.getTransform().getPosition();
				body.body.getTransform().setPosition(value);
			}
			@Override
			public void rollback() {
				body.body.getTransform().setPosition(position);
			}
		};
	}
	
	public static Command addShape(final WorldItem worldItem, final BodyItem bodyItem, final FixtureDef def){
		return new Command(){
			private FixtureItem fixtureItem;
			@Override
			public void commit() {
				Fixture fixture = bodyItem.body.createFixture(def);
				fixtureItem = new FixtureItem("Polygon", def, fixture);
				bodyItem.fixtures.add(fixtureItem);
			}
			@Override
			public void rollback() {
				bodyItem.body.destroyFixture(fixtureItem.fixture);
				bodyItem.fixtures.removeValue(fixtureItem, true);
				if(bodyItem.body.getFixtureList().size <= 0){
					worldItem.world.destroyBody(bodyItem.body);
					worldItem.items.bodies.removeValue(bodyItem, true);
				}
			}
		};
	}
	public static Command addBody(final WorldItem worldItem, final String name, final BodyDef def){
		return new Command(){
			private Body body;
			private BodyItem bodyItem;
			@Override
			public void commit() {
				body = worldItem.world.createBody(def);
				bodyItem = new BodyItem(null, name, def, body); // XXX
				worldItem.items.bodies.add(bodyItem);
			}
			@Override
			public void rollback() {
				worldItem.world.destroyBody(body);
				worldItem.items.bodies.removeValue(bodyItem, true);
			}
		};
	}
	public static Command addJoint(final WorldItem worldItem, final String name, final JointDef def){
		return new Command(){
			private Joint joint;
			private JointItem jointItem;
			@Override
			public void commit() {
				joint = worldItem.world.createJoint(def);
				jointItem = new JointItem(name, def, joint);
				worldItem.items.joints.add(jointItem);
			}
			@Override
			public void rollback() {
				worldItem.world.destroyJoint(joint);
				worldItem.items.joints.removeValue(jointItem, true);
			}
		};
	}
}
