package net.mgsx.game.plugins.pd.tasks;

import com.badlogic.gdx.ai.btree.Decorator;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import net.mgsx.game.plugins.btree.EntityBlackboard;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;
import net.mgsx.game.plugins.g3d.components.G3DModel;
import net.mgsx.game.plugins.pd.systems.MidiSequencerSystem;

/**
 * Scale G3D animation speed from BPM :
 * animation speed is scale to fit midi duration at any BPM.
 * 
 * For instance, with midi duration of 4 beats and an animation of 2 seconds, animation
 * will restart every 4 beats.
 * 
 * @author mgsx
 *
 */
@TaskAlias("midiAnimation")
public class MidiAnimationTask extends Decorator<EntityBlackboard>{ // TODO rename as MidiSyncAnimation ? split in 2 ?

	/** tick sync ? in defined resolution */
	@TaskAttribute
	public int ticks = 4; // TODO midi loop length
	/** ticks per beat resolution (4 mean 4 ticks per beat = 16th note) */
	@TaskAttribute
	public int division = 1;
	
	/** loop length in resolution */
	@TaskAttribute
	public int length = 4; // TODO animation clips count
	
	private long lastTick;

	@Override
	public void run() 
	{
		long newTick = getObject().engine.getSystem(MidiSequencerSystem.class).sequencer.position(division);
		boolean resync = false;
		if(newTick % ticks == 0 && newTick > lastTick || newTick < lastTick){
			if(child.getStatus() != Status.RUNNING || true){
				cancelRunningChildren(0);
				super.run();
			}
			resync = true;
			lastTick = newTick;
		}else if(child.getStatus() == Status.RUNNING){
			child.run();
		}
		
		G3DModel model = G3DModel.components.get(getObject().entity);
		float bpm = getObject().engine.getSystem(MidiSequencerSystem.class).sequencer.bpm;
		if(model != null && model.animationController.current != null)
		{
			float midiBeats = 1 / (float)division;
			float midiTime = midiBeats / (bpm / 60);
			float animTime = model.animationController.current.duration;
			model.animationController.current.speed = (animTime / midiTime) * length / ticks ;
			
			if(resync) model.animationController.current.time = 0;
			
			// System.out.println(model.animationController.current.time);
		}
	}
	
	@Override
	public void childSuccess(Task<EntityBlackboard> runningTask) {
		running();
	}
}
