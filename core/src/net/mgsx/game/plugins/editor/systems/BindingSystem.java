package net.mgsx.game.plugins.editor.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.utils.ObjectMap.Entry;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.binding.Binding;
import net.mgsx.game.core.binding.BindingManager;
import net.mgsx.game.core.storage.SystemSettingsListener;

@Storable(value="core.bindings", auto=true)
@EditableSystem()
public class BindingSystem extends EntitySystem implements SystemSettingsListener
{
	@Editable
	public String [] bindings = {};
	
	private EditorScreen editor;
	
	public BindingSystem(EditorScreen editor) {
		super();
		this.editor = editor;
	}
	@Override
	public void addedToEngine(Engine engine) 
	{
		super.addedToEngine(engine);
	}
	@Override
	public void removedFromEngine(Engine engine) {
		BindingManager.clear();
		
		super.removedFromEngine(engine);
	}
	@Override
	public void onSettingsLoaded() 
	{
		BindingManager.clear();
		
		if(bindings != null)
			for(String str : bindings)
			{
				String [] strs = str.split(":", 2);
				Binding b = new Binding();
				b.target = strs[0];
				b.command = strs[1];
				BindingManager.applyBindings(b, editor.stage);
			}
	}
	@Override
	public void beforeSettingsSaved() 
	{
		bindings = new String[BindingManager.bindings().size]; 
		int i=0;
		for(Entry<String, Binding> entry : BindingManager.bindings()){
			bindings[i++] = entry.key + ":" + entry.value.command;
		}
		
	}
}
