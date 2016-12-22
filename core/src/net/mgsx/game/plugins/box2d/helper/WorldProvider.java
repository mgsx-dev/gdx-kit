package net.mgsx.game.plugins.box2d.helper;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

/**
 * Encapsulate Box2D world calls for rayCast and AABB queries.
 * 
 * Results are reset before a new call. Caller must process result before invoke a
 * new query / raycast.
 * 
 * @author mgsx
 *
 */
public class WorldProvider implements RayCastCallback, QueryCallback
{
	protected static final Array<RayCastResult> rayCastResults = new Array<RayCastResult>();
	protected static final Array<Fixture> queryResults = new Array<Fixture>();
	final private static RayCast rayCast = new RayCast();
	
	private static boolean firstResult;

	private static Pool<RayCastResult> rayCastResultsPool = new Pool<RayCastResult>(){
		@Override
		protected RayCastResult newObject() {
			return new RayCastResult();
		}
	};
	
	private World world;
	
	public WorldProvider(World world) {
		super();
		this.world = world;
	}

	public Body queryFirstBody(Vector2 pos) 
	{
		Fixture fixture = queryFirstFixture(pos);
		return fixture == null ? null : fixture.getBody();
	}

	public Fixture queryFirstFixture(Vector2 pos) {
		firstResult = true;
		queryAABB(pos, pos);
		return queryResults.size > 0 ? queryResults.first() : null;
	}
	protected void queryAABB(Vector2 a, Vector2 b) {
		resetQuery();
		world.QueryAABB(this, a.x, a.y, b.x, b.y);
	}
	
	private void resetQuery() {
		queryResults.clear();
	}

	public RayCastResult rayCastFirst(Vector2 start, Vector2 direction, float length){
		return rayCastFirst(rayCast.set(start, direction, length));
	}
	public RayCastResult rayCastFirst(RayCast rayCast)
	{
		firstResult = true;
		rayCast(rayCast);
		return rayCastResults.size > 0 ? rayCastResults.first() : null;
	}
	
	protected void rayCast(RayCast rayCast){
		resetRayCast();
		// prevent raycast with zero length crash.
		if(rayCast.start.epsilonEquals(rayCast.end, 1e-6f)) return; // TODO what is the limit ?
		world.rayCast(this, rayCast.start, rayCast.end);
	}
	
	private void resetRayCast() {
		rayCastResultsPool.freeAll(rayCastResults);
		rayCastResults.clear();
	}
	
	@Override
	public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) 
	{
		RayCastResult result = rayCastResultsPool.obtain();
		result.fixture = fixture;
		result.fraction = fraction;
		result.normal.set(normal);
		result.point.set(point);
		rayCastResults.add(result);
		return firstResult ? 0 : 1; // TODO -1 if not match, 0 stop, 1 continue, fraction : ... ? change ray length ...
	}

	@Override
	public boolean reportFixture(Fixture fixture) {
		queryResults.add(fixture);
		return !firstResult;
	}
}
