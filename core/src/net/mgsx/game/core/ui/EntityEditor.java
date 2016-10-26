package net.mgsx.game.core.ui;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.helpers.ReflectionHelper;

public class EntityEditor extends Table
{
	public static class EntityEvent extends Event
	{
		public Object entity;
		public Accessor accessor;
		public Object value;
		public EntityEvent(Object entity, Accessor accessor, Object value) {
			super();
			this.entity = entity;
			this.accessor = accessor;
			this.value = value;
		}
	}
	private Array<Object> stack = new Array<Object>();
	
	public EntityEditor(Skin skin) {
		super(skin);
		setBackground(skin.getDrawable("default-window"));
	}
	public EntityEditor(Object entity, Skin skin) {
		this(skin);
		setEntity(entity);
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
	
	public static interface Accessor
	{
		public Object get();
		public void set(Object value);
		public String getName();
		public Class getType();
	}
	private static class FieldAccessor implements Accessor
	{
		private Object object;
		private Field field;
		
		public FieldAccessor(Object object, Field field) {
			super();
			this.object = object;
			this.field = field;
		}
		public FieldAccessor(Object object, String fieldName) {
			super();
			this.object = object;
			this.field = ReflectionHelper.field(object, fieldName);
		}

		@Override
		public Object get() {
			return ReflectionHelper.get(object, field);
		}

		@Override
		public void set(Object value) {
			ReflectionHelper.set(object, field, value);
		}
		@Override
		public String getName() {
			return field.getName();
		}
		@Override
		public Class getType() {
			return field.getType();
		}
		
	}
	private static class MethodAccessor implements Accessor
	{
		private Object object;
		private String name;
		private Method getter;
		private Method setter;
		
		
		public MethodAccessor(Object object, String name, Method getter, Method setter) {
			super();
			this.object = object;
			this.name = name;
			this.getter = getter;
			this.setter = setter;
		}

		@Override
		public Object get() {
			return ReflectionHelper.invoke(object, getter);
		}

		@Override
		public void set(Object value) {
			ReflectionHelper.invoke(object, setter, value);
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public Class getType() {
			return getter.getReturnType();
		}
		
	}
	
	private void generate(final Object entity, final Table table)
	{
		// prevent cycles
		if(entity == null || stack.contains(entity, true)) return;
		stack.add(entity);
		
		// first scan class to get all accessors
		Array<Accessor> accessors = new Array<Accessor>();
		
		// scan fields
		for(Field field : entity.getClass().getFields())
		{
			if(Modifier.isStatic(field.getModifiers())) continue;
			
			accessors.add(new FieldAccessor(entity, field));
		}
		
		// scan getter/setter pattern
		for(Method method : entity.getClass().getMethods())
		{
			if(Modifier.isStatic(method.getModifiers())) continue;
			
			if(method.getName().startsWith("set") && method.getName().length() > 3 && method.getParameterCount() == 1)
			{
				String getterName = "g" + method.getName().substring(1);
				Method getter = ReflectionHelper.method(entity.getClass(), getterName);
				if(getter == null || getter.getReturnType() != method.getParameterTypes()[0]){
					// try boolean pattern setX/isX
					getterName = "is" + method.getName().substring(3);
					getter = ReflectionHelper.method(entity.getClass(), getterName);
				}
				if(getter != null && getter.getReturnType() == method.getParameterTypes()[0]){
					String name = method.getName().substring(3,4).toLowerCase() + method.getName().substring(4);
					accessors.add(new MethodAccessor(entity, name, getter, method));
				}
			}
		}
		
		for(final Accessor accessor : accessors)
		{
			table.add(accessor.getName()).fill().left();
			if(accessor.getType() == int.class){
				// TODO slider
				Table sub = new Table(table.getSkin());
				final Label label = new Label("", getSkin());
				TextButton btPlus = new TextButton("+", getSkin());
				TextButton btMinus = new TextButton("-", getSkin());
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
						fire(new EntityEvent(entity, accessor, value));
					}
				});
				btMinus.addListener(new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						int value = -1 + (Integer)accessor.get();
						accessor.set(value);
						label.setText(String.valueOf(value));
						fire(new EntityEvent(entity, accessor, value));
					}
				});
				
			}else if(accessor.getType() == float.class){
				
				createSlider(table, entity, accessor, entity, accessor);
				
				
			}else if(accessor.getType() == boolean.class){
				String value = String.valueOf(accessor.get());
				final TextButton btCheck = new TextButton(value, getSkin(), "toggle");
				btCheck.setChecked((Boolean)accessor.get());
				btCheck.addListener(new ChangeListener() {
					
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						btCheck.setText(String.valueOf(btCheck.isChecked()));
						accessor.set(btCheck.isChecked());
						fire(new EntityEvent(entity, accessor, btCheck.isChecked()));
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
			}else if(accessor.getType().isEnum()){
				final SelectBox<Object> selector = new SelectBox<Object>(getSkin());
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
				// TODO background ?
				Table sub = new Table(getSkin());
				sub.setBackground(getSkin().getDrawable("default-window"));
				table.add(sub).expand().fill().left();
				generate(accessor.get(), sub);
			}
			table.row();
		}
	}
	
	private void createSlider2D(Table table, Object entity, String name, final Quaternion q) {
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
	static public void createSlider(final Table table, final Object entity, final Accessor accessor){
		createSlider(table, entity, accessor, entity, accessor);
	}	
	static public void createSlider(final Table table, final Object rootEntity, final Accessor rootField, final Object entity, final Accessor accessor){
		final Label label = new Label("", table.getSkin());
		label.setText(String.valueOf(accessor.get()));
		table.add(label);
		label.addListener(new DragListener(){
			@Override
			public void drag(InputEvent event, float x, float y,
					int pointer) {
				float value = (Float)accessor.get();
				value += value == 0 ? 0.1f : -getDeltaX() * value * 0.01;
				accessor.set(value);
				label.setText(String.valueOf(value));
				
				table.fire(new EntityEvent(rootEntity, rootField, rootField.get()));
				
				 // prevent other widget (like ScrollPane) to act during dragging value
				label.getStage().cancelTouchFocusExcept(this, label);
			}
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if(button == Input.Buttons.RIGHT){
					float value = (Float)accessor.get();
					value = -value;
					accessor.set(value);
					label.setText(String.valueOf(value));
					
					table.fire(new EntityEvent(rootEntity, rootField, rootField.get()));

					return true;
				}
				return super.touchDown(event, x, y, pointer, button);
			}
		});
	}
	
}