package net.mgsx.game.plugins.btree;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.helpers.NativeService;
import net.mgsx.game.core.helpers.NativeService.DefaultCallback;
import net.mgsx.game.core.helpers.ReflectionHelper;
import net.mgsx.game.core.plugins.EntityEditorPlugin;
import net.mgsx.game.core.ui.widgets.TreeEditor;
import net.mgsx.game.core.ui.widgets.TreeNode;
import net.mgsx.game.plugins.btree.storage.BehaviorTreeWriter;
import net.mgsx.game.plugins.btree.tools.BTreeTool;
import net.mgsx.game.plugins.btree.ui.TaskEditor;

public class BTreeEditor implements EntityEditorPlugin {

	private EditorScreen editor;
	
	public BTreeEditor(EditorScreen editor) {
		super();
		this.editor = editor;
	}
	
	private static <T> TreeNode<Task<T>> convertToTree(Task<T> root, Task<T> task){
		TreeNode<Task<T>> node = new TreeNode<Task<T>>();
		node.root = root;
		node.object = task;
		node.children = new Array<TreeNode<Task<T>>>();
		for(int i=0 ; i<task.getChildCount() ; i++){
			node.children.add(convertToTree(root, task.getChild(i)));
		}
		
		return node;
	}
	
	public Actor createEditor(final Entity entity, Skin skin) {
		final BTreeModel model = entity.getComponent(BTreeModel.class);
		BehaviorTree<EntityBlackboard> bTree = model.tree;
		
		final TreeEditor<Task<EntityBlackboard>> treeEditor = new TreeEditor<Task<EntityBlackboard>>(skin) {
			
			@Override
			protected Actor createRenderer(TreeNode<Task<EntityBlackboard>> node) {
				return new TaskEditor<EntityBlackboard>((BehaviorTree<EntityBlackboard>)node.root, node.object, getSkin());
			}

			@Override
			protected int maxChildren(TreeNode<Task<EntityBlackboard>> node) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			protected void commit() {
				// TODO Auto-generated method stub
			}
		};
		treeEditor.setRoot(convertToTree(bTree, bTree));
		
		Table table = new Table(skin);
		table.defaults().expand().left();
		
		final SelectBox<String> taskSelector = new SelectBox<String>(skin);
		Array<String> items = BTreePlugin.allTasks();
		items.insert(0, "");
		taskSelector.setItems(items);
		
		final Label libraryLabel = new Label(model.libraryName, skin);
		
		taskSelector.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				String type = taskSelector.getSelected();
				if(!type.isEmpty()){
					Task<EntityBlackboard> task = ReflectionHelper.newInstance(BTreePlugin.task(type));
					EntityBehaviorTree tree = new EntityBehaviorTree();
					tree.addChild(task);
					EntityBlackboard blackboard = new EntityBlackboard();
					blackboard.assets = editor.assets;
					blackboard.engine = editor.entityEngine;
					blackboard.entity = entity;
					tree.setObject(blackboard);
					model.tree = tree;
					BehaviorTree<EntityBlackboard> btree = entity.getComponent(BTreeModel.class).tree;
					treeEditor.setRoot(convertToTree(btree, btree));
				}
			}
		});

		TextButton btSave = new TextButton("Save", skin);
		btSave.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				BTreeModel model = entity.getComponent(BTreeModel.class);
				new BehaviorTreeWriter().write(model.tree, Gdx.files.internal(model.libraryName));
			}
		});
		

		TextButton btSaveAs = new TextButton("Save As...", skin);
		btSaveAs.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				NativeService.instance.openSaveDialog(new DefaultCallback() {
					@Override
					public void selected(FileHandle file) {
						new BehaviorTreeWriter().write(entity.getComponent(BTreeModel.class).tree, file);
					}
					@Override
					public boolean match(FileHandle file) {
						return file.extension().equals("json");
					}
					@Override
					public String description() {
						return "BehaviorTree files (json)";
					}
				});
			}
		});
		
		TextButton btLoad = new TextButton("Load...", skin);
		btLoad.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				NativeService.instance.openSaveDialog(new DefaultCallback() {
					@Override
					public void selected(FileHandle file) {
						BTreeTool.load(editor, file);
						libraryLabel.setText(entity.getComponent(BTreeModel.class).libraryName);
						BehaviorTree<EntityBlackboard> btree = entity.getComponent(BTreeModel.class).tree;
						treeEditor.setRoot(convertToTree(btree, btree));
					}
					@Override
					public boolean match(FileHandle file) {
						return file.extension().equals("json");
					}
					@Override
					public String description() {
						return "BehaviorTree files (json)";
					}
				});
			}
		});
		
		TextButton btReload = new TextButton("Reload", skin);
		btReload.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				editor.assets.reload(entity.getComponent(BTreeModel.class).libraryName);
				BehaviorTree<EntityBlackboard> btree = entity.getComponent(BTreeModel.class).tree;
				treeEditor.setRoot(convertToTree(btree, btree));
			}
		});
		
		Table menu = new Table(skin);
		
		menu.add(btReload);
		menu.add(btSave);
		menu.add(btLoad);
		menu.add(btSaveAs);
		menu.add(libraryLabel);
		
		menu.add("task test : ");
		menu.add(taskSelector).row();
		
		table.add(menu).row();
		table.add(treeEditor).row();
		
		// createEditor(table, bTree, bTree, skin, 0);
		return table;
	}
	public void createEditor(final Table parent, final BehaviorTree<EntityBlackboard> tree, final Task<EntityBlackboard> task, Skin skin, int depth) 
	{
		TaskEditor<EntityBlackboard> editor = new TaskEditor<EntityBlackboard>(tree, task, skin);
		
		parent.add(editor);
		
		Table sub = new Table(skin);
		sub.defaults().expand().left();
		for(int i=0 ; i<task.getChildCount() ; i++){
			// sub.add().padLeft(30);
			createEditor(sub, tree, task.getChild(i), skin, 0);
		}
		parent.add(sub).expand().left().row();
		
		
	}

}
