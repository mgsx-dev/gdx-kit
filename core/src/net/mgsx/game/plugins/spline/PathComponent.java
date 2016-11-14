package net.mgsx.game.plugins.spline;

import com.badlogic.ashley.core.Component;

import net.mgsx.SplineTest.BlenderCurve;
import net.mgsx.game.core.annotations.Storable;

@Storable("spline")
public class PathComponent implements Component
{
	public BlenderCurve path;
}
