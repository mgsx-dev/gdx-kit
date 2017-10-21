package net.mgsx.game.examples.convoy.ui;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.mgsx.game.examples.convoy.components.Planet;

public class PlanetUI extends Table
{

	private Entity planetEntity;
	private TextButton btAddGaz;
	
	public PlanetUI(Skin skin) {
		super(skin);
	}
	
	public void setPlanet(Entity planetEntity) 
	{
		this.planetEntity = planetEntity;
		
		clear();
		
		
		// buy gaz station ...
		if(planetEntity != null){
			
			final Planet planet = Planet.components.get(planetEntity);
			
			// add bt add gaz
			TextButton bt = btAddGaz = new TextButton("add gaz", getSkin());
			add(bt).row();
			bt.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					planet.gazAvailability++;
				}
			});
			
		}
		
	}
	
	@Override
	public void act(float delta) {
		
		// TODO generic UI : buttons enabled under condition (interface Condition)
		
		// update all dynamic fields and gui control
		if(planetEntity != null){
			final Planet planet = Planet.components.get(planetEntity);
			boolean canGaz = planet.gazAvailability < 3;
			btAddGaz.setDisabled(!canGaz);
			btAddGaz.getColor().a = canGaz ? 1 : .5f;
		}
		super.act(delta);
	}
	
}
