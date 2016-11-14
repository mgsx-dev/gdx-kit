package net.mgsx.game.core.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

public class TabPane extends Table
{
	private Table tabs;
	private ButtonGroup<Button> buttons;
	private Stack stack;
	private Array<Actor> pages = new Array<Actor>();
	public TabPane(Skin skin) 
	{
		super(skin);
		
		stack = new Stack();
		setBackground(skin.getDrawable("default-window-body"));
		buttons = new ButtonGroup<Button>();
		tabs = new Table(skin);
		add(tabs).left().row();
		add(stack).left().row(); // let caller add footer
	}
	
	public Button addTab(String title, final Actor page){
		final TextButton button = new TextButton(title, getSkin(), "toggle");
		button.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				changePage(button, page);
			}
		});
		buttons.add(button);
		pages.add(page);
		tabs.add(button);
		if(pages.size == 1) changePage(button, page);
		return button;
	}
	
	public void setTab(int index){
		changePage(buttons.getButtons().get(index), pages.get(index));
	}
	public void setTab(Actor page){
		setTab(pages.indexOf(page, true));
	}

	private void changePage(Button button, Actor page) 
	{
		stack.clear();
		stack.add(page);
	}
}
