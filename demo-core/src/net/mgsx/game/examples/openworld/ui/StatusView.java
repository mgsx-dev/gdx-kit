package net.mgsx.game.examples.openworld.ui;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import net.mgsx.game.examples.openworld.systems.OpenWorldGameSystem;

public class StatusView extends Table
{
	private OpenWorldGameSystem gameSystem;
	private Label energyLabel;
	private Label lifeLabel;
	private Label oxygenLabel;
	private Label temperatureLabel;
	
	public StatusView(Skin skin, Engine engine) {
		super(skin);
		gameSystem = engine.getSystem(OpenWorldGameSystem.class);
		
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
		
		double energyRate = gameSystem.player.energy / gameSystem.player.energyMax;
		energyLabel.setText("Energy: " + String.format("%.0f", energyRate * 100) + "%");
		
		double lifeRate = gameSystem.player.life / gameSystem.player.lifeMax;
		lifeLabel.setText("Life: " + String.format("%.0f", lifeRate * 100) + "%");
		
		double oxygenRate = gameSystem.player.oxygen / gameSystem.player.oxygenMax;
		oxygenLabel.setText("Oxygen: " + String.format("%.0f", oxygenRate * 100) + "%");
		
		temperatureLabel.setText("Temperature: " + String.format("%.1f", gameSystem.player.temperature) + "°");
		
		super.act(delta);
	}
	
}
