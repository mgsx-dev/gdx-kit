package net.mgsx.game.plugins.box2d.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.plugins.box2d.helper.RayCast;
import net.mgsx.game.plugins.box2d.helper.WorldProvider;
import net.mgsx.game.plugins.editor.systems.DebugRenderSystem;

@EditableSystem(isDebug=true)
public class Box2DRayCastDebugSystem  extends EntitySystem 
{
	@Inject protected DebugRenderSystem editor;
	private final Box2DWorldContext context;

	private WorldProvider original, tracker;
	
	private Pool<RayCast> rayPool = new Pool<RayCast>(){
		@Override
		protected RayCast newObject() {
			return new RayCast();
		}
	};
	private Array<RayCast> rays = new Array<RayCast>();
	
	private class WorldProviderTracker extends WorldProvider{

		public WorldProviderTracker(World world) {
			super(world);
		}
		
		@Override
		protected void rayCast(RayCast rayCast) {
			rays.add(rayPool.obtain().set(rayCast));
			super.rayCast(rayCast);
		}
		
	}
	
	public Box2DRayCastDebugSystem(Box2DWorldContext context) {
		super(GamePipeline.RENDER_OVER);
		this.context = context;
		
		// override provider to keep track of raycast during a frame.
		original = context.provider;
		context.provider = tracker = new WorldProviderTracker(context.world);
	}
	
	@Override
	public void setProcessing(boolean processing) 
	{
		if(processing != this.checkProcessing()){
			context.provider = processing ? tracker : original;
		}
		super.setProcessing(processing);
	}
	
	@Override
	public void update(float deltaTime) {
		editor.shapeRenderer.begin(ShapeType.Line);
		editor.shapeRenderer.setColor(Color.RED);
		
		for(RayCast ray : rays) {
			editor.shapeRenderer.line(ray.start, ray.end);
		}
		
		editor.shapeRenderer.end();
		
		rayPool.freeAll(rays);
		rays.clear();
	}
	
}