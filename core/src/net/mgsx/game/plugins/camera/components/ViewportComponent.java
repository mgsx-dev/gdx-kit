package net.mgsx.game.plugins.camera.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.components.Duplicable;

@Storable("viewport")
@EditableComponent(autoTool=false)
public class ViewportComponent implements Component, Duplicable
{
	public final static ComponentMapper<ViewportComponent> components = ComponentMapper.getFor(ViewportComponent.class);
	
	@Editable
	public Viewport viewport;
	
	@Override
	public Component duplicate(Engine engine) {
		ViewportComponent clone = engine.createComponent(ViewportComponent.class);
		
		if(viewport instanceof ScreenViewport){
			ScreenViewport cloneViewport = new ScreenViewport(null);
			cloneViewport.setUnitsPerPixel(((ScreenViewport) viewport).getUnitsPerPixel());
			clone.viewport = cloneViewport;
		}else if(viewport instanceof FitViewport){
			clone.viewport = new FitViewport(viewport.getWorldWidth(), viewport.getWorldHeight(), null);
		}else if(viewport instanceof FillViewport){
			clone.viewport = new FillViewport(viewport.getWorldWidth(), viewport.getWorldHeight(), null);
		}else if(viewport instanceof StretchViewport){
			clone.viewport = new StretchViewport(viewport.getWorldWidth(), viewport.getWorldHeight(), null);
		}else if(viewport instanceof ExtendViewport){
			ExtendViewport thisViewport = (ExtendViewport)viewport;
			ExtendViewport cloneViewport = new ExtendViewport(
					thisViewport.getMinWorldWidth(),
					thisViewport.getMinWorldHeight(),
					thisViewport.getMaxWorldWidth(),
					thisViewport.getMaxWorldHeight(),
					null);
			clone.viewport = cloneViewport;
		}else{
			// TODO how to clone custom viewports ?
			Gdx.app.error("graphics", "warning : viewport type not supported " + viewport.getClass().getName() + " use ScreenViewport instead.");
			clone.viewport = new ScreenViewport();
		}
		return clone;
	}
}
