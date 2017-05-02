package net.mgsx.game.examples.tactics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import net.mgsx.game.core.screen.StageScreen;
import net.mgsx.game.examples.tactics.actions.JoinAction;
import net.mgsx.game.examples.tactics.logic.AIControl;
import net.mgsx.game.examples.tactics.logic.BattleLogic;
import net.mgsx.game.examples.tactics.logic.BattleLogic.BattleListener;
import net.mgsx.game.examples.tactics.logic.CardBattle;
import net.mgsx.game.examples.tactics.logic.CharacterBattle;
import net.mgsx.game.examples.tactics.logic.CharacterControl;
import net.mgsx.game.examples.tactics.logic.EffectBattle;
import net.mgsx.game.examples.tactics.model.Model;
import net.mgsx.game.examples.tactics.ui.CharacterUI;
import net.mgsx.game.examples.tactics.ui.SmoothTable;

public class TacticsGameScreen extends StageScreen
{
	BattleLogic logic;
	SmoothTable tableTeamA, tableTeamB, tableCharacters, tableCurrentPlayer;
	TextButton turnButton;
	
	ObjectMap<CharacterBattle, CharacterUI> characterWidgets = new ObjectMap<CharacterBattle, CharacterUI>();
	
	Interpolation noiseInterpolation = new Interpolation() {
		@Override
		public float apply(float a) {
			float freq = 4;
			float noise = MathUtils.sinDeg(a * 360 * freq);
			float env = a * (1-a) * 4;
			
			return MathUtils.clamp(env * noise, 0, 1);
		}
	};
	
	Action lockTurnAction = new RunnableAction(){
		public boolean act(float delta) {
			turnButton.setDisabled(true);
			return true;
		};
	};
	
	Action unlockTurnAction = new RunnableAction(){
		public boolean act(float delta) {
			turnButton.setDisabled(false);
			return true;
		};
	};
	
	private SequenceAction sequence;
	
	public TacticsGameScreen(Skin skin) {
		super(skin);
		
		final Model model = Model.load(Gdx.files.internal("tactics/model.json"));
		
//		logic = BattleLogic.create(model, model.getTeam("heroes"), model.getTeam("monsters-1.1"));
		
		Table table = new Table(skin);
		
		tableTeamA = new SmoothTable(skin);
		tableTeamB = new SmoothTable(skin);
		tableCharacters = new SmoothTable(skin);
		tableCurrentPlayer = new SmoothTable(skin);
		tableCurrentPlayer.setBackground("default-pane");
		
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
		table.add(tableCurrentPlayer).fill().row();
		
		turnButton = new TextButton("Next Turn", skin);
		table.add(turnButton);
		
		table.setFillParent(true);
		
		stage.addActor(table);
		
		// updateAll();
		
		turnButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				
				sequence = new SequenceAction();
				
				sequence.addAction(lockTurnAction);
				
				if(currentCard != null){
					logic.selectAction(currentCard, targets);
				}
				currentCard = null;
				targets.clear();
				
				logic.nextTurn();
				
				// TODO reset turn status action ?
				
				sequence.addAction(Actions.run(new Runnable() {
					
					@Override
					public void run() {
						for(CharacterUI ui : characterWidgets.values()){
							ui.setTarget(false);
							ui.update();
						}
						
					}
				}));
				
				stage.addAction(sequence);
				
				// updateCharacters(tableCharacters, logic.characters, true);
				// XXX updateAll();
			}
		});
		
		final Action changePlayerAction = new RunnableAction(){
			public boolean act(float delta) {
				setCurrentPlayer(logic.current);
				return true;
			};
		};
		
		final BattleListener logicListener = new BattleListener() {
			
			@Override
			public void onPlayerChange(final CharacterBattle old, final CharacterBattle current) {
				
				sequence.addAction(Actions.run(new Runnable() {
					@Override
					public void run() {
						updateCharacters(tableCharacters, logic.characters, true);
						
						for(Table table : new Table[]{tableCharacters, tableTeamA, tableTeamB}){
							for(Cell<Actor> cell : table.getCells()){
								if(cell.getActor() != null && cell.getActor().getUserObject() == current){
									// ((Table)cell.getActor()).setBackground("invert-window");
									((Table)cell.getActor()).addAction(Actions.color(Color.GOLD, 1f));
								}
								if(cell.getActor() != null && cell.getActor().getUserObject() == old){
									//((Table)cell.getActor()).setBackground("default-window");
									((Table)cell.getActor()).addAction(Actions.color(Color.WHITE, 1f));
								}
							}
						}
						
						tableCurrentPlayer.setTransform(true);
						tableCurrentPlayer.setOrigin(Align.center);
						SequenceAction panelSeq = new SequenceAction(); 
						tableCurrentPlayer.addAction(panelSeq);
						if(old != null){
							panelSeq.addAction(Actions.scaleTo(0, 1, .1f, Interpolation.sineIn));
						}
						panelSeq.addAction(changePlayerAction);
						if(current != null){
							panelSeq.addAction(Actions.scaleTo(1, 1, .1f, Interpolation.sineOut));
						}
						sequence.addAction(new JoinAction(tableCurrentPlayer));
						if(logic.teamA.characters.contains(current, true)){
							sequence.addAction(unlockTurnAction);
						}else{
							sequence.addAction(Actions.run(new Runnable() {
								@Override
								public void run() {
									sequence.addAction(Actions.run(new Runnable() {
										
										@Override
										public void run() {
											for(CharacterUI ui : characterWidgets.values()){
												ui.setTarget(false);
												ui.update();
											}
											
											// iaPlay();
											
										}
									}));
								}
							}));
						}
					}
				}));
				
			}
			
			@Override
			public void onEnd(boolean victoryA, boolean victoryB) {
			}
			
			@Override
			public void onEffectApply(CharacterBattle target, final EffectBattle fx) {
				
				// TODO update all cards
				final CharacterUI ui = characterWidgets.get(target);
				
				sequence.addAction(Actions.run(new Runnable() {
					@Override
					public void run() {
						
						// TODO all this in character UI
						float x = ui.getX();
						float y = ui.getY();
						ui.addAction(Actions.sequence(
								
								Actions.color(fx.life < 0 ? Color.RED : Color.GREEN, .01f),
								Actions.moveBy(20, 20, .1f, noiseInterpolation),
								Actions.moveTo(x, y, .1f),
								Actions.color(Color.WHITE, .2f)
								));
						ui.update();
						
						ui.onEffectApply(fx);
						
						Vector2 p = ui.getLifePosition();
						
						Label lbl = new Label((fx.life < 0 ? "" : "+") + String.valueOf(fx.life), getSkin());
						lbl.setColor(fx.life < 0 ? Color.RED : Color.GREEN);
						lbl.setPosition(p.x, p.y, Align.center);
						stage.addActor(lbl);
						lbl.addAction(Actions.sequence(
								Actions.moveBy(0, -30 * (fx.life < 0 ? 1 : -1), .5f),
								Actions.fadeOut(.5f),
								Actions.removeActor()
								));
						
					}
				}));
				sequence.addAction(new JoinAction(ui));
				
			}
			
			@Override
			public void onDie(final CharacterBattle target) {
				sequence.addAction(Actions.run(new Runnable() {
					@Override
					public void run() {
						
						// animation for characters list :
						for(Cell<Actor> cell : tableCharacters.getCells()){
							if(cell.getActor() != null && cell.getActor().getUserObject() == target){
								cell.getActor().addAction(Actions.after(Actions.sequence(
										
										Actions.color(Color.RED, 1f),
										Actions.scaleTo(0, 1, 1f, Interpolation.sineIn),
										Actions.removeActor()
										
										)));
								
							}
						}
						
						// TODO in GUI
						characterWidgets.get(target).onDie();
						
						// animation for teams
						for(Cell<Actor> cell : tableTeamA.getCells()){
							if(cell.getActor() != null && cell.getActor().getUserObject() == target){
								cell.getActor().addAction(Actions.after(Actions.sequence(
										
										Actions.color(Color.GRAY, 1f)
										
										)));
							}
						}
						for(Cell<Actor> cell : tableTeamB.getCells()){
							if(cell.getActor() != null && cell.getActor().getUserObject() == target){
								cell.getActor().addAction(Actions.after(Actions.sequence(
										
										Actions.color(Color.GRAY, 1f)
										
										)));
							}
						}
					}
				}));
			}

			@Override
			public void onTarget(CardBattle card, CharacterBattle target) {
				CharacterUI ui = characterWidgets.get(target);
				ui.setTarget(true);
			}
		};
		
		final CharacterControl humanControl = new CharacterControl() {

			@Override
			public void enable(CharacterBattle character) {
				// TODO show turn buttons and allow selection ...
				
			}

			@Override
			public void disable(CharacterBattle character) {
				// TODO disable buttons and hide turns button
				
			}
			
		};
		
		reset.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				logic = BattleLogic.create(model, model.getTeam(teamABox.getSelected()), model.getTeam(teamBBox.getSelected()));
				for(CharacterBattle c : logic.teamA.characters){
					c.control = humanControl;
				}
				for(CharacterBattle c : logic.teamB.characters){
					c.control = new AIControl(logic){
						@Override
						public void enable(CharacterBattle character) {
							sequence.addAction(Actions.delay(1)); // simulate reflexion ...
							super.enable(character);
							if(!logic.isOver()){
								sequence.addAction(Actions.run(new Runnable() {
									@Override
									public void run() {
										logic.nextTurn();
									}
								}));
							}
						}
					};
				}
				logic.listener = logicListener;
				turnButton.setDisabled(false);
				stage.getRoot().clearActions();
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
		characterWidgets.clear();
		
		updateCharacters(tableTeamA, logic.teamA.characters, false);
		updateCharacters(tableTeamB, logic.teamB.characters, false);
		updateCharacters(tableCharacters, logic.characters, true);
		setCurrentPlayer(logic.current);
	}
	
	private void updateCharacters(SmoothTable table, Array<CharacterBattle> characters, boolean soft){
		table.clear();
		for(CharacterBattle character : characters){
			table.addSmooth(create(character, soft), .1f);
		}
		
	}

	private Actor create(final CharacterBattle character, boolean soft) 
	{
		if(soft){
			
			Table table = new Table(skin);
			table.setBackground("default-pane");
			
			table.defaults().pad(5);
			table.add(character.def.id).row();
			table.setTransform(true);
			table.setUserObject(character);
			
			return table;
		}
		else{
			
			final CharacterUI ui = new CharacterUI(skin, character);
			ui.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					if(!targets.removeValue(character, true)){
						targets.add(character);
					}
					ui.setTarget(targets.contains(character, true));
					// TODO update things ...
				}
			});
			characterWidgets.put(character, ui);
			return ui;
			
		}
		
	}
	
	
	private void setCurrentPlayer(CharacterBattle character) 
	{
		tableCurrentPlayer.clearChildren();
		if(character == null) return;
		
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
		if(card.turns <= 0){
			TextButton applyButton = new TextButton("", skin);
			applyButton.setText(card.def.id);
			table.add(applyButton);
			applyButton.addListener(new ChangeListener() {
				
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					currentCard = card;
					
				}
			});
		}else{
			table.add(card.def.id + " (" + String.valueOf((int)Math.ceil(card.turns)) + ")");
		}
		return table;
	}
	
	
	

}
