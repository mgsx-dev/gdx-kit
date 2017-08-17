package net.mgsx.game.examples.lsystem.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.utils.Pool.Poolable;

import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.examples.lsystem.model.Generator;
import net.mgsx.game.examples.lsystem.model.LSystem;
import net.mgsx.game.examples.lsystem.model.Symbol;
import net.mgsx.game.examples.lsystem.model.l2d.Context2D;

@Storable("procedural.lsystem.3d")
@EditableComponent
public class LSystem3D implements Component, Poolable
{
	
	public final static ComponentMapper<LSystem3D> components = ComponentMapper.getFor(LSystem3D.class);
	
	transient public LSystem system;

	transient public Generator<Context2D> generator;

	transient public Symbol seed;

	@Override
	public void reset() {
		system = null;
		generator = null;
		seed = null;
	}
	
	
}
