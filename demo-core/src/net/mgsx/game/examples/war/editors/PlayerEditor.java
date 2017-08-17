package net.mgsx.game.examples.war.editors;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ObjectMap.Entry;

import net.mgsx.game.core.plugins.EngineEditor;
import net.mgsx.game.examples.war.model.Goods;
import net.mgsx.game.examples.war.model.Player;
import net.mgsx.game.examples.war.model.Player.PlayerGoods;
import net.mgsx.game.examples.war.system.WarLogicSystem;

public class PlayerEditor implements EngineEditor
{
	private WarLogicSystem warLogic;
	
	@Override
	public Actor createEditor(Engine engine, AssetManager assets, Skin skin) 
	{
		warLogic = engine.getSystem(WarLogicSystem.class);
		
		Table table = new Table(skin);
		
		create(table, skin);
		
		return table;
	}
	
	private void create(final Table table, final Skin skin)
	{
		table.clear();
		
		Player player = warLogic.player;
		
		TextButton btRefresh = new TextButton("refresh", skin);
		btRefresh.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				create(table, skin);
			}
		});
		
		TextButton btTurn = new TextButton("Next Turn", skin);
		btTurn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				warLogic.nextTurn();
				create(table, skin);
			}
		});
		
		table.add("Money");
		table.add(String.format("%d$", player.money));
		table.row();
		table.add(btRefresh);
		table.add(btTurn);
		table.row();
		table.add("Goods");
		table.add("Owned");
		table.add("Value");
		table.row();
		
		for(Entry<Goods, PlayerGoods> entry : player.goods){
			final Goods goods = entry.key;
			final PlayerGoods playerGoods = entry.value;
			
			table.add(goods.name);
			table.add(String.valueOf(playerGoods.quantity));
			table.add(playerGoods.quantity <= 0 ? "-" : String.format("%d$", (int)Math.ceil(playerGoods.value / (float)playerGoods.quantity)));
			table.row();
		}
	}

	

}
