package net.mgsx.game.examples.openworld.utils;

/**
 * Virtual grid maintain a grid of cells in an infinite world.
 * Cells creations/destructions are delegated to subclass.
 * 
 * calling {@link #update(float, float)} will update the grid relative to a central point
 * (typically a ame camera). It ensures several things :
 * <ul>
 * <li>all cells in the inner range are created.</li>
 * <li>all cells out the outer range are deleted.</li>
 * <li>cells in the intermediate range may or not be created</li>
 * </ul>
 * 
 * The two range feature allow caching some cells at an intermediate range but also
 * could be used for logical cache.
 * 
 * Following use case illustrate it :
 * <ul>
 * <li>inner range is the visible range so cells have to be created before render anything.</li>
 * <li>first intermediate range keep some previously created entities and update the logic.</li>
 * <li>second intermediate range keep some previously created entities but don't update the logic.</li>
 * <li>outer range doesn't keep anything any entities are just garbaged.</li>
 * </ul>
 * 
 * Intermediate ranges are interpreted by subclass, when calling to update on cell, both logical 
 * distance and real distance are given to client code for range evaluation.
 * 
 * Even inner cells are updated and could be used for LOD evaluation.
 * 
 * @author mgsx
 *
 * @param <T>
 */
abstract public class VirtualGrid<T>
{
	private int logicWidth = 0, logicHeight = 0, marginWidth = 0, marginHeight = 0;
	
	public float worldCellScale = 1;
	private int logicOffsetX = 0, logicOffsetY = 0;
	private T [] grid = null;
	private T [] oldGrid = null;
	private boolean sizeChanged;
	
	public VirtualGrid() {
	}
	
	public void resize(int innerWidth, int innerHeight, int marginWidth, int marginHeight, float worldCellScale)
	{
		if(innerWidth <= 0 || innerHeight <= 0 || marginWidth < 0 || marginHeight < 0 || worldCellScale <= 0){
			throw new IllegalArgumentException();
		}
		int width = innerWidth + marginWidth*2;
		int height = innerHeight + marginHeight*2;
		if(grid != null) clear();
		// needs to resize tables ?
		if(grid == null || oldGrid == null || width != logicWidth || height != logicHeight){
			int arraySize = width * height;
			grid = (T[])new Object[arraySize];
			oldGrid = (T[])new Object[arraySize];
			logicWidth = width;
			logicHeight = height;
		}
		this.worldCellScale = worldCellScale;
		this.marginWidth = marginWidth;
		this.marginHeight = marginHeight;
		sizeChanged = true;
	}
	
	abstract protected void dispose(T cell);

	abstract protected T create(float worldX, float worldY);
	abstract protected void update(T cell, int distance);

	abstract protected void enter(T cell);
	abstract protected void exit(T cell);

	/**
	 * Update virtual grid relative to view point center coordinate.
	 * This will ensure cells around this point will be created/activated.
	 * @param centerWorldX
	 * @param centerWorldY
	 */
	public void update(float centerWorldX, float centerWorldY)
	{
		int logicCenterX = (int)Math.floor(centerWorldX / worldCellScale);
		int logicCenterY = (int)Math.floor(centerWorldY / worldCellScale);
		
		int newLogicOffsetX = logicCenterX - logicWidth / 2;
		int newLogicOffsetY = logicCenterY - logicHeight / 2;
		
		// remove out entities and/or components
		
		// copy old lands to new lands
		
		// swap tables
		T [] tmpLands = grid;
		grid = oldGrid;
		oldGrid = tmpLands;
		// reindex
		for(int y=0 ; y<logicHeight ; y++) {
			for(int x=0 ; x<logicWidth ; x++) {
				int index = y * logicWidth + x;
				T cell = oldGrid[index];
				
				int newX = (logicOffsetX + x) - newLogicOffsetX;
				int newY = (logicOffsetY + y) - newLogicOffsetY;
				
				// this cell has a target in the new table, let's copy it
				if(newX>=0 && newX<logicWidth && newY>=0 && newY<logicHeight) {
					
					// the new target is inside inner range
					if(isInside(newX, newY)){
						
						// the cell doesn't exists yet, let's create it
						if(cell == null){
							cell = create(worldCellScale * (newLogicOffsetX + newX), worldCellScale * (newLogicOffsetY + newY));
							enter(cell);
						}
						// the cell already exists, but was outside
						else if(!isInside(x, y)){
							enter(cell);
						}
						// otherwise the cell already exists and still in range.
					}
					// the new target is outside inner range
					else{
						if(cell != null){
							// it was in the inner range
							if(isInside(x, y)){
								exit(cell);
							}
							// otherwise it still in far range, no need to update
						}
						// otherwise no need to create it.
					}
					
					grid[newY * logicWidth + newX] = cell;
				} 
				// this cell has no target (too far)
				else {
					// let's remove it if exists.
					if(cell != null) {
						dispose(cell);
						cell = null;
					}
					// or create it if in range
					else if(isInside(x, y)){
						cell = grid[index] = create(worldCellScale * (newLogicOffsetX + x), worldCellScale * (newLogicOffsetY + y));
						enter(cell);
					}
				}
				oldGrid[index] = null;
			}
		}
		
		if(logicOffsetX != newLogicOffsetX || logicOffsetY != newLogicOffsetY || sizeChanged){
			for(int y=0 ; y<logicHeight ; y++) {
				for(int x=0 ; x<logicWidth ; x++) {
					int index = y * logicWidth + x;
					T cell = grid[index];
					if(cell != null){
						int distance = Math.max(Math.abs(x-logicWidth/2), Math.abs(y-logicHeight/2));
						update(cell, distance);
					}
				}
			}
		}

		
		logicOffsetX = newLogicOffsetX;
		logicOffsetY = newLogicOffsetY;
		
		sizeChanged = false;
	}
	
	public void shrink(){
		for(int y=0 ; y<logicHeight ; y++) {
			for(int x=0 ; x<logicWidth ; x++) {
				if(!isInside(x, y)){
					int index = y * logicWidth + x;
					T cell = grid[index];
					if(cell != null){
						dispose(cell);
						grid[index] = null;
					}
				}
			}
		}
	}
	
	public void clear(){
		for(int y=0 ; y<logicHeight ; y++) {
			for(int x=0 ; x<logicWidth ; x++) {
				int index = y * logicWidth + x;
				T cell = grid[index];
				if(cell != null){
					dispose(cell);
					grid[index] = null;
				}
			}
		}
	}
	
	private boolean isInside(int x, int y){
		return y >= marginHeight && y <logicHeight-marginHeight && x >= marginWidth && x<logicWidth-marginWidth;
	}

	
	
}
