package net.mgsx.fwk.editor.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class TabPane extends Table
{
	private Table tabs;
	private ButtonGroup<Button> buttons;
	private Stack stack;
	private Actor visible;
	public TabPane(Skin skin) 
	{
		super(skin);
		
		stack = new Stack();
		buttons = new ButtonGroup<Button>();
		tabs = new Table(skin);
		add(tabs).row();
		add(stack).row(); // let caller add footer
	}
	
	public Button addTab(String title, final Actor page){
		final TextButton button = new TextButton(title, getSkin());
		button.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				changePage(button, page);
			}
		});
		buttons.add(button);
		stack.add(page);
		page.setVisible(false);
		tabs.add(button);
		return button;
	}
	
	public void setTab(int index){
		changePage(buttons.getButtons().get(index), stack.getChildren().get(index));
	}
	public void setTab(Actor page){
		setTab(stack.getChildren().indexOf(page, true));
	}

	private void changePage(Button button, Actor page) 
	{
		if(visible != null) visible.setVisible(false);
		visible = page;
		page.setVisible(true);
	}
}
