package net.mgsx.game.plugins.btree;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.Task.Status;
import com.badlogic.gdx.ai.btree.leaf.Success;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeParser;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Clipboard;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.SerializationException;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.helpers.NativeService;
import net.mgsx.game.core.helpers.NativeService.DefaultCallback;
import net.mgsx.game.core.helpers.ReflectionHelper;
import net.mgsx.game.core.plugins.EntityEditorPlugin;
import net.mgsx.game.core.ui.accessors.FieldAccessor;
import net.mgsx.game.core.ui.widgets.EnumWidget;
import net.mgsx.game.core.ui.widgets.TreeEditor;
import net.mgsx.game.core.ui.widgets.TreeNode;
import net.mgsx.game.plugins.btree.storage.BehaviorTreeWriter;
import net.mgsx.game.plugins.btree.storage.BehaviorTreeXmlWriter;
import net.mgsx.game.plugins.btree.tools.BTreeTool;
import net.mgsx.game.plugins.btree.ui.TaskEditor;

public class BTreeEditor implements EntityEditorPlugin {

	private EditorScreen editor;
	
	// TODO should be in editor instance not this factory
	public Status editStatus = null;
	
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
			protected Actor createRenderer(final TreeNode<Task<EntityBlackboard>> node) {
				final TaskEditor e = new TaskEditor<EntityBlackboard>((BehaviorTree<EntityBlackboard>)node.root, node.object, getSkin());
				e.addListener(new ChangeListener(){
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						if(actor == e){
							if(editStatus != null){
								switch(editStatus){
								case CANCELLED:
									node.object.cancel();
									break;
								case FAILED:
									node.object.fail();
									break;
								case FRESH:
									node.object.reset();
									break;
								case RUNNING:
									node.object.running();
									break;
								case SUCCEEDED:
									node.object.success();
									break;
								default:
									break;
								}
								
							}
							
						}
					}
				});
				return e;
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
				FileHandle file = Gdx.files.internal(model.libraryName);
				if(file.extension().equals("xml"))
					new BehaviorTreeXmlWriter().write(entity.getComponent(BTreeModel.class).tree, file);
				else
					new BehaviorTreeWriter().write(model.tree, Gdx.files.internal(model.libraryName));
			}
		});
		
		TextButton btSaveXML = new TextButton("Save as XML...", skin);
		btSaveXML.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				NativeService.instance.openSaveDialog(new DefaultCallback() {
					@Override
					public void selected(FileHandle file) {
						new BehaviorTreeXmlWriter().write(entity.getComponent(BTreeModel.class).tree, file);
					}
					@Override
					public boolean match(FileHandle file) {
						return file.extension().equals("xml");
					}
					@Override
					public String description() {
						return "BehaviorTree files (xml)";
					}
				});
			}
		});
		
		TextButton btNew = new TextButton("New", skin);
		btNew.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				BTreeModel model = entity.getComponent(BTreeModel.class);
				model.tree = new BehaviorTree<EntityBlackboard>();
				model.tree.addChild(new Success<EntityBlackboard>());
				treeEditor.setRoot(convertToTree(model.tree, model.tree));
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
						return file.extension().equals("btree");
					}
					@Override
					public String description() {
						return "BehaviorTree files (btree)";
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
						entity.getComponent(BTreeModel.class).libraryName = file.path();
						libraryLabel.setText(entity.getComponent(BTreeModel.class).libraryName);
						BehaviorTree<EntityBlackboard> btree = entity.getComponent(BTreeModel.class).tree;
						treeEditor.setRoot(convertToTree(btree, btree));
					}
					@Override
					public boolean match(FileHandle file) {
						return file.extension().equals("btree") || file.extension().equals("xml");
					}
					@Override
					public String description() {
						return "BehaviorTree files (btree, xml)";
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
		
		TextButton btManual = new TextButton("Manual Step", skin);
		btManual.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				BehaviorTree<EntityBlackboard> btree = entity.getComponent(BTreeModel.class).tree;
				btree.getObject().entity = entity;
				btree.step();
			}
		});
		
		TextButton btResetTree = new TextButton("Reset Tree", skin);
		btResetTree.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				BehaviorTree<EntityBlackboard> btree = entity.getComponent(BTreeModel.class).tree;
				btree.getObject().entity = entity;
				btree.reset();
			}
		});
		
		TextButton btCopy = new TextButton("To Freemind", skin);
		btCopy.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				BehaviorTree<EntityBlackboard> btree = entity.getComponent(BTreeModel.class).tree;
				StringWriter w = new StringWriter();
				try{
					new BehaviorTreeWriter().writeTree(btree, new PrintWriter(w));
					String content = w.toString();
					Gdx.app.getClipboard().setContents(content);
				}catch(GdxRuntimeException e){
					Gdx.app.error("BTreePlugin", "can't serialize tree", e);
				}
			}
		});
		
		TextButton btPaste = new TextButton("From Freemind", skin);
		btPaste.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Clipboard clipboard = Gdx.app.getClipboard();
				String freeMindContent = clipboard.getContents();
				// freeMindContent = freeMindContent.replaceAll("\n    ", "\n").replaceFirst(".*\n", "");
				try{
					// add imports
					StringWriter w = new StringWriter();
					new BehaviorTreeWriter().writeAllImports(new PrintWriter(w));
					w.write("\n");
					w.write(freeMindContent);
					BehaviorTree<EntityBlackboard> newtree = new BehaviorTreeParser<EntityBlackboard>().parse(w.toString(), null);
					BehaviorTree<EntityBlackboard> btree = entity.getComponent(BTreeModel.class).tree = newtree;
					treeEditor.setRoot(convertToTree(btree, btree));
				}catch(SerializationException e){
					
					Gdx.app.error("BTreePlugin", "error parsing from clipboard : \n" + freeMindContent, e);
				}
			}
		});
		
		Actor statusSelector = new EnumWidget<Task.Status>(Task.Status.class).create(new FieldAccessor(this, "editStatus"), skin);
		
		
		Table menu = new Table(skin);
		
		menu.add(btNew);
		menu.add(btReload);
		menu.add(btSave);
		menu.add(btSaveXML);
		menu.add(btLoad);
		menu.add(btSaveAs);
		menu.add(btManual);
		menu.add(btResetTree);
		menu.add(btCopy);
		menu.add(btPaste);
		menu.add(statusSelector);
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
		final TaskEditor<EntityBlackboard> editor = new TaskEditor<EntityBlackboard>(tree, task, skin);
		
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
