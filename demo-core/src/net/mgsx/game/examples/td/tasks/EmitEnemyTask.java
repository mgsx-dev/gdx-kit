package net.mgsx.game.examples.td.tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;

import net.mgsx.game.examples.td.components.Damage;
import net.mgsx.game.examples.td.components.Enemy;
import net.mgsx.game.examples.td.components.Enemy.Type;
import net.mgsx.game.examples.td.components.Life;
import net.mgsx.game.examples.td.components.PathFollower;
import net.mgsx.game.examples.td.components.Speed;
import net.mgsx.game.examples.td.components.TileComponent;
import net.mgsx.game.examples.td.systems.MapSystem;
import net.mgsx.game.examples.td.systems.WaveSystem;
import net.mgsx.game.plugins.btree.BTreePlugin.EntityLeafTask;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;
import net.mgsx.game.plugins.core.components.Transform2DComponent;
import net.mgsx.game.plugins.g3d.components.G3DModel;

@TaskAlias("enemy")
public class EmitEnemyTask extends EntityLeafTask
{
	@TaskAttribute
	public float speed = 2;
	
	@TaskAttribute
	public float life = 5;
	
	@TaskAttribute
	public float damage = 1;
	
	@TaskAttribute
	public boolean direct = false;
	
	@TaskAttribute
	public Enemy.Type type = Type.SQUARE;
	
	@Override
	public com.badlogic.gdx.ai.btree.Task.Status execute() 
	{
		// TODO create entity from template ... ? or create sub tasks for each kind of enemy
		MapSystem map = getEngine().getSystem(MapSystem.class);
		WaveSystem wave = getEngine().getSystem(WaveSystem.class);
		
		Entity entity = getEngine().createEntity();
		
		Enemy enemy = getEngine().createComponent(Enemy.class);
		Life life = getEngine().createComponent(Life.class);
		PathFollower path = getEngine().createComponent(PathFollower.class);
		Transform2DComponent transform = getEngine().createComponent(Transform2DComponent.class);
		Damage damage = getEngine().createComponent(Damage.class);
		
		TileComponent tile = TileComponent.components.get(getEntity());
		
		Speed speed = getEngine().createComponent(Speed.class);
		speed.base = this.speed;

		enemy.type = this.type;
		
		if(direct){
			enemy.homeTarget = map.findDirectPathToHome(path, tile.x, tile.y);
		}else{
			enemy.homeTarget = map.findPathToHome(path, tile.x, tile.y);
		}
		path.path.valueAt(transform.position, 0); // init position
		
		damage.amount = this.damage * wave.waveFactor;
		
		life.max = life.current = this.life * wave.waveFactor;
		
		entity.add(transform);
		entity.add(path);
		entity.add(enemy);
		entity.add(life);
		entity.add(damage);
		entity.add(speed);
		
		// getEngine().getSystem(EditorSystem.class).
		G3DModel model = new G3DModel();
		model.modelInstance = new ModelInstance(map.monsterModel);
		
//		Node node;
//		
//		node = model.modelInstance.getNode("leg2.r");
//		node.getParent().removeChild(node);
//		
//		node = model.modelInstance.getNode("leg2.l");
//		node.getParent().removeChild(node);
//		
//		node = model.modelInstance.getNode("mesh");
//		
//		for(NodePart part : node.parts){
//			if("Cube_part1".equals(part.meshPart.id)){
//				part.enabled = false;
//				
//			}
//		}
		
		model.animationController = new AnimationController(model.modelInstance);
		
		
		
		entity.add(model);
		
		getEngine().addEntity(entity);
		
		return Status.SUCCEEDED;
	}
}
