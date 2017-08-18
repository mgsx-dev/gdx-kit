package net.mgsx.game.examples.td.tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.annotations.TaskAsset;
import net.mgsx.game.core.storage.EntityGroup;
import net.mgsx.game.examples.td.components.Damage;
import net.mgsx.game.examples.td.components.Life;
import net.mgsx.game.examples.td.systems.NavSystem;
import net.mgsx.game.examples.td.systems.WaveSystem;
import net.mgsx.game.plugins.btree.BTreeModel;
import net.mgsx.game.plugins.btree.BTreePlugin.EntityLeafTask;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

// TODO json file by type !
@TaskAlias("enemy")
public class EmitEnemyTask extends EntityLeafTask
{
	@TaskAsset(EntityGroup.class)
	@TaskAttribute
	public String enemy;

//	private MeshEmitter meshEmitter;
	
//	@Override
//	public void start() {
//		G3DModel model = G3DModel.components.get(getEntity());
//		ModelMesh mesh = new ModelMesh();
//		// mesh.vertices
//		// model.modelInstance.model.meshes.get(0).
//		meshEmitter = new MeshEmitter(mesh );
//	}
	
	
	@Override
	public com.badlogic.gdx.ai.btree.Task.Status execute() 
	{
		// find emitter (mesh emitter)
		WaveSystem wave = getEngine().getSystem(WaveSystem.class);
		
		Transform2DComponent emitterTransform = Transform2DComponent.components.get(getEntity());
		
		EntityGroup template = getObject().assets.get(enemy, EntityGroup.class);
		for(Entity entity : template.create(getObject().assets, getEngine())){
			Transform2DComponent t = Transform2DComponent.components.get(entity);
			if(t != null){
				t.position.set(emitterTransform.position);
				t.depth = emitterTransform.depth;
				t.rotation = true;
				t.angle = MathUtils.random(360);
			}
			BTreeModel btree = BTreeModel.components.get(entity);
			if(btree!=null){
				btree.enabled = true;
				btree.remove = false;
			}
			
			NavSystem nav = getEngine().getSystem(NavSystem.class);
			Vector3 p = new Vector3(t.position, t.depth+1);
			nav.navMesh.triCast(p, new Vector3(0,0,-1));
			t.depth = -p.z;
			nav.randomPath(entity, new Vector3(t.position, t.depth), new Vector3(0,0,-1));
			
			Damage damage = Damage.components.get(entity);
			if(damage != null){
				damage.amount *= wave.waveFactor;
			}
			Life life = Life.components.get(entity);
			if(life != null){
				life.current = life.max *= wave.waveFactor;
			}else{
				life = getEngine().createComponent(Life.class);
				life.current = life.max = wave.waveFactor;
				entity.add(life);
			}
		}
		
		return Status.SUCCEEDED;
	}
}
