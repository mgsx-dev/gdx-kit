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
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
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

import net.mgsx.core.NativeService.DialogCallback;
import net.mgsx.core.commands.Command;
import net.mgsx.core.commands.CommandHistory;
import net.mgsx.core.components.Attach;
import net.mgsx.core.components.Movable;
import net.mgsx.core.helpers.EntityHelper.SingleComponentIteratingSystem;
import net.mgsx.core.plugins.EditorPlugin;
import net.mgsx.core.plugins.EntityEditorPlugin;
import net.mgsx.core.plugins.GlobalEditorPlugin;
import net.mgsx.core.plugins.SelectorPlugin;
import net.mgsx.core.storage.Storage;
import net.mgsx.core.tools.DeleteTool;
import net.mgsx.core.tools.DuplicateTool;
import net.mgsx.core.tools.FollowSelectionTool;
import net.mgsx.core.tools.NoTool;
import net.mgsx.core.tools.PanTool;
import net.mgsx.core.tools.SelectTool;
import net.mgsx.core.tools.Tool;
import net.mgsx.core.tools.ToolGroup;
import net.mgsx.core.tools.UndoTool;
import net.mgsx.core.tools.ZoomTool;
import net.mgsx.core.ui.TabPane;
import net.mgsx.plugins.box2dold.SkinFactory;

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
	public CommandHistory history;
	protected Skin skin;
	protected Stage stage;
	protected Table panel;
	protected Table buttons;
	protected Table outline;
	protected TabPane global;
	
	public Array<SelectorPlugin> selectors = new Array<SelectorPlugin>();
	
	final private Array<ToolGroup> tools = new Array<ToolGroup>();
	
	private InputMultiplexer toolDelegator;
	
	private ToolGroup mainToolGroup; // XXX temporarily public ...
	public ToolGroup subToolGroup;
	
	private Map<String, GlobalEditorPlugin> globalEditors = new LinkedHashMap<String, GlobalEditorPlugin>();
	
	public void registerPlugin(EditorPlugin plugin) {
		editorPlugins.add(plugin);
	}

	
	@Override
	public void create() 
	{
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
		Table superGlobal = new Table(skin);
		
		TextButton btSave = new TextButton("Save", skin);
		TextButton btOpen = new TextButton("Open", skin);
		TextButton btReset = new TextButton("Reset", skin);
		
		superGlobal.add(btSave);
		superGlobal.add(btOpen);
		superGlobal.add(btReset);
		
		
		btSave.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				NativeService.instance.openSaveDialog(new DialogCallback() {
					@Override
					public void selected(FileHandle file) {
						Storage.save(entityEngine, assets, file, true, serializers); // TODO pretty configurable
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
						Storage.load(entityEngine, file, assets, serializers);
						// TODO ? rebuild();
					}
					@Override
					public void cancel() {
					}
				});
			}
		});
		btReset.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				entityEngine.removeAllEntities();
				selection.clear();
				invalidateSelection();
			}
		});
		
		
		
		
		panel.add(superGlobal).row();
		panel.add(global).row();
		panel.add(buttons).row();
		panel.add(outline).row();
		
		Table main = new Table();
		main.add(panel).expand().left().top();
		
		main.setFillParent(true);
		stage.addActor(main);
		
		createToolGroup().addProcessor(new UndoTool(this));
		
		subToolGroup = createToolGroup();
		mainToolGroup = createToolGroup();
		
		addTool(new NoTool("Select", this));;
		addTool(new DeleteTool("Delete", this));;
		
//		addGlobalTool(new SelectToolBase(this){
//			@Override
//			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
//				setSelection(null);
//				return super.touchDown(screenX, screenY, pointer, button);
//			}
//		});

		// order is very important !
		addGlobalTool(new SelectTool(this));
		addGlobalTool(new ZoomTool(this));
		addGlobalTool(new PanTool(this));
		addGlobalTool(new DuplicateTool(this));
		addGlobalTool(new FollowSelectionTool(this));

		// register listener after plugins creation to create filters on all possible components
		// finally initiate plugins.
		// TODO separate runtme plugin part (model, serialization, update, render) from editor part
		for(EditorPlugin plugin : editorPlugins){
			plugin.initialize(this);
		}
		


		global.addTab("Off", new Label("", skin));
		for(Entry<String, GlobalEditorPlugin> entry : globalEditors.entrySet()){
			global.addTab(entry.getKey(), entry.getValue().createEditor(this, skin));
		}
		// global.sett
		
		// TODO  maybe generalize as auto attach (Family) with a backed pool : pool.obtain, pool.release
		
		// listener added after : all entity add/remove can be rollback/restore
		// TODO it mess with pool and is over complicated !
		// should be done manual way (tools create commands !) :
		// add component command, add entity command ...
		entityEngine.addEntityListener(new EntityListener() {
			@Override
			public void entityRemoved(final Entity entity) {
				entity.remove(EditorEntity.class);
			}
			@Override
			public void entityAdded(final Entity entity) {
				EditorEntity config = new EditorEntity(); // TODO maybe if not already have a component, TODO create an auto component listener which handle these cases ....
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
		
		entityEngine.addSystem(new SingleComponentIteratingSystem<Attach>(Attach.class) {

			@Override
			protected void processEntity(Entity entity, Attach component, float deltaTime) {
				component.update();
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

	public static class EditorEntity implements Component
	{
		public Array<ComponentFactory> factories = new Array<ComponentFactory>();
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
				Array<EntityEditorPlugin> editors = editablePlugins.get(aspect.getClass());
				if(editors != null)
					for(EntityEditorPlugin editor : editors){
						outline.add(createOutlineHeader(editor, entity, aspect)).fill().row();
						outline.add(editor.createEditor(entity, skin)).fill().row();
					}
			}
		}
	}
	
	private Actor createOutlineHeader(EntityEditorPlugin plugin, final Entity entity, final Component component)
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
	
	private Map<Class, Array<EntityEditorPlugin>> editablePlugins = new HashMap<Class, Array<EntityEditorPlugin>>();
	public Array<Entity> selection = new Array<Entity>();
	public boolean selectionDirty;
	public <T> void registerPlugin(Class<T> type, EntityEditorPlugin plugin) 
	{
		Array<EntityEditorPlugin> plugins = editablePlugins.get(type);
		if(plugins == null) editablePlugins.put(type, plugins = new Array<EntityEditorPlugin>());
		plugins.add(plugin);
	}

	public Entity currentEntity() 
	{
		if(selection.size <= 0){
			Entity entity = createEntity();
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

	public <T> T loadAssetNow(String fileName, Class<T> type, AssetLoaderParameters<T> parameters) {
		assets.load(fileName, type, parameters);
		assets.finishLoadingAsset(fileName);
		return assets.get(fileName, type);
	}

	public void addGlobalEditor(String name, GlobalEditorPlugin plugin) 
	{
		globalEditors.put(name, plugin);
	}

	public Vector2 unproject(float screenX, float screenY) {
		Vector3 v = orthographicCamera.unproject(new Vector3(screenX, screenY, 0));
		return new Vector2(v.x, v.y);
	}

	public void addSelector(SelectorPlugin selector) {
		selectors.add(selector);
	}
	public void assetLookup(Class<Texture> type, final AssetLookupCallback<Texture> callback) 
	{
		// TODO open texture region selector if any registered
		// else auto open import window
		NativeService.instance.openLoadDialog(new DialogCallback() {
			@Override
			public void selected(FileHandle file) 
			{
				TextureParameter parameters = new TextureParameter();
				parameters.genMipMaps = true;
				Texture tex = loadAssetNow(file.path(), Texture.class, parameters);
				tex.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.MipMapLinearLinear);
				tex.setWrap(TextureWrap.ClampToEdge, TextureWrap.ClampToEdge);
				callback.selected(tex);
			}
			@Override
			public void cancel() {
			}
		});
	}

	
	public void addComponent(final ComponentFactory factory) 
	{
		final Entity entity = currentEntity();
		entity.getComponent(EditorEntity.class).factories.add(factory);
		
		history.add(new Command(){
			// TODO maybe it overrides a component so need to store it but
			// dont mess with pool, component object may be reused
			private Class<? extends Component> type;
			@Override
			public void commit() {
				Component component = factory.create(entity);
				type = component.getClass();
				entity.add(component);
			}
			@Override
			public void rollback() {
				entity.remove(type);
			}
		});
		
	}

	public Entity createEntity() {
		return entityEngine.createEntity(); // new Entity(); // TODO use pool
	}


}
