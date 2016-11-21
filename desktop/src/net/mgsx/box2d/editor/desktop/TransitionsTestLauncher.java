package net.mgsx.box2d.editor.desktop;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.ClasspathFileHandleResolver;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.helpers.AssetHelper;
import net.mgsx.game.core.screen.ScreenTransition;
import net.mgsx.game.core.screen.StageScreen;
import net.mgsx.game.core.screen.TransitionDesc;
import net.mgsx.game.core.screen.TransitionScreen;
import net.mgsx.game.core.screen.Transitions;

public class TransitionsTestLauncher 
{
	private static class TransitionsTestScreen extends StageScreen
	{
		private TransitionScreen ts;
		private TransitionDesc desc;
		
		public TransitionsTestScreen(Skin skin) {
			super(skin);
			
			ts = new TransitionScreen();
			desc = new TransitionDesc();
			desc.duration = 4;
			desc.interpolation = Interpolation.linear;
			desc.destination = Transitions.empty(Color.WHITE);
			ts.source = Transitions.empty(Color.BLACK);
			ts.setDesc(desc);
			
			Array<ScreenTransition> transitions = new Array<ScreenTransition>();
			
			transitions.add(Transitions.fadeTransition());
			// TODO add more transitions
			
			final SelectBox<ScreenTransition> transitionSelector = new SelectBox<ScreenTransition>(skin);
			transitionSelector.setItems(transitions);
			transitionSelector.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					setTransition(transitionSelector.getSelected());
				}
			});
			
			
			final TextButton btReplay = new TextButton("Play", skin);
			btReplay.addListener(new ChangeListener(){
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					ts.reset();
				}
			});
			
			setTransition(transitions.first());
			
			Table mainTable = new Table(skin);
			
			mainTable.add("Transition");
			mainTable.add(transitionSelector);
			mainTable.add(btReplay);
			
			Table screenTable = new Table(skin);
			screenTable.add(mainTable).expand().top().left();
			screenTable.setFillParent(true);
			stage.addActor(screenTable);
		}

		protected void setTransition(ScreenTransition transition) 
		{
			desc.transition = transition;
			transition.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		}
		
		@Override
		public void render(float deltaTime) {
			ts.render(deltaTime);
			super.render(deltaTime);
		}
		
		@Override
		public void resize(int width, int height) {
			ts.resize(width, height);
			super.resize(width, height);
		}
		
	}
	
	private static class TransitionsTestApplication extends Game
	{
		private AssetManager assets;
		@Override
		public void create() 
		{
			assets = new AssetManager(new ClasspathFileHandleResolver());
			Skin skin = AssetHelper.loadAssetNow(assets, "data/uiskin.json", Skin.class);
			setScreen(new TransitionsTestScreen(skin));
		}
	}
	
	public static void main(String[] args) 
	{
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new TransitionsTestApplication(), config);
	}
}
