package net.mgsx.game.examples.war.editors;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ObjectMap.Entry;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.plugins.EntityEditorPlugin;
import net.mgsx.game.examples.war.components.ZoneComponent;
import net.mgsx.game.examples.war.model.Goods;
import net.mgsx.game.examples.war.model.Player;
import net.mgsx.game.examples.war.model.Zone.ZoneGoods;
import net.mgsx.game.examples.war.system.WarLogicSystem;

public class ZoneEditor implements EntityEditorPlugin
{
	private EditorScreen editor;
	
	public ZoneEditor(EditorScreen editor) {
		super();
		this.editor = editor;
	}

	@Override
	public Actor createEditor(Entity entity, Skin skin) 
	{
		Table table = new Table(skin);
		
		create(table, entity, skin);
		
		return table;
	}
	
	private void create(final Table table, final Entity entity, final Skin skin)
	{
		table.clear();
		
		ZoneComponent zone = ZoneComponent.components.get(entity);
		
		Player player = editor.entityEngine.getSystem(WarLogicSystem.class).player;
		
		TextButton btUpdate = new TextButton("Update", skin);
		btUpdate.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				editor.entityEngine.getSystem(WarLogicSystem.class).updateZone(entity);
				create(table, entity, skin);
			}
		});
		
		table.add("Goods");
		table.add("Price");
		table.add("Available");
		table.add("-");
		table.add("Owned");
		table.add("Price");
		table.add("-");
		table.add(btUpdate);
		table.row();
		
		for(Entry<Goods, ZoneGoods> entry : zone.zone.goods){
			final Goods goods = entry.key;
			final ZoneGoods zoneGoods = entry.value;
			
			table.add(entry.key.name);
			table.add(zoneGoods.quantity > 0 ? String.format("%d$", entry.value.price) : "-");
			table.add(String.valueOf(entry.value.quantity));
			
			TextButton btBuy = new TextButton("Buy", skin);
			table.add(btBuy);
			
			table.add(zoneGoods.quantity > 0 ? String.format("%d$", (int)(entry.value.price * .9f)) : "-");
			table.add(String.valueOf(player.goods.get(entry.key).quantity));
			TextButton btSell = new TextButton("Sell", skin);
			table.add(btSell);
			table.add(new HistoryWidget(zoneGoods.priceHistory, 0, 200)).width(200).fillY();
			table.row();
			
			btBuy.setDisabled(zoneGoods.quantity <= 0 || player.money < zoneGoods.price);
			btSell.setDisabled(player.goods.get(goods).quantity <= 0);
			
			btBuy.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					// buy goods and redraw
					editor.entityEngine.getSystem(WarLogicSystem.class).buy(goods, zoneGoods, 1);
					create(table, entity, skin);
				}
			});
			btSell.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					// buy goods and redraw
					editor.entityEngine.getSystem(WarLogicSystem.class).sell(goods, zoneGoods, 1);
					create(table, entity, skin);
				}
			});
		
		}
	}

}
