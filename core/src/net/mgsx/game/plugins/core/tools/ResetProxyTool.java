package net.mgsx.game.plugins.core.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.storage.EntityGroupStorage;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.plugins.btree.BTreeModel;
import net.mgsx.game.plugins.core.components.ProxyComponent;

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
		// TODO clones are not always set ! fix this in loaders
		// TODO entities may be removed themself, entity reference is not valid anymore !!
		for(Entity entity : proxy.clones.entities()) editor.entityEngine.removeEntity(entity);
		proxy.clones.entities().clear();
		
		// recreate clones
		Array<Entity> clones = new Array<Entity>();
		proxy.clones = EntityGroupStorage.create(clones, editor.assets, getEngine(), proxy.template, master);
		
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
