package net.mgsx.game.plugins.btree;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.BehaviorTree;

public class BTreeModel implements Component
{
	public String libraryName;
	public BehaviorTree<Entity> tree;
}
