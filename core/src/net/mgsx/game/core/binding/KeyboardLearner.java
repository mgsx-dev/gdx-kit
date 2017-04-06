package net.mgsx.game.core.binding;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;

import net.mgsx.game.core.Kit;

public class KeyboardLearner implements Learner
{
	private InputProcessor oldProcessor;
	
	
	@Override
	public void startLearning(final LearnListener listener) 
	{
		oldProcessor = Gdx.input.getInputProcessor();
		Gdx.input.setInputProcessor(new InputAdapter(){
			@Override
			public boolean keyDown(int keycode) {
				if(keycode == Input.Keys.ESCAPE){
					listener.onCommand(KeyboardLearner.this, null);
				}else{
					listener.onCommand(KeyboardLearner.this, "kc:" + String.valueOf(keycode));
				}
				return true;
			}
		});
	}

	@Override
	public void stopLearning() {
		Gdx.input.setInputProcessor(oldProcessor);
	}

	@Override
	public void bind(final Binding binding) {
		String [] args = binding.command.split("kc:");
		if(args.length == 2){
			final int matchKeycode = Integer.valueOf(args[1]);
			InputProcessor processor = new InputAdapter(){
				@Override
				public boolean keyDown(int keycode) {
					if(keycode == matchKeycode){
						if(binding.accessor.getType() == void.class)
							binding.accessor.get(); //
						else if(binding.accessor.getType() == boolean.class)
							binding.accessor.set(!binding.accessor.get(Boolean.class)); //
						return true;
					}
					return false;
				}
			};
			binding.listener = processor;
			Kit.inputs.addProcessor(processor);
		}
	}

	@Override
	public void unbind(Binding binding) {
		if(binding.listener instanceof InputProcessor)
			Kit.inputs.removeProcessor((InputProcessor)binding.listener);
	}

}
