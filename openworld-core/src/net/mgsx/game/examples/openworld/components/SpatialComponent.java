package net.mgsx.game.examples.openworld.components;

import java.util.Comparator;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector3;

public class SpatialComponent implements Component
{
	
	public final static ComponentMapper<SpatialComponent> components = ComponentMapper.getFor(SpatialComponent.class);

	public static Comparator<SpatialComponent> distanceComparator = new Comparator<SpatialComponent>() {
		@Override
		public int compare(SpatialComponent o1, SpatialComponent o2) {
			return Float.compare(o1.distance, o2.distance);
		}
	};
	
	public Vector3 delta = new Vector3();
	public Vector3 center = new Vector3();
	
	public float distance, angle, occlusion, seed;
}
