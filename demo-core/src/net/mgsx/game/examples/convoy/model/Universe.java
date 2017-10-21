package net.mgsx.game.examples.convoy.model;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;

import net.mgsx.game.examples.convoy.components.Conveyor;
import net.mgsx.game.examples.convoy.components.Docked;
import net.mgsx.game.examples.convoy.components.Planet;

public class Universe {
	
	static final int SIZE = 5;
	static final float CELL_SCALE = 15;
	
	static final float GOODS_PER_SIZE = 0.3f;
	
	
	public static Universe init(Engine engine){
		
		
		Planet[] planets = new Planet[SIZE * SIZE];
		
		int count=0;
		for(int y=0 ; y<SIZE; y++){
			for(int x=0 ; x<SIZE; x++){
				
				Entity entity = engine.createEntity();
				
				Planet planet = engine.createComponent(Planet.class);
				planet.name = "Planet " + (count+1);
				planet.position.x = x * CELL_SCALE + MathUtils.random(CELL_SCALE * 4f/5f);
				planet.position.y = y * CELL_SCALE + MathUtils.random(CELL_SCALE * 4f/5f);
				planet.logicSize = MathUtils.random();
				planet.radius = MathUtils.lerp(1f, CELL_SCALE/5, planet.logicSize);
				
				entity.add(planet);
				
				engine.addEntity(entity);
				
				planets[count] = planet;
				
				count++;
			}
		}
		
		// distribute gaz
		int nStations = MathUtils.ceil(SIZE * SIZE / 4);
		for(int i=0  ; i<nStations ; i++){
			
			planets[MathUtils.random(planets.length-1)].gazAvailability += 1;
			
		}
		
		for(int i=0 ; i<planets.length ; i++)
		{
			Planet planet = planets[i];
			
			for(MaterialType type : MaterialType.ALL){
				MaterialInfo info;
				planet.materials.put(type, info = new MaterialInfo());
				
				// TODO balance normalized over the universe to always have needs and offers.
				float balance = MathUtils.random() + planet.logicSize - 1;
				
				info.stock = MathUtils.floor(balance * 10);
				// info.target = info.stock;
				
//				Goods goods = new Goods();
//				goods.type = type;
//				
//				
//				
//				
//				int destinationID = MathUtils.random(0, planets.length-2);
//				if(destinationID == i) destinationID = planets.length-1;
//				
//				goods.destination = planets[destinationID];
//				
//				float distance = planet.position.dst(goods.destination.position);
//				
//				goods.buyValue = 100;
//				goods.sellValue = (int)((MathUtils.random() + 1) * distance * 100 );
//				
//				planet.goods.add(goods);
			}
			
			int nGoods = (int)(planet.radius * planet.radius / (CELL_SCALE * CELL_SCALE* GOODS_PER_SIZE));
			for(int g=0 ; g<nGoods ; g++){
				
				Goods goods = new Goods();
				
				
				int destinationID = MathUtils.random(0, planets.length-2);
				if(destinationID == i) destinationID = planets.length-1;
				
				goods.destination = planets[destinationID];
				
				float distance = planet.position.dst(goods.destination.position);
				
				goods.buyValue = 100;
				goods.sellValue = (int)((MathUtils.random() + 1) * distance * 100 );
				
				planet.goods.add(goods);
				
			}
		}
		
		int totalPos = 0, totalNeg = 0;
		for(int i=0 ; i<planets.length ; i++)
		{
			Planet planet = planets[i];
			for(MaterialType type : MaterialType.ALL){
				if(planet.materials.get(type).stock > 0)
					totalPos += planet.materials.get(type).stock;
				else
					totalNeg -= planet.materials.get(type).stock;
			}
		}
		
		int offset = (totalNeg - totalPos) / (planets.length * 4);
		for(int i=0 ; i<planets.length ; i++)
		{
			Planet planet = planets[i];
			for(MaterialType type : MaterialType.ALL){
				planet.materials.get(type).stock += offset;
			}
		}
		
//		int resourcesDelta = SIZE * SIZE * 4;
//		while(resourcesDelta > 0){
//			Planet planet = planets[MathUtils.random(planets.length-1)];
//			MaterialInfo info = planet.materials.get(MaterialType.ALL[MathUtils.random(MaterialType.ALL.length-1)]);
//			info.stock += MathUt;
//			resourcesDelta -= 1;
//			info.target = info.stock;
//		}
		
		initConveyor(engine, null);
		
		return new Universe();
	}

	public static void initConveyor(Engine engine, Entity planetEntity) {

		Entity conveyorEntity = engine.createEntity();
		Conveyor conveyor = engine.createComponent(Conveyor.class);
		
		conveyor.position.setZero();
		conveyor.capacity = 10;
		conveyor.oil = conveyor.oilMax = SIZE * CELL_SCALE * 2; // enough for 2x universe size
		
		conveyor.storageCapacity = 10;
		
		for(MaterialType type : MaterialType.ALL){
			conveyor.materials.put(type, 0f);
		}
		
		conveyorEntity.add(conveyor);
		
		if(planetEntity != null){
			Planet planet = Planet.components.get(planetEntity);
			conveyor.position.set(planet.position);
			Docked d = engine.createComponent(Docked.class);
			d.planet = planetEntity;
			conveyorEntity.add(d);
		}
		
		engine.addEntity(conveyorEntity);
	}
	
	public float playerMoney = 1000;
	
}
