package net.mgsx.box2d.editor.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.scenes.scene2d.Actor;

import net.mgsx.box2d.editor.WorldItem;
import net.mgsx.fwk.editor.Tool;
import net.mgsx.fwk.editor.tools.RectangleTool;

public class PlayerTool extends RectangleTool
{
	private WorldItem worldItem;
	public PlayerTool(Camera camera, WorldItem worldItem) {
		super("Player", camera);
		this.worldItem = worldItem;
	}

	@Override
	protected void create(Vector2 startPoint, Vector2 endPoint) 
	{
		float x = (startPoint.x + endPoint.x) / 2;
		float y = (startPoint.y + endPoint.y) / 2;
		float w = Math.abs(startPoint.x - endPoint.x);
		float h = Math.abs(startPoint.y - endPoint.y);
		
		PolygonShape pshape = new PolygonShape();
		pshape.setAsBox(w/2, h/2); 
		
		CircleShape shape = new CircleShape();
		final float r = startPoint.dst(endPoint);
		shape.setRadius(r);
		
		BodyDef def = worldItem.settings.body();
		def.type = BodyType.DynamicBody;
		def.fixedRotation = true;
		def.position.set(x, y);
		def.linearDamping = 0.3f;
		
		// TODO fixture template from worldItem (from user edit)
		FixtureDef fix = worldItem.settings.fixture();
		fix.density = 0.5f; // half size
		fix.shape = pshape;
		
		final Body body = worldItem.world.createBody(def);
		body.createFixture(fix);
		
		// TODO could be reused as is!
		Actor actor = new Actor(){
			private boolean jumping = false;
			@Override
			public void act(float delta) {
				Vector2 vel = body.getLinearVelocity();
				Vector2 dir = new Vector2(vel).nor();
				boolean inTheAir = !worldItem.queryIsGroundTouch(body, new Vector2(0, -1), r * 31.5f); // TODO how ???
				if(!inTheAir) jumping = false;
				float force = inTheAir ? 0.5f : 1f;
				if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
					// vel.x = -force;
//					if(vel.len() > 0.5f)
//						body.applyForceToCenter(new Vector2(dir).scl(-force),  true);
//					else
						body.applyForceToCenter(new Vector2(-force, 0),  true);
				}else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
//					if(vel.len() > 0.5f)
//						body.applyForceToCenter(new Vector2(dir).scl(force),  true);
//					else
						body.applyForceToCenter(new Vector2(force, 0),  true);
				}
				;
				if(Gdx.input.isKeyPressed(Input.Keys.Z)){
					if(!jumping && !inTheAir){
						vel.y = 3;
						jumping = true;
					}
				}
				float limit = 1.2f;
				float limity = 6.f;
				if(vel.x > limit) vel.x = limit;
				if(vel.x < -limit) vel.x = -limit;
				if(vel.y > limity) vel.y = limity;
				if(vel.y < -limity) vel.y = -limity;
				body.setLinearVelocity(vel);
				
				
			}
		};
		worldItem.actors.add(actor);
		
	}


}
