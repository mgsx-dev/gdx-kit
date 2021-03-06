package net.mgsx.game.plugins.pd.midi;

import net.mgsx.midi.sequence.util.MidiEventListener;

public abstract class BaseSequencer
{
	protected final MidiEventListener listener;
	protected volatile long position;

	public long getPosition() {
		return position;
	}

	public BaseSequencer(MidiEventListener listener) {
		super();
		this.listener = listener;
	}

	public void sendAllNotesOff()
	{
		net.mgsx.midi.playback.BaseSequencer.sendAllNotesOff(listener);
	}
	
}
