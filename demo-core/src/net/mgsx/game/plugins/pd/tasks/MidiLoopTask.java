package net.mgsx.game.plugins.pd.tasks;

import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import net.mgsx.game.plugins.btree.BTreePlugin.EntityLeafTask;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;
import net.mgsx.game.plugins.pd.midi.MidiLoop;
import net.mgsx.game.plugins.pd.midi.MidiLooper;
import net.mgsx.game.plugins.pd.midi.SequenceMarker;
import net.mgsx.game.plugins.pd.systems.MidiSequencerSystem;

@TaskAlias("midiLoop")
public class MidiLoopTask extends EntityLeafTask
{
	public static enum Mode{
		REPLACE, QUEUE, STACK
	}
	
	@TaskAttribute
	public String track;
	
	@TaskAttribute
	public String start;
	
	@TaskAttribute
	public String end;
	
	@TaskAttribute
	public String head;
	
	@TaskAttribute
	public Integer syncDivision = 96, syncPosition;
	
	@TaskAttribute
	public Mode mode = Mode.REPLACE;
	
	@TaskAttribute
	public Integer count;
	
	@Override
	public com.badlogic.gdx.ai.btree.Task.Status execute() 
	{
		// TODO live sequencer.
		MidiSequencerSystem system = getEngine().getSystem(MidiSequencerSystem.class);
		
		SequenceMarker markerStart = start == null ? null : system.sequenceDesc.markers.get(start);
		SequenceMarker markerEnd = end == null ? null : system.sequenceDesc.markers.get(end);
		SequenceMarker markerHead = head == null ? null : system.sequenceDesc.markers.get(head);
		
		MidiLooper looper = track == null ? null : system.sequencer.getTrack(track);
		MidiLoop loop = new MidiLoop();
		if(markerStart != null) loop.start = markerStart.tick;
		if(markerEnd != null) loop.end = markerEnd.tick;
		// TODO head ?
		
		syncDivision =  96 * 1;
		
		loop.syncPosition = syncPosition;
		loop.syncDivision = syncDivision;
		loop.count = count;

		switch(mode){
		case QUEUE:
			if(looper != null) looper.queue(loop);
			else for(MidiLooper l : system.sequencer.getTracks()) l.queue(loop);
			break;
		case REPLACE:
			if(looper != null) looper.replace(loop);
			else for(MidiLooper l : system.sequencer.getTracks()) l.replace(loop);
			break;
		case STACK:
			if(looper != null) looper.push(loop);
			else for(MidiLooper l : system.sequencer.getTracks()) l.push(loop);
			break;
		default:
			break;
		
		}
		
		return Status.SUCCEEDED;
	}
}
