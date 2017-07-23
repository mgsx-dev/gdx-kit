package net.mgsx.game.core.helpers.shaders;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import net.mgsx.game.core.ui.FieldEditor;
import net.mgsx.game.core.ui.accessors.Accessor;
import net.mgsx.game.core.ui.accessors.MethodAccessor;
import net.mgsx.game.core.ui.accessors.VoidAccessor;
import net.mgsx.game.core.ui.widgets.BooleanWidget;
import net.mgsx.game.core.ui.widgets.VoidWidget;

public class ShaderProgramManagedEditor implements FieldEditor
{
	@Override
	public Actor create(Accessor accessor, Skin skin) {
		ShaderProgramManaged spm = accessor.get(ShaderProgramManaged.class);
		
		Table table = new Table(skin);
		
		table.add(VoidWidget.instance.create(new VoidAccessor(spm, "reload"), skin));
		
		// TODO helper for find method ...
		table.add(BooleanWidget.labeled("Frozen").create(new MethodAccessor(spm, "frozen", "isFrozen", "freeze"), skin));
		
		table.add(VoidWidget.instance.create(new VoidAccessor(spm, "dumpVS"), skin));
		table.add(VoidWidget.instance.create(new VoidAccessor(spm, "dumpFS"), skin));
		
		return table;
	}

}
