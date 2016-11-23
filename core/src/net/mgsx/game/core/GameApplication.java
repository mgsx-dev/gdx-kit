package net.mgsx.game.core;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.screen.ScreenClip;
import net.mgsx.game.core.screen.TransitionDesc;
import net.mgsx.game.core.screen.TransitionScreen;
import net.mgsx.game.core.screen.Transitions;
import net.mgsx.game.core.storage.EntityGroup;
import net.mgsx.game.core.storage.EntityGroupLoader;

public abstract class GameApplication extends Game
{
	protected AssetManager assets;
	
	private Array<Screen> sequences = new Array<Screen>();
	
	private ScreenClip defaultLoadingScreen;
	
	public GameApplication() {
		super();
	}
	
	@Override
	public void create() {
		assets = new AssetManager();
		assets.setLoader(EntityGroup.class, new EntityGroupLoader(assets.getFileHandleResolver()));
		Texture.setAssetManager(assets);
	}

	/**
	 * set default loading screen on pause/resume sequence.
	 * @param defaultLoadingScreen
	 */
	public void setDefaultLoadingScreen(ScreenClip defaultLoadingScreen) {
		this.defaultLoadingScreen = defaultLoadingScreen;
	}
	
	@Override
	public void render() 
	{
		if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && Gdx.input.isKeyPressed(Input.Keys.ENTER)){
			if(Gdx.graphics.isFullscreen()){
				Gdx.graphics.setWindowedMode(640, 480);
				// TODO Gdx.graphics.setSystemCursor(SystemCursor.Arrow);
			}
			else{
				DisplayMode dm = Gdx.graphics.getDisplayMode();
				Gdx.graphics.setFullscreenMode(dm);
				// TODO Gdx.graphics.setSystemCursor(null);
			}
			
		}
		
		super.render();
		
		// check complete status for clips
		if(screen instanceof ScreenClip)
		{
			ScreenClip clip = (ScreenClip)screen;
			if(clip.isComplete())
			{
				// handle screen switch for transitions (special hide/show workflow)
				if(clip instanceof TransitionScreen)
				{
					// same has setScreen but in this case destination screen
					// is already shown and resized. So we just have to set
					// destination as current screen.
					TransitionScreen ts = (TransitionScreen)clip;
					screen = ts.getDestination();
				}
				else
				{
					if(sequences.size > 0)
					{
						Screen next = sequences.removeIndex(0);
						if(next instanceof TransitionScreen){
							TransitionScreen ts = (TransitionScreen)next;
							ts.source = screen;
							screen = null;
						}
						setScreen(next);
					}
				}
			}
		}
	}
	
	@Override
	public void resume() 
	{
		super.resume();
		if(defaultLoadingScreen == null){
			// if no default loading screen has been configured, we simply
			// force textures to be reloaded in order to correctly display
			// current screen.
			assets.finishLoading();
		}else{
			// pre queue current screen and
			// switch to default loading screen.
			// sequences.insert(0, screen);
			if(screen != defaultLoadingScreen){
				screen = Transitions.queue(defaultLoadingScreen, screen);
			}
		}
	}
	
	public AssetManager getAssets() {
		return assets;
	}

	public void setTransition(TransitionDesc desc)
	{
		TransitionScreen ts = new TransitionScreen();
		ts.setDesc(desc);
		ts.source = screen;
		screen = null;
		setScreen(ts);
	}
	
	public void addTransition(TransitionDesc desc){
		TransitionScreen ts = new TransitionScreen();
		ts.setDesc(desc);
		sequences.add(ts);
	}
	
	/**
	 * add a screen to the queue. Will be active screen when current and all
	 * queued screens have complete.
	 * @param screen
	 */
	public void addScreen(Screen screen){
		sequences.add(screen);
	}
	
}
