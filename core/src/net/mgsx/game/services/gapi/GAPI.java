package net.mgsx.game.services.gapi;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;

import net.mgsx.game.core.helpers.ReflectionHelper;

/**
 * Google API abstraction
 * 
 * Implementations is platform-dependant :
 * <ul>
 * <li>Android target will use native SDK library</li>
 * <li>Desktop target will use REST API through Java client library</li>
 * </ul>
 * 
 * @author mgsx
 *
 */
public class GAPI {
	
	static
	{
		String className = null;
		if(Gdx.app.getType() == ApplicationType.Desktop){
			className = "net.mgsx.kit.services.gapi.GAPIServiceDesktop";
		}
		if(className != null){
			service = ReflectionHelper.newInstance(className);
		}else{
			service = new GAPIServiceStub();
		}
	}
	
	public static GAPIService service;
	
}
