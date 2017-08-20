package net.mgsx.kit.launcher;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.mgsx.game.core.Kit;

/**
 * Override {@link LwjglApplication} in order to catch errors during runnables executions.
 * 
 * @author mgsx
 *
 */
public class DesktopApplication extends LwjglApplication
{
	public DesktopApplication(ApplicationListener listener, LwjglApplicationConfiguration config) 
	{
		super(listener, config);
	}
	
	@Override
	public boolean executeRunnables() {
		try{
			return super.executeRunnables();
		}catch(Throwable e){
			Kit.exit(e);
			return true;
		}
	}
}
