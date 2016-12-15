package net.mgsx.game.plugins.btree;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeLibraryManager;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;

public class BTreeSystem extends IteratingSystem
{
	private final EntityBlackboard blackboard = new EntityBlackboard();
	
	public BTreeSystem(GameScreen game) {
		super(Family.all(BTreeModel.class).get(), GamePipeline.AFTER_LOGIC);
		blackboard.assets = game.assets;
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		blackboard.engine = engine;
	}
	
	@Override
	public void update(float deltaTime) {
		GdxAI.getTimepiece().update(deltaTime);
		super.update(deltaTime);
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		blackboard.entity = entity;
		BTreeModel model = BTreeModel.components.get(entity);
		model.tree.setObject(blackboard);
		if(model.enabled) model.tree.step();
	}

	public BehaviorTree<EntityBlackboard> createBehaviorTree(String libraryName) {
		return BehaviorTreeLibraryManager.getInstance().createBehaviorTree(libraryName, blackboard);
	}
}
