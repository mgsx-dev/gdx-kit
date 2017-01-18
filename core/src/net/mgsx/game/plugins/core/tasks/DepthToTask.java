package net.mgsx.game.plugins.core.tasks;

import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;

import net.mgsx.game.plugins.btree.BTreePlugin.EntityLeafTask;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;
import net.mgsx.game.plugins.core.components.Transform2DComponent;
import net.mgsx.game.plugins.g3d.components.G3DModel;

@TaskAlias("depthTo")
public class DepthToTask extends EntityLeafTask
{
	@TaskAttribute
	public float depth;
	
	@TaskAttribute
	public float duration;
	
	public Interpolation interpolation;
	
	private float origin;
	private float time;
	
	@Override
	public void start() {
		time = 0;
		if(interpolation == null) interpolation = Interpolation.linear;
		Transform2DComponent transform = Transform2DComponent.components.get(getEntity());
		if(transform != null){
			origin = transform.depth;
		}
	}
	
	@Override
	public Status execute() {
		time += GdxAI.getTimepiece().getDeltaTime();
		Transform2DComponent transform = Transform2DComponent.components.get(getEntity());
		if(transform != null){
			if(time > duration){
				transform.depth = depth;
				
			}else{
				float t = time / duration;
				transform.depth = interpolation.apply(origin, depth, t);
			}
			
			G3DModel model = G3DModel.components.get(getEntity());
			if(model != null){
				for(Material mat : model.modelInstance.materials){
					ColorAttribute color = (ColorAttribute)mat.get(ColorAttribute.Diffuse);
					float f = MathUtils.lerp(1, .2f, MathUtils.clamp(-transform.depth * 2, 0, 1));
					if(color != null)
					color.color.set(f, f, f, 1);
				}
			}
		}
		return time >= duration ? Status.SUCCEEDED : Status.RUNNING;
	}
}
