package net.mgsx.game.plugins.pd.midi;

import com.leff.midi.util.MidiEventListener;

public abstract class BaseSequencer
{
	protected final MidiEventListener listener;
	protected volatile long position;


	public BaseSequencer(MidiEventListener listener) {
		super();
		this.listener = listener;
	}

}
