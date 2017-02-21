package net.mgsx.game.plugins.box2d.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.World;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.commands.Command;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.components.Box2DFixtureModel;
import net.mgsx.game.plugins.box2d.components.Box2DJointModel;
import net.mgsx.game.plugins.box2d.systems.Box2DWorldContext;
import net.mgsx.game.plugins.editor.systems.SelectionSystem;

public class Box2DCommands {

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
	
	public static Command addShape(final Box2DWorldContext worldItem, final Box2DBodyModel bodyItem, final FixtureDef def){
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
				}
			}
		};
	}
	
	public static Command addJoint(final EditorScreen editor, final World world, final String name, final JointDef def){
		return new Command(){
			@Override
			public void commit() {
				Entity entity = editor.entityEngine.getSystem(SelectionSystem.class).currentEntity();
				
				// prevent add component joint on entity with body component. 
				// XXX it works so why not : bodyB carry joint which is logic : B depends A
				// problem is, creation order : if B created before A this will raise an error !
				// so need to have a lector for joints or attach it to a mesh or a sprite ...
				if(entity.getComponent(Box2DBodyModel.class) != null){
					entity = editor.entityEngine.createEntity();
					editor.entityEngine.addEntity(entity);
				}
					
				
				Joint joint = world.createJoint(def);
				joint.setUserData(entity);
				entity.add(new Box2DJointModel(name, def, joint));
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
