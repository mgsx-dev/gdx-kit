package net.mgsx.game.plugins.pd.tasks;

import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import net.mgsx.game.plugins.btree.BTreePlugin.EntityLeafTask;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;
import net.mgsx.game.plugins.pd.midi.BaseSequencer;
import net.mgsx.game.plugins.pd.systems.MidiSequencerSystem;

@TaskAlias("waitMidiMarker")
public class WaitMidiMarkerTask extends EntityLeafTask
{
	@TaskAttribute
	public String track;
	
	@TaskAttribute(required=true)
	public String marker;
	
	private long markerTick, tick;
	private BaseSequencer seq;
	
	@Override
	public void start() 
	{
		MidiSequencerSystem system = getEngine().getSystem(MidiSequencerSystem.class);
		markerTick = system.sequenceDesc.markers.get(marker).tick;
		seq = track == null ? system.sequencer : system.sequencer.getTrack(track);
		tick = system.sequencer.getPosition();
	}
	
	@Override
	public com.badlogic.gdx.ai.btree.Task.Status execute() 
	{
		long newTick = seq.getPosition();
		// System.out.println(newTick);
		if(newTick >= markerTick && tick < markerTick){ // TODO doesn't work with sequencer jump ! use listener instead ?
			return Status.SUCCEEDED;
		}
		tick = newTick;
		
		return Status.RUNNING;
	}
	
	@Override
	public void end() {
		seq = null;
	}
}
