package net.mgsx.box2d.editor.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FlushablePool;

import net.mgsx.box2d.editor.BodyItem;
import net.mgsx.box2d.editor.Box2DPresets;
import net.mgsx.box2d.editor.WorldItem;
import net.mgsx.fwk.editor.Tool;

public class ParticleTool extends Tool
{
	public float life = 3;
	public float rate = 4;
	private float time;
	
	private WorldItem worldItem;
	private Vector2 position;
	private Vector2 direction;

	private static class Particle
	{
		float life;
		protected Body body;
	}
	
	private Array<Particle> particles = new Array<ParticleTool.Particle>();
	private Array<Body> bodies = new Array<Body>();
	private FlushablePool<Particle> pool;
	
	public ParticleTool(Camera camera, WorldItem worldItem) {
		super("Particle", camera);
		this.worldItem = worldItem;
		bodies = new Array<Body>();
		pool = new FlushablePool<ParticleTool.Particle>(){
			@Override
			protected Particle newObject() {
				Particle p = new Particle();
				p.life = life;
				return p;
			}
		};
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		position = unproject(screenX, screenY);
		return super.touchDown(screenX, screenY, pointer, button);
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(position != null){
			direction = unproject(screenX, screenY).sub(position);
		}
		return super.touchDragged(screenX, screenY, pointer);
	}
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		position = null;
		direction = null;
		
		return super.touchUp(screenX, screenY, pointer, button);
	}
	
	@Override
	public void render(ShapeRenderer renderer) 
	{
		if(position != null)
		{
			time += Gdx.graphics.getDeltaTime();
			float period = 1.f / rate;
			if(time > period){
				time -= period;
				Particle p = pool.obtain();
				p.life = life;
				if(p.body == null){
					BodyItem item = Box2DPresets.ball(ParticleTool.this.worldItem.world, 0.1f, position.x, position.y);
					p.body = item.body;
				}
				particles.add(p);
				p.body.setTransform(position, 0);
				p.body.setLinearVelocity(new Vector2(direction).scl(5f));
				// p.body.
				// p.body.applyLinearImpulse(new Vector2(direction).scl(1e5f), position, true);
			}
		}
		for(int i=particles.size-1 ; i>=0 ; i--){
			Particle p = particles.get(i);
			p.life -= Gdx.graphics.getDeltaTime();
			if(p.life <= 0){
				pool.free(p);
				particles.removeIndex(i);
				if(position == null){
					worldItem.world.destroyBody(p.body);
					p.body = null;
				}
			}
		}
	}

}
