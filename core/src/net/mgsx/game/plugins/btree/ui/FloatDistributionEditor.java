package net.mgsx.game.plugins.btree.ui;

import com.badlogic.gdx.ai.utils.random.ConstantFloatDistribution;
import com.badlogic.gdx.ai.utils.random.FloatDistribution;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.ui.EntityEditor;
import net.mgsx.game.core.ui.FieldEditor;
import net.mgsx.game.core.ui.accessors.Accessor;
import net.mgsx.game.core.ui.accessors.AccessorBase;

public class FloatDistributionEditor implements FieldEditor
{

	@Override
	public Actor create(final Accessor accessor, Skin skin) 
	{
		Table table = new Table(skin);
		
		FloatDistribution d = accessor.get(FloatDistribution.class);
		if(d instanceof ConstantFloatDistribution){
			EntityEditor.createControl(table, d, new AccessorBase() {
				@Override
				public void set(Object value) {
					accessor.set(new ConstantFloatDistribution((Float)value));
				}
				@Override
				public Class getType() {
					return float.class;
				}
				
				@Override
				public String getName() {
					return "value";
				}
				
				@Override
				public Object get() {
					return accessor.get(ConstantFloatDistribution.class).getValue();
				}
				
				@Override
				public Editable config() {
					return null;
				}
			});
		}
		// TODO add support for other FloatDistribution sub types
		return table;
	}

}
