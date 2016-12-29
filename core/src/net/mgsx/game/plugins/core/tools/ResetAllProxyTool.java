package net.mgsx.game.plugins.core.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.helpers.ArrayHelper;
import net.mgsx.game.core.storage.EntityGroupStorage;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.plugins.btree.BTreeModel;
import net.mgsx.game.plugins.core.components.ProxyComponent;
import net.mgsx.game.plugins.core.systems.DependencySystem;

public class ResetAllProxyTool extends Tool
{

	public ResetAllProxyTool(EditorScreen editor) {
		super("Reset All Proxy", editor);
	}
	
	@Override
	protected void activate() {
		super.activate();
		
		// TODO refactor as helper to recreate clones (static method in proxy component ?)
		
		// TODO ther is a bug, sometimes entities are not reset and never die ... !
		
		for(Entity e : getEngine().getEntitiesFor(Family.all(ProxyComponent.class).get())){
			getEngine().getSystem(DependencySystem.class).removeChildren(e);
		}
		
		Array<Entity> clones = new Array<Entity>();
		
		for(Entity master : ArrayHelper.array(getEngine().getEntitiesFor(Family.all(ProxyComponent.class).get()))){
			
			ProxyComponent proxy = ProxyComponent.components.get(master);
			
			EntityGroupStorage.create(clones, editor.assets, getEngine(), proxy.template, master);
		}
		
		for(Entity entity : clones){
			BTreeModel btree = BTreeModel.components.get(entity);
			if(btree != null){
				btree.enabled = true;
				btree.remove = true;
			}
		}
		
		end();
	}
	
}
