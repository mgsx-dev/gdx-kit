package net.mgsx.game.examples.openworld.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.PostInitializationListener;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.examples.openworld.components.CellDataComponent;
import net.mgsx.game.examples.openworld.components.LandMeshComponent;
import net.mgsx.game.examples.openworld.components.TreesComponent;
import net.mgsx.game.examples.openworld.model.OpenWorldPool;
import net.mgsx.game.examples.openworld.model.OpenWorldPool.CellData;
import net.mgsx.game.plugins.bullet.components.BulletHeightFieldComponent;
import net.mgsx.game.plugins.core.components.HeightFieldComponent;

@Storable(value="ow.manager")
@EditableSystem
public class OpenWorldManagerSystem extends EntitySystem implements PostInitializationListener
{
	@Inject OpenWorldGeneratorSystem generator;
	
	private transient Entity [] lands;
	private transient Entity [] oldLands;
	
	@Editable public int logicSize = 3;
	
	/**
	 * 4 textures holding world layer information :
	 * R = altitude
	 * G = flora
	 * B = undefined
	 * A = undefined
	 * 
	 * each texture cover a big world part and just a small part
	 * is updated when needed.
	 * 
	 * Since quality is very low, shaders never render it directly but
	 * select appropriate shading based on this information. For instance,
	 * when flora is at its maximum value then shader mix diffuse as a full
	 * grass area.
	 * 
	 * Texture coordinates are setup to avoid binding the 4 textures at a time and
	 * to avoid recopy previous pixels.
	 * 
	 * Then texture is aligned to cells and lands can get appropriate texture depending on
	 * its location. Texture coordinates are also computed to match the area.
	 * 
	 * XXX a simpler solution would be to attach a texture to each cells ... (small like 16x16)
	 * 
	 */
	// XXX private Texture [] worldTextures = new Texture[4];
	
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
	
	private Vector3 dx = new Vector3();
	private Vector3 dy = new Vector3();

	
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
		
		for(int ey=0 ; ey<eHeight ; ey++){
			for(int ex=0 ; ex<eWidth ; ex++){
				int x = ex - 1;
				int y = ey - 1;
				
				float fy = worldCellScale * (float)y / (float)(height-1);
				float fx = worldCellScale * (float)x / (float)(width-1);
				
				float ax = fx + offsetWorldX;
				float ay = fy + offsetWorldY;
				
				extraValues[ey*eWidth+ex] = generator.getAltitude(ax, ay);
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
		
		// create the associated texture
		// TODO mutualize with 2D map which is the info texture rendered differently (height layering, ... hidden parts from user ...and so on ...)
		// TODO use bigger map to reduce texture switching
		// TODO compute borders to avoid breaks between cells.
		// TODO more info :
		// altitude => under/above water, mountain, snow
		// flora VS desert
		// humidity => big green trees VS savana
		// 4th ? diffculty or other important things (wind ?...)
		Vector2 flow = new Vector2(); 
		Pixmap pixmap = OpenWorldPool.pixmap;
		pixmap.setBlending(Blending.None);
		for(int y=0 ; y<pixmap.getHeight() ; y++){
			for(int x=0 ; x<pixmap.getWidth() ; x++){
				float fx = (float)x / (float)pixmap.getWidth();
				float fy = (float)y / (float)pixmap.getHeight();
				float ax = offsetWorldX + fx * worldCellScale;
				float ay = offsetWorldY + fy * worldCellScale;
				float altitude = generator.getAltitude(ax, ay);
				float altitudeNor = (altitude / generator.scale + 1) * .5f;
				
				float flora = generator.getFlora(ax, ay);
				float floraNor = (flora + 1) * .5f;
				
				// generator.getWaterCurrent(flow, ax, ay).scl(.5f / generator.waterFlowForce);
				float flowRate = generator.getWaterCurrent(flow, ax, ay).scl(1.f / generator.waterFlowForce).len() * 2 - 1;
				
				pixmap.drawPixel(x, y, Color.rgba8888(altitudeNor, floraNor, flowRate, 0));
			}
		}
		//cdc.data.infoTexture.load(new PixmapTextureData(pixmap, null, false, false));
		cdc.data.infoTexture.draw(pixmap, 0, 0);
		
		// TODO set filter at creation time
		cdc.data.infoTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
	}
}
