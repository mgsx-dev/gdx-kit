package net.mgsx.game.examples.openworld.ui;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import net.mgsx.game.examples.openworld.systems.OpenWorldEnvSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldGameSystem;

public class StatusView extends Table
{
	private OpenWorldGameSystem gameSystem;
	private OpenWorldEnvSystem envSystem;
	
	private Label energyLabel;
	private Label lifeLabel;
	private Label oxygenLabel;
	private Label temperatureLabel;
	
	public StatusView(Skin skin, Engine engine) {
		super(skin);
		gameSystem = engine.getSystem(OpenWorldGameSystem.class);
		envSystem = engine.getSystem(OpenWorldEnvSystem.class);
		
		energyLabel = new Label("", skin);
		lifeLabel = new Label("", skin);
		oxygenLabel = new Label("", skin);
		temperatureLabel = new Label("", skin);
		
		add(lifeLabel);
		add(energyLabel);
		add(oxygenLabel);
		add(temperatureLabel);
	}
	
	@Override
	public void act(float delta) {
		
		// update realtime values
		
		// TODO sync with logic values for some threashold
		
		double energyRate = gameSystem.player.energy / gameSystem.player.energyMax;
		energyLabel.setText("Energy: " + String.format("%.0f", energyRate * 100) + "%");
		if(energyRate <= 0.0) energyLabel.setColor(Color.RED);
		else if(energyRate < 0.5) energyLabel.setColor(Color.ORANGE);
		else if(energyRate >= 0.9) energyLabel.setColor(Color.GREEN);
		else energyLabel.setColor(Color.WHITE);
		
		double lifeRate = gameSystem.player.life / gameSystem.player.lifeMax;
		lifeLabel.setText("Life: " + String.format("%.0f", lifeRate * 100) + "%");
		if(lifeRate < 0.3) lifeLabel.setColor(Color.RED);
		else if(lifeRate < 0.7) lifeLabel.setColor(Color.ORANGE);
		else lifeLabel.setColor(Color.WHITE);

		
		double oxygenRate = gameSystem.player.oxygen / gameSystem.player.oxygenMax;
		oxygenLabel.setText("Oxygen: " + String.format("%.0f", oxygenRate * 100) + "%");
		if(oxygenRate < 0.1) oxygenLabel.setColor(Color.RED);
		else if(oxygenRate < 0.5) oxygenLabel.setColor(Color.ORANGE);
		else oxygenLabel.setColor(Color.WHITE);
		
		temperatureLabel.setText("Temperature: " + String.format("%.1f", gameSystem.player.temperature) + "°");
		if( gameSystem.player.temperature < gameSystem.player.temperatureMin ||
			gameSystem.player.temperature > gameSystem.player.temperatureMax)
			temperatureLabel.setColor(Color.RED);
		else if(envSystem.temperature < gameSystem.player.temperatureMin ||
				envSystem.temperature > gameSystem.player.temperatureMax) 
			temperatureLabel.setColor(Color.ORANGE);
		else 
			temperatureLabel.setColor(Color.WHITE);
		
		super.act(delta);
	}
	
}
