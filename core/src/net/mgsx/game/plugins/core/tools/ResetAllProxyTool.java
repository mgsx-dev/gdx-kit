package net.mgsx.game.plugins.core.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.components.Repository;
import net.mgsx.game.core.helpers.ArrayHelper;
import net.mgsx.game.core.storage.EntityGroupStorage;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.plugins.btree.BTreeModel;
import net.mgsx.game.plugins.camera.components.CameraComponent;
import net.mgsx.game.plugins.core.components.ProxyComponent;

public class ResetAllProxyTool extends Tool
{

	public ResetAllProxyTool(EditorScreen editor) {
		super("Reset All Proxy", editor);
	}
	
	@Override
	protected void activate() {
		super.activate();
		
		// TODO refactor as helper to recreate clones (static method in proxy component ?)
		
		ImmutableArray<Entity> transcient = getEngine().getEntitiesFor(Family.exclude(Repository.class, CameraComponent.class).get()); // XXX cameras !
		Array<Entity> toRemove = ArrayHelper.array(transcient);
		for(Entity e : toRemove){
			getEngine().removeEntity(e);
		}
		
		for(Entity master : getEngine().getEntities()){
			
			ProxyComponent proxy = ProxyComponent.components.get(master);
			if(proxy == null) continue;
			
//			for(Entity entity : proxy.clones.entities()) editor.entityEngine.removeEntity(entity);
//			proxy.clones.entities().clear();
//			
//			// recreate clones
			proxy.clones = EntityGroupStorage.create(new Array<Entity>(), editor.assets, getEngine(), proxy.template, master);
			
			for(Entity entity : proxy.clones.entities()){
				BTreeModel btree = BTreeModel.components.get(entity);
				if(btree != null){
					btree.enabled = true;
					btree.remove = true;
				}
			}
		}
		
		end();
	}
	
}
