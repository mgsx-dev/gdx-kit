package net.mgsx.box2d.editor;

import java.lang.reflect.Field;

import net.mgsx.box2d.editor.Box2DPresets.Box2DPreset;
import net.mgsx.box2d.editor.persistence.Repository;
import net.mgsx.box2d.editor.tools.CreateChainTool;
import net.mgsx.box2d.editor.tools.CreateCircleTool;
import net.mgsx.box2d.editor.tools.CreateEdgeTool;
import net.mgsx.box2d.editor.tools.CreateLoopTool;
import net.mgsx.box2d.editor.tools.CreatePolygonTool;
import net.mgsx.box2d.editor.tools.CreateRectangleTool;
import net.mgsx.box2d.editor.tools.JointDistanceTool;
import net.mgsx.box2d.editor.tools.JointFrictionTool;
import net.mgsx.box2d.editor.tools.JointGearTool;
import net.mgsx.box2d.editor.tools.JointMotorTool;
import net.mgsx.box2d.editor.tools.JointMouseTool;
import net.mgsx.box2d.editor.tools.JointPrismaticTool;
import net.mgsx.box2d.editor.tools.JointPulleyTool;
import net.mgsx.box2d.editor.tools.JointRevoluteTool;
import net.mgsx.box2d.editor.tools.JointRopeTool;
import net.mgsx.box2d.editor.tools.JointWeldTool;
import net.mgsx.box2d.editor.tools.JointWheelTool;
import net.mgsx.box2d.editor.tools.MoveTool;
import net.mgsx.box2d.editor.tools.ParticleTool;
import net.mgsx.box2d.editor.tools.PresetTool;
import net.mgsx.box2d.editor.tools.SelectTool;
import net.mgsx.fwk.editor.Command;
import net.mgsx.fwk.editor.Editor;
import net.mgsx.fwk.editor.NativeService;
import net.mgsx.fwk.editor.NativeService.DialogCallback;
import net.mgsx.fwk.editor.ReflectionHelper;
import net.mgsx.fwk.editor.Tool;
import net.mgsx.fwk.editor.ToolGroup;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Json;

public class Box2DEditor extends Editor 
{
	// box2D scene rendering
	Box2DDebugRenderer renderer;
	OrthographicCamera camera;
	SpriteBatch batch;
	
	SelectBox<BodyItem> bodySelector;
	
	EntityEditor entityEditor, worldEditor;
	
	private void save(){
		new Json().toJson(worldItem.settings);
	}
	
	
	// NOTE keep it !
	private class BodySelectCommand extends Command
	{
		private BodyItem item, previous;
		public BodySelectCommand(BodyItem item){
			this.item = item;
		}
		@Override
		public void commit() {
			previous = bodySelector.getSelected();
			bodySelector.setSelected(item);
		}
		@Override
		public void rollback() {
			bodySelector.setSelected(previous);
		}
	}
	
	private WorldItem worldItem;

	@Override
	public void create () 
	{
		super.create();
		
		// box 2D
		worldItem = new WorldItem();
		worldItem.world = new World(worldItem.settings.gravity, true);

		rebuild();
	}
	
	public void reset(){
		super.reset();
		worldItem.items.clear();
		worldItem.selection.clear();
		worldItem.world.dispose();
		worldItem.world = new World(worldItem.settings.gravity, true);
		rebuild();
	}
	
	private void rebuild()
	{
		stage.clear();
		
		// rendering
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		renderer = new Box2DDebugRenderer();
		
		// tools
		final ToolGroup mainTools = createToolGroup();
		
		mainTools.tools.add(new SelectTool(camera, worldItem));
		mainTools.tools.add(new MoveTool(camera, worldItem));
		
		mainTools.tools.add(new CreateRectangleTool(camera, worldItem, null));
		mainTools.tools.add(new CreatePolygonTool(camera, worldItem, null));
		mainTools.tools.add(new CreateCircleTool(camera, worldItem, null));
		mainTools.tools.add(new CreateChainTool(camera, worldItem, null));
		mainTools.tools.add(new CreateLoopTool(camera, worldItem, null));
		mainTools.tools.add(new CreateEdgeTool(camera, worldItem, null));
		
		
//		WeldJointDef;
//		WheelJointDef;
		
		mainTools.tools.add(new JointDistanceTool(camera, worldItem));
		mainTools.tools.add(new JointFrictionTool(camera, worldItem));
		mainTools.tools.add(new JointGearTool(camera, worldItem));
		mainTools.tools.add(new JointMotorTool(camera, worldItem));
		mainTools.tools.add(new JointMouseTool(camera, worldItem));
		mainTools.tools.add(new JointPrismaticTool(camera, worldItem));
		mainTools.tools.add(new JointPulleyTool(camera, worldItem));
		mainTools.tools.add(new JointRevoluteTool(camera, worldItem));
		mainTools.tools.add(new JointRopeTool(camera, worldItem));
		mainTools.tools.add(new JointWeldTool(camera, worldItem));
		mainTools.tools.add(new JointWheelTool(camera, worldItem));

		mainTools.tools.add(new ParticleTool(camera, worldItem));
		
		mainTools.setDefaultTool(mainTools.tools.get(0));
		mainTools.setActiveTool(mainTools.tools.get(1));
		
		// remaining...
		worldEditor = new EntityEditor(skin);
		worldEditor.setEntity(worldItem.settings);
		worldEditor.addListener(new EventListener() {
			
			@Override
			public boolean handle(Event event) {
				if(event instanceof EntityEditor.EntityEvent){
					EntityEditor.EntityEvent e = ((EntityEditor.EntityEvent) event);
					ReflectionHelper.set(e.entity, e.field, e.value);
					worldItem.world.setGravity(worldItem.settings.gravity);
					// TODO update others ...
					return true;
				}
				return false;
			}
		});
		
		// TODO transform to tools
		/*
				if(keycode == Input.Keys.Z)
				{
					if(ctrl)
					{
						if(shift){
							history.redo();
						}else{
							history.undo();
						}
					}
				}
			private int px, py;
			@Override
			public boolean touchDragged(int screenX, int screenY, int pointer) {
				
				if(Gdx.input.isButtonPressed(Input.Buttons.MIDDLE))
				{
					Vector3 a = camera.unproject(new Vector3(px, py, 0));
					Vector3 b = camera.unproject(new Vector3(screenX, screenY, 0));
					b.sub(a);
					camera.translate(-b.x, -b.y);
				}else{
				}
				px = screenX;
				py = screenY;
				return super.touchDragged(screenX, screenY, pointer);
			}
			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				
				Vector3 worldPos = camera.unproject(new Vector3(screenX, screenY, 0));
				
				if(button == Input.Buttons.LEFT)
				{
					Body body = queryFirstBody(worldPos);
					if(body != null)
					{
						BodyItem item = (BodyItem)body.getUserData();
						if(ctrl || shift){
							selection.add(item);
							history.add(new BodySelectCommand(item));
						}else{
							selection.clear();
							selection.add(item);
							history.add(new BodySelectCommand(item));
						}
						
						if(body != null) bodySelector.setSelected(item);
					}
					else
					{
						// TODO history : add selection command, ... etc
						selection.clear();
					}
				}
				px = screenX;
				py = screenY;
				return true;
			}
		};
		
		Gdx.input.setInputProcessor(new InputMultiplexer(stage, customInput));
		*/
		
		
		TextButton btReset = new TextButton("Reset", skin);
		btReset.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				reset();
			}
		});
		
		final TextButton playPauseButton = new TextButton("pause", skin);
		playPauseButton.setChecked(true);
		playPauseButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				worldItem.settings.runSimulation = playPauseButton.isChecked();
				playPauseButton.setText(worldItem.settings.runSimulation ? "pause" : "play");
			}
		});
		
		bodySelector = new SelectBox<BodyItem>(skin);
		
		
		// XXX worldItem.items.bodies.add(new BodyItem("", null, null));
		
		// convert to tools
		ButtonGroup<TextButton> grpTools = new ButtonGroup<TextButton>();
		HorizontalGroup presetTable = new HorizontalGroup();
		presetTable.wrap(true);
		for(Field field : Box2DPresets.class.getDeclaredFields()){
			if(field.getType() == Box2DPreset.class){
				final Box2DPreset preset = ReflectionHelper.get(null, field, Box2DPreset.class);
				final Tool tool = new PresetTool(field.getName(), camera, worldItem, preset);
				mainTools.tools.add(tool);
			}
		}
		
		// create tool bar
		for(Tool tool : mainTools.tools){
			final Tool finalTool = tool;
			TextButton btTool = new TextButton(tool.name, skin);
			btTool.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					mainTools.setActiveTool(finalTool);
				}
			});
			grpTools.add(btTool);
			presetTable.addActor(btTool);
		}
		
		bodySelector.setItems(worldItem.items.bodies);
		
		TextButton btSave = new TextButton("Save", skin);
		TextButton btOpen = new TextButton("Open", skin);
		
		btSave.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				NativeService.instance.openSaveDialog(new DialogCallback() {
					@Override
					public void selected(FileHandle file) {
						Repository.save(file, worldItem);
					}
					@Override
					public void cancel() {
					}
				});
			}
		});
		btOpen.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				NativeService.instance.openSaveDialog(new DialogCallback() {
					@Override
					public void selected(FileHandle file) {
						Repository.load(file, worldItem);
						rebuild();
					}
					@Override
					public void cancel() {
					}
				});
			}
		});
		
		
		Table hgMain = new Table();
		hgMain.add(btReset);
		hgMain.add(btSave);
		hgMain.add(btOpen);
		hgMain.add(playPauseButton);
		
		
		Table vg = new Table();
		vg.add(hgMain).row();
		vg.add(presetTable).expand().fill().row();
		vg.add(worldEditor).row();
		
		Table main = new Table(skin);
		main.add(vg).expand().top().left();
		
		main.setFillParent(true);
		stage.addActor(main);

//		table.add(bodySelector);
//		table.row();
		
		/*
		entityEditor = new EntityEditor(skin);
		table.add(entityEditor);
		
		bodySelector.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				entityEditor.setEntity(bodySelector.getSelected());
			}
		});
		
		
		
		entityEditor.addListener(new EventListener() {
			
			@Override
			public boolean handle(Event event) {
				if(event instanceof EntityEditor.EntityEvent){
					EntityEditor.EntityEvent e = (EntityEditor.EntityEvent)event;
					
					try {
						ReflectionHelper.set(e.entity, e.field, e.value);
					} catch (ReflectionHelper.ReflectionError ex) {
						Gdx.app.error(Box2DEditor.class.toString(), "unsupported", ex);
					} 
					return true;
				}
				return false;
			}
		});
		*/
		
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
	
	@Override
	public void render () {
		
		if(worldItem.settings.runSimulation)
		{
			worldItem.world.step(
					worldItem.settings.timeStep, 
					worldItem.settings.velocityIterations, 
					worldItem.settings.positionIterations);
		}
		
		camera.update(true);
		
		Gdx.gl.glClearColor(.5f, .5f, .5f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		renderer.render(worldItem.world, camera.combined);
		
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeType.Filled);
		Vector2 s = Tool.pixelSize(camera).scl(3);
		for(BodyItem item : worldItem.selection.bodies){
			shapeRenderer.rect(item.body.getPosition().x-s.x, item.body.getPosition().y-s.y, 2*s.x, 2*s.y);
		}
		shapeRenderer.end();
		
		super.render();
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		camera.setToOrtho(false, 5 * (float)width/(float)height, 5);
		float wscale = 0.01f;
		//camera.setToOrtho(false, width * wscale, height * wscale);
		camera.update(true);

	}
}
