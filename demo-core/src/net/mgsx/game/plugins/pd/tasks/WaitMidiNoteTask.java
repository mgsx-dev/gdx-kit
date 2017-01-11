package net.mgsx.game.plugins.pd.tasks;

import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOn;
import com.leff.midi.util.MidiEventListener;

import net.mgsx.game.plugins.btree.BTreePlugin.EntityLeafTask;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;
import net.mgsx.game.plugins.pd.systems.MidiSequencerSystem;

@TaskAlias("waitMidiNote")
public class WaitMidiNoteTask extends EntityLeafTask
{
	@TaskAttribute(required=true)
	public String track;
	
	@TaskAttribute
	public int noteMin = 0;
	
	@TaskAttribute
	public int noteMax = 127;
	
	private MidiEventListener listener;
	
	volatile private int noteCounter = 0;
	
	@Override
	public void start() 
	{
		listener = new MidiEventListener() {
			
			@Override
			public void onStop(boolean finished) {
				noteCounter = 0;
			}
			
			@Override
			public void onStart(boolean fromBeginning) {
				noteCounter = 0;
			}
			
			@Override
			public void onEvent(MidiEvent event, long ms) {
				if(event instanceof NoteOn){
					NoteOn noteOn = (NoteOn) event;
					if(noteOn.getVelocity() > 0 && noteOn.getNoteValue() >= noteMin && noteOn.getNoteValue() <= noteMax ){
						noteCounter++;
					}
				}
			}
		};
		getEngine().getSystem(MidiSequencerSystem.class).sequencer.getTrack(track).addListener(listener);
		noteCounter = 0;
	}
	
	@Override
	public com.badlogic.gdx.ai.btree.Task.Status execute() 
	{
		if(noteCounter > 0){
			return Status.SUCCEEDED;
		}
		
		
		return Status.RUNNING;
	}
	
	@Override
	public void end() {
		getEngine().getSystem(MidiSequencerSystem.class).sequencer.getTrack(track).removeListener(listener);
		listener = null;
	}
}
