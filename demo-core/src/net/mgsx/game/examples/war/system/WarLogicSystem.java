package net.mgsx.game.examples.war.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.math.OpenSimplexNoise;
import net.mgsx.game.examples.war.components.ZoneComponent;
import net.mgsx.game.examples.war.model.Event;
import net.mgsx.game.examples.war.model.Goods;
import net.mgsx.game.examples.war.model.Player;
import net.mgsx.game.examples.war.model.Player.PlayerGoods;
import net.mgsx.game.examples.war.model.Zone;
import net.mgsx.game.examples.war.model.Zone.ZoneGoods;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

@EditableSystem
public class WarLogicSystem extends EntitySystem
{
	private ImmutableArray<Entity> zones;
	private OpenSimplexNoise noise = new OpenSimplexNoise();
	private Array<Goods> allGoods = new Array<Goods>();
	private int turn;
	private Array<Event> allEvents;
	
	@Editable
	public Player player;
	
	public WarLogicSystem() {
		super(GamePipeline.LOGIC);
		
		allEvents = new Json().fromJson(Array.class, Event.class, Gdx.files.internal("events.json"));
		
		goods("al", 1, 0);
		goods("tt", 10, 0.2f);
		goods("cc", 50, 0.5f);
		
		player = new Player();
		player.goods = new ObjectMap<Goods, PlayerGoods>();
		resetPlayer();
	}
	
	private Goods goods(String name, int basePrice, float rarity){
		Goods goods = new Goods();
		goods.index = allGoods.size;
		goods.name = name;
		goods.basePrice = basePrice;
		goods.rarity = rarity;
		goods.seed = MathUtils.random(0, 1e6f);
		allGoods.add(goods);
		return goods;
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		zones = engine.getEntitiesFor(Family.all(ZoneComponent.class, Transform2DComponent.class).get());
		
		engine.addEntityListener(Family.all(ZoneComponent.class, Transform2DComponent.class).get(), new EntityListener() {

			@Override
			public void entityRemoved(Entity entity) {
			}
			
			@Override
			public void entityAdded(Entity entity) {
				ZoneComponent zone = ZoneComponent.components.get(entity);
				zone.zone = new Zone();
				for(Goods goods : allGoods){
					ZoneGoods zoneGoods = new ZoneGoods();
					zone.zone.goods.put(goods, zoneGoods);
				}
				updateZone(entity);
			}
		});
	}
	
	public void updateZone(Entity entity){
		ZoneComponent zone = ZoneComponent.components.get(entity);
		Transform2DComponent transform = Transform2DComponent.components.get(entity);
		float worldScale = .1f;
		float timeScale = 0.5f;
		for(Entry<Goods, ZoneGoods> entry : zone.zone.goods){
			float noiseOffset = turn * timeScale + entry.key.seed;
			float quantityRate = (float)(noise.eval(transform.position.x * worldScale, transform.position.y * worldScale, noiseOffset) * .5f + .5f);
			if(quantityRate > entry.key.rarity){
				entry.value.quantity = (int)Math.ceil((quantityRate - entry.key.rarity) * 100);
				entry.value.price = (int)Math.ceil((float)entry.key.basePrice * 100f / (float)entry.value.quantity);
			}else{
				entry.value.quantity = 0;
				entry.value.price = -1; // inf
			}
			
		}
	}
	
	public void updateZoneHistory(Entity entity){
		ZoneComponent zone = ZoneComponent.components.get(entity);
		for(Entry<Goods, ZoneGoods> entry : zone.zone.goods){
			entry.value.priceHistory.add(entry.value.price);
		}
	}
	
	@Editable
	public void nextTurn(){
		
		for(Entity entity : zones){
			updateZone(entity);
		}
		for(Entity entity : zones){
			updateZoneHistory(entity);
		}
		
		player.money -= 50;
		
		turn++;
	}
	
	@Editable
	public void resetPlayer(){
		
		for(Goods goods : allGoods)
			player.goods.put(goods, new PlayerGoods());
		player.money = 500;
	}
	
	@Editable
	public void refund(){
		
		if(MathUtils.randomBoolean(1.f / 6.f)){
			// dead
			player.alive = false;
		}else{
			player.money += 1000;
		}
	}
	
	@Editable
	public void event(){
		// pickup random event
		float value = MathUtils.random();
		float scale = 0;
		for(Event e : allEvents){
			scale += e.chance;
		}
		value *= scale;
		Event current = null;
		for(Event e : allEvents){
			current = e;
			value -= e.chance;
			if(value < 0){
				break;
			}
		}
		System.out.println(current.name);
	}
	
	
	@Override
	public void update(float deltaTime) 
	{
		super.update(deltaTime);
	}

	public void buy(Goods goods, ZoneGoods zoneGoods, int quantity) {
		player.goods.get(goods).quantity += quantity;
		player.goods.get(goods).value += zoneGoods.price;
		player.money -= zoneGoods.price;
		zoneGoods.quantity -= quantity;
	}

	public void sell(Goods goods, ZoneGoods zoneGoods, int quantity) {
		player.goods.get(goods).value *= 1 - (1f / (float)player.goods.get(goods).quantity);
		player.goods.get(goods).quantity -= quantity;
		player.money += zoneGoods.price;
	}
}
