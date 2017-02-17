package net.mgsx.game.core.ui.widgets;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;

import net.mgsx.game.core.ui.accessors.Accessor;
import net.mgsx.game.core.ui.events.SetEvent;

public class FloatWidget extends Label
{
	private Accessor accessor;
	private boolean dynamic;
	
	@Override
	public float getPrefWidth() {
		float width = 10 * 11; // TODO compute size from font ?
		Drawable background = getStyle().background;
		if (background != null) width += background.getLeftWidth() + background.getRightWidth();
		return width;
	}
	
	public FloatWidget(Accessor accessor, boolean dynamic, Skin skin) {
		super("", skin);
		// XXX this.dynamic = dynamic;
		this.accessor = accessor;
		setAlignment(Align.center);
		setColor(Color.ORANGE);
		updateValue();
		addListener(new DragListener(){
			@Override
			public void drag(InputEvent event, float x, float y, int pointer) {
				onDrag(getDeltaX(), getDeltaY());
				// prevent other widget (like ScrollPane) to act during dragging value
				if(getStage() != null) getStage().cancelTouchFocusExcept(this, FloatWidget.this);
			}
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if(button == Input.Buttons.RIGHT){
					setValue(-getValue());
					fireChange();
					return true;
				}else if(button == Input.Buttons.MIDDLE){
					setValue(0);
					fireChange();
					return true;
				}
				return super.touchDown(event, x, y, pointer, button);
			}
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				if(button == Input.Buttons.LEFT){
					fireChange();
				}
				super.touchUp(event, x, y, pointer, button);
			}
		});
	}
	
	@Override
	public void act(float delta) 
	{
		super.act(delta);
		if(dynamic){
			updateValue();
		}
	}
	
	private void onDrag(float dx, float dy){
		float value = (Float)accessor.get();
		value += value == 0 ? 0.1f : -dx * value * 0.01;
		setValue(value);
	}
	
	private void updateValue(){
		setText(String.valueOf(getValue()));
	}
	
	private float getValue(){
		Float f = (Float)accessor.get();
		if(f.isNaN()) return 0.1f;
		return f;
	}
	
	private void setValue(float value){
		accessor.set(value);
		updateValue();
	}
	
	private void fireChange(){
		SetEvent event = new SetEvent();
		event.accessor = accessor;
		fire(event);
	}
	
	
	
	
}
