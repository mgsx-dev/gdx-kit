package net.mgsx.game.plugins.core.tools;

import com.badlogic.gdx.Gdx;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.meta.ClassRegistry;
import net.mgsx.game.core.meta.ClassRegistryExporter;
import net.mgsx.game.core.tools.Tool;

@Editable
public class ExportClassesTool extends Tool
{
	@Editable
	public String path = "../src";
	
	public ExportClassesTool(EditorScreen editor) {
		super("Export Classes", editor);
	}
	
	@Editable
	public void export(){
		new ClassRegistryExporter().export(ClassRegistry.instance, Gdx.files.local(path));
	}
}
