package net.mgsx.game.core.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.mgsx.game.core.helpers.ReflectionHelper;
import net.mgsx.game.core.ui.FieldEditor;
import net.mgsx.game.core.ui.accessors.Accessor;

public class BitsWidget implements FieldEditor
{
	public static final BitsWidget instance = new BitsWidget();
	
	@Override
	public Actor create(Accessor accessor, Skin skin) 
	{
		Table table = new Table(skin);
		int size = 0;
		
		if(ReflectionHelper.instanceOf(accessor.getType(), short.class)){
			size = 16;
		}
		
		for(int i=0 ; i<size ; i++){
			table.add(createFlagEditor(accessor, i, skin));
		}
		return table;
	}
	
	private static long asLong(Accessor accessor){
		
		long value = 0;
		if(ReflectionHelper.instanceOf(accessor.getType(), short.class)){
			value = (long)((Short)accessor.get() & 0xFFFF);
		}
		
		return value;
	}
	private static void fromLong(Accessor accessor, long value){
		if(ReflectionHelper.instanceOf(accessor.getType(), short.class)){
			accessor.set(new Short((short)(value & 0xFFFF)));
		}
	}

	private Actor createFlagEditor(final Accessor accessor, final int i, Skin skin) 
	{
		final TextButton button = new TextButton(String.valueOf(i), skin, "toggle");
		long bits = asLong(accessor);
		boolean initValue = (bits & (1 << i)) != 0;
		button.setChecked(initValue);
		button.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				long bits = asLong(accessor);
				boolean oldValue = (bits & (1 << i)) != 0;
				boolean newValue = button.isChecked();
				if(oldValue != newValue){
					if(newValue){
						bits |= 1 << i;
					}else{
						bits &= ~(1 << i); // TODO sure ? could be an or and xor
					}
				}
				fromLong(accessor, bits);
			}
		});
		
		return button;
	}

	
}
