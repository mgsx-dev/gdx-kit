package net.mgsx.game.examples.openworld.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.PostInitializationListener;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.EnumType;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.plugins.procedural.model.ClassicalPerlinNoise;

/**
 * System responsible of world generation. It doesn't generate any meshes or
 * textures but is used by other systems in order to querying world information
 * at any precision.
 * 
 * @author mgsx
 *
 */
@Storable("ow.generator")
@EditableSystem
public class OpenWorldGeneratorSystem extends EntitySystem implements PostInitializationListener
{
	public static final int SEED_LAYER_ALTITUDE = 0;
	public static final int SEED_LAYER_FLORA = 1;
	public static final int SEED_LAYER_ROUGHNESS = 2;
	public static final int SEED_LAYER_PLANARITY = 3;
	public static final int SEED_LAYER_WATER_FLOW = 4;
	public static final int SEED_LAYERS_COUNT = 5;
	
	/**
	 * Height map scale (shouldn't be changed in order to preserve object positions.
	 */
	// TODO public static final float heightScale = 5000;
	
	
	/** this is the main game seed, different for each game, all other seeds are
	 * based on this seed. */
	@Editable(type=EnumType.RANDOM) public long seed = 0xdeadbeef;
	
	// TODO rename altitude
	@Editable public float scale = 10;
	@Editable public float frequency = .2f;
	@Editable public float persistence = .5f;
	@Editable public int octaves = 3;

	@Editable public boolean fBm = false;

	@Editable public float floraFrequency = .1f;

	@Editable public float waterFlowFrequency = .1f;
	@Editable public float waterFlowForce = 1f;
	
	public transient long [] seedLayers = new long[SEED_LAYERS_COUNT];

	private ClassicalPerlinNoise noise = new ClassicalPerlinNoise();
	private RandomXS128 rand = new RandomXS128();
	
	
	public OpenWorldGeneratorSystem() {
		super(GamePipeline.FIRST);
	}
	
	@Override
	public void onPostInitialization() {
		reset();
	}
	
	@Editable
	public void reset()
	{
		// build seed layers
		rand.setSeed(seed);
		for(int i=0 ; i<seedLayers.length ; i++){
			seedLayers[i] = rand.nextLong();
		}
	}
	
	/**
	 * Main land function
	 * @param ax
	 * @param ay
	 * @return
	 */
	public float getAltitude(float ax, float ay)
	{
		if(fBm){
			// TODO config planarity frequency
			float planarity = 0.5f + .5f * getPerlin(ax, ay, seedLayers[SEED_LAYER_PLANARITY], frequency * 8, 1, 1);
			
			// TODO config roughness frequency
			float rougness = 0.5f + 0.25f * getPerlin(ax, ay, seedLayers[SEED_LAYER_ROUGHNESS], frequency * 5.5f, 1, 1);
			
			float altitude = getPerlin(ax, ay, seedLayers[SEED_LAYER_ALTITUDE], frequency, octaves, rougness);
			
			return scale * altitude * planarity;
		}
		return scale * getPerlin(ax, ay, seedLayers[SEED_LAYER_ALTITUDE], frequency, octaves, persistence);
	}

	public float getFlora(float ax, float ay){
		
		// TODO depends on altitude and planarity and maybe roughness.
		
		return getPerlin(ax, ay, seedLayers[SEED_LAYER_FLORA], floraFrequency, 2, .5f); // TODO config ?
	}
	
	private float getPerlin(float ax, float ay, long seed, float frequency, int octaves, float persistence)
	{
		rand.setSeed(seed);
		
		float amplitude = 1;
		float sum = 0;
		float value = 0;
		for(int i=0 ; i<octaves ; i++){
			noise.seed(rand.nextLong());
			value += noise.get(ax * frequency, ay * frequency) * amplitude;
			sum += amplitude;
			amplitude *= persistence;
			frequency *= 2;
		}
		// normalized values
		return value / sum;
	}

	public Vector2 getWaterCurrent(Vector2 waterFlow, float ax, float ay) {
		rand.setSeed(seedLayers[SEED_LAYER_WATER_FLOW]);
		
		float lx = ax * waterFlowFrequency;
		float ly = ay * waterFlowFrequency;
		
		// Force is never zero and range from 0.5 to 1
		noise.seed(rand.nextLong());
		float force = noise.get(lx, ly) * .25f + .75f;

		noise.seed(rand.nextLong());
		return noise.get(waterFlow, lx, ly).scl(force * waterFlowForce);
	}


}
