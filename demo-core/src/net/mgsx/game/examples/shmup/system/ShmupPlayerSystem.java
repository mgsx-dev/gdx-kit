package net.mgsx.game.examples.shmup.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Asset;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.examples.shmup.component.Enemy;
import net.mgsx.game.examples.shmup.component.Player;
import net.mgsx.game.examples.shmup.component.PlayerBullet;
import net.mgsx.game.examples.shmup.utils.ShmupCollision;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.components.Box2DFixtureModel;
import net.mgsx.game.plugins.box2d.listeners.Box2DAdapter;
import net.mgsx.game.plugins.box2d.listeners.Box2DEntityListener;
import net.mgsx.game.plugins.box2d.listeners.Box2DListener;
import net.mgsx.game.plugins.camera.model.POVModel;
import net.mgsx.game.plugins.core.components.SlavePhysics;
import net.mgsx.game.plugins.core.components.Transform2DComponent;
import net.mgsx.game.plugins.g2d.components.SpriteModel;
import net.mgsx.game.plugins.g3d.components.G3DModel;

@EditableSystem
public class ShmupPlayerSystem extends IteratingSystem
{
	@Inject public POVModel pov;
	@Editable public float shootCooldown = .1f;
	@Editable public float bulletSpeed = 4f;

	@Asset("particles/sugar.png")
	public Texture shotTexture;
	
	private final Box2DListener bulletListener = new Box2DEntityListener(){
		@Override
		protected void beginContact(Contact contact, Fixture self, Fixture other, Entity otherEntity) {
			Enemy enemy = Enemy.components.get(otherEntity);
			if(enemy != null){
				// TODO remove some energy ... etc
				System.out.println("player shot on enemy");
			}
			G3DModel model = G3DModel.components.get(otherEntity);
			if(model != null){
				Color c = model.modelInstance.materials.first().get(ColorAttribute.class, ColorAttribute.Diffuse).color;
				c.set(c.r >= 1 ? Color.BLACK : Color.WHITE);
			}
			getEngine().removeEntity((Entity)self.getBody().getUserData());
		}
	};

	
	public ShmupPlayerSystem() {
		super(Family.all(Transform2DComponent.class, Player.class, Box2DBodyModel.class).get(), GamePipeline.LOGIC);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		engine.addEntityListener(getFamily(), new EntityListener() {
			@Override
			public void entityRemoved(Entity entity) {
			}
			
			@Override
			public void entityAdded(Entity entity) {
				// XXX patch filters
				Box2DBodyModel physics = Box2DBodyModel.components.get(entity);
				Filter filter = physics.fixtures.first().fixture.getFilterData();
				filter.categoryBits = ShmupCollision.player;
				filter.maskBits = ShmupCollision.playerMask;
				physics.fixtures.first().fixture.setFilterData(filter);
				
				physics.setListener(new Box2DAdapter() {
					@Override
					public void beginContact(Contact contact, Fixture self, Fixture other) {
						if(other.getFilterData().categoryBits == ShmupCollision.enemy){
							System.out.println("player collides with enemy");
						}else if(other.getFilterData().categoryBits == ShmupCollision.background){
							System.out.println("player collides with bg");
						}else if(other.getFilterData().categoryBits == ShmupCollision.enemyBullet){
							System.out.println("player collides with enemy bullet");
						}else{
							System.out.println("player collides with ???");
						}
					}
				});
			}
		});
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Player player = Player.components.get(entity);
		Transform2DComponent t = Transform2DComponent.components.get(entity);
		
		// TODO add slave physics ... or it is a storable !
		// SlavePhysics sp = SlavePhysics.components.get(entity);
		SlavePhysics sp = SlavePhysics.components.get(entity);
		if(sp == null) entity.add(getEngine().createComponent(SlavePhysics.class));
		// TODO clamp to screen
		//if(Gdx.input.isKeyPressed(Input.Keys.U))
		
		// TODO how to control physics (because of move) and get position from physic (because of walls)
		
		// XXX
		Box2DBodyModel pp = Box2DBodyModel.components.get(entity);
		pp.body.setAngularVelocity(0);
		//pp.body.setTransform(t.position, 0);
		
		t.position.set(pp.body.getPosition());
		// TODO inject POV instead ...
		Camera camera = pov.camera;
		
		float z = camera.project(new Vector3()).z;
		
		Vector3 a = camera.unproject(new Vector3(0, 0, z));
		Vector3 b = camera.unproject(new Vector3(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), z));
		
		if(t.position.x < a.x){
			t.position.x = a.x;
		}
		if(t.position.x > b.x){
			t.position.x = b.x;
		}
		if(t.position.y > a.y){
			t.position.y = a.y;
		}
		if(t.position.y < b.y){
			t.position.y = b.y;
		}
		
		if(Gdx.input.isKeyPressed(Input.Keys.A)){
			player.shootCooldown -= deltaTime;
			if(player.shootCooldown <= 0){
				player.shootCooldown = shootCooldown;
				
				// emit bullet
				Entity e = getEngine().createEntity();
				Transform2DComponent pbt = getEngine().createComponent(Transform2DComponent.class);
				pbt.position.set(t.position);
				
				Box2DBodyModel physics = getEngine().createComponent(Box2DBodyModel.class);
				BodyDef def = physics.def = new BodyDef();
				def.allowSleep = false;
				def.type = BodyType.DynamicBody;
				def.bullet = true;
				def.linearVelocity.set(bulletSpeed, 0);
				def.gravityScale = 0;
				
				Box2DFixtureModel fixMod = new Box2DFixtureModel();
				FixtureDef fdef = fixMod.def = new FixtureDef();
				CircleShape shape = new CircleShape();
				shape.setRadius(.5f);
				fdef.filter.categoryBits = ShmupCollision.playerBullet;
				fdef.filter.maskBits = ShmupCollision.playerBulletMask;
				fdef.shape = shape;
				physics.fixtures.add(fixMod);
				
				physics.setListener(bulletListener);
				
				SpriteModel sprite = getEngine().createComponent(SpriteModel.class);
				sprite.sprite.setTexture(shotTexture);
				//sprite.sprite.setSize(100, 100);
				sprite.sprite.setRegion(0f, 0f, 1f, 1f);
				sprite.sprite.setSize(1, 1);
				// sprite.sprite.setPosition(pbt.position.x, pbt.position.y);
				getEngine().addEntity(e.add(pbt).add(physics).add(sprite).add(getEngine().createComponent(PlayerBullet.class)));
			}
			
		}else{
			player.shootCooldown = 0;
		}
		
		
	}

}
