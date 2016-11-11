package net.mgsx.game.core.ui;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
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

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.EditableSystem;
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
	
	public static interface Accessor
	{
		public Object get();
		public void set(Object value);
		public String getName();
		public Class getType();
	}
	
	private static class VoidAccessor implements Accessor
	{
		private final Object object;
		private final Method method;
		private final String name;
		
		public VoidAccessor(Object object, Method method) {
			this(object, method, method.getName());
		}
		
		public VoidAccessor(Object object, Method method, String name) {
			super();
			this.object = object;
			this.method = method;
			this.name = name;
		}

		@Override
		public Object get() {
			ReflectionHelper.invoke(object, method);
			return null;
		}

		@Override
		public void set(Object value) {
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public Class getType() {
			return void.class;
		}
		
	}
	
	private static class FieldAccessor implements Accessor
	{
		private Object object;
		private Field field;
		private String label;
		
		public FieldAccessor(Object object, Field field, String labelName) {
			super();
			this.object = object;
			this.field = field;
			this.label = labelName;
		}
		public FieldAccessor(Object object, Field field) {
			super();
			this.object = object;
			this.field = field;
			this.label = field.getName();
		}
		public FieldAccessor(Object object, String fieldName) {
			super();
			this.object = object;
			this.field = ReflectionHelper.field(object, fieldName);
			this.label = fieldName;
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
			return label;
		}
		@Override
		public Class getType() {
			return field.getType();
		}
		
	}
	public static class MethodAccessor implements Accessor
	{
		private Object object;
		private String name;
		private Method getter;
		private Method setter;
		
		
		public MethodAccessor(Object object, String name, String getter, String setter) {
			super();
			this.object = object;
			this.name = name;
			this.getter = ReflectionHelper.method(object.getClass(), getter);
			this.setter = ReflectionHelper.method(object.getClass(), setter, this.getter.getReturnType());
		}
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
		
		// first scan class to get all accessors
		Array<Accessor> accessors = new Array<Accessor>();
		
		// scan fields
		for(Field field : entity.getClass().getFields())
		{
			if(Modifier.isStatic(field.getModifiers())) continue;
			
			if(annotationBased){
				Editable editable = field.getAnnotation(Editable.class);
				if(editable == null){
					continue;
				}
				if(editable.value().isEmpty())
					accessors.add(new FieldAccessor(entity, field));
				else
					accessors.add(new FieldAccessor(entity, field, editable.value()));
			}
			else
			{
				accessors.add(new FieldAccessor(entity, field));
			}
		}
		
		// scan getter/setter pattern
		for(Method method : entity.getClass().getMethods())
		{
			if(Modifier.isStatic(method.getModifiers())) continue;
			
			if(annotationBased){
				Editable editable = method.getAnnotation(Editable.class);
				if(editable == null){
					continue;
				}
				if(method.getReturnType() != void.class) continue;
				if(method.getParameterCount() > 0) continue;
				if(editable.value().isEmpty())
					accessors.add(new VoidAccessor(entity, method));
				else
					accessors.add(new VoidAccessor(entity, method, editable.value()));
			}
			else
			{
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
			
		}
		
		for(final Accessor accessor : accessors)
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
	static public void createSlider(final Table table, final Object entity, final Accessor accessor){
		createSlider(table, entity, accessor, entity, accessor);
	}	
	static public void createSlider(final Table table, final Object rootEntity, final Accessor rootField, final Object entity, final Accessor accessor){
		final Label label = new Label("", table.getSkin()){
			@Override
			public void act(float delta) {
				super.act(delta);
				setText(String.valueOf(accessor.get()));
			}
		};
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
		
		if(accessor.getType() == int.class){
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
					btPlus.fire(new EntityEvent(entity, accessor, value));
				}
			});
			btMinus.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					int value = -1 + (Integer)accessor.get();
					accessor.set(value);
					label.setText(String.valueOf(value));
					btMinus.fire(new EntityEvent(entity, accessor, value));
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
					table.fire(new EntityEvent(entity, accessor, btCheck.isChecked()));
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