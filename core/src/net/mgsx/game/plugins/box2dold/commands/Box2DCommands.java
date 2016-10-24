package net.mgsx.game.plugins.box2dold.commands;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.World;

import net.mgsx.game.core.Editor;
import net.mgsx.game.core.commands.Command;
import net.mgsx.game.plugins.box2d.model.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.model.Box2DFixtureModel;
import net.mgsx.game.plugins.box2d.model.Box2DJointModel;
import net.mgsx.game.plugins.box2dold.Box2DPresets.Box2DPreset;
import net.mgsx.game.plugins.box2dold.model.Items;
import net.mgsx.game.plugins.box2dold.model.WorldItem;

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
	public static Command moveBody(final Box2DBodyModel body, final Vector2 value){
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
	
	public static Command addShape(final WorldItem worldItem, final Box2DBodyModel bodyItem, final FixtureDef def){
		return new Command(){
			private Box2DFixtureModel fixtureItem;
			@Override
			public void commit() {
				Fixture fixture = bodyItem.body.createFixture(def);
				fixtureItem = new Box2DFixtureModel("Polygon", def, fixture);
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
			private Box2DBodyModel bodyItem;
			@Override
			public void commit() {
				body = worldItem.world.createBody(def);
				bodyItem = new Box2DBodyModel(worldItem, null, name, def, body); // XXX
				worldItem.items.bodies.add(bodyItem);
			}
			@Override
			public void rollback() {
				worldItem.world.destroyBody(body);
				worldItem.items.bodies.removeValue(bodyItem, true);
			}
		};
	}
	public static Command addJoint(final Editor editor, final World world, final String name, final JointDef def){
		return new Command(){
			@Override
			public void commit() {
				Entity entity = editor.currentEntity();
				
				// prevent add component joint on entity with body component. 
				// XXX it works so why not : bodyB carry joint which is logic : B depends A
				// problem is, creation order : if B created before A this will raise an error !
				// so need to have a lector for joints or attach it to a mesh or a sprite ...
				if(entity.getComponent(Box2DBodyModel.class) != null)
					entity = editor.createAndAddEntity();
				
				entity.add(new Box2DJointModel(name, def, world.createJoint(def)));
			}
			@Override
			public void rollback() {
				// TODO support undo ...
				// world.destroyJoint(joint);
				// worldItem.items.joints.removeValue(jointItem, true);
			}
		};
	}
}
