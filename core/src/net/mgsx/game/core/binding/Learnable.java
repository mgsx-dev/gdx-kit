package net.mgsx.game.core.binding;

import net.mgsx.game.core.ui.accessors.Accessor;

public interface Learnable {

	public Accessor accessorToBind();
	
	public String bindKey();

}
