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
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import net.mgsx.core.plugins.EditablePlugin;
import net.mgsx.core.plugins.Plugin;
import net.mgsx.core.plugins.StorablePlugin;
import net.mgsx.core.tools.Tool;
import net.mgsx.core.tools.ToolGroup;
import net.mgsx.core.tools.UndoTool;
import net.mgsx.plugins.box2d.SkinFactory;
import net.mgsx.plugins.sprite.SpriteModel;

public class Editor extends ApplicationAdapter
{
	protected CommandHistory history;
	protected Skin skin;
	protected Stage stage;
	protected ShapeRenderer shapeRenderer;
	protected SpriteBatch batch;
	
	protected Table panel;
	protected Table outline;
	
	private Array<Plugin> plugins = new Array<Plugin>();
	
	final private Array<ToolGroup> tools = new Array<ToolGroup>();
	
	public PooledEngine entityEngine;
	
	private InputMultiplexer toolDelegator;
	
	public OrthographicCamera orthographicCamera;
	public PerspectiveCamera perspectiveCamera;
	
	public void registerPlugin(Plugin plugin) {
		plugins.add(plugin);
	}
	
	@Override
	public void create() {
		super.create();
		
		entityEngine = new PooledEngine();
		
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
		panel.add(outline);
		
		Table main = new Table();
		main.add(panel).expand().left().top();
		
		main.setFillParent(true);
		stage.addActor(main);
		
		createToolGroup().addProcessor(new UndoTool(history));
		
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
		// TODO maybe legacy
		batch.begin();
		for(ToolGroup g : tools){
			g.render(batch);
		}
		batch.end();
		for(ToolGroup g : tools){
			g.render(shapeRenderer);
		}
		
		entityEngine.update(Gdx.graphics.getDeltaTime());
		
		stage.act();
		stage.draw();
		
		super.render();
	}
	
	@Override
	public void resize(int width, int height) {
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
	private Entity selected = null;
	private static class EditorEntity implements Component
	{
	}
	public void setSelection(Entity entity) 
	{
		outline.clear();
		selected = entity;
		// EditorEntity config = entity.getComponent(EditorEntity.class);
		for(Object aspect : entity.getComponents()){
			Array<EditablePlugin> editors = editablePlugins.get(aspect.getClass());
			if(editors != null)
				for(EditablePlugin editor : editors){
					
					outline.add(editor.createEditor(entity, skin)).fill().row();
				}
		}
		
	}
	
	private Map<Class, Array<EditablePlugin>> editablePlugins = new HashMap<Class, Array<EditablePlugin>>();
	public Array<Entity> selection = new Array<Entity>();
	public <T> void registerPlugin(Class<T> type, EditablePlugin plugin) 
	{
		Array<EditablePlugin> plugins = editablePlugins.get(type);
		if(plugins == null) editablePlugins.put(type, plugins = new Array<EditablePlugin>());
		plugins.add(plugin);
	}

	public Entity currentEntity() 
	{
		if(selected == null){
			selected = entityEngine.createEntity();
			entityEngine.addEntity(selected);
		}
		return selected;
	}

}
