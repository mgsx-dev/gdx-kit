package net.mgsx.game.core;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.screen.ScreenClip;
import net.mgsx.game.core.screen.ScreenManager;
import net.mgsx.game.core.screen.ScreenTransitionListener;
import net.mgsx.game.core.screen.TransitionDesc;
import net.mgsx.game.core.screen.TransitionScreen;
import net.mgsx.game.core.screen.Transitions;

public abstract class GameApplication extends Game implements ScreenManager
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
		Texture.setAssetManager(assets);
		Gdx.input.setInputProcessor(Kit.inputs);
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
		try
		{
			super.render();
			updateTransitions();
		} 
		catch (Throwable e) 
		{
			Kit.exit(e);
		}
	}
	
	private void updateTransitions()
	{
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

	@Override
	public void setTransition(TransitionDesc desc)
	{
		TransitionScreen ts = new TransitionScreen();
		ts.setDesc(desc);
		
		// case when set transition when current transition is not complete or just complete
		if(screen instanceof TransitionScreen){
			((TransitionScreen) screen).hide();
			screen = ((TransitionScreen) screen).getDestination();
		}
		
		ts.source = screen;
		screen = null;
		sequences.clear();
		setScreen(ts);
	}
	
	@Override
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
	
	@Override
	public void setScreen(Screen screen) 
	{
		if (this.screen instanceof ScreenTransitionListener) {
			((ScreenTransitionListener) this.screen).preHide();
		}
		super.setScreen(screen);
		if (this.screen instanceof ScreenTransitionListener) {
			((ScreenTransitionListener) this.screen).postShow();
		}
	}
	
	@Override
	public void dispose() {
		Kit.exit(null);
		super.dispose();
	}
	
}
