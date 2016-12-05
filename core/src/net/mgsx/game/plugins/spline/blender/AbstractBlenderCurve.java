package net.mgsx.game.plugins.spline.blender;

import com.badlogic.gdx.math.Path;
import com.badlogic.gdx.math.Vector3;

public abstract class AbstractBlenderCurve{
	abstract public Path<Vector3> toPath();
}