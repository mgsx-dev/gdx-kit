package net.mgsx.game.core.profiling;

public class ScalarStatistics {

	private float min, sum, max;
	private float [] history;
	private int counter, index;
	
	public ScalarStatistics(int size) 
	{
		history = new float[size];
	}
	
	public void push(float value)
	{
		if(counter == 0){
			min = max = value; 
		}else {
			index = (index + 1) % history.length;
			if(value < min) min = value;
			if(value > max) max = value;
		}
		if(counter > history.length){
			sum -= history[index];
		}
		sum += history[index] = value;
		counter++;
	}
	
	public void clear(){
		counter = index = 0;
		min = max = sum = 0;
	}
	
	public float getMin() {
		return min;
	}
	
	public float getMax() {
		return max;
	}
	
	public float getAverage(){
		int total = Math.min(counter, history.length);
		return total <= 0 ? 0 : sum / total;
	}
}
