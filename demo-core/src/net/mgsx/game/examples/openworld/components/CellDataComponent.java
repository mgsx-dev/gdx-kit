package net.mgsx.game.examples.openworld.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

import net.mgsx.game.examples.openworld.model.OpenWorldPool;
import net.mgsx.game.examples.openworld.model.OpenWorldPool.CellData;

public class CellDataComponent implements Component, Poolable
{
	public CellData data;

	@Override
	public void reset() {
		OpenWorldPool.freeCellData(data);
		data = null;
	}
	
}
