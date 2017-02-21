package net.mgsx.game.plugins.btree.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeLibraryManager;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeParser;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.SerializationException;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.helpers.NativeService;
import net.mgsx.game.core.helpers.NativeService.DefaultCallback;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.plugins.btree.BTreeModel;
import net.mgsx.game.plugins.btree.EntityBlackboard;
import net.mgsx.game.plugins.editor.systems.SelectionSystem;

@Editable
public class BTreeTool extends Tool
{
	public BTreeTool(EditorScreen editor) {
		super("Behavor Tree", editor);
	}
	@Editable
	public void load(){
		NativeService.instance.openLoadDialog(new DefaultCallback() {
			@Override
			public void selected(FileHandle file) {
				load(editor, file);
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
	
	@Editable
	public void empty(){
		NativeService.instance.openSaveDialog(new DefaultCallback() {
			@Override
			public void selected(FileHandle file) {
				file.writeString("success", false);
				load(editor, file);
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
	
	public static void load(EditorScreen editor, FileHandle file){
		Entity entity = editor.entityEngine.getSystem(SelectionSystem.class).currentEntity();
		
		if(!editor.assets.isLoaded(file.path())){
			if(file.extension().equals("btree")){
				try{
					new BehaviorTreeParser<EntityBlackboard>(BehaviorTreeParser.DEBUG_LOW).parse(file, null);
				}catch(SerializationException e){
					// TODO raise UI Error ...
					Gdx.app.error("BTree", "Error parsing file", e);
					return;
				}
			}
			
			editor.loadAssetNow(file.path(), BehaviorTree.class);
		}
		BTreeModel model = BTreeModel.components.get(entity);
		if(model == null){
			model = editor.entityEngine.createComponent(BTreeModel.class);
		}
		
		model.libraryName = file.path();
		model.tree = BehaviorTreeLibraryManager.getInstance().createBehaviorTree(file.path());
		
		entity.add(model);
	}
	
}
