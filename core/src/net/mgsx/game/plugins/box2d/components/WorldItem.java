package net.mgsx.game.plugins.box2d.components;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.commands.Command;
import net.mgsx.game.core.commands.CommandHistory;
import net.mgsx.game.plugins.box2dold.model.EditorSettings;
import net.mgsx.game.plugins.box2dold.model.Items;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

// TODO it is more an EditorContext (ctx) ...
public class WorldItem 
{
	public EditorSettings settings = new EditorSettings();
	public World world;
	public EditorScreen editor;
	public Items items = new Items();
	private Items selection = new Items();
	public Array<Actor> actors = new Array<Actor>();
	
	private Array<Box2DBodyModel> scheduledForDeletion = new Array<Box2DBodyModel>();
	private Array<Runnable> scheduled= new Array<Runnable>();
	private final CommandHistory commandHistory;
	
	
	
	public WorldItem(CommandHistory commandHistory) {
		super();
		this.commandHistory = commandHistory;
	}
	
	public void initialize() {
		if(world == null){
			world = new World(settings.gravity, true); // TODO settings for doSleep 
		}
	}
	
	public void dispose() {
		items.clear();
		selection.clear();
		if(world != null) world.dispose();
	}

	
	
	public void addAll(Items items) {
		this.items.addAll(items);
	}
	public void destroy(Items items) 
	{
		for(Box2DJointModel joint : items.joints)
			world.destroyJoint(joint.joint);
		for(Box2DBodyModel body : items.bodies)
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
	public void update()
	{
		// update logic
		for(Actor actor : actors){
			actor.act(Gdx.graphics.getDeltaTime());
		}
		for(Box2DBodyModel bodyItem : items.bodies)
		{
			if(bodyItem.behavior != null) bodyItem.behavior.act();
		}
		
		// update physics
		if(settings.runSimulation)
		{
			world.step(
					settings.timeStep, 
					settings.velocityIterations, 
					settings.positionIterations);
			
			for(Runnable runnable : scheduled){
				runnable.run();
			}
			scheduled.clear();
			
			for(Box2DBodyModel body : scheduledForDeletion)
			{
				body.dispose();
			}
			scheduledForDeletion.clear();
		}
		
		
	}
	public boolean queryIsGroundTouch(final Body body, Vector2 direction, final float radius) 
	{
		final boolean [] found = new boolean[]{false};
		RayCastCallback callback = new RayCastCallback() {
			
			@Override
			public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
				if(fixture.getBody() == body) return -1;
				found[0] = radius > point.dst(body.getPosition());
				return 0;
			}
		};
		world.rayCast(callback, body.getPosition(), new Vector2(direction).scl(1).add(body.getPosition()));
		return found[0];
	}
	public Box2DBodyModel currentBody(String defaultName, float x, float y) 
	{
		Entity entity = editor.currentEntity();
		Box2DBodyModel item = entity == null ? null : entity.getComponent(Box2DBodyModel.class);
		if(item == null){
			
			BodyDef def = settings.body();
			// XXX ???
			if(entity != null && entity.getComponent(Transform2DComponent.class) != null){
				x = entity.getComponent(Transform2DComponent.class).position.x;
				y = entity.getComponent(Transform2DComponent.class).position.y;
				
			}
			def.position.set(x, y);
			Body body = world.createBody(def);
			item = editor.entityEngine.createComponent(Box2DBodyModel.class);
			item.context = this;
			item.entity = entity;
			item.id = defaultName;
			item.def = def;
			item.body = body;
			item.body.setUserData(entity);
			// XXX item = new Box2DBodyModel(this, entity, defaultName, def, body);
			entity.add(item);
		}
		return item;
	}
	public Fixture queryFirstFixture(Vector2 pos) {
		final Array<Fixture> objects = new Array<Fixture>();
		QueryCallback callback = new QueryCallback() {
			@Override
			public boolean reportFixture(Fixture fixture) {
				objects.add(fixture);
				return false;
			}
		};
		world.QueryAABB(callback, pos.x, pos.y, pos.x, pos.y);
		
		return objects.size > 0 ? objects.get(0) : null;
	}
	public Fixture rayCastFirst(final Vector2 start, Vector2 direction, final float length) {
		final Fixture [] found = new Fixture[]{null};
		RayCastCallback callback = new RayCastCallback() {
			
			@Override
			public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
				found[0] = point.dst(start) < length ? fixture : null;
				return 0;
			}
		};
		world.rayCast(callback, start, new Vector2(direction).scl(length).add(start));
		return found[0];
	}
	public Fixture rayCastFirst(Vector2 start, Vector2 end) {
		final Fixture [] found = new Fixture[]{null};
		RayCastCallback callback = new RayCastCallback() {
			
			@Override
			public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
				found[0] = fixture;
				return 0;
			}
		};
		world.rayCast(callback, start, end);
		return found[0];
	}
	public void performCommand(Command command) 
	{
		// TODO add to history ... 
		commandHistory.add(command);
		
	}

	public void scheduleRemove(Entity entity, Box2DBodyModel body) 
	{
		scheduledForDeletion.add(body);
	}

	public void schedule(Runnable runnable) 
	{
		scheduled.add(runnable);
	}

	
}