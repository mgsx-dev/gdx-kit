package net.mgsx.game.plugins.pd.midi;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.util.MidiEventListener;

public class LiveSequencer extends BaseSequencer
{
	private final Array<LiveTrack> tracks = new Array<LiveTrack>();
	private final ObjectMap<String, LiveTrack> tracksByName = new ObjectMap<String, LiveTrack>();
	
	private volatile boolean shouldPlay = true;
	
	private Thread thread;
	
	/** ticks per quarter note */
	private int resolution;
	
	public volatile float bpm = 100f;
	
	public LiveSequencer(MidiEventListener listener) {
		super(listener);
	}
	
	public void load(MidiFile file)
	{
		resolution = file.getResolution(); // TODO resolution may change between files but not in tracks !
		
		for(MidiTrack track : file.getTracks()){
			LiveTrack t = new LiveTrack(this, file, track, listener);
			tracks.add(t);
			if(t.name != null) tracksByName.put(t.name, t);
		}
	}
	
	public Array<LiveTrack> getTracks() {
		return tracks;
	}

	public LiveTrack getTrack(int index) {
		return tracks.get(index);
	}

	public LiveTrack getTrack(String name) {
		return tracksByName.get(name);
	}

	public void stop() {
		shouldPlay = false;
	}
	
	public void play() 
	{
		// copy tracks reference to avoid concurrent access on list.
		final Array<LiveTrack> runningTracks = new Array<LiveTrack>(tracks);
		shouldPlay = true;
		thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				long previousTimeNS = System.nanoTime();
				float timeS = 0;
				
				// reset track before a new run
				for(LiveTrack track : runningTracks){
					track.reset();
				}
				
				position = 0;
				
				while(shouldPlay)
				{
					float ticksPerSec = (bpm / 60f) * resolution;
					int bestTickDurationMS = Math.max(1, (int)(1000f / ticksPerSec));
					// TODO tickDurationMS could be more (tradeoff between performance and accuracy)
					int tickDurationMS = bestTickDurationMS;
					
					long currentTimeNS = System.nanoTime();
					long deltaTimeNS = currentTimeNS - previousTimeNS;
					previousTimeNS = currentTimeNS;
					
					timeS += (float)deltaTimeNS * ticksPerSec / 1e9f;
					long posTick = (long)(timeS);
					long deltaTick = posTick - position;
					position = posTick;
					// System.out.println(position);
					for(LiveTrack track : runningTracks){
						if(track.active) track.update(deltaTick);
					}
					try {
						Thread.sleep(tickDurationMS);
					} catch (InterruptedException e) {
						// silent fail.
					}
				}
				// XXX force all note off (all note and all channels)
				ResetNote off = new ResetNote();
				for(int i=0 ; i<16 ; i++){
					for(int j=0 ; j<127 ; j++){
						listener.onEvent(off.set(i, j), 0);
					}
				}
				
			}
		}, "LiveSequencer");
		thread.start();
	}
	
	public void clear() {
		tracks.clear();
	}


}
