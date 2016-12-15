package net.mgsx.game.plugins.btree;

import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeLibrary;

public class BehaviorTreeRepository extends BehaviorTreeLibrary
{
	public String findTreeReference(BehaviorTree tree){
		return repository.findKey(tree, true);
	}
}
