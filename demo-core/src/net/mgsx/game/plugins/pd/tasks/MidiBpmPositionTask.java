package net.mgsx.game.plugins.pd.tasks;

import com.badlogic.gdx.math.MathUtils;

import net.mgsx.game.plugins.btree.BTreePlugin.EntityLeafTask;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;
import net.mgsx.game.plugins.core.components.Transform2DComponent;
import net.mgsx.game.plugins.pd.systems.MidiSequencerSystem;

@TaskAlias("midiBpmPosition")
public class MidiBpmPositionTask extends EntityLeafTask
{
	@Override
	public com.badlogic.gdx.ai.btree.Task.Status execute() {
		int bpmMin = 30;
		int bpmMax = 180;
		float t = .5f;
		Transform2DComponent trans = Transform2DComponent.components.get(getEntity());
		if(trans != null){
			t = MathUtils.clamp(trans.position.y / 2, 0, 1);
		}
		
		getEngine().getSystem(MidiSequencerSystem.class).sequencer.bpm = MathUtils.lerp(bpmMin, bpmMax, t);
		return Status.RUNNING;
	}
}
