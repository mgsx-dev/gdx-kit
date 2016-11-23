package net.mgsx.game.core.exceptions;

import net.mgsx.game.core.storage.EntityGroup;

@SuppressWarnings("serial")
public class NotSupportedEntityGroupException extends Error{

	public NotSupportedEntityGroupException(String path, EntityGroup group){
		super("Multi proxy not supported : having " + group.size() + " elements in file " + path);
	}
}
