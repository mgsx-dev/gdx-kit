package net.mgsx.game.examples.shmup.editors;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.mgsx.game.blueprint.model.Graph;
import net.mgsx.game.blueprint.model.Graph.CopyStrategy;
import net.mgsx.game.blueprint.ui.GraphView;
import net.mgsx.game.core.meta.ClassRegistry;
import net.mgsx.game.core.ui.FieldEditor;
import net.mgsx.game.core.ui.accessors.Accessor;
import net.mgsx.game.examples.shmup.blueprint.ShmupNode;

public class ShmupBlueprintEditor implements FieldEditor
{

	@Override
	public Actor create(final Accessor accessor, final Skin skin) 
	{
		final TextButton bt = new TextButton("Edit", skin);
		
		bt.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				
				Graph graph = accessor.get(Graph.class);
				graph.copyStrategy = CopyStrategy.FROM_DST;
				GraphView view = new GraphView(graph, skin);
				view.addNodeType(ClassRegistry.instance.getSubTypesOf(ShmupNode.class));
				
				ScrollPane scroll = new ScrollPane(view);
				scroll.setFillParent(true);
				
				TextButton btClose = new TextButton("Close", skin);
				
				Table menu = new Table(skin);
				menu.add(btClose).expand().top().left();
				
				Table bg = new Table(skin);
				bg.setBackground("default-rect");
				bg.getColor().a = .8f;
				
				final Stack stack = new Stack();
				stack.setFillParent(true);
				stack.add(bg);
				stack.add(scroll);
				stack.add(menu);
				
				
				bt.getStage().addActor(stack);
				
				bt.getStage().setKeyboardFocus(view);
				
				btClose.addListener(new ChangeListener() {
					
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						stack.remove();
					}
				});
			}
		});
		
		return bt;
	}

}
