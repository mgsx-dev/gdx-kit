package net.mgsx.game.plugins.box2d.tools.shapes;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.tools.RectangleTool;
import net.mgsx.game.plugins.box2d.systems.Box2DEditorSettings;
import net.mgsx.game.plugins.box2d.systems.Box2DWorldContext;

public abstract class AbstractBoundShapeTool extends RectangleTool{

	@Editable
	final public Box2DEditorSettings settings;
	
	final protected Box2DWorldContext worldItem;
	
	public AbstractBoundShapeTool(String name, EditorScreen editor, Box2DWorldContext worldItem) {
		super(name, editor);
		this.worldItem = worldItem;
		settings = worldItem.settings;
	}

}
