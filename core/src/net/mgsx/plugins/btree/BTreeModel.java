package net.mgsx.plugins.btree;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.BehaviorTree;

import net.mgsx.core.storage.Storable;

public class BTreeModel implements Component, Storable
{
	public String libraryName;
	public BehaviorTree<Entity> tree;
}
