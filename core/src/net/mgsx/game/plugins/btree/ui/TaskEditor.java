package net.mgsx.game.plugins.btree.ui;

import java.lang.reflect.Field;

import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.BehaviorTree.Listener;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.Task.Status;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;
import com.badlogic.gdx.ai.btree.branch.Parallel;
import com.badlogic.gdx.ai.btree.branch.Parallel.Policy;
import com.badlogic.gdx.ai.btree.branch.Selector;
import com.badlogic.gdx.ai.btree.branch.Sequence;
import com.badlogic.gdx.ai.btree.leaf.Failure;
import com.badlogic.gdx.ai.btree.leaf.Success;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.Disposable;

import net.mgsx.game.core.ui.EntityEditor;
import net.mgsx.game.core.ui.accessors.FieldAccessor;

public class TaskEditor<T> extends Table implements Disposable, Listener<T>
{
	public static class TaskSelectEvent extends ChangeEvent
	{
		public Task task;

		public TaskSelectEvent(Task task) {
			super();
			this.task = task;
		}
		
	}
	
	private BehaviorTree<T> tree;
	private Task<T> task;
	private Color color = new Color();
	private Button taskLabel;
	private Status status;
	
	public TaskEditor(BehaviorTree<T> tree, final Task<T> task, Skin skin) {
		super(skin);
		this.tree = tree;
		this.task = task;
		tree.addListener(this);
		defaults().left();
		// TODO alias ?
		taskLabel = new Button(skin);
		add(taskLabel).expand().fill();
		
		boolean displayFields = false;
		boolean hasIcon = true;
		if(task.getClass() == Sequence.class){
			taskLabel.add(new Image(skin.getDrawable("btree_sequence")));
		}
		else if(task.getClass() == Selector.class){
			taskLabel.add(new Image(skin.getDrawable("btree_selector")));
		}
		else if(task.getClass() == Parallel.class){
			if(((Parallel)task).policy == Policy.Selector)
				taskLabel.add(new Image(skin.getDrawable("btree_parallel-selector")));
			else
				taskLabel.add(new Image(skin.getDrawable("btree_parallel")));
		}
		else if(task.getClass() == Success.class){
			taskLabel.add(new Image(skin.getDrawable("btree_success")));
		}
		else if(task.getClass() == Failure.class){
			taskLabel.add(new Image(skin.getDrawable("btree_failure")));
		}
		else{
			hasIcon = false;
		}
		if(hasIcon){
			// taskLabel.add(task.getClass().getSimpleName()).padLeft(3);
		}else{
			taskLabel.add(task.getClass().getSimpleName());
		}
		displayFields = true;
		
		
		if(displayFields){
			for(Field field : task.getClass().getFields()){
				TaskAttribute taskAttribute = field.getAnnotation(TaskAttribute.class);
				if(taskAttribute != null){
					String name = taskAttribute.name().isEmpty() ? field.getName() : taskAttribute.name();
					add(new Label(name, skin));
					
					EntityEditor.createControl(this, task, new FieldAccessor(task, field, name));
				}
			}
		}
		
		statusUpdated(task, null);
		
		taskLabel.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				TaskEditor.this.fire(new ChangeEvent());
			}
		});
	}

	@Override
	public void dispose() {
		tree.removeListener(this);
	}
	
	@Override
	protected void setParent(Group parent) {
		if(parent == null) dispose();
		super.setParent(parent);
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		// handle reset event (not notified)
		if(task.getStatus() != status){
			statusUpdated(task, status);
		}
	}
	
	
	@Override
	public void statusUpdated(Task<T> task, Status previousStatus) {
		if(this.task == task && status != task.getStatus()){
			status = task.getStatus();
			switch(task.getStatus()){
			case CANCELLED: 
				color.set(Color.CYAN);
				break;
			case FAILED:
				color.set(Color.RED);
				break;
			case FRESH:
				color.set(Color.WHITE);
				break;
			case RUNNING:
				color.set(Color.ORANGE);
				break;
			case SUCCEEDED:
				color.set(Color.GREEN);
				break;
			default:
				color.set(Color.BLACK);
				break;
			}
			if(task instanceof LeafTask){
				color.mul(1f);
			}else{
				color.mul(.5f);
			}
			taskLabel.setColor(color);
		}
	}

	@Override
	public void childAdded(Task<T> task, int index) {
		
	}

	public void resetStatus() {
		statusUpdated(task, null);
	}
	
	
	
}
