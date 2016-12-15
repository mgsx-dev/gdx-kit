package net.mgsx.game.plugins.btree.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeLibraryManager;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeLoader.BehaviorTreeParameter;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeParser;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.SerializationException;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.helpers.NativeService;
import net.mgsx.game.core.helpers.NativeService.DefaultCallback;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.plugins.btree.BTreeModel;
import net.mgsx.game.plugins.btree.EntityBlackboard;

public class BTreeTool extends Tool
{
	public BTreeTool(EditorScreen editor) {
		super("Behavor Tree", editor);
	}
	
	public static void load(EditorScreen editor, FileHandle file){
		Entity entity = editor.currentEntity();
		EntityBlackboard blackboard = new EntityBlackboard();
		blackboard.engine = editor.entityEngine;
		blackboard.entity = entity;
		blackboard.assets = editor.assets;
		
		if(!editor.assets.isLoaded(file.path())){
			BehaviorTreeParameter params = new BehaviorTreeParameter(blackboard, new BehaviorTreeParser<EntityBlackboard>());
			try{
				new BehaviorTreeParser<EntityBlackboard>(BehaviorTreeParser.DEBUG_LOW).parse(file, null);
			}catch(SerializationException e){
				// TODO raise UI Error ...
				Gdx.app.error("BTree", "Error parsing file", e);
				return;
			}
			
			BehaviorTree tree = editor.loadAssetNow(file.path(), BehaviorTree.class, params);
			BehaviorTreeLibraryManager.getInstance().getLibrary().registerArchetypeTree(file.path(), tree);
		}
		
		BTreeModel model = editor.entityEngine.createComponent(BTreeModel.class);
		
		model.libraryName = file.path();
		model.tree = BehaviorTreeLibraryManager.getInstance().createBehaviorTree(file.path(), blackboard);
		
		entity.add(model);
	}
	
	@Override
	protected void activate() {
		
		// TODO open texture region selector if any registered
		
		// else auto open import window
		NativeService.instance.openLoadDialog(new DefaultCallback() {
			@Override
			public void selected(FileHandle file) {
				load(editor, file);
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
}
