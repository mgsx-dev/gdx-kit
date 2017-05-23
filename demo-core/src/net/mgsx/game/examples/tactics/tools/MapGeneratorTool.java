package net.mgsx.game.examples.tactics.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultConnection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.ai.pfa.PathFinder;
import com.badlogic.gdx.ai.pfa.indexed.DefaultIndexedGraph;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.ai.pfa.indexed.IndexedNode;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.Asset;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EnumType;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.core.ui.widgets.IntegerWidget;
import net.mgsx.game.examples.tactics.systems.TacticMapHUD;
import net.mgsx.game.examples.tactics.util.Perlin3D;
import net.mgsx.game.examples.tactics.util.TiledMapHelper;
import net.mgsx.game.examples.tactics.util.Voronoi2D;
import net.mgsx.game.examples.tactics.util.Voronoi2D.VoronoiResult;
import net.mgsx.game.plugins.editor.systems.DebugRenderSystem;

public class MapGeneratorTool extends Tool
{
	@Inject
	public TacticMapHUD hud;
	
	@Editable(type=EnumType.RANDOM, editor=IntegerWidget.class)
	public long seed = MathUtils.random(Long.MAX_VALUE);
	
	@Asset("tactics/base.tmx")
	public TiledMap tileTemplate;
	
	@Editable public int tileHeight = 32;
	@Editable public int tileWidth = 32;
	@Editable public int height = 32;
	@Editable public int width = 32;
	@Editable public long xOffset = 0;
	@Editable public long yOffset = 0;
	
	OrthogonalTiledMapRenderer renderer;
	TiledMap map;
	
	@Inject
	public DebugRenderSystem renderSystem;

	@Editable
	public float mapFreq = .2f;
	@Editable
	public float islandFreq = .2f;
	@Editable
	public float timeScale = .1f;
	@Editable
	public float waterRate = .67f;
	@Editable
	public float seaRate = .75f;
	
	@Editable
	public float rndRate = 1;
	@Editable
	public float opacity = .33f;

	@Editable
	public float zoneOpacity = .05f;

	@Editable
	public float timeOpacity = .05f;

	@Editable
	public float levelFreq = .2f;
	
	@Editable
	public float zoneFreq = .2f;
	
	@Editable
	public float innerDotsFreq = .3f;
	
	@Editable
	public float villageRate = .3f;
	
	@Editable
	public float missionRate = .3f;
	
	@Editable
	public float bonusRate = .3f;
	
	@Editable
	public float scale = .5f;
	
	@Editable
	public float time = 0;
	
	@Editable
	public boolean genMissions = false;
	
	private Perlin3D perlin = new Perlin3D();
	
	private WorldMap worldMap = new WorldMap();
	
	@Editable
	public void moveLeft(){
		xOffset -= 8;
		generate();
	}
	
	@Editable
	public void moveRight(){
		xOffset += 8;
		generate();
	}
	
	@Editable
	public void moveTop(){
		yOffset += 8;
		generate();
	}
	
	@Editable
	public void moveDown(){
		yOffset -= 8;
		generate();
	}
	
	public static class Mission
	{
		public GraphPath<Connection<Node>> path;
		public float difficulty;
		public Color color;
		public float[] vertices;
	}
	
	private static Array<Connection<Node>> cache = new Array<Connection<Node>>();
	public static class Node implements IndexedNode<Node>
	{
		public int id;
		public int x, y;
		private Array<Connection<Node>> cnx = new Array<Connection<Node>>();
		public TiledMapTile tile;
		public float difficulty;
		public int ax, ay;
		public boolean isSea;
		public boolean isCity, isMission;
		public boolean isDiscovered;
		public Node(TiledMapTile tile, int id, int x, int y, float difficulty) {
			this.tile = tile;
			this.id = id;
			this.x = x;
			this.y = y;
			this.difficulty = difficulty;
		}

		@Override
		public int getIndex() {
			return id;
		}

		@Override
		public Array<Connection<Node>> getConnections() {
			return cnx;
		}
		
	}
	
	public MapGeneratorTool(EditorScreen editor) {
		super(editor);
	}
	
	@Override
	protected void activate() 
	{
		super.activate();
		renderer = new OrthogonalTiledMapRenderer(null, 1f / 32f);
		if(tileTemplate == null){
			tileTemplate = new TmxMapLoader().load("tactics/base.tmx");
		}
		if(map == null)
			map = new TiledMap();
	}
	
	@Editable
	public void generateHero()
	{
		seed = (long)(Math.random() * Long.MAX_VALUE);
		RandomXS128 random = new RandomXS128(seed);
		worldMap.originX = worldMap.heroX = random.nextInt() % 32768;
		worldMap.originY = worldMap.heroY = random.nextInt() % 32768;
		
		xOffset = worldMap.heroX - width/2;
		yOffset = worldMap.heroY - height/2;
		
		generate();
	}

	@Editable
	public void generate()
	{
		worldMap.seed = seed;
				
		// XXX assume first tileset
		TiledMapTileSet tileset = tileTemplate.getTileSets().getTileSet(0);
		ObjectMap<String, TiledMapTile> tilesByName = TiledMapHelper.groupBy(tileset, "name");
		
		TiledMapTileLayer layer = new TiledMapTileLayer(width, height, tileWidth, tileHeight);
		TiledMapTileLayer layerDifficulty = new TiledMapTileLayer(width, height, tileWidth, tileHeight);
		TiledMapTileLayer layerZone = new TiledMapTileLayer(width, height, tileWidth, tileHeight);
		TiledMapTileLayer layerTime = new TiledMapTileLayer(width, height, tileWidth, tileHeight);
		TiledMapTileLayer layerBonus = new TiledMapTileLayer(width, height, tileWidth, tileHeight);
		TiledMapTileLayer layerDisco = new TiledMapTileLayer(width, height, tileWidth, tileHeight);
		
		while(map.getLayers().getCount() > 0) map.getLayers().remove(0);
		map.getLayers().add(layer);
		map.getLayers().add(layerBonus);
		map.getLayers().add(layerDifficulty);
		map.getLayers().add(layerZone);
		map.getLayers().add(layerTime);
		map.getLayers().add(layerDisco);
		// TODO gen map
		
		build();
		
		// TODO ... get region from a tileset !
		TiledMapTile groundTile = tilesByName.get("forest");
		TiledMapTile waterTile = tilesByName.get("water");
		TiledMapTile sandTile = tilesByName.get("sand");
		TiledMapTile mountainTile = tilesByName.get("mountain");
		TiledMapTile cityTile = tilesByName.get("city");
		TiledMapTile lavaTile = tilesByName.get("lava");
		TiledMapTile water2Tile = tilesByName.get("water2");
		TiledMapTile redTile = tilesByName.get("red");
		TiledMapTile zodiacTile = tilesByName.get("zodiac");
		TiledMapTile missionTile = tilesByName.get("mission");
		TiledMapTile heroTile = tilesByName.get("hero");
		TiledMapTile grayTile = tilesByName.get("gray");
		TiledMapTile bonusTile = tilesByName.get("bonus");
		TiledMapTile discoTile = tilesByName.get("discovered");
		
		// place village
		Node village = null;		
		perlin.setSeed(seed);

		// rasterization
		Array<Node> nodes = new Array<Node>();
		Array<Node> cells = new Array<Node>();
		for(int y=0 ; y<height ; y++)
		{
			float ty = 2 * (float)y / (float)(height - 1) - 1;
			for(int x=0 ; x<width ; x++)
			{
				float tx = 2 * (float)x / (float)(width - 1) - 1;
				
				float d = (float)Math.sqrt(tx*tx+ty*ty);
				d = 0f;
				
				int ax = x + (int)xOffset;
				int ay = y + (int)yOffset;
				
				float rndWater = lookupVoro(ax, ay, mapFreq * scale);
				float rndIsland = lookup(1, ax, ay, islandFreq * scale);
				
//				float difficulty = lookup(2, ax, ay, levelFreq * scale );
				float difficulty = rndWater <= -1 ? .5f : MathUtils.clamp(rndWater * lookupVoroID(ax, ay, mapFreq * scale ) * .7f + lookup(2, ax, ay, 1 )* .3f, 0, 1);
				
				float radius = 10 / scale;
				float dx = (ax - worldMap.originX) / radius;
				float dy = (ay - worldMap.originY) / radius;
				
				float falloff = Math.min(1, (float)Math.sqrt(dx*dx+dy*dy));
				difficulty *= falloff;
				
//				rndWater = (rndWater + rndIsland * .5f) / 1.5f;
//				
				
				if(rndWater >= 0)
					rndWater = (rndWater + Math.abs(rndRate * rndIsland * 2 - 1) * 1.5f)
						* (1 - d) * 2 / 1.5f;
				
				
				boolean isSea = false;
				TiledMapTile tile;
				if(rndWater < waterRate){
					tile = waterTile;
					isSea = true;
				}else if(rndWater < waterRate + .9f){
					tile = water2Tile;
					isSea = true;
				}else if(rndWater < waterRate + 1.1f){
					tile = sandTile;
				}else if(rndWater < waterRate + 2.7f){
					tile = groundTile;
				}else if(rndWater < waterRate + 8f){
					tile = mountainTile;
				}else{
					tile = lavaTile;
				}
				Cell cell = new Cell().setTile(tile);
				layer.setCell(x, y, cell);
				
				// FIXME difficulty should be stricktly positive (for path finding)
				Node node =new Node(tile, y*width+x, x, y, Math.max(1e-30f, difficulty));
				node.ax = ax;
				node.ay = ay;
				node.isSea = isSea;
				
				if(tile == sandTile || tile == groundTile){
					cells.add(node);
				}
				
				int z = (int)(lookupVoroID(ax, ay, zoneFreq * scale ) * 12);
				layerZone.setCell(x, y, new Cell().setTile(tileset.getTile(zodiacTile.getId() + z)));
				
				int id = redTile.getId() + Math.min(9, (int)((1 - difficulty) * 10));
				layerDifficulty.setCell(x, y, new Cell().setTile(tileset.getTile(id)));
				
				nodes.add(node);
				
				voro.setSeed(seed);
				VoronoiResult r = voro.generate(ax * zoneFreq * scale, ay * zoneFreq * scale, 3);
				voro.setSeed(seed);
				VoronoiResult r2 = voro.generate(ax * innerDotsFreq * scale, ay * innerDotsFreq * scale, 3);
				float r2x = r2.x;
				float r2y = r2.y;
				float r2id = r2.id;
				
				
				if(ax == 0 && ay == 0){ // XXX origin
					village = node;
					cell.setTile(cityTile);
				}
				// XXX 
				float threshold = .1f;
				voro.setSeed(seed);
				if(lookupVoro(ax, ay, zoneFreq * scale) > -1 && 
					Math.abs(r2x - ax * innerDotsFreq * scale ) < threshold && 
					Math.abs(r2y - ay * innerDotsFreq * scale ) < threshold){
					
					if(r2id < villageRate){
						village = node;
						// cell.setTile(cityTile);
					}else if(r2id < missionRate){
						// cell.setTile(missionTile);
					}
				}
				
				if(ax == worldMap.heroX && ay == worldMap.heroY){
					cell.setTile(heroTile);
				}
				
				perlin.setSeed(seed);
				float f = perlin.generate(ax * scale * timeScale, ay * scale * timeScale, time);
				float steps = 10;
				int tid = grayTile.getId() + Math.max(0, Math.min(9, (int)((int)((1 - f) * steps)/steps * 10)));
				layerTime.setCell(x, y, new Cell().setTile(tileset.getTile(tid)));
				
				perlin.setSeed(seed);
				if(perlin.generate(ax * scale, ay * scale, 0) < bonusRate)
					layerBonus.setCell(x, y, new Cell().setTile(bonusTile));
			}
		}
		
		int ax = (int)xOffset;
		int ay = (int)yOffset;
		int bx = ax + width;
		int by = ay + height;
		
		for(WorldCell cell : worldMap.discovered){
			int mx = cell.x - (int)xOffset;
			int my = cell.y - (int)yOffset;
			if(mx >= 0 && mx<width && my>=0 && my<height){
				layerDisco.setCell(mx, my, new Cell().setTile(discoTile));
				nodes.get(my*width+mx).isDiscovered = true;
			}
		}
		
		
		
		Array<Voronoi2D.Node> spots = new Array<Voronoi2D.Node>();
		voro.setSeed(seed);
		voro.query(spots, ax * zoneFreq * scale, ay * zoneFreq * scale, bx * zoneFreq * scale, by * zoneFreq * scale);

		for(Voronoi2D.Node spot : spots){
			int ix = MathUtils.floor((spot.x / (zoneFreq * scale) )) - (int)xOffset;
			int iy = MathUtils.floor((spot.y / (zoneFreq * scale) )) - (int)yOffset;
			if(spot.index * 100  - ((int)(spot.index * 100)) < seaRate) continue;
			if(ix>=0 && ix<width && iy>=0 && iy<height){
				Cell cell = layer.getCell(ix, iy);
				Node node = nodes.get(iy * width + ix);
				if(spot.index < villageRate){
					node.isCity = true;
					cell.setTile(cityTile);
//				}else if(spot.index < missionRate){
//					node.isMission = true;
//					cell.setTile(missionTile);
				}else{
					// XXX
					node.isMission = true;
					cell.setTile(missionTile); 
				}
			}
			
		}
		
		
		int[][] m = {{1,0},{0,1},{-1,0},{0,-1}};
		for(int y=0 ; y<height ; y++)
		{
			for(int x=0 ; x<width ; x++)
			{
				final Node current = nodes.get(y * width + x);
				for(int i=0 ; i<4 ; i++){
					int dx = x + m[i][0];
					int dy = y + m[i][1];
					
					if(dx>=0 && dx<width && dy>=0 && dy<height){
						final Node n = nodes.get(dy * width + dx);
						n.cnx.add(new DefaultConnection<Node>(n, current){
							@Override
							public float getCost() {
								return current.difficulty;
							}
						});
						current.cnx.add(new DefaultConnection<Node>(current, n){
							@Override
							public float getCost() {
								return n.difficulty;
							}
						});
					}
				}
			}
		}

		
		IndexedGraph<Node> graph = new DefaultIndexedGraph<Node>(nodes);
		PathFinder<Node> pf = new IndexedAStarPathFinder<Node>(graph);
		village = null;
		int hx = worldMap.heroX -(int)xOffset;
		int hy = worldMap.heroY -(int)yOffset;
		if(hx>=0 && hx<width && hy>=0 && hy<height)
			village = nodes.get(hy * width+hx);
		
		
		// update hud :
		if(village != null){
			Node current = village;
			
			hud.setCellInfo(worldMap, current);
		}
		
		
		
		
		missions.clear();
		if(!genMissions) return;
		
		for(Voronoi2D.Node spot : spots){
			int ix = MathUtils.floor((spot.x / (zoneFreq * scale) )) - (int)xOffset;
			int iy = MathUtils.floor((spot.y / (zoneFreq * scale) )) - (int)yOffset;
			if(ix<0 || ix>=width || iy<0 || iy>=height) continue;
			if(spot.index * 100  - ((int)(spot.index * 100)) < seaRate) continue;
			
			Node hot = nodes.get(iy * width+ix);
			
			if(hot != null && village != null){
				if(hot.isDiscovered) continue;
				GraphPath<Connection<Node>> outPath = new DefaultGraphPath<Connection<Node>>();
				Heuristic<Node> distanceHeuristic = new Heuristic<MapGeneratorTool.Node>() {
					@Override
					public float estimate(Node node, Node endNode) {
						float dx = Math.abs(endNode.x - node.x);
						float dy = Math.abs(endNode.y - node.y);
						return (dx*dx+dy*dy) * 0.001f; // XXX
					}
				};
				if(pf.searchConnectionPath(village, hot, distanceHeuristic , outPath)){
					Mission mission = new Mission();
					mission.color = new Color(hot.difficulty / 10, 1 - hot.difficulty / 10, 0, 1);
					mission.difficulty = hot.difficulty;
					mission.path = outPath;
					mission.vertices = new float [(outPath.getCount()+1)*2];
					int v = 0;
					float s = 1;
					mission.vertices[v++] = (village.x + .5f) * s;
					mission.vertices[v++] = (village.y + .5f) * s;
					for(Connection<Node> cnx : outPath){
						float x = cnx.getToNode().x;
						float y = cnx.getToNode().y;
						mission.vertices[v++] = (x + .5f) * s;
						mission.vertices[v++] = (y + .5f) * s;
					}
					missions.add(mission);
				}
			}
		}

	}
	
	private Voronoi2D voro = new Voronoi2D();
	
	private float lookupVoro(int x, int y, float freq) 
	{
		voro.setSeed(seed);
		VoronoiResult r = voro.generate(x * freq, y * freq, 3);
		if(r.id * 100  - ((int)(r.id * 100)) < seaRate) return -1f;
//		return Math.min(.5f, r.f[0]); //r.f[1] - r.f[0];
		return (r.f[1]-r.f[0]) * 4;
	}
	private float lookupVoroID(int x, int y, float freq) 
	{
		voro.setSeed(seed);
		VoronoiResult r = voro.generate(x * freq, y * freq, 3);
		return r.id; //r.f[1] - r.f[0];
	}

	@Override
	public void render(Batch batch) 
	{
		batch.end();
	
		if(map.getLayers().getCount() >= 2){
			TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get(1);
			layer.setOpacity(MathUtils.clamp(.1f, 0, 1));
		}
		if(map.getLayers().getCount() >= 3){
			TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get(2);
			layer.setOpacity(MathUtils.clamp(opacity, 0, 1));
		}
		if(map.getLayers().getCount() >= 4){
			TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get(3);
			layer.setOpacity(MathUtils.clamp(zoneOpacity, 0, 1));
		}
		if(map.getLayers().getCount() >= 5){
			TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get(4);
			layer.setOpacity(MathUtils.clamp(timeOpacity, 0, 1));
		}
		
		renderer.setMap(map);
		renderer.setView(editor.getGameCamera().combined, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		renderer.getBatch().setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		renderer.render();
		
		if(map.getLayers().getCount() >= 6){
			renderer.getBatch().enableBlending();
			renderer.getBatch().setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
			renderer.getBatch().begin();
			renderer.renderTileLayer((TiledMapTileLayer)map.getLayers().get(4));
			renderer.getBatch().flush();
			renderer.getBatch().end();
		}
		
		batch.begin();
	}
	
	private Array<Mission> missions = new Array<Mission>();
	
	@Override
	public void render(ShapeRenderer renderer) 
	{
		renderer.begin(ShapeType.Line);
		for(Mission mission : missions){
			if(mission.vertices.length < 4) continue;
			renderer.setColor(mission.color);
			renderer.polyline(mission.vertices);
		}
		
		renderer.end();
	}
	
	private float[][][] tables;
	private int layers = 8;
	
	private void build() {
		RandomXS128 random = new RandomXS128(seed);
		
		tables = new float[height][][];
		for(int y=0 ; y<height ; y++)
		{
			float[][] cols = tables[y] = new float[width][];
			for(int x=0 ; x<width ; x++)
			{
				// locate seed on 2D plane
				random.setSeed(seed + y);
				random.setSeed(random.nextInt() + x);
				
				float[] cell  = cols[x] = new float[layers];
				
				for(int i=0 ; i<layers ; i++){
					cell[i] = random.nextFloat();
				}
				
			}
		}
	}

	protected float lookup(int i, int x, int y) 
	{
		return tables[y][x][i];
	}
	protected float lookup(int i, float fx, float fy, float frequency) 
	{
		float ax = fx * frequency;
		int x = MathUtils.floor(ax);
		float tx = ax - x;
		float ay = fy * frequency;
		int y = MathUtils.floor(ay);
		float ty = ay - y;
		
		int ix1 = ((x%width)+width)%width;
		int ix2 = (((x+1)%width)+width)%width;
		int iy1 = ((y%height)+height)%height;
		int iy2 = (((y+1)%height)+height)%height;
		
		float a = tables[iy1][ix1][i];
		float b = tables[iy1][ix2][i];
		float c = tables[iy2][ix1][i];
		float d = tables[iy2][ix2][i];
		
		return MathUtils.lerp(MathUtils.lerp(a, b, tx), MathUtils.lerp(c, d, tx), ty);
	}
	
	@Override
	public boolean keyDown(int keycode) {
		
		if(keycode == Input.Keys.LEFT){
			worldMap.heroX--;
		}else if(keycode == Input.Keys.RIGHT){
			worldMap.heroX++;
		}else if(keycode == Input.Keys.UP){
			worldMap.heroY++;
		}else if(keycode == Input.Keys.DOWN){
			worldMap.heroY--;
		}else if(keycode == Input.Keys.SPACE){
			// next turn
		}else{
			return false;
		}
		
		time += .01f;

		if(worldMap.heroX < xOffset + width/4) xOffset--;
		if(worldMap.heroX > xOffset + 3*width/4) xOffset++;
		if(worldMap.heroY < yOffset + height/4) yOffset--;
		if(worldMap.heroY > yOffset + 3*height/4) yOffset++;
		
		generate();
		
		return true;
	}
	
}
