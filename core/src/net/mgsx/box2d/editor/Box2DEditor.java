package net.mgsx.box2d.editor;

import java.lang.reflect.Field;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import net.mgsx.box2d.editor.Box2DPresets.Box2DPreset;
import net.mgsx.box2d.editor.persistence.Repository;
import net.mgsx.box2d.editor.tools.AddSpriteTool;
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
import net.mgsx.box2d.editor.tools.MoveShapeTool;
import net.mgsx.box2d.editor.tools.MoveTool;
import net.mgsx.box2d.editor.tools.ParticleTool;
import net.mgsx.box2d.editor.tools.PlayerTool;
import net.mgsx.box2d.editor.tools.PresetTool;
import net.mgsx.box2d.editor.tools.SelectTool;
import net.mgsx.fwk.editor.Command;
import net.mgsx.fwk.editor.Editor;
import net.mgsx.fwk.editor.NativeService;
import net.mgsx.fwk.editor.NativeService.DialogCallback;
import net.mgsx.fwk.editor.ReflectionHelper;
import net.mgsx.fwk.editor.Tool;
import net.mgsx.fwk.editor.ToolGroup;
import net.mgsx.fwk.editor.tools.PanTool;
import net.mgsx.fwk.editor.ui.TabPane;

public class Box2DEditor extends Editor 
{
	// box2D scene rendering
	Box2DDebugRenderer renderer;
	OrthographicCamera camera;
	AssetManager assets;
	
	Array<Sprite> sprites = new Array<Sprite>();
	
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
		
		assets = new AssetManager();
		Texture.setAssetManager(assets);
		
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
		camera = new OrthographicCamera();
		renderer = new Box2DDebugRenderer();
		
		// tools
		ToolGroup panToolGroup = createToolGroup();
		panToolGroup.setActiveTool(new PanTool(camera));
		
		
		final ToolGroup mainTools = createToolGroup();
		
		
		// Edit tools
		final Array<Tool> editTools = new Array<Tool>();
		
		editTools.add(new SelectTool(camera, worldItem));
		editTools.add(new MoveTool(camera, worldItem));
		editTools.add(new AddSpriteTool(camera, worldItem));
		editTools.add(new MoveShapeTool(camera, worldItem));

		mainTools.tools.addAll(editTools);
		
		// Shape tools
		final Array<Tool> shapeTools = new Array<Tool>();
		
		shapeTools.add(new CreateRectangleTool(camera, worldItem));
		shapeTools.add(new CreatePolygonTool(camera, worldItem));
		shapeTools.add(new CreateCircleTool(camera, worldItem));
		shapeTools.add(new CreateChainTool(camera, worldItem));
		shapeTools.add(new CreateLoopTool(camera, worldItem));
		shapeTools.add(new CreateEdgeTool(camera, worldItem));
		
		mainTools.tools.addAll(shapeTools);
		
		// Joint tools
		final Array<Tool> jointTools = new Array<Tool>();
		
		jointTools.add(new JointDistanceTool(camera, worldItem));
		jointTools.add(new JointFrictionTool(camera, worldItem));
		jointTools.add(new JointGearTool(camera, worldItem));
		jointTools.add(new JointMotorTool(camera, worldItem));
		jointTools.add(new JointMouseTool(camera, worldItem));
		jointTools.add(new JointPrismaticTool(camera, worldItem));
		jointTools.add(new JointPulleyTool(camera, worldItem));
		jointTools.add(new JointRevoluteTool(camera, worldItem));
		jointTools.add(new JointRopeTool(camera, worldItem));
		jointTools.add(new JointWeldTool(camera, worldItem));
		jointTools.add(new JointWheelTool(camera, worldItem));

		mainTools.tools.addAll(jointTools);
		
		// Test tools
		final Array<Tool> testTools = new Array<Tool>();
		
		testTools.add(new ParticleTool(camera, worldItem));
		testTools.add(new PlayerTool(camera, worldItem));
		
		// ...
		
		mainTools.setDefaultTool(mainTools.tools.get(0));
		// mainTools.setActiveTool(mainTools.tools.get(1));
		
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
		VerticalGroup presetTable = new VerticalGroup();
		presetTable.wrap(false);
		for(Field field : Box2DPresets.class.getDeclaredFields()){
			if(field.getType() == Box2DPreset.class){
				final Box2DPreset preset = ReflectionHelper.get(null, field, Box2DPreset.class);
				final Tool tool = new PresetTool(field.getName(), camera, worldItem, preset);
				mainTools.tools.add(tool);
				presetTable.addActor(createToolButton(mainTools, tool));
			}
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
		TextButton btBg = new TextButton("Bg", skin);
		btBg.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				NativeService.instance.openLoadDialog(new DialogCallback() {
					@Override
					public void selected(FileHandle file) {
						Texture bg = new Texture(file, true);
						bg.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.MipMapLinearLinear);
						bg.setWrap(TextureWrap.ClampToEdge, TextureWrap.ClampToEdge);
						Sprite bgs = new Sprite(bg); // TODO scale !
						bgs.setBounds(0, 0, 0.02f * bg.getWidth(), 0.02f * bg.getHeight());
						// bgs.setScale(camera.combined.getScaleX(), camera.combined.getScaleY());
						sprites.add(bgs);
					}
					@Override
					public void cancel() {
					}
				});
			}
		});

		
		VerticalGroup worldPane = new VerticalGroup();
		worldPane.addActor(btReset);
		worldPane.addActor(btSave);
		worldPane.addActor(btOpen);
		worldPane.addActor(playPauseButton);
		worldPane.addActor(btBg);
		
		VerticalGroup shapePane = new VerticalGroup();
		for(Tool tool : shapeTools) shapePane.addActor(createToolButton(mainTools, tool));
		
		VerticalGroup jointPane = new VerticalGroup();
		for(Tool tool : jointTools) jointPane.addActor(createToolButton(mainTools, tool));

		VerticalGroup testPane = new VerticalGroup();
		for(Tool tool : testTools) testPane.addActor(createToolButton(mainTools, tool));

		VerticalGroup editPane = new VerticalGroup();
		for(Tool tool : editTools) editPane.addActor(createToolButton(mainTools, tool));
		
		TabPane tabs = new TabPane(skin);
		tabs.addTab("World", worldPane);
		tabs.addTab("Shapes", shapePane);
		tabs.addTab("Edit", editPane);
		tabs.addTab("Joints", jointPane);
		tabs.addTab("Test", testPane);
		tabs.addTab("Presets", presetTable);
		tabs.addTab("Test", testPane);

		tabs.add(worldEditor);
		
		tabs.setTab(worldPane);
		
		Table main = new Table(skin);
		main.add(tabs).expand().top().left();
		
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
	
	private TextButton createToolButton(final ToolGroup group, final Tool tool) 
	{
		final TextButton btTool = new TextButton(tool.name, skin);
		btTool.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(btTool.isChecked()) group.setActiveTool(tool);
			}
		});
		group.addButton(btTool);
		return btTool;
	}

	@Override
	public void render () 
	{
		// update logic
		worldItem.update();

		// draw
		camera.update(true);
		
		Gdx.gl.glClearColor(.5f, .5f, .5f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		// draw sprites (background and then other sprites)
		batch.setTransformMatrix(camera.view);
		batch.setProjectionMatrix(camera.projection);
		batch.begin();
		for(Sprite sprite : sprites) sprite.draw(batch);
		for(SpriteItem spriteItem : worldItem.sprites) spriteItem.sprite.draw(batch);
		batch.end();
		
		// draw box2D debug
		renderer.render(worldItem.world, camera.combined);
		
		// draw other shapes (tools)
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
//		float wscale = 0.01f;
//		camera.setToOrtho(false, width , height);
		camera.update(true);

	}
}
