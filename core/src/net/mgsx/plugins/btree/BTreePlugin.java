package net.mgsx.plugins.btree;

import net.mgsx.core.Editor;
import net.mgsx.core.plugins.EditorPlugin;
import net.mgsx.core.storage.Storage;

public class BTreePlugin extends EditorPlugin
{
	@Override
	public void initialize(Editor editor) 
	{
		Storage.register(BTreeModel.class, "btree");
	}
}
