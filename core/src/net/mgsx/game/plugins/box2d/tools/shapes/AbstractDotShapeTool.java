package net.mgsx.game.plugins.box2d.tools.shapes;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.tools.MultiClickTool;
import net.mgsx.game.plugins.box2d.systems.Box2DEditorSettings;
import net.mgsx.game.plugins.box2d.systems.Box2DWorldContext;

public abstract class AbstractDotShapeTool extends MultiClickTool
{
	@Editable
	final public Box2DEditorSettings settings;
	
	final protected Box2DWorldContext worldItem;
	
	public AbstractDotShapeTool(String name, EditorScreen editor, Box2DWorldContext worldItem) {
		this(name, editor, worldItem, -1);
	}
	
	public AbstractDotShapeTool(String name, EditorScreen editor, Box2DWorldContext worldItem, int maxPoints) {
		super(name, editor, maxPoints);
		this.worldItem = worldItem;
		settings = worldItem.settings;
	}


}
