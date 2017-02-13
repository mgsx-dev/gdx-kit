package net.mgsx.kit.launcher;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.utils.GdxRuntimeException;

import net.mgsx.game.core.EditorApplication;

/**
 * Override {@link LwjglApplication} in order to catch errors during runnables executions.
 * 
 * @author mgsx
 *
 */
public class DesktopApplication extends LwjglApplication
{
	protected EditorApplication editor;
	
	public DesktopApplication(EditorApplication editor, LwjglApplicationConfiguration config) 
	{
		super(editor, config);
	}
	
	@Override
	public boolean executeRunnables() {
		try{
			return super.executeRunnables();
		}catch(Throwable e){
			editor.backupWork();
			throw new GdxRuntimeException(e);
		}
	}
}
