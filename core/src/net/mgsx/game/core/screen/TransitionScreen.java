package net.mgsx.game.core.screen;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.MathUtils;

/**
 * Screen playing smooth transition between 2 screens.
 * 
 * Transition effect itself is handled by ScreenTransition
 * 
 * @author mgsx
 *
 */
public class TransitionScreen implements ScreenClip
{
	public Screen source;
	private TransitionDesc desc;
	private float time;
	private boolean complete;

	public Screen getDestination(){
		return desc.destination;
	}
	
	public void setDesc(TransitionDesc desc) {
		this.desc = desc;
	}
	
	@Override
	public void render(float deltaTime) 
	{
		time += deltaTime;
		if(!complete && time > desc.duration){
			complete = true;
			end();
			if(desc.listener != null) desc.listener.end();
		}
		float t = desc.interpolation.apply(MathUtils.clamp(time / desc.duration, 0, 1));
		desc.transition.render(source, desc.destination, deltaTime, t);
	}

	@Override
	public void resize(int width, int height) {
		source.resize(width, height);
		desc.destination.resize(width, height);
		desc.transition.resize(width, height);
	}
	
	private void begin() 
	{
		if(source instanceof ScreenTransitionListener) {
			((ScreenTransitionListener) source).preHide();
		}
		
		desc.destination.show();
	}

	private void end() 
	{
		source.hide();
		
		if(desc.destination instanceof ScreenTransitionListener) {
			((ScreenTransitionListener) desc.destination).postShow();
		}
	}


	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		reset();
		begin();
	}
	
	@Override
	public void hide() 
	{
		// called only if transition is cancelled.
		// in that case we have to hide both source and destination ? TODO
		// we don't hide destination
		end();
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isComplete() 
	{
		return complete;
	}

	public void reset() 
	{
		complete = false;
		time = 0;
	}

	@Override
	public void preHide() {
		// transition screens doesn't support nested transitions.
	}

	@Override
	public void postShow() {
		// transition screens doesn't support nested transitions.
	}
	
}
