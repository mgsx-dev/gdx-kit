package net.mgsx.game.plugins.core.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.storage.EntityGroupStorage;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.plugins.btree.BTreeModel;
import net.mgsx.game.plugins.core.components.ProxyComponent;
import net.mgsx.game.plugins.core.systems.DependencySystem;

public class ResetProxyTool extends Tool
{

	public ResetProxyTool(EditorScreen editor) {
		super("Reset Proxy", editor);
		activator = Family.all(ProxyComponent.class).get();
	}
	
	@Override
	protected void activate() {
		super.activate();
		
		Entity master = editor.getSelected();
		ProxyComponent proxy = ProxyComponent.components.get(master);
		
		// remove clones
		getEngine().getSystem(DependencySystem.class).removeChildren(master);
		
		// recreate clones
		Array<Entity> clones = new Array<Entity>();
		EntityGroupStorage.create(clones, editor.assets, getEngine(), proxy.template, master);
		
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
