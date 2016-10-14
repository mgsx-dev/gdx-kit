package net.mgsx.fwk.editor;

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
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;

import net.mgsx.plugins.btree.BTreeEditor;
import net.mgsx.plugins.btree.BTreeModel;

public class AspectEditorTest 
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
				
				Entity entity = Entity.create();
				
				final BTreeModel model = new BTreeModel();
				BehaviorTreeLibrary library = new BehaviorTreeLibrary();
				
				// BehaviorTree<Entity> tree = new BehaviorTree<Entity>();
				
				Dog dog = new Dog();
				dog.name = "Dog 1";
				dog.brainLog = "Dog 1 brain";
				
				entity.set(dog);
				
				
				BehaviorTree<Entity> tree = new BehaviorTreeParser<Entity>(BehaviorTreeParser.DEBUG_LOW)
					.parse(AspectEditorTest.class.getClassLoader().getResourceAsStream(AspectEditorTest.class.getPackage().getName().replaceAll("\\.", "/") + "/btree-official-example.btree"), null);
				
				
				// tree.addChild(new Success<Entity>());
				
				
				
				library.registerArchetypeTree("toto", tree);
				
				BehaviorTreeLibraryManager.getInstance().setLibrary(library);
				model.tree = BehaviorTreeLibraryManager.getInstance().createBehaviorTree("toto", entity);
				
				entity.set(model);
				setSelection(entity);
				
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
