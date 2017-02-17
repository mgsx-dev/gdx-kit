package net.mgsx.game.core.meta;

import net.mgsx.game.core.annotations.Kit;

public class StaticClassRegistry extends CollectionClassRegistry
{
	public StaticClassRegistry(Class type) 
	{
		Kit config = (Kit)type.getAnnotation(Kit.class);
		if(config != null) classes.addAll(config.dependencies());
	}

}
