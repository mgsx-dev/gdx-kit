package net.mgsx.game.examples.lsystem.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.examples.lsystem.components.LSystem2D;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class LSystem2DRenderer extends IteratingSystem
{
	private ShapeRenderer renderer;
	private GameScreen game;
	
	private static class Context{
		public Vector2 p = new Vector2();
		public Vector2 d = new Vector2();
		public float scale;
		public float depth;
		public int index;
	}
	
	private Array<Context> stack = new Array<LSystem2DRenderer.Context>();
	private Pool<Context> pool = new Pool<LSystem2DRenderer.Context>(){
		@Override
		protected Context newObject() {
			return new Context();
		}
	};
	
	
	public LSystem2DRenderer(GameScreen game) {
		super(Family.all(Transform2DComponent.class, LSystem2D.class).get(), GamePipeline.RENDER);
		renderer = new ShapeRenderer();
		this.game = game;
	}

	@Override
	public void update(float deltaTime) 
	{
		renderer.setProjectionMatrix(game.camera.combined);
		renderer.begin(ShapeType.Line);
		super.update(deltaTime);
		renderer.end();
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		LSystem2D lsystem = LSystem2D.components.get(entity);
		Transform2DComponent transform = Transform2DComponent.components.get(entity);
		
		stack.clear();
		
		Context ctx = pool.obtain();
		ctx.p.set(transform.position);
		ctx.d.set(Vector2.X).rotate(transform.angle);
		ctx.depth = lsystem.depth;
		ctx.scale = lsystem.size * (lsystem.normalized ? 1 : (lsystem.depth + 1));
		ctx.index = 0;
		
		while(ctx != null)
		{
			if(ctx.index >= lsystem.rules.length()){
				pool.free(ctx);
				break;
			}
			char code = lsystem.rules.charAt(ctx.index);
			if(code == 'F'){
				if(ctx.depth > 1){
					
					Context nextContext = pool.obtain();
					nextContext.p.set(ctx.p);
					nextContext.d.set(ctx.d);
					nextContext.scale = ctx.scale * lsystem.scale;
					nextContext.depth = ctx.depth - 1;
					nextContext.index = -1;
					ctx.p.mulAdd(ctx.d, ctx.scale);
					stack.add(ctx);
					ctx = nextContext;
				}else{
					// renderer.setColor(Color.WHITE);
					renderer.line(ctx.p.x, ctx.p.y, ctx.p.x + ctx.d.x * ctx.scale, ctx.p.y + ctx.d.y * ctx.scale);
					ctx.p.mulAdd(ctx.d, ctx.scale);
				}
			}else if(code == 'G'){
				if(ctx.depth > 1){
					
					Context nextContext = pool.obtain();
					nextContext.p.set(ctx.p);
					nextContext.d.set(ctx.d);
					nextContext.scale = ctx.scale * lsystem.scale;
					nextContext.depth = ctx.depth - 1;
					nextContext.index = -1;
					ctx.p.mulAdd(ctx.d, ctx.scale * Math.min(1, ctx.depth));
					stack.add(ctx);
					ctx = nextContext;
				}else{
					renderer.line(ctx.p.x, ctx.p.y, ctx.p.x + ctx.d.x * ctx.scale * Math.min(1, ctx.depth), ctx.p.y + ctx.d.y * ctx.scale * Math.min(1, ctx.depth),
							Color.WHITE, Color.GREEN);
					ctx.p.mulAdd(ctx.d, ctx.scale);
				}
			}else if(code == 'O'){
				
				float r = ctx.scale * .1f;
				ctx.p.mulAdd(ctx.d, r);
				renderer.circle(ctx.p.x, ctx.p.y, r, 12);
				ctx.p.mulAdd(ctx.d, r);
				
			}else if(code == '+'){
				ctx.d.rotate(lsystem.angle);
			}else if(code == '*'){
				ctx.scale *= 0.5f;
			}else if(code == '-'){
				ctx.d.rotate(-lsystem.angle);
			}else if(code == '['){
				Context nextContext = pool.obtain();
				nextContext.p.set(ctx.p);
				nextContext.d.set(ctx.d);
				nextContext.scale = ctx.scale;
				nextContext.depth = ctx.depth;
				nextContext.index = ctx.index;
				stack.add(ctx);
				ctx = nextContext;
			}else if(code == ']'){
				Context prevContext = stack.pop();
				prevContext.index = ctx.index;
				pool.free(ctx);
				ctx = prevContext;
			}
			
			while(ctx != null){
				ctx.index++;
				if(ctx.index >= lsystem.rules.length()){
					if(stack.size > 0){
						pool.free(ctx);
						ctx = stack.pop();
					}else{
						ctx = null;
					}
				}else{
					break;
				}
			}
			
		}
		
		
	}

}
