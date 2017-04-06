package net.mgsx.game.plugins.pd.systems;

import com.badlogic.ashley.core.EntitySystem;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.plugins.pd.midi.LiveSequencer;
import net.mgsx.game.plugins.pd.midi.LiveTrack;
import net.mgsx.game.plugins.pd.midi.MidiLoop;
import net.mgsx.game.plugins.pd.midi.SequenceDesc;
import net.mgsx.midi.sequence.MidiSequence;
import net.mgsx.pd.Pd;

@EditableSystem
public class MidiSequencerSystem extends EntitySystem
{
	public LiveSequencer sequencer;
	public SequenceDesc sequenceDesc;
	private GameScreen game;
	
	@Editable
	public void setBpm(float bpm){
		sequencer.bpm = bpm;
	}
	
	@Editable
	public float getBpm(){
		return sequencer.bpm;
	}
	
	
	
	@Editable
	public void play(){
		
		MidiLoop loop = new MidiLoop();
		loop.start = 0L;
		loop.end = (long)(96 * 4 * 4);
		for(LiveTrack track : sequencer.getTracks()){
			track.replace(loop);
		}
		
		sequencer.play();
	}
	@Editable
	public void stop(){
		sequencer.stop();
	}
	
	public MidiSequencerSystem(GameScreen game) {
		super(GamePipeline.AFTER_LOGIC);
		this.game = game;
		this.sequencer = new LiveSequencer(Pd.midi.getPdSynth());
	}
	
	public void loadSequence(MidiSequence sequence) {
		sequencer.load(sequence);
	}
	
	@Override
	public void update(float deltaTime) 
	{
		// simply send bpm to pd in order to apply some sync FX (delay, tremolo ...)
		// TODO maybe send only when changed ?
		// Pd.audio.sendFloat("bpm", sequencer.bpm);
		
	}
}
