package net.mgsx.game.core.binding;

public interface Learner {

	public void startLearning(LearnListener listener);
	public void stopLearning();
	
	public void bind(Binding binding);
	public void unbind(Binding binding);
}
