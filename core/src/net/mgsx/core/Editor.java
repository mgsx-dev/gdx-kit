package net.mgsx.core;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import net.mgsx.core.plugins.EditablePlugin;
import net.mgsx.core.plugins.EditorPlugin;
import net.mgsx.core.plugins.Movable;
import net.mgsx.core.plugins.Plugin;
import net.mgsx.core.tools.MoveToolBase;
import net.mgsx.core.tools.PanTool;
import net.mgsx.core.tools.SelectToolBase;
import net.mgsx.core.tools.Tool;
import net.mgsx.core.tools.ToolGroup;
import net.mgsx.core.tools.UndoTool;
import net.mgsx.core.ui.TabPane;
import net.mgsx.plugins.box2d.SkinFactory;
import net.mgsx.plugins.box2d.model.BodyItem;
import net.mgsx.plugins.box2d.tools.NoTool;

// TODO avoid complexity here : 
// panel could be separated
// editor could be a screen (to be used within a game or why not stacked on other screen !)
// TODO default switch to toggle debug display and simple display (aka blender : show only render)
// maybe move things to factory/plugin registry ...
// just keep GUI and rendering inside
//
//
// TODO rendering pipeline could be a SystemProcessor :
// it has a predifiend and order stack
// where plugins can attach things ...
//
// TODO storage considerations :
// by default, a component is not saved, plugins have to register a serializer for it.
// TODO concept of import/save/load/export : load/save is handled directly, import/export is throw plugins,
// example : import a png, work on it and export it as png
//
public class Editor extends GameEngine
{
	protected CommandHistory history;
	protected Skin skin;
	protected Stage stage;
	protected Table panel;
	protected Table buttons;
	protected Table outline;
	protected TabPane global;
	
	final private Array<ToolGroup> tools = new Array<ToolGroup>();
	
	private InputMultiplexer toolDelegator;
	
	private ToolGroup mainToolGroup; // XXX temporarily public ...
	public ToolGroup subToolGroup;
	
	private Map<String, EditorPlugin> globalEditors = new LinkedHashMap<String, EditorPlugin>();
	
	@Override
	public void create() {
		super.create();
		
		skin = SkinFactory.createSkin();
		stage = new Stage(new ScreenViewport());
		history = new CommandHistory();
		
		toolDelegator = new InputMultiplexer();
		
		Gdx.input.setInputProcessor(new InputMultiplexer(stage, toolDelegator));

		outline = new Table(skin);
		
		
		panel = new Table(skin);
		// TODO add menu
		global = new TabPane(skin);
		buttons = new Table(skin);
		panel.add(global).row();
		panel.add(buttons).row();
		panel.add(outline).row();
		
		Table main = new Table();
		main.add(panel).expand().left().top();
		
		main.setFillParent(true);
		stage.addActor(main);
		
		createToolGroup().addProcessor(new UndoTool(history));
		
		subToolGroup = createToolGroup();
		mainToolGroup = createToolGroup();
		
		addTool(new NoTool("Select", this));;
		
		addGlobalTool(new MoveToolBase(this));
		addGlobalTool(new PanTool(orthographicCamera));
//		addGlobalTool(new SelectToolBase(this){
//			@Override
//			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
//				setSelection(null);
//				return super.touchDown(screenX, screenY, pointer, button);
//			}
//		});

		// register listener after plugins creation to create filters on all possible components
		// finally initiate plugins.
		// TODO separate runtme plugin part (model, serialization, update, render) from editor part
		for(Plugin plugin : plugins){
			plugin.initialize(this);
		}

		global.addTab("Off", new Label("", skin));
		for(Entry<String, EditorPlugin> entry : globalEditors.entrySet()){
			global.addTab(entry.getKey(), entry.getValue().createEditor(this, skin));
		}
		// global.sett

		// TODO  maybe generalize as auto attach (Family) with a backed pool : pool.obtain, pool.release
		entityEngine.addEntityListener(new EntityListener() {
			@Override
			public void entityRemoved(Entity entity) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void entityAdded(Entity entity) {
				EditorEntity config = new EditorEntity();
				// could be used to store things about editor ...
				entity.add(config);
			}
		});
		for(Class<? extends Component> type : editablePlugins.keySet()){
			entityEngine.addEntityListener(Family.one(type).get(), new EntityListener() {
				
				@Override
				public void entityRemoved(Entity entity) {
					if(entity == getSelected()) invalidateSelection();
				}
				
				@Override
				public void entityAdded(Entity entity) {
					if(entity == getSelected()) invalidateSelection();
				}
			});
		}
		
		entityEngine.addSystem(new EntitySystem() {
			
			@Override
			public void update(float deltaTime) {
				Vector3 pos = new Vector3();
				Vector2 s = Tool.pixelSize(orthographicCamera).scl(5);
				shapeRenderer.setProjectionMatrix(orthographicCamera.combined);
				shapeRenderer.begin(ShapeType.Line);
				for(Entity e : entityEngine.getEntitiesFor(Family.one(Movable.class).get())){
					if(selection.contains(e, true)){
						Movable movable = e.getComponent(Movable.class);
						if(movable != null){
							movable.getPosition(e, pos);
							shapeRenderer.rect(pos.x-s.x, pos.y-s.y, 2*s.x, 2*s.y);
						}
					}
				}
				shapeRenderer.end();
			}
		});

		
		// build GUI
		updateSelection();
	}

	public ToolGroup createToolGroup() 
	{
		ToolGroup g = new ToolGroup();
		toolDelegator.addProcessor(g);
		tools.add(g);
		return g;
	}
	public void addGlobalTool(Tool tool) 
	{
		ToolGroup g = createToolGroup();
		g.setActiveTool(tool);
	}
	
	public void reset(){
//		toolDelegator.clear(); // XXX
//		tools.clear();
	}
	
	@Override
	public void render() 
	{
		super.render();
		
		if(selectionDirty){
			selectionDirty = false;
			updateSelection();
		}
		
		orthographicCamera.update(true);
		
		shapeRenderer.setProjectionMatrix(orthographicCamera.combined);
		
		batch.setTransformMatrix(orthographicCamera.view);
		batch.setProjectionMatrix(orthographicCamera.projection);
		batch.enableBlending();
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		
		Gdx.gl.glClearColor(.5f, .5f, .5f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		entityEngine.update(Gdx.graphics.getDeltaTime());
		
		// TODO maybe legacy
		batch.begin();
		for(ToolGroup g : tools){
			g.render(batch);
		}
		batch.end();
		for(ToolGroup g : tools){
			g.render(shapeRenderer);
		}
		
		stage.act();
		stage.draw();
		
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		Vector3 oldPos = new Vector3();
		oldPos.set(orthographicCamera.position);
		orthographicCamera.setToOrtho(false, 5 * (float)width/(float)height, 5);
		orthographicCamera.position.set(oldPos); // XXX workaround to restore translation hich is reset in ortho ...
		orthographicCamera.update(true);
		stage.getViewport().update(width, height, true);
	}
	
	
	@Override
	public void dispose () {
	}

	private static class EditorEntity implements Component
	{
	}
	
	
	public void updateSelection() 
	{
		Entity entity = selection.size == 1 ? selection.first() : null;
		outline.clear();
		
		
		// rebuild menus as well :
		buttons.clear();
//		for(ToolGroup toolGroup : tools)
//		{
			for(Tool tool : mainToolGroup.tools){
				if(tool.activator == null || (entity != null && tool.activator.matches(entity))){
					buttons.add(createToolButton(tool.name, mainToolGroup, tool));
				}
			}
//		}

		
		if(entity != null){
			// EditorEntity config = entity.getComponent(EditorEntity.class);
			
			for(Component aspect : entity.getComponents()){
				Array<EditablePlugin> editors = editablePlugins.get(aspect.getClass());
				if(editors != null)
					for(EditablePlugin editor : editors){
						outline.add(createOutlineHeader(editor, entity, aspect)).fill().row();
						outline.add(editor.createEditor(entity, skin)).fill().row();
					}
			}
		}
	}
	
	private Actor createOutlineHeader(EditablePlugin plugin, final Entity entity, final Component component)
	{
		TextButton btRemove = new TextButton("Remove " + plugin.getClass().getSimpleName(), skin);
		btRemove.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				entity.remove(component.getClass());
			}
		});
		return btRemove;
	}
	
	private Map<Class, Array<EditablePlugin>> editablePlugins = new HashMap<Class, Array<EditablePlugin>>();
	public Array<Entity> selection = new Array<Entity>();
	private boolean selectionDirty;
	public <T> void registerPlugin(Class<T> type, EditablePlugin plugin) 
	{
		Array<EditablePlugin> plugins = editablePlugins.get(type);
		if(plugins == null) editablePlugins.put(type, plugins = new Array<EditablePlugin>());
		plugins.add(plugin);
	}

	public Entity currentEntity() 
	{
		if(selection.size <= 0){
			Entity entity = entityEngine.createEntity();
			entityEngine.addEntity(entity);
			return entity;
		}
		return selection.get(selection.size-1);
	}

	public void addTool(Tool tool) {
		mainToolGroup.tools.add(tool);
	}
	
	
	public TextButton createToolButton(final ToolGroup group, final Tool tool) 
	{
		return createToolButton(tool.name, group, tool);
	}
	protected TextButton createToolButton(String name, final ToolGroup group, final Tool tool) 
	{
		final TextButton btTool = new TextButton(name, skin);
		btTool.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(btTool.isChecked()) group.setActiveTool(tool);
			}
		});
		group.addButton(btTool);
		return btTool;
	}

	public void invalidateSelection() {
		selectionDirty = true;
	}

	public Entity getSelected() 
	{
		// returns the last selected.
		return selection.size > 0 ? selection.get(selection.size-1) : null;
	}

	public <T> T loadAssetNow(String fileName, Class<T> type) {
		assets.load(fileName, type);
		assets.finishLoadingAsset(fileName);
		return assets.get(fileName, type);
	}

	public void addGlobalEditor(String name, EditorPlugin plugin) 
	{
		globalEditors.put(name, plugin);
	}


}
