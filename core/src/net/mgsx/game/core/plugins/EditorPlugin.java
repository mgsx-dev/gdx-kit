package net.mgsx.game.core.plugins;

import net.mgsx.game.core.Editor;

/**
 * Base plugin for editor.
 */
public class EditorPlugin  // TODO could be an interface ?
{
	/**
	 * Initialize plugin from an editor.
	 * <br>
	 * On initialization, editor plugins can register the following :
	 * {@link Editor#addGlobalEditor(String, GlobalEditorPlugin)} to add a global panel if your plugin
	 * require global configuration/settings not linked to one entity/component.
	 * 
	 * {@link Editor#addSelector(SelectorPlugin)} if component can be selectable other way than defaut knot selector.
	 * 
	 * {@link Editor#addGlobalTool(net.mgsx.game.core.tools.Tool)} to add a global tool which is always active,
	 * TODO take care to not override another tool ?
	 * 
	 * {@link Editor#addTool(net.mgsx.game.core.tools.Tool)} to add a tool in the main tool group which is
	 * applyable to an entity.
	 * 
	 * @param editor current editor, subclass could keep reference to this editor.
	 */
	public void initialize(Editor editor){} // TODO dont pass the entire editor, just interface for EditorPlugin (EditorProvider) or EditorManager
}
