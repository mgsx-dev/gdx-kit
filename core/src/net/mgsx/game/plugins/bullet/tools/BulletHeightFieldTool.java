package net.mgsx.game.plugins.bullet.tools;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btHeightfieldTerrainShape;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.plugins.bullet.components.BulletComponent;
import net.mgsx.game.plugins.bullet.components.BulletHeightFieldComponent;
import net.mgsx.game.plugins.bullet.system.BulletWorldSystem;
import net.mgsx.game.plugins.core.components.HeightFieldComponent;

@Editable
public class BulletHeightFieldTool extends Tool
{
	@Inject public BulletWorldSystem bulletWorldSystem;
	
	public BulletHeightFieldTool(EditorScreen editor) {
		super("Height Field", editor);
	}
	
	@Override
	public boolean allowed(Array<Entity> selection) {
		return selection.size == 1 && HeightFieldComponent.components.has(selection.first());
	}
	
	@Override
	protected void activate() 
	{
		super.activate();
		
		HeightFieldComponent hfc = HeightFieldComponent.components.get(currentEntity());
		
		int width = hfc.width;
		int height = hfc.height;
		
		FloatBuffer heightfieldData = ByteBuffer.allocateDirect(width * height * Float.SIZE / 8).order(ByteOrder.nativeOrder()).asFloatBuffer();
		heightfieldData.put(hfc.values);
		float min, max;
		min = max = hfc.values[0];
		for(int i=1 ; i<hfc.values.length ; i++){
			float value = hfc.values[i];
			if(value < min) min = value;
			if(value > max) max = value;
		}
		heightfieldData.flip();
		
		btHeightfieldTerrainShape heightfieldTerrainShape = new btHeightfieldTerrainShape(
				width, height, heightfieldData, 
				1f, // scaling is ignored for float data
				min, max, 
				1, // y-up axis
				false); // ?
		
		BulletComponent bullet = getEngine().createComponent(BulletComponent.class);
		bullet.world = bulletWorldSystem.collisionWorld;
		bullet.object = new btCollisionObject();
		bullet.shape = heightfieldTerrainShape;
		bullet.object.setCollisionShape(bullet.shape);
		
		// keep reference on buffer to prevent from GC
		BulletHeightFieldComponent bhfc = getEngine().createComponent(BulletHeightFieldComponent.class);
		bhfc.data = heightfieldData;
		currentEntity().add(bhfc);
		
		currentEntity().remove(BulletComponent.class);
		currentEntity().add(bullet);
		bulletWorldSystem.collisionWorld.addCollisionObject(bullet.object);
		bullet.object.setWorldTransform(new Matrix4().setToTranslation(0, (max+min)/2, 0));
	}
	
}
