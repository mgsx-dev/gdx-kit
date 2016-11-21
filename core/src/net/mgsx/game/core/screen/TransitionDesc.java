package net.mgsx.game.core.screen;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Interpolation;

public class TransitionDesc 
{
	public ScreenTransition transition;
	public Screen destination;
	public float duration;
	public Interpolation interpolation = Interpolation.linear;
	public TransitionListener listener = null;
}
