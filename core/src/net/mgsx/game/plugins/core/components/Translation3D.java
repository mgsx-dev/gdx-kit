package net.mgsx.game.plugins.core.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector3;

public class Translation3D implements Component
{
	
	public final static ComponentMapper<Translation3D> components = ComponentMapper.getFor(Translation3D.class);
	
	public Vector3 origin = new Vector3(), target = new Vector3();
	public float time;
	public float duration = 1;
	public Interpolation interpolation = Interpolation.linear;
}
