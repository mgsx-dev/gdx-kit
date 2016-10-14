package net.mgsx.plugins.btree;

import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.BranchTask;
import com.badlogic.gdx.ai.btree.Decorator;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.BehaviorTree.Listener;
import com.badlogic.gdx.ai.btree.Task.Status;
import com.badlogic.gdx.ai.btree.branch.Sequence;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import net.mgsx.fwk.editor.Entity;
import net.mgsx.fwk.editor.plugins.EditablePlugin;

public class BTreeEditor implements EditablePlugin<BTreeModel> {

	public Actor createEditor(BTreeModel object, Skin skin) {
		BehaviorTree<Entity> bTree = object.tree;
		Table table = new Table(skin);
		createEditor(table, bTree, bTree, skin, 0);
		return table;
	}
	public void createEditor(final Table parent, BehaviorTree<Entity> tree, final Task<Entity> task, Skin skin, int depth) 
	{
		final Label taskLabel = new Label(task.getClass().getSimpleName(), parent.getSkin());
		taskLabel.getStyle().background = parent.getSkin().getDrawable("default");
		tree.addListener(new Listener<Entity>() {
			@Override
			public void statusUpdated(Task<Entity> currentTask, Status previousStatus) {
				if(currentTask == task){
					Color color = new Color();
					switch(currentTask.getStatus()){
					case CANCELLED: 
						color.set(Color.BLACK);
						break;
					case FAILED:
						color.set(Color.RED);
						break;
					case FRESH:
						color.set(Color.GRAY);
						break;
					case RUNNING:
						color.set(Color.WHITE);
						break;
					case SUCCEEDED:
						color.set(Color.GREEN);
						break;
					default:
						break;
					}
					if(currentTask instanceof LeafTask){
						color.mul(1f);
					}else{
						color.mul(.5f);
					}
					// color.mul(0.5f);
					if(!color.equals(taskLabel.getColor())){
						Action delegate = Actions.addAction(
								Actions.parallel(
										Actions.color(color, 0.1f), 
										Actions.sequence(
												Actions.scaleTo(2, 2),
												Actions.scaleTo(1, 1, 0.25f)
										)
								), taskLabel
						);
						parent.addAction(Actions.after(Actions.sequence(delegate, Actions.delay(0.5f))));
					}
				}
			}

			@Override
			public void childAdded(Task<Entity> task, int index) {
				// TODO unsupported ...
			}
		});
		Cell cell = parent.add(taskLabel).padLeft(depth * 60); // TODO nb pixels
		// TODO add status ... ?
		if(task instanceof Sequence){
			// TODO ninepatch on the left with loop
			taskLabel.setText("sequence []");
			cell.fillY();
			Table sub = new Table(skin);
			for(int i=0 ; i<task.getChildCount() ; i++){
				createEditor(sub, tree, task.getChild(i), skin, 0);
			}
			parent.add(sub);
			
		}
		else if(task instanceof Decorator){
			Task<Entity> decorated = task.getChild(0);
			createEditor(parent, tree, decorated, skin, depth); // TODO +1 or not ?
			parent.row();
		}else if(task instanceof BranchTask || task instanceof BehaviorTree){
			parent.row();
			for(int i=0 ; i<task.getChildCount() ; i++){
				createEditor(parent, tree, task.getChild(i), skin, depth+1);
			}
		}else{
			parent.row();
		}
		
	}

}
