package net.mgsx.game.plugins.pd.midi;

public class SequenceMarker
{
	public String name;
	public long tick;
	
	@Override
	public String toString() {
		return "Marker " + name + " (" + String.valueOf(tick) + ")";
	}
}
