package net.mgsx.game.examples.openworld.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.PostInitializationListener;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.EnumType;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.examples.openworld.components.CellDataComponent;
import net.mgsx.game.examples.openworld.components.LandMeshComponent;
import net.mgsx.game.examples.openworld.components.TreesComponent;
import net.mgsx.game.examples.openworld.model.OpenWorldPool;
import net.mgsx.game.examples.openworld.model.OpenWorldPool.CellData;
import net.mgsx.game.plugins.bullet.components.BulletHeightFieldComponent;
import net.mgsx.game.plugins.core.components.HeightFieldComponent;
import net.mgsx.game.plugins.procedural.model.ClassicalPerlinNoise;

@Storable(value="ow.manager")
@EditableSystem
public class OpenWorldManagerSystem extends EntitySystem implements PostInitializationListener
{
	@Editable public float scale = 10;
	@Editable public float frequency = .2f;
	@Editable public float persistence = .5f;
	@Editable public int octaves = 3;
	
	/** this is the main game seed, different for each game, all other seeds are
	 * based on this seed. */
	@Editable(type=EnumType.RANDOM) public long seed = 0xdeadbeef;
	
	public static final int SEED_LAYER_ALTITUDE = 0;
	public static final int SEED_LAYER_FLORA = 1;
	public static final int SEED_LAYERS_COUNT = 2;
	
	private long [] seedLayers = new long[SEED_LAYERS_COUNT];
	
	private transient Entity [] lands;
	private transient Entity [] oldLands;
	
	@Editable public int logicSize = 3;
	
	private int logicWidth;
	private int logicHeight;
	
	private int verticesPerCell = 16;
	public float worldCellScale = verticesPerCell-1;
	
	/** aka camera in a 2D plan */
	@Editable public Vector2 viewPoint = new Vector2();
	
	private int logicOffsetX, logicOffsetY;
	
	public OpenWorldManagerSystem() {
		super(GamePipeline.LOGIC);
	}
	
	@Override
	public void onPostInitialization() {
		clear();
	}
	
	public int getLogicOffsetX() {
		return logicOffsetX;
	}
	public int getLogicOffsetY() {
		return logicOffsetY;
	}
	
	public Entity[] getHeightMaps() {
		return lands;
	}
	public int getLogicWidth() {
		return logicWidth;
	}
	public int getLogicHeight() {
		return logicHeight;
	}
	
	@Editable
	public void clear() {
		if(lands != null){
			for(int i=0 ; i<lands.length ; i++) {
				Entity entity = lands[i];
				if(entity != null) {
					removeCell(entity);
					lands[i] = null;
				}
			}
		}
		logicWidth = logicHeight = logicSize;
		lands = new Entity[logicWidth * logicHeight];
		oldLands = new Entity[logicWidth * logicHeight];
		
		// build seed layers
		rand.setSeed(seed);
		for(int i=0 ; i<seedLayers.length ; i++){
			seedLayers[i] = rand.nextLong();
		}
	}
	
	private void removeCell(Entity entity){
		// TODO get BHFC in order to reuse the float buffer
		// Bullet body have to be removed ... but could be reused as well (just disable it and the nre-enable it ?)
		getEngine().removeEntity(entity);
	}
	
	@Override
	public void update(float deltaTime) 
	{
		// compute logic offset based on POV position
		int logicCenterX = MathUtils.floor(viewPoint.x / worldCellScale);
		int logicCenterY = MathUtils.floor(viewPoint.y / worldCellScale);
		
		int newLogicOffsetX = logicCenterX - logicWidth / 2;
		int newLogicOffsetY = logicCenterY - logicHeight / 2;
		
		// remove out entities and/or components
		
		// copy old lands to new lands
		
		// swap tables
		Entity [] tmpLands = lands;
		lands = oldLands;
		oldLands = tmpLands;
		// reindex
		for(int y=0 ; y<logicHeight ; y++) {
			for(int x=0 ; x<logicWidth ; x++) {
				int index = y * logicWidth + x;
				
				int newX = x + logicOffsetX - newLogicOffsetX;
				int newY = y + logicOffsetY - newLogicOffsetY;
				
				// this cell has a target in the new table, let's copy it
				if(newX>=0 && newX<logicWidth && newY>=0 && newY<logicHeight) {
					lands[newY * logicWidth + newX] = oldLands[index];
				} 
				// this cell has no target (too far), let's remove it if exists.
				else {
					Entity entity = oldLands[index];
					if(entity != null) {
						removeCell(entity);
					}
				}
				oldLands[index] = null;
			}
		}
		
		logicOffsetX = newLogicOffsetX;
		logicOffsetY = newLogicOffsetY;
		
		// create in entities and/or components
		for(int y=0 ; y<logicHeight ; y++) {
			for(int x=0 ; x<logicWidth ; x++) {
				int index = y * logicWidth + x;
				if(lands[index] == null) {
					Entity entity = getEngine().createEntity();
					generate(entity, worldCellScale * (logicOffsetX + x), worldCellScale * (logicOffsetY + y));
					getEngine().addEntity(entity);
					lands[index] = entity;
				}
			}
		}
		
	}
	
	private ClassicalPerlinNoise noise = new ClassicalPerlinNoise();
	private RandomXS128 rand = new RandomXS128();
	private Vector3 dx = new Vector3();
	private Vector3 dy = new Vector3();

	/**
	 * Main land function
	 * @param absoluteX
	 * @param absoluteY
	 * @return
	 */
	public float generateAltitude(float absoluteX, float absoluteY)
	{
		rand.setSeed(this.seedLayers[SEED_LAYER_ALTITUDE]);
		
		float amplitude = 1;
		float sum = 0;
		float frequency = this.frequency;
		float value = 0;
		for(int i=0 ; i<octaves ; i++){
			noise.seed(rand.nextLong());
			value += noise.get(absoluteX * frequency, absoluteY * frequency) * amplitude;
			sum += amplitude;
			amplitude *= persistence;
			frequency *= 2;
		}
		// normalized values
		return value * scale / sum;
	}
	
	// TODO extract absolute generator part from sampling part (refactor with generate absolute ...)
	private void generate(Entity entity, float offsetWorldX, float offsetWorldY)
	{
		int width = verticesPerCell;
		int height = verticesPerCell;
		
		int eWidth = width+2;
		int eHeight = height+2;
		
		CellData data = OpenWorldPool.obtainCellData(width, height);
		float [] values = data.values;
		float [] extraValues = data.extraValues;
		Vector3 [] normals = data.normals;
		
		// reset from pool
		for(int i=0 ; i<extraValues.length ; i++) extraValues[i] = 0;
		
		rand.setSeed(this.seedLayers[SEED_LAYER_ALTITUDE]);
		float amplitude = 1;
		float sum = 0;
		float frequency = this.frequency;
		for(int i=0 ; i<octaves ; i++){
			noise.seed(rand.nextLong());
			for(int ey=0 ; ey<eHeight ; ey++){
				for(int ex=0 ; ex<eWidth ; ex++){
					int x = ex - 1;
					int y = ey - 1;
					
					float fy = worldCellScale * (float)y / (float)(height-1);
					float fx = worldCellScale * (float)x / (float)(width-1);
					
					float value = noise.get((fx + offsetWorldX) * frequency, (fy + offsetWorldY) * frequency) * amplitude;
					
					extraValues[ey*eWidth+ex] += value;
				}
			}
			sum += amplitude;
			amplitude *= persistence;
			frequency *= 2;
		}
		// normalized values
		for(int y=0 ; y<eHeight ; y++){
			for(int x=0 ; x<eWidth ; x++){
				extraValues[y*eWidth+x] *= scale / sum;
			}
		}
		// extract inner values and compute normals
		
		for(int y=0 ; y<height ; y++){
			for(int x=0 ; x<width ; x++){
				values[y*width+x] = extraValues[(y+1)*eWidth+x+1];
				
				dx.set(2, 0, extraValues[y*eWidth+x+2] - extraValues[y*eWidth+x]);
				dy.set(0, 2, extraValues[(y+2)*eWidth+x] - extraValues[(y+2)*eWidth+x]);
				
				Vector3 n = dx.crs(dy).nor();
				
				normals[y*width+x].set(n);
			}
		}
		
		// compute normals
		
		CellDataComponent cdc = getEngine().createComponent(CellDataComponent.class);
		cdc.data = data;
		entity.add(cdc);
		
		HeightFieldComponent hfc = getEngine().createComponent(HeightFieldComponent.class);
		
		hfc.position.set(offsetWorldX, 0, offsetWorldY);
		hfc.width = width;
		hfc.height = height;
		hfc.values = values;
		hfc.extraValues = extraValues;
		hfc.normals = normals;
		
		entity.add(hfc);
		
		// add bullet
		BulletHeightFieldComponent bhfc = getEngine().createComponent(BulletHeightFieldComponent.class);
		entity.add(bhfc);
		
		// add mesh
		LandMeshComponent lmc = getEngine().createComponent(LandMeshComponent.class);
		entity.add(lmc);
		
		// TODO not all ?!
		// add trees
		TreesComponent tc = getEngine().createComponent(TreesComponent.class);
		entity.add(tc);
		
	}
}
