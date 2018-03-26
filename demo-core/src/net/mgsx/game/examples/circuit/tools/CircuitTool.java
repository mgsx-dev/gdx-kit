package net.mgsx.game.examples.circuit.tools;

import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.tools.ClickTool;
import net.mgsx.game.examples.circuit.model.CircuitModel;

@Editable
public class CircuitTool extends ClickTool{

	@Inject
	public CircuitModel circuit;
	
	public CircuitTool(EditorScreen editor) {
		super("Circuit Tool", editor);
	}

	@Override
	protected void create(Vector2 position) {
		// TODO find nearest dot
		circuit.dots.add(position);
		circuit.invalidate();
	}

}
