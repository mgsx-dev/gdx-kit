package net.mgsx.game.plugins.pd.systems;

import com.badlogic.gdx.utils.SnapshotArray;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.util.MidiEventListener;

public class MidiEventMultiplexer implements MidiEventListener {

	public SnapshotArray<MidiEventListener> listeners = new SnapshotArray<MidiEventListener>(MidiEventListener.class);
	
	
	public MidiEventMultiplexer(MidiEventListener ...listeners) {
		this.listeners.addAll(listeners);
	}
	
	@Override
	public void onStart(boolean fromBeginning) {
		MidiEventListener[] snapshot = listeners.begin();
		try{
			for(int i=0 ; i<listeners.size ; i++) snapshot[i].onStart(fromBeginning);
		}finally{
			listeners.end();
		}
	}

	@Override
	public void onEvent(MidiEvent event, long ms) {
		MidiEventListener[] snapshot = listeners.begin();
		try{
			for(int i=0 ; i<listeners.size ; i++) snapshot[i].onEvent(event, ms);
		}finally{
			listeners.end();
		}
	}

	@Override
	public void onStop(boolean finished) {
		MidiEventListener[] snapshot = listeners.begin();
		try{
			for(int i=0 ; i<listeners.size ; i++) snapshot[i].onStop(finished);
		}finally{
			listeners.end();
		}
	}

}
