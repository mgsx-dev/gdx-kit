package net.mgsx.game.core.ui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.ui.accessors.Accessor;
import net.mgsx.game.core.ui.accessors.AccessorScanner;
import net.mgsx.game.core.ui.accessors.FieldAccessor;
import net.mgsx.game.core.ui.widgets.FloatWidget;

public class EntityEditor extends Table
{
	private Array<Object> stack = new Array<Object>();
	private final boolean annotationBased;
	
	public EntityEditor(Skin skin) {
		this(skin, false);
	}
	public EntityEditor(Skin skin, boolean annotationBased) {
		this(null, annotationBased, skin);
	}
	public EntityEditor(Object entity, Skin skin) {
		this(entity, false, skin);
	}
	public EntityEditor(Object entity, boolean annotationBased, Skin skin) {
		super(skin);
		this.annotationBased = annotationBased;
		setEntity(entity);
	}
	
	private EntityEditor(Object entity, boolean annotationBased, Skin skin, Array<Object> stack) {
		super(skin);
		this.annotationBased = annotationBased;
		this.stack = stack;
		generate(entity, this);
	}
	
	public void setEntity(Object entity)
	{
		stack.clear();
		
		clearChildren();
		
		if(entity != null)
		{
			generate(entity, this);
		}
	}
	
	public void generate(final Object entity, final Table table)
	{
		// prevent cycles
		if(entity == null || stack.contains(entity, true)) return;
		stack.add(entity);
		
		if(annotationBased){
			boolean match = false;
			
			if(entity.getClass().getAnnotation(EditableSystem.class) != null) match = true;
			if(entity.getClass().getAnnotation(EditableComponent.class) != null) match = true;
			
			if(!match) return;
		}
		
		// scan class to get all accessors
		for(final Accessor accessor : AccessorScanner.scan(entity, annotationBased))
		{
			Label accessorLabel = new Label(accessor.getName(), table.getSkin());
			table.add(accessorLabel).fill().left();
			
			if(!createControl(table, entity, accessor, stack))
			{
				// create recursively on missing type (object)
				// TODO background ?
				accessorLabel.setStyle(table.getSkin().get("tab-left", LabelStyle.class));
				Table sub = new Table(getSkin());
				sub.setBackground(getSkin().getDrawable("default-window-body-right"));
				table.add(sub).expand().fill().left();
				generate(accessor.get(), sub);
			}
			
			table.row();
		}
	}
	
	private static void createSlider2D(Table table, Object entity, String name, final Quaternion q) {
		Label ctrl = new Label("CTRL", table.getSkin());
		ctrl.addListener(new DragListener(){
			Quaternion m = new Quaternion();
			@Override
			public void drag(InputEvent event, float x, float y, int pointer) {
				float dx = getDeltaX();
				float dy = getDeltaY();
				q.mul(m.setEulerAngles(dx, dy,0));
				event.cancel();
			}
		});
		table.add(ctrl);
	}
	static private void createSlider(final Table table, final Object rootEntity, final Accessor rootField, final Object entity, final Accessor accessor){
		final Label label = new FloatWidget(accessor, true, table.getSkin());
		table.add(label);
	}
	public static Button createBoolean(Skin skin, boolean value) 
	{
		final TextButton btCheck = new TextButton(String.valueOf(value), skin, "toggle");
		btCheck.setChecked(value);
		btCheck.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				btCheck.setText(String.valueOf(btCheck.isChecked()));
			}
		});
		return btCheck;
	}
	public static boolean createControl(final Table table, final Object entity, final Accessor accessor) 
	{
		return createControl(table, entity, accessor, new Array<Object>());
	}
	private static boolean createControl(final Table table, final Object entity, final Accessor accessor, Array<Object> stack) 
	{
		Skin skin = table.getSkin();
		
		if(accessor.getType() == void.class){
			TextButton bt = new TextButton(accessor.getName(), skin);
			table.add(bt);
			bt.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					accessor.get();
				}
			});
		}else if(accessor.getType() == int.class){
			// TODO slider
			Table sub = new Table(skin);
			final Label label = new Label("", skin);
			final TextButton btPlus = new TextButton("+", skin);
			final TextButton btMinus = new TextButton("-", skin);
			sub.add(btMinus);
			sub.add(label).pad(4);
			sub.add(btPlus);
			label.setText(String.valueOf(accessor.get()));
			
			table.add(sub).fill();
			
			btPlus.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					int value = 1 + (Integer)accessor.get();
					accessor.set(value);
					label.setText(String.valueOf(value));
					// XXX btPlus.fire(new EntityEvent(entity, accessor, value));
				}
			});
			btMinus.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					int value = -1 + (Integer)accessor.get();
					accessor.set(value);
					label.setText(String.valueOf(value));
					// XXX btMinus.fire(new EntityEvent(entity, accessor, value));
				}
			});
			
		}else if(accessor.getType() == float.class){
			
			createSlider(table, entity, accessor, entity, accessor);
			
			
		}else if(accessor.getType() == String.class){
			
			final TextField field = new TextField(String.valueOf(accessor.get()), skin);
			table.add(field);
			field.addListener(new ChangeListener(){
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					accessor.set(field.getText());
				}
			});
			
			field.addListener(new ClickListener(){
				@Override
				public void touchDragged(InputEvent event, float x, float y, int pointer) {
					field.getStage().cancelTouchFocusExcept(field.getDefaultInputListener(), field);
					super.touchDragged(event, x, y, pointer);
				}
				@Override
				public boolean keyDown(InputEvent event, int keycode) {
					if(keycode == Input.Keys.ENTER) field.getStage().setKeyboardFocus(null);
					return super.keyDown(event, keycode);
				}
			});
			
		}else if(accessor.getType() == boolean.class){
			final Button btCheck = createBoolean(skin, (Boolean)accessor.get());
			btCheck.addListener(new ChangeListener() {
				
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					accessor.set(btCheck.isChecked());
					// XXX table.fire(new EntityEvent(entity, accessor, btCheck.isChecked()));
				}
			});
			table.add(btCheck);
			
		}else if(accessor.getType() == Vector2.class){
			Vector2 v = (Vector2)accessor.get();
			Table sub = new Table(table.getSkin());
			sub.add("(");
			createSlider(sub, entity, accessor, v, new FieldAccessor(v, "x"));
			sub.add(",");
			createSlider(sub, entity, accessor, v, new FieldAccessor(v, "y"));
			sub.add(")");
			table.add(sub);
		}else if(accessor.getType() == Quaternion.class){
			Quaternion q = (Quaternion)accessor.get();
			createSlider2D(table, entity, accessor.getName(), q);
		}else if(accessor.getType() == Color.class){
			Color c = (Color)accessor.get();
			
			Table sub = new Table(table.getSkin());
			sub.add("(");
			createSlider(sub, entity, accessor, c, new FieldAccessor(c, "r"));
			sub.add(",");
			createSlider(sub, entity, accessor, c, new FieldAccessor(c, "g"));
			sub.add(",");
			createSlider(sub, entity, accessor, c, new FieldAccessor(c, "b"));
			sub.add(",");
			createSlider(sub, entity, accessor, c, new FieldAccessor(c, "a"));
			sub.add(")");
			table.add(sub);
		}else if(accessor.getType().isEnum()){
			final SelectBox<Object> selector = new SelectBox<Object>(skin);
			Array<Object> values = new Array<Object>();
			for(Object o : accessor.getType().getEnumConstants()) values.add(o);
			selector.setItems(values);
			selector.setSelected(accessor.get());
			selector.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					accessor.set(selector.getSelected());
				}
			});
			table.add(selector);
		}else{
			
			table.add(new EntityEditor(accessor.get(), false, skin, stack)).row();
			// XXX return false;
		}
		return true;
	}
	
}