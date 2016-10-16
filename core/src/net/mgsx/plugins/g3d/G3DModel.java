package net.mgsx.plugins.g3d;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g3d.ModelInstance;

import net.mgsx.core.storage.Storable;

public class G3DModel implements Component, Storable
{
	public ModelInstance modelInstance;
}
