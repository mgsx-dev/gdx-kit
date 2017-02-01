package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.mgsx.game.core.GameScreen;
import net.mgsx.game.examples.td.actions.AbstractTileAction;
import net.mgsx.game.examples.td.actions.DeleteAction;
import net.mgsx.game.examples.td.actions.PlatformAction;
import net.mgsx.game.examples.td.actions.RoadAction;
import net.mgsx.game.plugins.core.systems.HUDSystem;

public class TowerDefenseHUD extends HUDSystem
{
	private GameScreen game;
	private Skin skin;
	private AbstractTileAction currentTileAction;
	private Entity selectedCell;
	private ButtonGroup<Button> tileActionGroup;
	
	public TowerDefenseHUD(GameScreen game) 
	{
		this.game = game;
	}
	
	@Override
	public void addedToEngine(Engine engine) 
	{
		super.addedToEngine(engine);
		
		buildHUD();
	}
	
	private void buildHUD()
	{
		tileActionGroup = new ButtonGroup<Button>();
		tileActionGroup.setMinCheckCount(0);
		tileActionGroup.setMaxCheckCount(1);
		
		// TODO get td skin from assets instead of default editor skin
		skin = new Skin(Gdx.files.classpath("uiskin.json"));
		
		
		// creates the HUD
		// for now it's just a column of buttons docked on the right.
		Table root = new Table(skin);
		root.setFillParent(true);
		getStage().addActor(root);
		
		VerticalGroup group = new VerticalGroup();
		
		addActions(group);

		Table gameZone = new Table();
		root.add(gameZone).expand().fill();
		root.add(new ScrollPane(group, skin)).expandY().top();
		
		gameZone.setTouchable(Touchable.enabled);
		gameZone.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float sx, float sy) 
			{
				// convert stage to screen coordinates
				Vector2 v = getStage().stageToScreenCoordinates(new Vector2(sx, sy));
				// convert screen to world coordinates
				float z = game.camera.project(new Vector3()).z;
				Vector3 worldPoint = game.camera.unproject(new Vector3(v.x,v.y,z));
				// convert world to map coordinates
				int x = MathUtils.floor(worldPoint.x);
				int y = MathUtils.floor(worldPoint.y);
				MapSystem map = getEngine().getSystem(MapSystem.class);
				if(currentTileAction != null){
					currentTileAction.apply(x, y);
				}else{
					selectedCell = map.getTile(x, y);
				}
			}
		});
	}

	private void addActions(VerticalGroup group) 
	{
		// tiles actions
		group.addActor(addTileAction("Delete", new DeleteAction(getEngine())));
		group.addActor(addTileAction("Road", new RoadAction(getEngine())));
		group.addActor(addTileAction("Platform", new PlatformAction(getEngine())));
		
		// edit tile action
		
	}

	private Actor addTileAction(String label, final AbstractTileAction action) 
	{
		final TextButton button = new TextButton(label, skin, "toggle");
		button.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(button.isChecked()){
					currentTileAction = action;
				}
			}
		});
		tileActionGroup.add(button);
		return button;
	}
}
