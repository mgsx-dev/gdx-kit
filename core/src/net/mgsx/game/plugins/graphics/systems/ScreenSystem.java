package net.mgsx.game.plugins.graphics.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.mgsx.game.core.screen.ScreenManager;
import net.mgsx.game.core.screen.TransitionDesc;

/**
 * Wrapper to the root screen manager (typically the game application)
 * 
 * @author mgsx
 *
 */
public class ScreenSystem extends EntitySystem
{
	private final ScreenManager screenManager;
	public ScreenSystem(ScreenManager screenManager) 
	{
		this.screenManager = screenManager;
	}

	public EventListener trigger(final TransitionDesc...transitions) {
		return new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				screenManager.setTransition(transitions[0]);
				for(int i=1 ; i<transitions.length ; i++)
					screenManager.addTransition(transitions[i]);					
			}
		};
	}
}
