package net.mgsx.game.plugins.btree.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.utils.random.ConstantIntegerDistribution;
import com.badlogic.gdx.ai.utils.random.IntegerDistribution;
import com.badlogic.gdx.ai.utils.random.TriangularIntegerDistribution;
import com.badlogic.gdx.ai.utils.random.UniformIntegerDistribution;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.ui.EntityEditor;
import net.mgsx.game.core.ui.FieldEditor;
import net.mgsx.game.core.ui.accessors.Accessor;
import net.mgsx.game.core.ui.accessors.AccessorBase;

public class IntegerDistributionEditor implements FieldEditor
{
	@Override
	public Actor create(final Accessor accessor, Skin skin) 
	{
		final Table table = new Table(skin);
		
		create(table, accessor, skin);
		
		return table;
	}
	
	private void create(final Table table, final Accessor accessor, final Skin skin)
	{
		IntegerDistribution d = accessor.get(IntegerDistribution.class);
		if(d instanceof ConstantIntegerDistribution){
			createConstant(table, accessor, skin);
		}else if(d instanceof UniformIntegerDistribution){
			createUniform(table, accessor, skin);
		}else if(d instanceof TriangularIntegerDistribution){
			createTriangular(table, accessor, skin);
		}else{
			Gdx.app.error("Editor", "unsupported type " + d.getClass());
		}
	}
	
	private void createConstant(final Table table, final Accessor accessor, final Skin skin)
	{
		EntityEditor.createControl(table, accessor.get(), new AccessorBase() {
			@Override
			public void set(Object value) {
				accessor.set(new ConstantIntegerDistribution((Integer)value));
			}
			@Override
			public Class getType() {
				return int.class;
			}
			
			@Override
			public String getName() {
				return "value";
			}
			
			@Override
			public Object get() {
				return accessor.get(ConstantIntegerDistribution.class).getValue();
			}
			
			@Override
			public Editable config() {
				return null;
			}
		});
		TextButton btPlus = new TextButton(">", skin);
		table.add(btPlus);
		btPlus.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) 
			{
				ConstantIntegerDistribution current = (ConstantIntegerDistribution)accessor.get();
				accessor.set(new UniformIntegerDistribution(current.getValue(), current.getValue()));
				table.clear();
				create(table, accessor, skin);
			}
		});
	}

	private void createUniform(final Table table, final Accessor accessor, final Skin skin)
	{
		EntityEditor.createControl(table, accessor.get(), new AccessorBase() {
			@Override
			public void set(Object value) {
				int high = accessor.get(UniformIntegerDistribution.class).getHigh();
				accessor.set(new UniformIntegerDistribution((Integer)value, high));
			}
			@Override
			public Class getType() {
				return int.class;
			}
			
			@Override
			public String getName() {
				return "low";
			}
			
			@Override
			public Object get() {
				return accessor.get(UniformIntegerDistribution.class).getLow();
			}
			
			@Override
			public Editable config() {
				return null;
			}
		});
		
		EntityEditor.createControl(table, accessor.get(), new AccessorBase() {
			@Override
			public void set(Object value) {
				int low = accessor.get(UniformIntegerDistribution.class).getLow();
				accessor.set(new UniformIntegerDistribution(low, (Integer)value));
			}
			@Override
			public Class getType() {
				return int.class;
			}
			
			@Override
			public String getName() {
				return "high";
			}
			
			@Override
			public Object get() {
				return accessor.get(UniformIntegerDistribution.class).getHigh();
			}
			
			@Override
			public Editable config() {
				return null;
			}
		});
		
		TextButton btMinus = new TextButton("<", skin);
		table.add(btMinus);
		btMinus.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) 
			{
				int low = accessor.get(UniformIntegerDistribution.class).getLow();
				int high = accessor.get(UniformIntegerDistribution.class).getHigh();
				
				accessor.set(new ConstantIntegerDistribution((low + high) / 2));
				table.clear();
				create(table, accessor, skin);
			}
		});
		
		TextButton btPlus = new TextButton(">", skin);
		table.add(btPlus);
		btPlus.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) 
			{
				UniformIntegerDistribution current = accessor.get(UniformIntegerDistribution.class);
				accessor.set(new TriangularIntegerDistribution(current.getLow(), current.getHigh()));
				table.clear();
				create(table, accessor, skin);
			}
		});
	}
	
	private void createTriangular(final Table table, final Accessor accessor, final Skin skin)
	{
		EntityEditor.createControl(table, accessor.get(), new AccessorBase() {
			@Override
			public void set(Object value) {
				int high = accessor.get(TriangularIntegerDistribution.class).getHigh();
				float mode = accessor.get(TriangularIntegerDistribution.class).getMode();
				accessor.set(new TriangularIntegerDistribution((Integer)value, high, mode));
			}
			@Override
			public Class getType() {
				return int.class;
			}
			
			@Override
			public String getName() {
				return "low";
			}
			
			@Override
			public Object get() {
				return accessor.get(TriangularIntegerDistribution.class).getLow();
			}
			
			@Override
			public Editable config() {
				return null;
			}
		});
		
		EntityEditor.createControl(table, accessor.get(), new AccessorBase() {
			@Override
			public void set(Object value) {
				int low = accessor.get(TriangularIntegerDistribution.class).getLow();
				float mode = accessor.get(TriangularIntegerDistribution.class).getMode();
				accessor.set(new TriangularIntegerDistribution(low, (Integer)value, mode));
			}
			@Override
			public Class getType() {
				return int.class;
			}
			
			@Override
			public String getName() {
				return "high";
			}
			
			@Override
			public Object get() {
				return accessor.get(TriangularIntegerDistribution.class).getHigh();
			}
			
			@Override
			public Editable config() {
				return null;
			}
		});
		
		EntityEditor.createControl(table, accessor.get(), new AccessorBase() {
			@Override
			public void set(Object value) {
				int low = accessor.get(TriangularIntegerDistribution.class).getLow();
				int high = accessor.get(TriangularIntegerDistribution.class).getHigh();
				accessor.set(new TriangularIntegerDistribution(low, high, (Float)value));
			}
			@Override
			public Class getType() {
				return float.class;
			}
			
			@Override
			public String getName() {
				return "mode";
			}
			
			@Override
			public Object get() {
				return accessor.get(TriangularIntegerDistribution.class).getMode();
			}
			
			@Override
			public Editable config() {
				return null;
			}
		});
		
		TextButton btMinus = new TextButton("<", skin);
		table.add(btMinus);
		btMinus.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) 
			{
				int low = accessor.get(TriangularIntegerDistribution.class).getLow();
				int high = accessor.get(TriangularIntegerDistribution.class).getHigh();
				
				accessor.set(new UniformIntegerDistribution(low, high));
				table.clear();
				create(table, accessor, skin);
			}
		});
		
		
		
	}
}
