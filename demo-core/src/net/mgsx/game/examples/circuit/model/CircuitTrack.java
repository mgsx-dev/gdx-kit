package net.mgsx.game.examples.circuit.model;

import java.util.Comparator;

import com.badlogic.gdx.utils.Array;

public class CircuitTrack {

	public float v;
	
	private CircuitModel circuit;
	public Array<CircuitCar> cars = new Array<CircuitCar>();
	private Comparator<CircuitCar> comparator = new Comparator<CircuitCar>() {
		@Override
		public int compare(CircuitCar o1, CircuitCar o2) {
			return Float.compare(o1.uvPos.x, o2.uvPos.x);
		}
	};

	public float vLen;
	public boolean ccw;
	
	public CircuitTrack(CircuitModel circuit, float v, float vLen, boolean ccw) {
		super();
		this.circuit = circuit;
		this.v = v;
		this.vLen = vLen;
		this.ccw = ccw;
	}

	public CircuitCar getCarFront(CircuitCar baseCar) {
		if(ccw)
			return cars.get((cars.indexOf(baseCar, true)+1)%cars.size);
		else
			return cars.get((cars.indexOf(baseCar, true)-1+cars.size)%cars.size);
	}

	public void update(float delta) {
		cars.sort(comparator );
	}

	public CircuitTrack getFastTrack() {
		int index = circuit.tracks.indexOf(this, true);
		if(index == 0){
			return circuit.tracks.get(1);
		}
		if(index == 3){
			return circuit.tracks.get(2);
		}
		return null;
	}
}
