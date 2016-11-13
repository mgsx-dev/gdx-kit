package net.mgsx.game.core.plugins;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.GameScreen;

/**
 * Base plugin for editor.
 */
public abstract class EditorPlugin implements Plugin
{
	/**
	 * Initialize plugin from an editor.
	 * <br>
	 * On initialization, editor plugins can register the following :
	 * {@link EditorScreen#addGlobalEditor(String, GlobalEditorPlugin)} to add a global panel if your plugin
	 * require global configuration/settings not linked to one entity/component.
	 * 
	 * {@link EditorScreen#addSelector(SelectorPlugin)} if component can be selectable other way than defaut knot selector.
	 * 
	 * {@link EditorScreen#addGlobalTool(net.mgsx.game.core.tools.Tool)} to add a global tool which is always active,
	 * TODO take care to not override another tool ?
	 * 
	 * {@link EditorScreen#addTool(net.mgsx.game.core.tools.Tool)} to add a tool in the main tool group which is
	 * applyable to an entity.
	 * 
	 * @param editor current editor, subclass could keep reference to this editor.
	 */
	public abstract void initialize(EditorScreen editor); // TODO dont pass the entire editor, just interface for EditorPlugin (EditorProvider) or EditorManager

	@Override
	public void initialize(GameScreen engine) {
		// TODO ??? nothing ? make reference to cam and entity engine ?!
		
	}
}
