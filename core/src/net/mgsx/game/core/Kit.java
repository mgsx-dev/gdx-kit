package net.mgsx.game.core;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.reflect.ClassReflection;

import net.mgsx.game.core.meta.KitMeta;

public class Kit {

	public static final InputMultiplexer inputs = new InputMultiplexer();
	
	public static KitMeta meta;
	
	/**
	 * Game listeners allow hook in game workflow.
	 */
	public static final Array<KitGameListener> gameListeners = new Array<KitGameListener>();
	
	private static boolean exited = false;
	
	static{
		if(Gdx.app.getType() == ApplicationType.WebGL){
			meta = null;
		}else{
			try {
				meta = (KitMeta)ClassReflection.newInstance(ClassReflection.forName("net.mgsx.game.core.meta.ReflectionCache"));
			} catch (Exception e) {
				throw new GdxRuntimeException(e);
			}
		}
	}
	
	/**
	 * Exit application, all gameListeners will be called.
	 * @param e optional error causing the exit.
	 */
	public synchronized static void exit(Throwable e){
		if(!exited){
			exited = true;
			if(e != null){
				Gdx.app.error("KIT", "exiting because of unhandled error", e);
			}
			for(KitGameListener listener : gameListeners){
				try {
					listener.exit(e);
				} catch (Throwable e2) {
					Gdx.app.error("KIT", "error in exit hooks", e2);
				}
			}
			if(e != null){
				// don't re throw because GLThread only will end and app won't exit ..
				// throw new RuntimeException(e);
				Gdx.app.exit();
			}
		}
	}
}
