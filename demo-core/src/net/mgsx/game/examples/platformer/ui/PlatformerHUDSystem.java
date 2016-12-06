package net.mgsx.game.examples.platformer.ui;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.examples.platformer.PlatformerAssets;
import net.mgsx.game.plugins.core.systems.HUDSystem;

// TODO separate act from draw ? 2 systems and use StageComponent instead !
@EditableSystem
public class PlatformerHUDSystem extends HUDSystem
{
	private final AssetManager assets;
	private Skin skin;
	
	private Label pointsLabel;
	private Label lifeLabel;
	
	private int score;
	private int life;
	
	private Table banner;
	
	public PlatformerHUDSystem(AssetManager assets) 
	{
		this.assets = assets;
		assets.load(PlatformerAssets.skin);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		assets.finishLoadingAsset(PlatformerAssets.skin.fileName);
		skin = assets.get(PlatformerAssets.skin);
		
		Table main = new Table(skin);
		
		pointsLabel = new Label("Points ", skin, "score");
		lifeLabel = new Label("Life ", skin, "score");
		
		banner = new Table(skin);
		banner.setTransform(true);
		
		banner.add(pointsLabel);
		banner.add(lifeLabel).expandX().right();
		main.add(banner).expand().fillX().top();
		
		main.setFillParent(true);
		getStage().addActor(main);
		
		banner.addAction(Actions.visible(false));
		// banner.setVisible(false);
	}
	
	public void updateStats(int points, int life){
		this.score = points;
		this.life = life;
		pointsLabel.setText("Points " + points);
		lifeLabel.setText("Life " + life);
	}
	
	public void displayTitle(String levelName, Runnable callback){
		
		Table table = new Table(skin);
		table.setFillParent(true);
		
		table.add(levelName);
		
		getStage().addActor(table);
		
		table.addAction(Actions.sequence(Actions.delay(2), Actions.alpha(0, 1), Actions.run(callback), Actions.removeActor()));
	}

	@Editable
	public void show() 
	{
		if(!banner.isVisible()){
			banner.addAction(Actions.sequence(
					Actions.moveBy(0, 60),
					Actions.visible(true),
					Actions.moveBy(0, -60, .3f, Interpolation.pow2Out)));
		}
	}
	@Editable
	public void hide() 
	{
		if(banner.isVisible()){
			banner.addAction(Actions.sequence(
					Actions.moveBy(0, 60, .3f, Interpolation.pow2In), 
					Actions.visible(false),
					Actions.moveBy(0, -60)));
		}
	}

	public void addScore(int points) {
		updateStats(points + score, life);
	}
}
