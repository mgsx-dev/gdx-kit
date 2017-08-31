package net.mgsx.game.core.ui.accessors;

import java.lang.annotation.Annotation;

import net.mgsx.game.core.annotations.Editable;

abstract public class AccessorWrapper extends AccessorBase
{
	protected Accessor original;
	
	public AccessorWrapper(Accessor original) {
		super();
		this.original = original;
	}

	@Override
	public Editable config() {
		return original.config();
	}
	@Override
	public <T extends Annotation> T config(Class<T> annotation) {
		return original.config(annotation);
	}
}
