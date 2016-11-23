package net.mgsx.game.examples.platformer.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;

import net.mgsx.game.core.helpers.EntityHelper;
import net.mgsx.game.core.helpers.FamilyBuilder;
import net.mgsx.game.examples.platformer.components.BonusComponent;
import net.mgsx.game.examples.platformer.components.PlayerComponent;
import net.mgsx.game.examples.platformer.components.TreeComponent;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.listeners.Box2DComponentTrigger;
import net.mgsx.game.plugins.box2d.systems.AbstractBox2DSystem;
import net.mgsx.game.plugins.core.components.Transform2DComponent;
import net.mgsx.game.plugins.g3d.components.G3DModel;

public class TreeSystem extends AbstractBox2DSystem
{

	public TreeSystem() {
		super(new FamilyBuilder().all(TreeComponent.class, G3DModel.class));
	}
	
	@Override
	protected void registerListener(final Entity entity, final Box2DBodyModel physics) {
		physics.fixtures.get(1).fixture.setUserData(new Box2DComponentTrigger<PlayerComponent>(PlayerComponent.class){

			@Override
			protected void enter(Contact contact, Fixture self, Fixture other, Entity otherEntity,
					PlayerComponent otherComponent, boolean b) {
				physics.context.schedule(new Runnable() {
					@Override
					public void run() {
						onPlayerEnter(entity);
					}
				});
			}

			@Override
			protected void exit(Contact contact, Fixture self, Fixture other, Entity otherEntity,
					PlayerComponent otherComponent, boolean b) {
				physics.context.schedule(new Runnable() {
					@Override
					public void run() {
						onPlayerExit(entity);
					}
				});
			}
			
		});
	}
	
	private void onPlayerEnter(Entity tree)
	{
		Box2DBodyModel physics = Box2DBodyModel.components.get(tree);
		G3DModel model = G3DModel.components.get(tree);
		
		// TODO launch bonus !
		// how to do that ? we need a factory or duplicate an entity ?
		
		// XXX find template !
		// TODO use entity emitter instead !
		Entity template = getEngine().getEntitiesFor(Family.one(BonusComponent.class).get()).first();
		
		Entity newEntity = EntityHelper.clone(getEngine(), template);
		Transform2DComponent t = EntityHelper.getOrCreate(getEngine(), newEntity, Transform2DComponent.class);
		t.position.set(physics.body.getPosition());
		t.position.x += 3.2f; // TODO how to set it ? 
		t.position.y += 1;
		newEntity.add(t);
		newEntity.getComponent(Box2DBodyModel.class).def.type = BodyType.DynamicBody; // XXX Booooo !
		
		model.animationController.allowSameAnimation = true;
		model.animationController.animate(model.modelInstance.animations.get(0).id, -1, 0.6f, null, 1f);
	}
	
	private void onPlayerExit(Entity tree)
	{
		G3DModel model = G3DModel.components.get(tree);
		model.animationController.animate(model.modelInstance.animations.get(0).id, -1, 0.0001f, null, 1f);
	}
	

}
