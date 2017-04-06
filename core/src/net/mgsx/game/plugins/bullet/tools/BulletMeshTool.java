package net.mgsx.game.plugins.bullet.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.plugins.bullet.components.BulletComponent;
import net.mgsx.game.plugins.bullet.system.BulletWorldSystem;
import net.mgsx.game.plugins.g3d.components.G3DModel;

@Editable
public class BulletMeshTool extends Tool
{
	@Inject
	public BulletWorldSystem bulletWorldSystem;
	
	public BulletMeshTool(EditorScreen editor) {
		super("Mesh", editor);
	}
	
	@Override
	public boolean allowed(Array<Entity> selection) {
		return selection.size == 1 && G3DModel.components.has(selection.first());
	}
	
	@Override
	protected void activate() 
	{
		super.activate();
		
		G3DModel model = G3DModel.components.get(currentEntity());
		
		BulletComponent bullet = getEngine().createComponent(BulletComponent.class);
		bullet.world = bulletWorldSystem.collisionWorld;
		bullet.object = new btCollisionObject();
		bullet.shape = Bullet.obtainStaticNodeShape(model.modelInstance.nodes);
		bullet.object.setCollisionShape(bullet.shape);
		
		currentEntity().add(bullet);
		bulletWorldSystem.collisionWorld.addCollisionObject(bullet.object);
		bullet.object.setWorldTransform(model.modelInstance.transform);
	}

}
