package net.mgsx.game.examples.openworld.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.EnumType;
import net.mgsx.game.examples.openworld.components.LandMeshComponent;
import net.mgsx.game.plugins.bullet.components.BulletHeightFieldComponent;
import net.mgsx.game.plugins.core.components.HeightFieldComponent;
import net.mgsx.game.plugins.procedural.model.ClassicalPerlinNoise;

@EditableSystem
public class OpenWorldManagerSystem extends EntitySystem
{
	@Editable public float scale = 10;
	@Editable public float frequency = .2f;
	@Editable public float persistence = .5f;
	@Editable public int octaves = 3;
	@Editable(type=EnumType.RANDOM) public long seed = 0xdeadbeef;
	
	private transient Entity [] lands;
	private transient Entity [] oldLands;
	
	private int logicWidth = 3;
	private int logicHeight = 3;
	
	private int verticesPerCell = 16;
	private float worldCellScale = verticesPerCell-1;
	
	/** aka camera in a 2D plan */
	@Editable public Vector2 viewPoint = new Vector2();
	
	private int logicOffsetX, logicOffsetY;
	
	public OpenWorldManagerSystem() {
		super(GamePipeline.LOGIC);
		
		lands = new Entity[logicWidth * logicHeight];
		oldLands = new Entity[logicWidth * logicHeight];
	}
	
	@Editable
	public void clear() {
		for(int i=0 ; i<lands.length ; i++) {
			Entity entity = lands[i];
			if(entity != null) {
				getEngine().removeEntity(entity);
				lands[i] = null;
			}
		}
	}
	
	@Override
	public void update(float deltaTime) 
	{
		// compute logic offset based on POV position
		int logicCenterX = MathUtils.round(viewPoint.x / worldCellScale);
		int logicCenterY = MathUtils.round(viewPoint.y / worldCellScale);
		
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
						getEngine().removeEntity(entity);
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
	
	public void generate(Entity entity, float offsetWorldX, float offsetWorldY)
	{
		ClassicalPerlinNoise noise = new ClassicalPerlinNoise();
		RandomXS128 rand = new RandomXS128(this.seed);
		
		int width = verticesPerCell;
		int height = verticesPerCell;
		
		float [] values = new float[width * height];
		
		long seed = this.seed;
		float amplitude = 1;
		float sum = 0;
		float frequency = this.frequency;
		for(int i=0 ; i<octaves ; i++){
			noise.seed(seed);
			for(int y=0 ; y<height ; y++){
				for(int x=0 ; x<width ; x++){
					float fy = worldCellScale * (float)y / (float)(height-1);
					float fx = worldCellScale * (float)x / (float)(width-1);
					
					values[y*width+x] += noise.get((fx + offsetWorldX) * frequency, (fy + offsetWorldY) * frequency) * amplitude;
				}
			}
			sum += amplitude;
			amplitude *= persistence;
			frequency *= 2;
			seed = rand.nextLong();
		}
		for(int y=0 ; y<height ; y++){
			for(int x=0 ; x<width ; x++){
				values[y*width+x] *= scale / sum;
			}
		}
		
		HeightFieldComponent hfc = getEngine().createComponent(HeightFieldComponent.class);
		
		hfc.position.set(offsetWorldX, 0, offsetWorldY);
		hfc.width = width;
		hfc.height = height;
		hfc.values = values;
		
		entity.add(hfc);
		
		// add bullet
		BulletHeightFieldComponent bhfc = getEngine().createComponent(BulletHeightFieldComponent.class);
		entity.add(bhfc);
		
		// add mesh
		LandMeshComponent lmc = getEngine().createComponent(LandMeshComponent.class);
		entity.add(lmc);
		
	}
}
