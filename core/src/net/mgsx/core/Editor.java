package net.mgsx.core;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import net.mgsx.core.plugins.EditablePlugin;
import net.mgsx.core.plugins.Plugin;
import net.mgsx.core.plugins.StorablePlugin;
import net.mgsx.core.tools.MoveToolBase;
import net.mgsx.core.tools.PanTool;
import net.mgsx.core.tools.Tool;
import net.mgsx.core.tools.ToolGroup;
import net.mgsx.core.tools.UndoTool;
import net.mgsx.plugins.box2d.SkinFactory;
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
public class Editor extends ApplicationAdapter
{
	protected CommandHistory history;
	protected Skin skin;
	protected Stage stage;
	protected ShapeRenderer shapeRenderer;
	protected SpriteBatch batch;
	
	protected Table panel;
	protected Table buttons;
	protected Table outline;
	
	private Array<Plugin> plugins = new Array<Plugin>();
	
	final private Array<ToolGroup> tools = new Array<ToolGroup>();
	
	public PooledEngine entityEngine;
	
	private InputMultiplexer toolDelegator;
	
	public OrthographicCamera orthographicCamera;
	public PerspectiveCamera perspectiveCamera;
	
	private ToolGroup mainToolGroup;
	
	public void registerPlugin(Plugin plugin) {
		plugins.add(plugin);
	}
	
	@Override
	public void create() {
		super.create();
		
		entityEngine = new PooledEngine();
		
		// TODO keep it ? store some info about editor ? yes : isSelected, ...etc
		// maybe generalize as auto attach (Family) with a backed pool :
		// pool.obtain, pool.release
		entityEngine.addEntityListener(new EntityListener() {
			@Override
			public void entityRemoved(Entity entity) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void entityAdded(Entity entity) {
				EditorEntity config = new EditorEntity();
				entity.add(config);
			}
		});
		
		
		orthographicCamera = new OrthographicCamera();
		skin = SkinFactory.createSkin();
		stage = new Stage(new ScreenViewport());
		history = new CommandHistory();
		shapeRenderer = new ShapeRenderer();
		batch = new SpriteBatch();
		
		toolDelegator = new InputMultiplexer();
		
		Gdx.input.setInputProcessor(new InputMultiplexer(stage, toolDelegator));

		outline = new Table(skin);
		
		
		panel = new Table(skin);
		// TODO add menu
		buttons = new Table(skin);
		panel.add(buttons);
		panel.add(outline);
		
		Table main = new Table();
		main.add(panel).expand().left().top();
		
		main.setFillParent(true);
		stage.addActor(main);
		
		createToolGroup().addProcessor(new UndoTool(history));
		
		mainToolGroup = createToolGroup();
		addTool("Select", new NoTool(orthographicCamera));;
		
		addGlobalTool(new MoveToolBase(this));
		addGlobalTool(new PanTool(orthographicCamera));
		
		// finally initiate plugins.
		for(Plugin plugin : plugins){
			plugin.initialize(this);
		}
		
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
		if(selectionDirty){
			selectionDirty = false;
			if(selection.size > 0) setSelection(selection.get(selection.size-1)); else setSelection(null);
		}
		
		orthographicCamera.update(true);
		
		shapeRenderer.setProjectionMatrix(orthographicCamera.combined);
		
		batch.setTransformMatrix(orthographicCamera.view);
		batch.setProjectionMatrix(orthographicCamera.projection);
		batch.enableBlending();
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		
		Gdx.gl.glClearColor(.5f, .5f, .5f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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
		
		super.render();
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		orthographicCamera.setToOrtho(false, 5 * (float)width/(float)height, 5);
		orthographicCamera.update(true);
		stage.getViewport().update(width, height, true);
	}
	
	
	@Override
	public void dispose () {
	}

	private Json json;
	
	public <T> void registerPlugin(Class<T> type, StorablePlugin<T> plugin) 
	{
		json.setSerializer(type, plugin);
	}
	private static class EditorEntity implements Component
	{
	}
	public void setSelection(Entity entity) 
	{
		outline.clear();
		selection.clear();
		if(entity != null){
			selection.add(entity);
			// EditorEntity config = entity.getComponent(EditorEntity.class);
			for(Object aspect : entity.getComponents()){
				Array<EditablePlugin> editors = editablePlugins.get(aspect.getClass());
				if(editors != null)
					for(EditablePlugin editor : editors){
						
						outline.add(editor.createEditor(entity, skin)).fill().row();
					}
			}
		}
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
			selection.add(entity);
		}
		return selection.get(selection.size-1);
	}

	public void addTool(String name, Tool tool) {
		buttons.add(createToolButton(name, mainToolGroup, tool));
	}
	
	@Deprecated
	protected TextButton createToolButton(final ToolGroup group, final Tool tool) 
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


}
