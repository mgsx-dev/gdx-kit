package net.mgsx.game.plugins.box2dold.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FlushablePool;

import net.mgsx.game.core.Editor;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.plugins.box2d.model.Box2DBodyModel;
import net.mgsx.game.plugins.box2dold.Box2DPresets;
import net.mgsx.game.plugins.box2dold.model.WorldItem;

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
	private FlushablePool<Particle> pool;
	
	public ParticleTool(Editor editor, WorldItem worldItem) {
		super("Particle", editor);
		this.worldItem = worldItem;
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
					Box2DBodyModel item = Box2DPresets.ball(ParticleTool.this.worldItem.world, 0.1f, position.x, position.y);
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