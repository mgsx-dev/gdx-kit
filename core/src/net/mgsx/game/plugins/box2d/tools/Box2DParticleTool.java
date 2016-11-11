package net.mgsx.game.plugins.box2d.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FlushablePool;

import net.mgsx.game.core.Editor;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.core.tools.DuplicateTool;

// TODO could be generalized as PArticleTool ith a special Box2D implementtion which send speed at start ...
// or abstract physic layer with a component and a field wantedSpeed not null
public class Box2DParticleTool  extends Tool
{
	public float life = 3;
	public float rate = 4;
	private float time;
	
	private Vector2 position;
	private Vector2 direction;

	private static class Particle
	{
		float life;
		protected Entity entity;
	}
	
	private Array<Particle> particles = new Array<Box2DParticleTool.Particle>();
	private FlushablePool<Particle> pool;
	private Entity base;
	
	public Box2DParticleTool(Editor editor) {
		super("Particle", editor);
		activator = Family.all(Box2DBodyModel.class).get();
		
		pool = new FlushablePool<Box2DParticleTool.Particle>(){
			@Override
			protected Particle newObject() {
				Particle p = new Particle();
				p.life = life;
				return p;
			}
		};
	}
	
	@Override
	protected void activate() {
		super.activate();
		base = editor.getSelected();
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(button == Input.Buttons.LEFT){
			position = unproject(screenX, screenY);
			return true;
		}
		return super.touchDown(screenX, screenY, pointer, button);
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(position != null){
			direction = unproject(screenX, screenY).sub(position);
			return true;
		}
		return super.touchDragged(screenX, screenY, pointer);
	}
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(button == Input.Buttons.LEFT){
			position = null;
			direction = null;
			return true;
		}
		return super.touchUp(screenX, screenY, pointer, button);
	}
	
	@Override
	public void render(ShapeRenderer renderer) 
	{
		if(position != null && direction != null)
		{
			time += Gdx.graphics.getDeltaTime();
			float period = 1.f / rate;
			if(time > period){
				time -= period;
				Particle p = pool.obtain();
				p.life = life;
				if(p.entity == null)
				{
					p.entity = DuplicateTool.duplicateEntity(editor, base);
				}
				particles.add(p);
				Box2DBodyModel b2 = p.entity.getComponent(Box2DBodyModel.class);
				if(b2 != null){
					b2.body.setTransform(position, 0);
					b2.body.setLinearVelocity(new Vector2(direction).scl(5f));
				}
			}
		}
		for(int i=particles.size-1 ; i>=0 ; i--){
			Particle p = particles.get(i);
			p.life -= Gdx.graphics.getDeltaTime();
			if(p.life <= 0){
				pool.free(p);
				particles.removeIndex(i);
				if(position == null){
					editor.entityEngine.removeEntity(p.entity);
					p.entity = null;
				}
			}
		}
	}

}
