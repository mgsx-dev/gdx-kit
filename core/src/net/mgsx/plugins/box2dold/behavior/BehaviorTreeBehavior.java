package net.mgsx.plugins.box2dold.behavior;

import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.BehaviorTree.Listener;
import com.badlogic.gdx.ai.btree.BranchTask;
import com.badlogic.gdx.ai.btree.Decorator;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.Task.Status;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeLibraryManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import net.mgsx.plugins.box2d.model.Box2DBodyModel;
import net.mgsx.plugins.box2dold.model.WorldItem;

/**
 * Editor behavior with underlying behavior tree applied on body.
 * real BlackBoard could be obtain throw body user data (in editor case : the related BodyItem)
 */
public class BehaviorTreeBehavior extends BodyBehavior
{
	private BehaviorTree<Body> bTree;
	
	public BehaviorTreeBehavior(WorldItem worldItem, Box2DBodyModel bodyItem, String treeReference) 
	{
		bTree = BehaviorTreeLibraryManager.getInstance().createBehaviorTree(treeReference, bodyItem.body);
		bTree.setObject(bodyItem.body);
	}
	
	@Override
	public void act() {
		bTree.step();
	}
	
	public void createDisplay(Table parent){
		createDisplay(parent, bTree, 0);
	}
	
	private void createDisplay(Table parent, final Task<Body> task, int depth)
	{
		final Label taskLabel = new Label(task.getClass().getSimpleName(), parent.getSkin());
		bTree.addListener(new Listener<Body>() {
			@Override
			public void statusUpdated(Task<Body> currentTask, Status previousStatus) {
				if(currentTask == task){
					switch(currentTask.getStatus()){
					case CANCELLED: 
						taskLabel.setColor(Color.BLACK);
						break;
					case FAILED:
						taskLabel.setColor(Color.RED);
						break;
					case FRESH:
						taskLabel.setColor(Color.GRAY);
						break;
					case RUNNING:
						taskLabel.setColor(Color.WHITE);
						break;
					case SUCCEEDED:
						taskLabel.setColor(Color.GREEN);
						break;
					default:
						break;}
				}
			}

			@Override
			public void childAdded(Task<Body> task, int index) {
				// TODO unsupported ...
			}
		});
		parent.add(taskLabel).padLeft(depth * 30); // TODO nb pixels
		// TODO add status ... ?
		if(task instanceof Decorator){
			Task<Body> decorated = task.getChild(0);
			createDisplay(parent, decorated, depth); // TODO +1 or not ?
			parent.row();
		}else if(task instanceof BranchTask){
			parent.row();
			for(int i=0 ; i<bTree.getChildCount() ; i++){
				createDisplay(parent, bTree.getChild(i), depth+1);
			}
		}else{
			parent.row();
		}
		
	}
	
}
