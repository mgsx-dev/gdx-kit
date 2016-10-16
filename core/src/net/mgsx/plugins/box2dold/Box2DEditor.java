package net.mgsx.plugins.box2dold;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
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

import net.mgsx.core.Editor;
import net.mgsx.core.EntityEditor;
import net.mgsx.core.NativeService;
import net.mgsx.core.NativeService.DialogCallback;
import net.mgsx.core.helpers.ReflectionHelper;
import net.mgsx.core.tools.Tool;
import net.mgsx.core.tools.ToolGroup;
import net.mgsx.core.ui.TabPane;
import net.mgsx.plugins.box2d.model.Box2DBodyModel;
import net.mgsx.plugins.box2dold.model.SpriteItem;
import net.mgsx.plugins.box2dold.model.WorldItem;
import net.mgsx.plugins.box2dold.persistence.Repository;

// TODO some shapes (chain) can'be used in dynamic objects ! use polygon instead but restrict to convex hull and 3 to 8 vertex !
public class Box2DEditor extends Editor 
{
	// box2D scene rendering
	Box2DDebugRenderer renderer;
	AssetManager assets;
	
	Array<Sprite> sprites = new Array<Sprite>();
	
	SelectBox<Box2DBodyModel> bodySelector;
	
	EntityEditor entityEditor, worldEditor;
	
	private WorldItem worldItem;

	private String lastLoadedAbsolutePath;
	public Box2DEditor(String absolutePath) 
	{
		super();
		lastLoadedAbsolutePath = absolutePath;
	}
	
	@Override
	public void create () 
	{
		super.create();
		
		assets = new AssetManager();
		Texture.setAssetManager(assets);
		
		// box 2D
		worldItem = new WorldItem(history);
		if(lastLoadedAbsolutePath != null){
			FileHandle file = Gdx.files.absolute(lastLoadedAbsolutePath);
			if(file.exists())
			{
				Repository.load(file, worldItem);
			}
		}
		worldItem.initialize();

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
		super.reset();
		
		stage.clear();
		
		// rendering
		orthographicCamera.position.set(0, 0, 0);
		orthographicCamera.update();
		renderer = new Box2DDebugRenderer();
		
		// Tools stack (order is important !)
		//
		final ToolGroup mainTools = createToolGroup(); // TODO mainTools are contectual tools ....

		
		// Edit tools
		final Array<Tool> editTools = new Array<Tool>();
		

		mainTools.tools.addAll(editTools);
		
		// Shape tools
		final Array<Tool> shapeTools = new Array<Tool>();
		
		
		mainTools.tools.addAll(shapeTools);
		
		// Joint tools
		final Array<Tool> jointTools = new Array<Tool>();
		

		mainTools.tools.addAll(jointTools);
		
		// Test tools
		final Array<Tool> testTools = new Array<Tool>();
		
		
		// ...
		


		
		mainTools.setDefaultTool(null);
		mainTools.setActiveTool(null);
		
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
		
		bodySelector = new SelectBox<Box2DBodyModel>(skin);
		
		
		// convert to tools
		VerticalGroup presetTable = new VerticalGroup();
		presetTable.wrap(false);
		
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
						bgs.setBounds(0, 0, 0.005f * bg.getWidth() , 0.005f * bg.getHeight());
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

		// tabs.add(createToolButton(mainTools, new NoTool("no tool", orthographicCamera))).row();
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
	

	@Override
	public void render () 
	{
		// update logic
		worldItem.update();

		// draw
		orthographicCamera.update(true);
		
		Gdx.gl.glClearColor(.5f, .5f, .5f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		// draw sprites (background and then other sprites)
		
		batch.begin();
		for(Sprite sprite : sprites) sprite.draw(batch);
		for(SpriteItem spriteItem : worldItem.sprites) spriteItem.sprite.draw(batch);
		for(SpriteItem spriteItem : worldItem.items.sprites) spriteItem.sprite.draw(batch);
		batch.end();
		
		if(true){
		// draw box2D debug
		renderer.render(worldItem.world, orthographicCamera.combined);
		
		// draw other shapes (tools)
		
		shapeRenderer.begin(ShapeType.Filled);
		Vector2 s = Tool.pixelSize(orthographicCamera).scl(3);
		for(Box2DBodyModel item : worldItem.selection.bodies){
			shapeRenderer.rect(item.body.getPosition().x-s.x, item.body.getPosition().y-s.y, 2*s.x, 2*s.y);
		}	
		for(Box2DBodyModel item : worldItem.items.bodies){
			if(item.behavior != null) item.behavior.renderDebug(shapeRenderer);
		}
		
		shapeRenderer.end();
		
		shapeRenderer.begin(ShapeType.Line);
		for(SpriteItem item : worldItem.selection.sprites){
			Rectangle r = item.sprite.getBoundingRectangle();
			shapeRenderer.rect(item.sprite.getX(), item.sprite.getY(), item.sprite.getWidth(), item.sprite.getHeight());
			shapeRenderer.rect(r.x, r.y, r.width, r.height);
		}
		shapeRenderer.end();
		}
		//System.out.println(1.f / Gdx.graphics.getDeltaTime());
		super.render();
	}
	
	
}
