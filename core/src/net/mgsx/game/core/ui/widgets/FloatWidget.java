package net.mgsx.game.core.ui.widgets;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.Align;

import net.mgsx.game.core.annotations.EnumType;
import net.mgsx.game.core.ui.accessors.Accessor;
import net.mgsx.game.core.ui.events.SetEvent;

public class FloatWidget extends Label
{
	private Accessor accessor;
	private boolean dynamic;
	private String typing;
	private EnumType type;
	
	@Override
	public float getPrefWidth() {
		float width = 10 * 11; // TODO compute size from font ?
		Drawable background = getStyle().background;
		if (background != null) width += background.getLeftWidth() + background.getRightWidth();
		return width;
	}
	
	public FloatWidget(Accessor accessor, boolean dynamic, boolean readonly, Skin skin) {
		super("", skin);
		this.dynamic = dynamic;
		this.accessor = accessor;
		this.type = accessor.config() != null ? accessor.config().type() : null;
		setAlignment(Align.center);
		
		if(readonly){
			setColor(Color.CYAN);
		}else{
			setColor(this.dynamic ? Color.GOLD : Color.ORANGE);
			addListener(new FocusListener() {
				@Override
				public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
					actor.setDebug(focused);
					super.keyboardFocusChanged(event, actor, focused);
				}
			});
			addListener(new ClickListener(){
				@Override
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
					if(getTapCount() >= 1){ // TODO not work properly
						typing = "";
						getStage().setKeyboardFocus(FloatWidget.this);
					}
					return super.touchDown(event, x, y, pointer, button);
				}
			});
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
				@Override
				public boolean keyTyped(InputEvent event, char character) {
					if(typing != null && character != 0){
						try{
							float f = Float.valueOf(typing + character);
							typing += character;
							setValue(f);
							fireChange();
						}catch(NumberFormatException e){
							// silent fail
						}
					}
					return super.keyTyped(event, character);
				}
				@Override
				public boolean keyDown(InputEvent event, int keycode) 
				{
					if(keycode == Input.Keys.ENTER){
						getStage().setKeyboardFocus(null);
					}else if(keycode == Input.Keys.ESCAPE){
						getStage().setKeyboardFocus(null);
					}
					
					// TODO Auto-generated method stub
					return super.keyDown(event, keycode);
				}
				
			});
		}
		updateValue();
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
		if(type == EnumType.BYTES){
			// format bytes
			float value = getValue();
			String suffix = "";
			if(value > 1024){
				value /= 1024;
				suffix = " Ko";
			}
			if(value > 1024){
				value /= 1024;
				suffix = " Mo";
			}
			if(value > 1024){
				value /= 1024;
				suffix = " Go";
			}
			if(value > 1024){
				value /= 1024;
				suffix = " To";
			}
			String formatted = String.valueOf(Math.round(value * 100) / 100f) + suffix;
			setText(formatted);
		}else{
			setText(String.valueOf(getValue()));
		}
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
