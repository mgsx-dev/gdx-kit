package net.mgsx.fwk.editor;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeLibrary;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeLibraryManager;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeParser;
import com.badlogic.gdx.ai.tests.btree.dog.Dog;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.mgsx.core.Editor;
import net.mgsx.plugins.btree.BTreeEditor;
import net.mgsx.plugins.btree.BTreeModel;

public class BTreeEditorTest 
{

	public static void main(String[] args) 
	{
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new Editor(){
			@Override
			public void create() 
			{
				super.create();
				
				registerPlugin(BTreeModel.class, new BTreeEditor());
				
				
				
				Entity entity = entityEngine.createEntity();
				entityEngine.addEntity(entity);
				
				final BTreeModel model = new BTreeModel();
				BehaviorTreeLibrary library = new BehaviorTreeLibrary();
				
				// BehaviorTree<Entity> tree = new BehaviorTree<Entity>();
				
				Dog dog = new Dog();
				dog.name = "Dog 1";
				dog.brainLog = "Dog 1 brain";
				
				entity.add(dog);
				
				
				BehaviorTree<Entity> tree = new BehaviorTreeParser<Entity>(BehaviorTreeParser.DEBUG_LOW)
					.parse(BTreeEditorTest.class.getClassLoader().getResourceAsStream(BTreeEditorTest.class.getPackage().getName().replaceAll("\\.", "/") + "/btree-official-example.btree"), null);
				
				
				// tree.addChild(new Success<Entity>());
				
				
				
				library.registerArchetypeTree("toto", tree);
				
				BehaviorTreeLibraryManager.getInstance().setLibrary(library);
				model.tree = BehaviorTreeLibraryManager.getInstance().createBehaviorTree("toto", entity);
				
				entity.add(model);
				// setSelection(entity);
				
				TextButton btStep = new TextButton("Step", skin);
				btStep.addListener(new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						model.tree.step();
					}
				});
				
				outline.add(btStep).row();
				
			}
		}, config);
	}

}
