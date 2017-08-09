package net.mgsx.game.examples.platformer.tasks;

import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;
import com.badlogic.gdx.math.MathUtils;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EnumType;
import net.mgsx.game.plugins.btree.BTreePlugin.EntityLeafTask;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;
import net.mgsx.game.plugins.g3d.components.G3DModel;

@TaskAlias("blink")
public class BlinkTask extends EntityLeafTask
{
	@TaskAttribute
	public float speed = 2;
	
	@TaskAttribute
	public float duration = 2;
	
	@Editable(type=EnumType.UNIT)
	@TaskAttribute
	public float opacity = .5f;
	
	private float time;
	
	@Override
	public com.badlogic.gdx.ai.btree.Task.Status execute() {
		time += GdxAI.getTimepiece().getDeltaTime();
		G3DModel model = G3DModel.components.get(getEntity());
		if(model != null){
			if(MathUtils.floor(time * speed) % 2 == 0){
				model.blended = false;
				model.opacity = 1;
			}else{
				model.blended = true;
				model.opacity = opacity;
			}
			model.applyBlending();
		}
		
		if(time > duration){
			if(model != null){
				model.blended = false;
				model.opacity = 1;
				model.applyBlending();
			}
			time = 0;
			return Status.SUCCEEDED;
		}
		else{
			return Status.RUNNING;
		}
	}
}
