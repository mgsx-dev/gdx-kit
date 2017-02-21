package net.mgsx.game.core;

import java.util.Comparator;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json.Serializer;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.commands.Command;
import net.mgsx.game.core.commands.CommandHistory;
import net.mgsx.game.core.components.Movable;
import net.mgsx.game.core.components.Repository;
import net.mgsx.game.core.editors.AnnotationBasedComponentEditor;
import net.mgsx.game.core.helpers.AssetHelper;
import net.mgsx.game.core.helpers.AssetLookupCallback;
import net.mgsx.game.core.helpers.EditorAssetManager;
import net.mgsx.game.core.helpers.NativeService;
import net.mgsx.game.core.helpers.NativeService.DefaultCallback;
import net.mgsx.game.core.plugins.EntityEditorPlugin;
import net.mgsx.game.core.plugins.GlobalEditorPlugin;
import net.mgsx.game.core.plugins.SelectorPlugin;
import net.mgsx.game.core.screen.ScreenDelegate;
import net.mgsx.game.core.storage.LoadConfiguration;
import net.mgsx.game.core.tools.ComponentTool;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.core.tools.ToolGroup;
import net.mgsx.game.core.ui.EntityEditor;
import net.mgsx.game.core.ui.accessors.Accessor;
import net.mgsx.game.core.ui.events.AccessorHelpEvent;
import net.mgsx.game.core.ui.events.EditorListener;
import net.mgsx.game.core.ui.widgets.TabPane;
import net.mgsx.game.plugins.core.tools.UndoTool;
import net.mgsx.game.plugins.editor.systems.EditorSystem;

/**
 * Base screen for game editor.
 * Work on Game screen (the screen to edit)
 * 
 * @author mgsx
 *
 */
public class EditorScreen extends ScreenDelegate implements EditorContext
{
	// TODO handle selection in separated class
	
	private static final String STATUS_HIDDEN_TEXT = "Press F1 to toggle help";

	public CommandHistory history;
	
	private boolean showStatus;
	private String currentText;
	
	public Skin skin;
	protected Stage stage;
	protected Table panel;
	protected Table buttons;
	protected Table outline;
	protected TabPane global;
	private Table superGlobal;
	private VerticalGroup pinStack;
	
	private SpriteBatch editorBatch;
	
	public EditorRegistry registry;
	
	public Array<SelectorPlugin> selectors = new Array<SelectorPlugin>();
	
	final private Array<ToolGroup> tools = new Array<ToolGroup>();
	
	private InputMultiplexer toolDelegator;
	
	private ToolGroup mainToolGroup;
	
	/** tools displayed as button when selection change (contextual tools) */
	private Array<Tool> mainTools = new Array<Tool>();
	
	
	private Array<Tool> autoTools = new Array<Tool>();
	public ShapeRenderer shapeRenderer;
	public GameScreen game;
	
	private EditorCamera editorCamera;
	
	final public EditorAssetManager assets;
	final public Engine entityEngine;
	
	public final ObjectMap<Class, Serializer> serializers;

	private Label status;

	private Array<Button> contextualButtons = new Array<Button>();

	private ObjectSet<EditorListener> listeners = new ObjectSet<EditorListener>();
	
	public EditorScreen(EditorConfiguration config, GameScreen screen, EditorAssetManager assets, Engine engine) {
		super(screen);
		this.game = screen;
		this.game.registry = config.registry;
		this.entityEngine = engine;
		this.assets = assets;
		this.serializers = config.registry.serializers;
		this.registry = config.registry;
		editorCamera = new EditorCamera();
		init();
	}
	
	public void addListener(EditorListener listener)
	{
		listeners.add(listener);
	}
	
	public void fireLoadEvent(LoadConfiguration cfg)
	{
		pinEditors(cfg.visibleSystems);
		
		for(EditorListener listener : listeners) listener.onLoad(cfg);
	}
	
	private Table createMainTable()
	{
		status = new Label(STATUS_HIDDEN_TEXT, skin);
		LabelStyle style = new LabelStyle(status.getStyle());
		style.fontColor.set(Color.ORANGE);
		status.setStyle(style);
		
		pinStack = new VerticalGroup();
		pinStack.fill();
		
		Table table = new Table(skin);
		table.add(panel).expand().left().top().row();
		
		toolOutline = new Table(skin);
		table.add(toolOutline).left().row();
		table.add(status).left();
		// table.add(scroll).expand().right().top();
		
		Table rootTable = new Table(skin);
		
		rootTable.add(table).expand().fillY().left();
		rootTable.add(new ScrollPane(pinStack, skin, "light")).expandY().top().fillX();
		
		return rootTable;
	}
	
	private void init()
	{
		editorBatch = new SpriteBatch();
		
		shapeRenderer = new ShapeRenderer();
		
		skin = new Skin(Gdx.files.classpath("uiskin.json"));
		
		stage = new Stage(new ScreenViewport());
		history = new CommandHistory();
		
		toolDelegator = new InputMultiplexer();
		
		panel = new Table(skin);
		
		
		// TODO add menu
		global = new TabPane(skin);
		
		buttons = new Table(skin); 
		buttons.setBackground(skin.getDrawable("default-rect"));
		
		outline = new Table(skin); 
		superGlobal = new Table(skin);
		
		
		Table globalBlock = new Table(skin);
		// globalBlock.setBackground(skin.getDrawable("default-window-body"));
		
		globalBlock.add(superGlobal).left().row();
		globalBlock.add(global).left().row();
		
		panel.add(globalBlock);
		
		Table main = createMainTable();
		main.setFillParent(true);
		stage.addActor(main);
		
		main.addListener(new EventListener() {
			@Override
			public boolean handle(Event event) {
				if(event instanceof AccessorHelpEvent){
					Accessor a = ((AccessorHelpEvent) event).getAccessor();
					Editable c = a.config();
					showStatus = true; // force show help
					if(c != null && !c.doc().isEmpty()) setInfo(c.doc());
					return true;
				}
				return false;
			}
		});
		
		createToolGroup().addProcessor(new UndoTool(this));
		
		mainToolGroup = createToolGroup();

		registry.init(this);

		for(final Class<? extends Component> type : registry.components){
			EditableComponent config = type.getAnnotation(EditableComponent.class);
			if(config != null){
				registry.registerPlugin(type, new AnnotationBasedComponentEditor(type));
				if(config.autoTool()){
					Family family = null;
					if(config.all().length > 0 || config.one().length > 0 || config.exclude().length > 0){
						family = Family.all(config.all()).one(config.one()).exclude(config.exclude()).get();
					}
					String name = config.name().isEmpty() ? type.getSimpleName() : config.name();
					Tool autoTool = new ComponentTool(name, this, family){
						@Override
						protected Component createComponent(Entity entity) {
							return entityEngine.createComponent(type);
						}
					};
					registry.setTag(autoTool, registry.getTagByType(type));
					autoTools.add(autoTool);
				}
			}
		}
		
		for(Tool tool : autoTools){
			addTool(tool);
		}
		
		
		global.addTab("Tools", new ScrollPane(buttons, skin));
		global.addTab("Components", new ScrollPane(outline, skin, "light"));
		for(Entry<String, GlobalEditorPlugin> entry : entityEngine.getSystem(EditorSystem.class).globalEditors.entries()){
			global.addTab(entry.key, entry.value.createEditor(this, skin));
		}
		
		// listener for component add/remove
		EntityListener selectionListener = new EntityListener() {
			
			@Override
			public void entityRemoved(Entity entity) {
				if(selection.contains(entity, true)){
					invalidateSelection();
				}
			}
			
			@Override
			public void entityAdded(Entity entity) {
				if(entity == getSelected()) invalidateSelection();
			}
		};
		
		for(Class<? extends Component> type : registry.editablePlugins.keySet()){
			entityEngine.addEntityListener(Family.one(type).get(), selectionListener);
		}
		
		// listener for entity add/remove
		entityEngine.addEntityListener(new EntityListener() {
			
			@Override
			public void entityRemoved(Entity entity) {
				if(selection.contains(entity, true)){
					selection.removeValue(entity, true);
					invalidateSelection();
				}
			}
			
			@Override
			public void entityAdded(Entity entity) {
			}
		});
		
		
		// build GUI
		updateSelection();
		
		// patch input processor : some systems (HUD) may have set input processor so
		// we need to just override it, not replace it.
		InputProcessor previousInputProcessor = Gdx.input.getInputProcessor();
		if(previousInputProcessor == null){
			Gdx.input.setInputProcessor(new InputMultiplexer(stage, toolDelegator));
		}else{
			Gdx.input.setInputProcessor(new InputMultiplexer(stage, toolDelegator, previousInputProcessor));
		}
	}

	public ToolGroup createToolGroup() 
	{
		ToolGroup g = new ToolGroup(this);
		toolDelegator.addProcessor(g);
		tools.add(g);
		return g;
	}
	public void addGlobalTool(Tool tool) 
	{
		ToolGroup g = createToolGroup();
		g.setActiveTool(tool);
	}
	private Array<Actor> rootBackup;
	final private Array<EntitySystem> processingDebugSystem = new Array<EntitySystem>();
	
	public void switchVisibility(){
		if(displayEnabled){
			rootBackup = new Array<Actor>(stage.getRoot().getChildren());
			stage.getRoot().clearChildren();
			displayEnabled = false;
			processingDebugSystem.clear();
			for(EntitySystem system : entityEngine.getSystems())
			{
				EditableSystem config = system.getClass().getAnnotation(EditableSystem.class);
				if(config != null && config.isDebug() && system.checkProcessing()){
					processingDebugSystem.add(system);
					system.setProcessing(false);
				}
			}
		}else{
			for(Actor actor : rootBackup) stage.addActor(actor);
			rootBackup = null;
			displayEnabled = true;
			
			for(EntitySystem system : processingDebugSystem)
			{
				system.setProcessing(true);
			}
		}
	}
	
	@Override
	public void render(float deltaTime) 
	{
		if(selectionDirty)
		{
			selectionDirty = false;
			updateSelection();
		}
		
		shapeRenderer.setProjectionMatrix(getGameCamera().combined);
		
		current.render(deltaTime);

		if(displayEnabled){
			stage.act();
		}
		
		
		// TODO use systems instead (used by sprite tools ...)
		
		editorBatch.setProjectionMatrix(getGameCamera().combined);
		editorBatch.begin();
		for(ToolGroup g : tools){
			g.render(editorBatch);
		}
		editorBatch.end();
		
		for(ToolGroup g : tools){
			g.update(deltaTime);
			g.render(shapeRenderer);
		}
		
		if(displayEnabled){

			
			stage.draw();
		}
		
		
	}
	
	public Movable getMovable(Entity entity)
	{
		// temporarily use Movable component ... for now
		return Movable.components.get(entity);
	}
	
	public EditorCamera getEditorCamera() {
		return editorCamera;
	}
	
	@Override
	public void resize(int width, int height) 
	{
		super.resize(width, height);
		
		editorCamera.resize(width, height);
		
		stage.getViewport().update(width, height, true);
	}
	
	
	@Override
	public void dispose () {
	}

	private String pluginFilter;
	public boolean showAllTools = false;
	public final Array<EntitySystem> pinnedSystems = new Array<EntitySystem>();
	
	private void updateSelection() 
	{
		final Entity entity = selection.size > 0 ? selection.peek() : null;
		
		for(Button button : contextualButtons ){
			mainToolGroup.removeButton(button);
		}
		contextualButtons.clear();
		
		buttons.clear();
		outline.clear();
		outline.setBackground((Drawable)null);
		
		buttons.add(String.valueOf(selection.size) + " entities").expandX().fill().row();
			
		
		// TODO move to Tool bar class update ...
			
		// Display all tools
		buttons.add("Tools").expandX().center().row();
		
		// add filter
		final SelectBox<String> pluginFilterBox = new SelectBox<String>(skin);
		
		Array<String> allPlugins = new Array<String>();
		allPlugins.add("");
		allPlugins.addAll(registry.allTags());
		allPlugins.sort();
		pluginFilterBox.setItems(allPlugins);
		pluginFilterBox.setSelected(pluginFilter == null ? "" : pluginFilter);
		pluginFilterBox.addListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				pluginFilter = pluginFilterBox.getSelected();
				updateSelection();
			}
		});
		
		
		buttons.add(pluginFilterBox).expandX().center();
		
		final Button btShow = EntityEditor.createBoolean(skin, showAllTools);
		btShow.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				showAllTools = btShow.isChecked();
				invalidateSelection();
			}
		});
		buttons.add("Unavailable");
		buttons.add(btShow).row();
		
		// TODO maybe not at each time ... ?
		mainTools.sort(new Comparator<Tool>() {

			@Override
			public int compare(Tool o1, Tool o2) {
				return o1.name.compareTo(o2.name);
			}
		});
		
		for(Tool tool : mainTools)
		{
			// check if tool is in current plugin filter.
			boolean accepted = true;
			accepted &= tool.allowed(selection);
			accepted &= pluginFilter == null || pluginFilter.isEmpty() || pluginFilter.equals(registry.getTag(tool));
			accepted &= tool.activator == null || (entity != null && tool.activator.matches(entity));
			if(accepted || showAllTools)
			{
				Button button = createToolButton(tool.name, mainToolGroup, tool, accepted);
				contextualButtons.add(button);
				buttons.add(button).fill().row();
			}
		}
		
		// Display all entity components (unique entity only)
		if(selection.size == 1)
		{
			Button btRemove = new Button(skin.getDrawable("tree-minus"));
			btRemove.addListener(new ChangeListener(){
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					entityEngine.removeEntity(entity);
				}
			});
			
			outline.setBackground(skin.getDrawable("default-rect"));
			String title = "Entity # " + String.valueOf(entityEngine.getEntities().indexOf(entity, true));
			outline.add(title).expandX().center();
			outline.add(btRemove).row();
			for(Component aspect : entity.getComponents()){
				outline.add(createOutline(entity, aspect)).expandX().fill().row();
			}
		}
		
	}
	
	private Actor createOutline(final Entity entity, final Component component)
	{
		final boolean hasEditors = registry.editablePlugins.get(component.getClass()) != null;
		
		
		final Table bodyTable = new Table(skin);
		bodyTable.setBackground(skin.getDrawable("default-window-body"));
		
		final Table headerTable = new Table(skin);
		if(hasEditors){
			final Button btOpenClose = new Button(skin, "node");
			
			headerTable.add(btOpenClose).padRight(4);
			btOpenClose.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) 
				{
					if(btOpenClose.isChecked()){
						createComponentEditor(bodyTable, entity, component);
					}else{
						bodyTable.clear();
					}
				}
			});
			
			final Button btPin = new TextButton("pin", skin);
			
			headerTable.add(btPin).padRight(4);
			btPin.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) 
				{
					pinEditor(entity, component);
				}
			});
		}
		
		Button btRemove = new Button(skin.getDrawable("tree-minus"));
		btRemove.addListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				entity.remove(component.getClass());
			}
		});
		
		headerTable.add(component.getClass().getSimpleName()).expandX().left();
		headerTable.add(btRemove);
		
		headerTable.setBackground(skin.getDrawable("default-window-header"));
		
		
		final Table group = new Table(skin);
		
		group.add(headerTable).expandX().fill().row();
		group.add(bodyTable).expandX().fill();
		
		
		return group;
	}
	
	public void pinEditor(Entity entity, Component component) 
	{
		pinStack.addActor(createPinEditor(entity, component));
		
	}
	public void pinEditor(Actor editor) 
	{
		pinStack.addActor(editor);
	}
	public void pinEditor(EntitySystem system) 
	{
		if(!pinnedSystems.contains(system, true)){
			pinnedSystems.add(system);
			pinStack.addActor(createPinEditor(system));
		}
	}
	public void pinEditors(Array<EntitySystem> systems) {
		for(EntitySystem system : systems)
		{
			pinEditor(system);
		}
	}
	
	public void unpinEditor(Actor editor){
		pinStack.removeActor(editor);
	}

	private Actor createPinEditor(final Entity entity, final Component component) 
	{
		final boolean hasEditors = registry.editablePlugins.get(component.getClass()) != null;
		
		final Table group = new Table(skin);

		
		final Table bodyTable = new Table(skin);
		bodyTable.setBackground(skin.getDrawable("default-window-body"));
		
		final Table headerTable = new Table(skin);
		if(hasEditors){
			final Button btOpenClose = new Button(skin, "node");
			
			headerTable.add(btOpenClose).padRight(4);
			btOpenClose.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) 
				{
					if(btOpenClose.isChecked()){
						createComponentEditor(bodyTable, entity, component);
					}else{
						bodyTable.clear();
					}
				}
			});
			
			btOpenClose.setChecked(true);
		}
		
		Button btRemove = new Button(skin.getDrawable("tree-minus"));
		btRemove.addListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				unpinEditor(group);
			}
		});
		
		headerTable.add(component.getClass().getSimpleName()).expandX().left();
		headerTable.add(btRemove).padRight(24);
		
		headerTable.setBackground(skin.getDrawable("default-window-header"));
		
		
		group.add(headerTable).expandX().fill().row();
		group.add(bodyTable).expandX().fill();
		
		
		return group;
	}
	private Actor createPinEditor(final EntitySystem system) 
	{
		final Table group = new Table(skin);
		
		final Table bodyTable = new Table(skin);
		bodyTable.setBackground(skin.getDrawable("default-window-body"));
		
		final Table headerTable = new Table(skin);
		final Button btOpenClose = new Button(skin, "node");
		
		headerTable.add(btOpenClose).padRight(4);
		btOpenClose.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) 
			{
				if(btOpenClose.isChecked()){
					createSystemEditor(bodyTable, system);
				}else{
					bodyTable.clear();
				}
			}
		});
			
		Button btRemove = new Button(skin.getDrawable("tree-minus"));
		btRemove.addListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				pinnedSystems.removeValue(system, true);
				unpinEditor(group);
			}
		});
		
		headerTable.add(system.getClass().getSimpleName()).expandX().left();
		headerTable.add(btRemove);
		
		headerTable.setBackground(skin.getDrawable("default-window-header"));
		
		
		group.add(headerTable).expandX().fill().row();
		group.add(bodyTable).expandX().fill();
		
		btOpenClose.setChecked(true);
		
		return group;
	}
	protected void createSystemEditor(Table table, EntitySystem system) {
		table.add(new EntityEditor(system, true, skin));
	}

	private void createComponentEditor(Table table, Entity entity, Component component)
	{
		Array<EntityEditorPlugin> editors = registry.editablePlugins.get(component.getClass());
		if(editors != null){
			for(EntityEditorPlugin editor : editors){
				table.add(editor.createEditor(entity, skin)).row();
			}
		}
	}
	
	
	public Array<Entity> selection = new Array<Entity>();
	private boolean selectionDirty;
	private boolean displayEnabled = true; // true by default

	public Table toolOutline;

	/**
	 * get current entity which can be the selected entity (last in selection)
	 * or a fresh new one.
	 * Note that entity will have repository component which mark it as persistable.
	 * use {@link #transcientEntity()} to create a non persistable entity.
	 * @return
	 */
	public Entity currentEntity() 
	{
		if(selection.size <= 0){
			Entity entity = entityEngine.createEntity();
			entity.add(entityEngine.createComponent(Repository.class));
			entityEngine.addEntity(entity);
			return entity;
		}
		return selection.get(selection.size-1);
	}
	
	public Entity transcientEntity(){
		if(selection.size <= 0){
			return entityEngine.createEntity();
		}
		Entity entity = selection.get(selection.size-1);
		if(Repository.components.has(entity)){
			return entityEngine.createEntity();
		}
		return entity;
	}

	public void addTool(Tool tool) {
		mainToolGroup.tools.add(tool);
		mainTools.add(tool);
	}
	public void addSuperTool(Tool tool) {
		mainToolGroup.tools.add(tool);
		superGlobal.add(createToolButton(tool, true));
	}
	
	public void addSubTool(Tool tool) {
		mainToolGroup.tools.add(tool);
	}
	
	
	public Button createToolButton(final Tool tool, boolean enabled) 
	{
		return createToolButton(tool.name, mainToolGroup, tool, enabled); // all groups
	}
	private Button createToolButton(String name, final ToolGroup group, final Tool tool, boolean enabled) 
	{
		final Button btTool = new ImageButton(skin, "toggle");
				// new TextButton("", skin, "toggle");
		btTool.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(btTool.isChecked()) group.setActiveTool(tool);
				// else group.setActiveTool(null);
			}
		});

		Image img = new Image(skin, "kit_tool");
		
		Color color;
		if(!enabled)
		{
			btTool.setDisabled(true);
			color = Color.GRAY;
		}
		else
		{
			color = tool instanceof ComponentTool ? Color.GREEN : Color.ORANGE;
		}
		
		Label nameLabel = new Label(name, skin);
		nameLabel.setColor(btTool.isDisabled() ? Color.LIGHT_GRAY : Color.WHITE);
		
		img.setColor(color);
		btTool.add(img).padRight(6);
		btTool.add(nameLabel).expand().fill();
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
		return AssetHelper.loadAssetNow(assets, fileName, type);
	}
	public <T> T loadAssetNow(String fileName, Class<T> type, AssetLoaderParameters<T> parameters) {
		return AssetHelper.loadAssetNow(assets, fileName, type, parameters);
	}

	public Vector2 unproject(float screenX, float screenY) {
		return Tool.unproject(getGameCamera(), screenX, screenY);
	}

	public void addSelector(SelectorPlugin selector) {
		selectors.add(selector);
	}
	public void assetLookup(Class<Texture> type, final AssetLookupCallback<Texture> callback) 
	{
		// TODO open texture region selector if any registered
		// else auto open import window
		NativeService.instance.openLoadDialog(new DefaultCallback() {
			@Override
			public void selected(FileHandle file) 
			{
				TextureParameter parameters = new TextureParameter();
				parameters.genMipMaps = true;
				Texture tex = loadAssetNow(file.path(), Texture.class, parameters);
				tex.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.MipMapLinearLinear);
				tex.setWrap(TextureWrap.ClampToEdge, TextureWrap.ClampToEdge); // XXX maybe not !
				callback.selected(tex);
			}
			@Override
			public boolean match(FileHandle file) {
				// TODO others ?
				return file.extension().equals("png") || file.extension().equals("jpg") || file.extension().equals("bmp");
			}
			@Override
			public String description() {
				return "Pixel files (png, jpg, bmp)";
			}
		});
	}

	public void performCommand(Command command) 
	{
		history.add(command);
	}

	public void reset() 
	{
		entityEngine.removeAllEntities();
		
		// clear all assets used by engine (not these loaded by editor).
		assets.clear();
		
		editorCamera.reset();
		
		selection.clear();
		invalidateSelection();
	}

	// TODO externalize selection : editor.selection.set/clear/add...etc invalidating is done in it
	public void setSelection(Entity entity) {
		selection.clear();
		selection.add(entity);
		invalidateSelection();
	}

	public void setInfo(String message) {
		currentText = message;
		if(showStatus) status.setText(message);
	}

	public void toggleStatus() {
		if(showStatus){
			status.setText(STATUS_HIDDEN_TEXT);
			showStatus = false;
		}else{
			status.setText(currentText);
			showStatus = true;
		}
	}

	public Camera getGameCamera() { // TODO rename getCamera
		return editorCamera.isActive() ? editorCamera.camera() : game.camera;
	}

	public void setTool(Tool tool) 
	{
		mainToolGroup.setActiveTool(tool);
	}

}
