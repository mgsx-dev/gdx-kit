package net.mgsx.game.core.screen;

import com.badlogic.gdx.Screen;

public class ScreenClipDelegate extends ScreenDelegate implements ScreenClip
{
	public ScreenClipDelegate(Screen current) 
	{
		super(current);
	}

	@Override
	public boolean isComplete() 
	{
		if(current instanceof ScreenClip){
			return ((ScreenClip) current).isComplete();
		}
		return true;
	}
}
