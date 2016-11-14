package net.mgsx.game.plugins.btree;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.BehaviorTree;

import net.mgsx.game.core.annotations.Storable;

@Storable("btree")
public class BTreeModel implements Component
{
	public String libraryName;
	public BehaviorTree<Entity> tree;
}
