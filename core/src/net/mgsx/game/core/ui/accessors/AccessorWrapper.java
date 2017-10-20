package net.mgsx.game.core.ui.accessors;

import java.lang.annotation.Annotation;

abstract public class AccessorWrapper extends AccessorBase
{
	protected Accessor original;
	
	public AccessorWrapper(Accessor original) {
		super();
		this.original = original;
	}

	@Override
	public <T extends Annotation> T config(Class<T> annotation) {
		return original.config(annotation);
	}
}
