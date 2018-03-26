package net.mgsx.game.examples.circuit.model;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class CircuitTemplates {

	public static void simple(CircuitModel circuit, Model model)
	{
		float range = 460;
		
		int segments = 30;
		for(int i=0 ; i<segments ; i++){
			float alpha = 360f * i / (float)segments;
			float alpha2 = alpha * 2;
			float range2 = 10 * (MathUtils.sin(alpha/2)+1)/2;
			circuit.dots.add(new Vector2(range * MathUtils.cosDeg(alpha), range * MathUtils.sinDeg(alpha)).add(new Vector2(range2 * MathUtils.cosDeg(alpha2), range2 * MathUtils.sinDeg(alpha2))));
		}
		
//		circuit.dots.add(new Vector2(0, 0));
//		
//		
//		circuit.dots.add(new Vector2(1 * range, 1 * range));
//		circuit.dots.add(new Vector2(-1 * range, 1 * range));
//		circuit.dots.add(new Vector2(-1 * range, -1 * range));
//
//		circuit.dots.add(new Vector2(0 * range, -1 * range));
//		circuit.dots.add(new Vector2(1 * range, -1 * range));
//		circuit.dots.add(new Vector2(1 * range, -2 * range));
//		circuit.dots.add(new Vector2(2 * range, -2 * range));
//		circuit.dots.add(new Vector2(2 * range, -1 * range));
//
//		circuit.dots.add(new Vector2(1 * range, -1 * range));
		
		circuit.tracks.add(new CircuitTrack(circuit, .00f, .25f, true));
		circuit.tracks.add(new CircuitTrack(circuit, .25f, .25f, true));
		circuit.tracks.add(new CircuitTrack(circuit, .50f, .25f, false));
		circuit.tracks.add(new CircuitTrack(circuit, .75f, .25f, false));
		
		int nbCars = 40;
		for(CircuitTrack track : circuit.tracks){
			for(int i=0 ; i<nbCars ; i++){
				
				CircuitCar car = new CircuitCar(track);
				car.uvPos.x = (float)i / (float)(nbCars);
				car.uvDir.y = 0;
				car.angle = 0;
				car.busy = 0;
				car.prefSpeed = MathUtils.random(50, 100);
				car.speed = 0;
				
				car.model = new ModelInstance(model, "car.1", false);
				
				track.cars.add(car);
				circuit.cars.add(car);
			}
		}
	}
	
	public static void simpleBig(CircuitModel circuit)
	{
		float range = 100;
		circuit.dots.add(new Vector2(0, 0));
		circuit.dots.add(new Vector2(1 * range, 1 * range));
		circuit.dots.add(new Vector2(-1 * range, 1 * range));
		circuit.dots.add(new Vector2(-1 * range, -1 * range));
		circuit.dots.add(new Vector2(1 * range, -1 * range));
		
		circuit.tracks.add(new CircuitTrack(circuit, .00f, .25f, true));
		circuit.tracks.add(new CircuitTrack(circuit, .25f, .25f, true));
		circuit.tracks.add(new CircuitTrack(circuit, .50f, .25f, false));
		circuit.tracks.add(new CircuitTrack(circuit, .75f, .25f, false));
		
		int nbCars = 10;
		for(CircuitTrack track : circuit.tracks){
			for(int i=0 ; i<nbCars ; i++){
				
				CircuitCar car = new CircuitCar(track);
				car.uvPos.x = (float)i / (float)(nbCars);
				car.uvDir.y = 0;
				car.angle = 0;
				car.busy = 0;
				car.prefSpeed = MathUtils.random(50, 100);
				car.speed = 0;
				
				track.cars.add(car);
				circuit.cars.add(car);
			}
		}
	}
}
