package net.mgsx.game.plugins.core.math;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;

abstract public class Signal extends Interpolation
{
	public static final Signal sin = new Signal() {
		
		@Override
		public float apply(float a) {
			return MathUtils.sin(a * MathUtils.PI2);
		}
	};
	
}
