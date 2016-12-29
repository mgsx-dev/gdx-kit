package net.mgsx.game.examples.platformer.tasks;

import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;
import com.badlogic.gdx.math.MathUtils;

import net.mgsx.game.plugins.btree.annotations.TaskAlias;
import net.mgsx.game.plugins.g3d.components.G3DModel;

@TaskAlias("alpha")
public class AlphaTask extends AbstractTimeTask
{
	@TaskAttribute
	public float from = 0;
	@TaskAttribute
	public float to = 1;
	
	@Override
	protected void update(float t) 
	{
		G3DModel model = G3DModel.components.get(getEntity());
		if(model != null){
			model.opacity = MathUtils.lerp(from, to, t);
		}
		// TODO particles / G2D, ...etc
	}

}
