package net.mgsx.game.core.ui.widgets;

import com.badlogic.gdx.utils.Array;

public class TreeNode<T> {

	public Array<TreeNode<T>> children;
	
	public T object;

	public T root;
}
