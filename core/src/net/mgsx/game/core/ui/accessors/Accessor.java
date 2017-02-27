package net.mgsx.game.core.ui.accessors;

import java.lang.annotation.Annotation;

import net.mgsx.game.core.annotations.Editable;

public interface Accessor
{
	public Object get();
	public void set(Object value);
	public String getName();
	public Class getType();
	public Editable config();
	public <T> T get(Class<T> type);
	public <T extends Annotation> T config(Class<T> annotation);
}