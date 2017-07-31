package net.mgsx.game.core.ui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
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
import com.badlogic.gdx.utils.ObjectMap;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.helpers.ReflectionHelper;
import net.mgsx.game.core.ui.accessors.Accessor;
import net.mgsx.game.core.ui.accessors.AccessorScanner;
import net.mgsx.game.core.ui.accessors.FieldAccessorWrapper;
import net.mgsx.game.core.ui.events.AccessorHelpEvent;
import net.mgsx.game.core.ui.widgets.BitsWidget;
import net.mgsx.game.core.ui.widgets.BlendWidget;
import net.mgsx.game.core.ui.widgets.BooleanWidget;
import net.mgsx.game.core.ui.widgets.FloatWidget;
import net.mgsx.game.core.ui.widgets.IntegerWidget;
import net.mgsx.game.core.ui.widgets.VoidWidget;

public class EntityEditor extends Table
{
	public static final ObjectMap<Class, FieldEditor> defaultTypeEditors = new ObjectMap<Class, FieldEditor>();
	
	public static class Config{
		
		public ObjectMap<Accessor, FieldEditor> accessorEditors = new ObjectMap<Accessor, FieldEditor>();
		public ObjectMap<Class, FieldEditor> typeEditors = new ObjectMap<Class, FieldEditor>();
		
		public Config() {
			typeEditors.putAll(defaultTypeEditors);
		}
	}
	
	final public Config config;
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
		this.config = new Config();
		setEntity(entity);
	}
	
	private EntityEditor(Object entity, boolean annotationBased, Skin skin, Array<Object> stack, Config config) {
		super(skin);
		this.annotationBased = annotationBased;
		this.stack = stack;
		this.config = config;
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
			if(entity.getClass().getAnnotation(Editable.class) != null) match = true;
			if(entity.getClass().getAnnotation(EditableSystem.class) != null) match = true;
			if(entity.getClass().getAnnotation(EditableComponent.class) != null) match = true;
			if(net.mgsx.game.core.tools.Tool.class.isAssignableFrom(entity.getClass())) match = true;
			
			if(!match) return;
		}
		
		// scan class to get all accessors
		for(final Accessor accessor : AccessorScanner.scan(entity, annotationBased))
		{
			Label accessorLabel = new Label(accessor.getName(), table.getSkin());
			table.add(accessorLabel).fill().left();
			
			if(accessor.config() == null || accessor.config().doc().isEmpty()){
				table.add().fill();
			}else{
				final TextButton btHelp = new TextButton("?", table.getSkin());
				table.add(btHelp).fill();
				btHelp.addListener(new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						btHelp.fire(new AccessorHelpEvent(accessor));
					}
				});
			}
			
			if(!createControl(table, entity, accessor, stack, config))
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
		// TODO ? ctrl.setTouchable(Touchable.enabled);
		ctrl.addListener(new DragListener(){
			Quaternion m = new Quaternion();
			@Override
			public void drag(InputEvent event, float x, float y, int pointer) {
				float dx = getDeltaX();
				float dy = getDeltaY();
				q.mul(m.setEulerAngles(dx, dy,0));
				event.getStage().cancelTouchFocusExcept(this, event.getTarget());
				event.cancel();
			}
		});
		table.add(ctrl);
	}
	static private void createSlider(final Table table, final Object rootEntity, final Accessor rootField, final Object entity, final Accessor accessor){
		boolean dynamic = rootField.config() != null && rootField.config().realtime();
		boolean readonly = rootField.config() != null && rootField.config().readonly();
		final Label label = new FloatWidget(accessor, dynamic, readonly, table.getSkin());
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
		return createControl(table, entity, accessor, new Array<Object>(), new Config());
	}
	
	private static boolean createControl(final Table table, final Object entity, final Accessor accessor, Array<Object> stack, Config config) 
	{
		Skin skin = table.getSkin();
		
		// find appropriate control in following order :
		// 1 - explicit editor for accessor
		// 2 - annotation on accessor (predefined types)
		// 3 - primitive types
		FieldEditor accessorEditor = config.accessorEditors.get(accessor);
		if(accessorEditor != null){
			table.add(accessorEditor.create(accessor, skin));
			return true;
		}
		FieldEditor typeEditor = config.typeEditors.get(accessor.getType());
		if(typeEditor != null){
			table.add(typeEditor.create(accessor, skin));
			return true;
		}
		
		Editable accessorConfig = accessor.config();
		
		if(accessorConfig != null && accessorConfig.editor() != DefaultFieldEditor.class){
			// TODO cache factory as singleton ...
			FieldEditor editor = ReflectionHelper.newInstance(accessorConfig.editor());
			table.add(editor.create(accessor, skin));
			return true;
		}
		
		// XXX doesn't support inherited ...
		Editable typeConfig = (Editable)accessor.getType().getAnnotation(Editable.class);
		if(typeConfig != null && typeConfig.editor() != DefaultFieldEditor.class){
			// TODO cache factory as singleton ...
			FieldEditor editor = ReflectionHelper.newInstance(typeConfig.editor());
			table.add(editor.create(accessor, skin));
			return true;
		}
		
		if(accessorConfig != null ){
			switch(accessorConfig.type()){
			case BITS: table.add(BitsWidget.instance.create(accessor, skin)); return true;
			case BLEND_MODE: table.add(BlendWidget.instance.create(accessor, skin)); return true;
				// TODO others ...
			default:
			}
			
		}
		if(accessor.getType() == void.class){
			
			table.add(VoidWidget.instance.create(accessor, skin));
		
		}else if(accessor.getType() == int.class){
			
			table.add(IntegerWidget.unlimited.create(accessor, skin)).fill();
		
		}else if(accessor.getType() == long.class){
			
			table.add(IntegerWidget.unlimited.create(accessor, skin)).fill();
		
		}else if(accessor.getType() == short.class){
			
			table.add(IntegerWidget.unsignedShort.create(accessor, skin)).fill();
		
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
			table.add(BooleanWidget.instance.create(accessor, skin));
		}else if(accessor.getType() == Vector2.class){
			Vector2 v = (Vector2)accessor.get();
			Table sub = new Table(table.getSkin());
			sub.add("(");
			createSlider(sub, entity, accessor, v, new FieldAccessorWrapper(accessor, "x"));
			sub.add(",");
			createSlider(sub, entity, accessor, v, new FieldAccessorWrapper(accessor, "y"));
			sub.add(")");
			table.add(sub);
		}else if(accessor.getType() == Vector3.class){
			Vector3 v = (Vector3)accessor.get();
			Table sub = new Table(table.getSkin());
			sub.add("(");
			createSlider(sub, entity, accessor, v, new FieldAccessorWrapper(accessor, "x"));
			sub.add(",");
			createSlider(sub, entity, accessor, v, new FieldAccessorWrapper(accessor, "y"));
			sub.add(",");
			createSlider(sub, entity, accessor, v, new FieldAccessorWrapper(accessor, "z"));
			sub.add(")");
			table.add(sub);
		}else if(accessor.getType() == Quaternion.class){
			Quaternion q = (Quaternion)accessor.get();
			createSlider2D(table, entity, accessor.getName(), q);
		}else if(accessor.getType() == Color.class){
			Color c = (Color)accessor.get();
			
			Table sub = new Table(table.getSkin());
			sub.add("(");
			createSlider(sub, entity, accessor, c, new FieldAccessorWrapper(accessor, "r"));
			sub.add(",");
			createSlider(sub, entity, accessor, c, new FieldAccessorWrapper(accessor, "g"));
			sub.add(",");
			createSlider(sub, entity, accessor, c, new FieldAccessorWrapper(accessor, "b"));
			sub.add(",");
			createSlider(sub, entity, accessor, c, new FieldAccessorWrapper(accessor, "a"));
			sub.add(")");
			table.add(sub);
		}else if(accessor.getType().isEnum()){
			// TODO EnumWidget
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
			
			// allow non editable type scan since current accessor has Editable annotation.
			boolean editableTypeOnly = false;
			
			table.add(new EntityEditor(accessor.get(), editableTypeOnly, skin, stack, config)).row();
		}
		return true;
	}
	
}