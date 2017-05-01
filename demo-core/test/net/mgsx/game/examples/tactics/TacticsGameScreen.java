package net.mgsx.game.examples.tactics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.screen.StageScreen;
import net.mgsx.game.examples.tactics.logic.BattleLogic;
import net.mgsx.game.examples.tactics.logic.BattleLogic.BattleListener;
import net.mgsx.game.examples.tactics.logic.CardBattle;
import net.mgsx.game.examples.tactics.logic.CharacterBattle;
import net.mgsx.game.examples.tactics.logic.EffectBattle;
import net.mgsx.game.examples.tactics.model.Model;

public class TacticsGameScreen extends StageScreen
{
	BattleLogic logic;
	Table tableTeamA, tableTeamB, tableCharacters, tableCurrentPlayer;
	TextButton turnButton;
	
	public TacticsGameScreen(Skin skin) {
		super(skin);
		
		final Model model = Model.load(Gdx.files.internal("tactics/model.json"));
		
//		logic = BattleLogic.create(model, model.getTeam("heroes"), model.getTeam("monsters-1.1"));
		
		Table table = new Table(skin);
		
		tableTeamA = new Table(skin);
		tableTeamB = new Table(skin);
		tableCharacters = new Table(skin);
		tableCurrentPlayer = new Table(skin);
		
		Table header = new Table(skin);
		final SelectBox<String> teamABox = new SelectBox<String>(skin);
		teamABox.setItems(model.teams.keys().toArray());
		final SelectBox<String> teamBBox = new SelectBox<String>(skin);
		teamBBox.setItems(model.teams.keys().toArray());
		TextButton reset = new TextButton("Reset", skin);
		header.add(teamABox);
		header.add(teamBBox);
		header.add(reset);
		
		teamABox.setSelected("heroes");
		teamBBox.setSelected("monsters-1.1");
		
		table.add(header).row();
		table.add(tableTeamA).row();
		table.add(tableTeamB).row();
		table.add(tableCharacters).row();
		table.add(tableCurrentPlayer).row();
		
		turnButton = new TextButton("Next Turn", skin);
		table.add(turnButton);
		
		table.setFillParent(true);
		
		stage.addActor(table);
		
		// updateAll();
		
		turnButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				
				if(currentCard != null){
					logic.selectAction(currentCard, targets);
				}
				currentCard = null;
				targets.clear();
				
				logic.nextTurn();
				updateAll();
			}
		});
		
		final BattleListener logicListener = new BattleListener() {
			
			@Override
			public void onPlayerChange(CharacterBattle old, CharacterBattle current) {
				setCurrentPlayer(current);
			}
			
			@Override
			public void onEnd(boolean victoryA, boolean victoryB) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onEffectApply(CharacterBattle target, EffectBattle fx) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onDie(CharacterBattle target) {
				// TODO Auto-generated method stub
				
			}
		};
		
		reset.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				logic = BattleLogic.create(model, model.getTeam(teamABox.getSelected()), model.getTeam(teamBBox.getSelected()));
				logic.listener = logicListener;
				updateAll();
			}
		});
	}
	
	@Override
	public void render(float deltaTime) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		super.render(deltaTime);
	}

	private void updateAll() 
	{
		updateCharacters(tableTeamA, logic.teamA.characters);
		updateCharacters(tableTeamB, logic.teamB.characters);
		updateCharacters(tableCharacters, logic.characters);
		setCurrentPlayer(logic.current);
	}
	
	private void updateCharacters(Table table, Array<CharacterBattle> characters){
		table.clear();
		for(CharacterBattle character : characters){
			table.add(create(character));
		}
		
	}

	private Actor create(final CharacterBattle character) 
	{
		Table table = new Table(skin);
		table.defaults().pad(5);
		table.add(character.def.id).row();
		table.add(String.format("life %d / %d", character.life, character.def.life)).row();
		table.add(String.format("turns %d", (int)character.turns)).row();
		
		final TextButton btSelect = new TextButton("Select", skin, "toggle");
		table.add(btSelect).row();
		
		btSelect.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(btSelect.isChecked()){
					targets.add(character);
				}else{
					targets.removeValue(character, true);
				}
				
			}
		});
		
		return table;
	}
	
	
	private void setCurrentPlayer(CharacterBattle character) 
	{
		tableCurrentPlayer.clear();
		if(character == null) return;
		
		Table table = new Table(skin);
		table.defaults().pad(5);
		table.add("id");
		table.add(character.def.id).row();
		
		tableCurrentPlayer.add(table).row();
		
		for(CardBattle card : character.cards){
			tableCurrentPlayer.add(createCard(card));
		}
	}

	private CardBattle currentCard;
	private Array<CharacterBattle> targets = new Array<CharacterBattle>();
	
	private Actor createCard(final CardBattle card) 
	{
		Table table = new Table(skin);
		table.defaults().pad(5);
		table.add(card.def.id);
		TextButton applyButton = new TextButton("", skin);
		if(card.turns <= 0){
			applyButton.setText("Apply Now!");
		}else{
			applyButton.setDisabled(true);
			applyButton.setText(String.valueOf((int)Math.ceil(card.turns)) + " turns");
		}
		applyButton.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				currentCard = card;
				
			}
		});
		table.add(applyButton);
		return table;
	}
	
	
	

}
