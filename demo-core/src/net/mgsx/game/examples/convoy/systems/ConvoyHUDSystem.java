package net.mgsx.game.examples.convoy.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.Kit;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.examples.convoy.components.Conveyor;
import net.mgsx.game.examples.convoy.components.Docked;
import net.mgsx.game.examples.convoy.components.Planet;
import net.mgsx.game.examples.convoy.components.Selected;
import net.mgsx.game.examples.convoy.components.Transit;
import net.mgsx.game.examples.convoy.model.Goods;
import net.mgsx.game.examples.convoy.model.MaterialInfo;
import net.mgsx.game.examples.convoy.model.MaterialType;
import net.mgsx.game.examples.convoy.model.Universe;
import net.mgsx.game.examples.convoy.ui.PlanetUI;
import net.mgsx.game.plugins.core.systems.HUDSystem;

public class ConvoyHUDSystem extends HUDSystem
{
	private Entity currentConveyor, currentPlanet;
	
	// @Inject POVModel pov;
	
	private Skin skin;
	private ImmutableArray<Entity> planets;
	private ImmutableArray<Entity> conveyors;

	private Table planetInfo;

	private Table convInfo;

	private Table playerInfo;

	@Inject
	public Universe universe;

	private Label labelOil;

	private EditorScreen screen;
	
	public ConvoyHUDSystem(EditorScreen screen) {
		super();
		this.screen = screen;
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		planets = getEngine().getEntitiesFor(Family.all(Planet.class).get());
		conveyors = getEngine().getEntitiesFor(Family.all(Conveyor.class).get());
	}
	
	@Override
	public void update(float deltaTime) {
		
		if(currentConveyor != null){
			Conveyor conveyor = Conveyor.components.get(currentConveyor);
			labelOil.setText("Oil : " + conveyor.oil + "/" + conveyor.oilMax);
		}
		
		super.update(deltaTime);
	}
	
	public void updateConveyor(Entity conveyorEntity){
		
		if(conveyorEntity != currentConveyor) return;
		
		convInfo.clear();
		
		Conveyor conveyor = Conveyor.components.get(conveyorEntity);
		convInfo.add("Conveyor : " + conveyor.goods.size + "/" + conveyor.capacity).row();
		
		convInfo.add(labelOil = new Label("Oil : " + conveyor.oil + "/" + conveyor.oilMax, skin)).row();
		
		Docked docking = Docked.components.get(conveyorEntity);
		if(docking != null){
			convInfo.add(createRefillButton(docking.planet)).row();
		}
		
		for(Goods goods : conveyor.goods){
			
			convInfo.add("- " + goods.sellValue + "$ for " + goods.destination.name + " invest: " + goods.buyValue + "$");
			
			convInfo.add(createDumpButton(currentConveyor, goods));
			
			convInfo.row();
		}
		
		convInfo.add("Material:").row();
		for(final MaterialType type : MaterialType.ALL){
			float contains = conveyor.materials.get(type);
			
			Label lb;
			convInfo.add(lb = new Label(type.name + ": " + contains, skin));
			lb.setColor(type.color);
			convInfo.row();
		}
	}
	
	private Actor createRefillButton(Entity planetEntity) {
		Planet planet = Planet.components.get(planetEntity);
		Conveyor convoy = Conveyor.components.get(currentConveyor);
		
		
		
		TextButton bt = new TextButton("", skin);
		
		if(planet.gazAvailability <= 0){
			bt.setText("No Station here");
			bt.setDisabled(true);
			bt.getColor().a = .5f;
		}else{
			final float refillPricePerLiter = 1f / planet.gazAvailability;
			float refillNeeded = convoy.oilMax - convoy.oil;
			final float refillPriceTotal = refillNeeded * refillPricePerLiter;
			bt.setText("Refill for " + refillPriceTotal + "$");
			bt.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					
					Conveyor convoy = Conveyor.components.get(currentConveyor);
					
					float spend = Math.min(universe.playerMoney, refillPriceTotal);
					
					float oil = spend / refillPricePerLiter;
					
					convoy.oil += oil;
					
					universe.playerMoney -= spend;
					
					updatePlayer();
					updateConveyor(currentConveyor);
				}
			});
		}
		
		return bt;
	}

	private Actor createDumpButton(final Entity conveyorEntity, final Goods goods) {
		int dumpValue = Docked.components.has(conveyorEntity) ? goods.buyValue/2 : 0;
		TextButton bt = new TextButton("Dump for " + dumpValue + "$", skin);
		bt.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				
				Conveyor convoy = Conveyor.components.get(conveyorEntity);
				
				Docked docking = Docked.components.get(conveyorEntity);
				
				if(docking != null){
					universe.playerMoney += goods.buyValue/2;
					
					updatePlayer();
				}
				
				convoy.goods.removeValue(goods, true);
				
				updateConveyor(currentConveyor);
			}
		});
		return bt;
	}

	public void updatePlayer(){
		playerInfo.clear();
		playerInfo.add("Money: " + universe.playerMoney + "$").row();
	}
	
	public void updatePlanet(Entity planetEntity){
		if(currentPlanet != planetEntity) return;
		displayPlanet(planetEntity);
	}
	public void displayPlanet(final Entity planetEntity){
		
		if(currentPlanet != null) currentPlanet.remove(Selected.class);
		
		currentPlanet = planetEntity;
		
		if(currentConveyor != null){
			Conveyor conveyor = Conveyor.components.get(currentConveyor);
			conveyor.oilRequired = 0;
		}
		
		planetInfo.clear();
		
		PlanetUI pui = new PlanetUI(skin);
		pui.setPlanet(currentPlanet);
		planetInfo.add(pui).row();
		
		if(planetEntity != null){
			
			currentPlanet.add(getEngine().createComponent(Selected.class));
			
			final Planet planet = Planet.components.get(planetEntity);
			
			if(currentConveyor != null){
				Conveyor conveyor = Conveyor.components.get(currentConveyor);
				conveyor.oilRequired = conveyor.position.dst(planet.position);
			}
			
			
			planetInfo.add(planet.name + " (" + planet.position.x + ", " + planet.position.y + ")").row();
			planetInfo.add("size : " + planet.radius).row();
			planetInfo.add("Gaz : " + (planet.gazAvailability == 0 ? "no" : "" + 1f/planet.gazAvailability + "$/pc")).row();
			planetInfo.add("Goods :").row();
			
			for(Goods goods : planet.goods){
				
				planetInfo.add("- " + goods.buyValue + " => " + goods.sellValue + "$ for " + goods.destination.name);
				
				planetInfo.add(createShipButton(planet, goods));
				
				planetInfo.row();
			}
			
			planetInfo.add("Material :").row();
			for(final MaterialType type : MaterialType.ALL){
				final MaterialInfo info = planet.materials.get(type);
				
				Label lb;
				planetInfo.add(lb = new Label(type.name + ": " + info.stock, skin));
				lb.setColor(type.color);
				
				if(currentConveyor != null){
					final Conveyor convoy = Conveyor.components.get(currentConveyor);
					if(info.stock > 0){
						TextButton bt = new TextButton("buy", skin);
						planetInfo.add(bt);
						bt.addListener(new ChangeListener() {
							@Override
							public void changed(ChangeEvent event, Actor actor) {
								float toBuy = Math.min(1, convoy.storageCapacity - convoy.storageUsed());
								toBuy = Math.min(toBuy, info.stock);
								convoy.materials.put(type, convoy.materials.get(type) + toBuy);
								universe.playerMoney -= MathUtils.lerp(100, 10, info.stock/10) * toBuy;
								info.stock -= toBuy;
								updatePlanet(planetEntity);
								updateConveyor(currentConveyor);
								updatePlayer();
							}
						});
						
					}else if(info.stock < 0){
						TextButton bt = new TextButton("sell", skin);
						planetInfo.add(bt);
						bt.addListener(new ChangeListener() {
							@Override
							public void changed(ChangeEvent event, Actor actor) {
								float toSell = Math.min(1, convoy.materials.get(type));
								toSell = Math.min(toSell, -info.stock);
								convoy.materials.put(type, convoy.materials.get(type) - toSell);
								universe.playerMoney += toSell * MathUtils.lerp(100, 1000, -info.stock/10);
								info.stock += toSell;
								updatePlanet(planetEntity);
								updateConveyor(currentConveyor);
								updatePlayer();
							}
						});
					}
				}
				planetInfo.row();
			}
			
			if(currentConveyor != null){
				Conveyor convoy = Conveyor.components.get(currentConveyor);
				float distance = planet.position.dst(convoy.position);
				planetInfo.add("distance: " + distance + "pc => " + distance + " oil").row();
				planetInfo.add(createButtonMoveTo(planetEntity)).row();
			}else{
				planetInfo.add("select a conveyor").row();
			}
			
			float conveyorPrice = 100; // TODO more
			if(universe.playerMoney >= conveyorPrice){
				TextButton bt = new TextButton("Buy Conveyor for " + conveyorPrice, skin);
				planetInfo.add(bt).row();
				bt.addListener(new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						// TODO check money
						Universe.initConveyor(getEngine(), currentPlanet);
					}
				});
			}
			
		}
		
		
	}
	
	@Override
	protected void create() 
	{
		skin = new Skin(Gdx.files.classpath("uiskin.json"));
		
		Table menu = new Table(skin);
		menu.add("Convoy menu :").row();
		
		planetInfo = new Table(skin);
		convInfo = new Table(skin);
		playerInfo = new Table(skin);
		
		updatePlayer();
		
		menu.add(playerInfo).row();
		
		menu.add(convInfo).row();
		
		menu.add(planetInfo).row();
		
		Table root = new Table();
		
		root.add().expand();
		
		root.add(menu).expandY().top();

		root.setFillParent(true);
		getStage().addActor(root);
		
		Kit.inputs.addProcessor(new InputAdapter(){
			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				Vector2 p = Tool.unproject(new Vector2(), screen.getGameCamera(), screenX, screenY);
				
				Entity selectedConveyor = null;
				for(Entity e : conveyors){
					Conveyor conveyor = Conveyor.components.get(e);
					if(conveyor.position.dst2(p) < .5f * .5f){
						selectedConveyor = e;
						break;
					}
				}
				
				Entity selectedPlanet = null;
				if(selectedConveyor == null){
					for(Entity e : planets){
						Planet planet = Planet.components.get(e);
						if(planet.position.dst2(p) < planet.radius * planet.radius){
							selectedPlanet = e;
							break;
						}
					}
				}else{
					Docked docked = Docked.components.get(selectedConveyor);
					if(docked != null){
						selectedPlanet = docked.planet;
					}
				}
				
				displayPlanet(selectedPlanet);
				
				
				if(selectedConveyor != null){
					if(currentConveyor != null) currentConveyor.remove(Selected.class);
					currentConveyor = selectedConveyor;
					currentConveyor.add(getEngine().createComponent(Selected.class));
					
					updateConveyor(selectedConveyor);
					
					
				}
				
				
				return false;
			}
		});
	}

	protected Actor createShipButton(final Planet planet, final Goods goods) 
	{
		TextButton bt = new TextButton("Ship", skin);
		bt.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				
				if(currentConveyor != null){
					Conveyor convoy = Conveyor.components.get(currentConveyor);
					if(convoy.capacity > convoy.goods.size && goods.buyValue <= universe.playerMoney){
						planet.goods.removeValue(goods, true);
						convoy.goods.add(goods);
						universe.playerMoney -= goods.buyValue;
						updateConveyor(currentConveyor);
						updatePlanet(currentPlanet);
						updatePlayer();
					}
				}
				
			}
		});
		return bt;
	}

	protected Actor createButtonMoveTo(final Entity selectedPlanet) 
	{
		Planet planet = Planet.components.get(selectedPlanet);
		TextButton bt = new TextButton("Move to " + planet.name, skin);
		bt.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				
				Conveyor convoy = Conveyor.components.get(currentConveyor);
				
				Transit transit = getEngine().createComponent(Transit.class);
				transit.origin.set(convoy.position);
				transit.target = selectedPlanet;
				transit.speed = 1;
				transit.t = 0;
				currentConveyor.add(transit);
				
				updateConveyor(currentConveyor);
			}
		});
		return bt;
	}
}
