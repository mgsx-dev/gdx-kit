package net.mgsx.core;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

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

public class EntityEditor extends Table
{
	public static class EntityEvent extends Event
	{
		public Object entity;
		public Field field;
		public Object value;
		public EntityEvent(Object entity, Field field, Object value) {
			super();
			this.entity = entity;
			this.field = field;
			this.value = value;
		}
	}
	public EntityEditor(Skin skin) {
		super(skin);
	}
	
	public void setEntity(Object entity)
	{
		clearChildren();
		
		try {
			if(entity != null) generate(entity);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	private void generate(final Object entity) throws IllegalArgumentException, IllegalAccessException{
		for(Field field : entity.getClass().getFields())
		{
			final Field fField = field;
			if(Modifier.isStatic(field.getModifiers())) continue;
			add(field.getName()).fill().left();
			if(field.getType() == int.class){
				// TODO slider
				final Label label = new Label("", getSkin());
				TextButton btPlus = new TextButton("+", getSkin());
				TextButton btMinus = new TextButton("-", getSkin());
				add(label);
				add(btPlus);
				add(btMinus);
				label.setText(String.valueOf(field.getInt(entity)));
				
				btPlus.addListener(new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						int value = 1 + (Integer)ReflectionHelper.get(entity, fField);
						ReflectionHelper.set(entity, fField, value);
						label.setText(String.valueOf(value));
						fire(new EntityEvent(entity, fField, value));
					}
				});
				btMinus.addListener(new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						int value = -1 + (Integer)ReflectionHelper.get(entity, fField);
						ReflectionHelper.set(entity, fField, value);
						label.setText(String.valueOf(value));
						fire(new EntityEvent(entity, fField, value));
					}
				});
				
			}else if(field.getType() == float.class){
				
				createSlider(entity, fField, entity, fField);
				
				
			}else if(field.getType() == boolean.class){
				String value = String.valueOf(field.getBoolean(entity));
				final TextButton btCheck = new TextButton(value, getSkin());
				btCheck.setChecked(field.getBoolean(entity));
				btCheck.addListener(new ChangeListener() {
					
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						btCheck.setText(String.valueOf(btCheck.isChecked()));
						fire(new EntityEvent(entity, fField, btCheck.isChecked()));
					}
				});
				add(btCheck);
				
			}else if(field.getType() == Vector2.class){
				Vector2 v = ReflectionHelper.get(entity, fField, Vector2.class);
				add("(");
				createSlider(entity, fField, v, ReflectionHelper.field(v, "x"));
				add(",");
				createSlider(entity, fField, v, ReflectionHelper.field(v, "y"));
				add(")");
			}else if(field.getType().isEnum()){
				final SelectBox<Object> selector = new SelectBox<Object>(getSkin());
				Array<Object> values = new Array<Object>();
				for(Object o : field.getType().getEnumConstants()) values.add(o);
				selector.setItems(values);
				selector.setSelected(ReflectionHelper.get(entity, fField));
				selector.addListener(new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						ReflectionHelper.set(entity, fField, selector.getSelected());
					}
				});
				add(selector);
			}else{
				EntityEditor subEditor = new EntityEditor(getSkin());
				subEditor.setEntity(field.get(entity));
				add(subEditor).fill();
			}
			row();
		}
	}
	
	private void createSlider(final Object rootEntity, final Field rootField, final Object entity, final Field fField){
		final Label label = new Label("", getSkin());
		label.setText(String.valueOf(ReflectionHelper.get(entity, fField)));
		add(label);
		label.addListener(new DragListener(){
			@Override
			public void drag(InputEvent event, float x, float y,
					int pointer) {
				float value = (Float)ReflectionHelper.get(entity, fField);
				value += value == 0 ? 0.1f : -getDeltaX() * value * 0.01;
				ReflectionHelper.set(entity, fField, value);
				label.setText(String.valueOf(value));
				
				fire(new EntityEvent(rootEntity, rootField, ReflectionHelper.get(rootEntity, rootField)));
			}
		});
	}
	
}