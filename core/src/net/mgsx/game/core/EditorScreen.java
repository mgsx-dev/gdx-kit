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
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
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
import com.badlogic.gdx.utils.ObjectSet;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.binding.Learnable;
import net.mgsx.game.core.components.Movable;
import net.mgsx.game.core.editors.AnnotationBasedComponentEditor;
import net.mgsx.game.core.helpers.AssetHelper;
import net.mgsx.game.core.helpers.EditorAssetManager;
import net.mgsx.game.core.plugins.EngineEditor;
import net.mgsx.game.core.plugins.EntityEditorPlugin;
import net.mgsx.game.core.plugins.SelectorPlugin;
import net.mgsx.game.core.screen.ScreenDelegate;
import net.mgsx.game.core.storage.LoadConfiguration;
import net.mgsx.game.core.tools.ComponentTool;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.core.tools.ToolGroup;
import net.mgsx.game.core.tools.ToolGroup.ToolGroupHandler;
import net.mgsx.game.core.ui.EntityEditor;
import net.mgsx.game.core.ui.accessors.Accessor;
import net.mgsx.game.core.ui.accessors.VoidAccessor;
import net.mgsx.game.core.ui.events.AccessorHelpEvent;
import net.mgsx.game.core.ui.events.EditorListener;
import net.mgsx.game.core.ui.widgets.TabPane;
import net.mgsx.game.plugins.core.tools.UndoTool;
import net.mgsx.game.plugins.editor.systems.EditorSystem;
import net.mgsx.game.plugins.editor.systems.SelectionSystem;

/**
 * Base screen for game editor.
 * Work on Game screen (the screen to edit)
 * 
 * @author mgsx
 *
 */
public class EditorScreen extends ScreenDelegate implements EditorContext // TODO should be a HUD system (system with stage)
{
	// TODO handle selection in separated class
	
	private static final String STATUS_HIDDEN_TEXT = "Press F1 to toggle help";

	// new version
	
	@Inject protected SelectionSystem selection;
	@Inject protected EditorSystem editor;
	
	// new version
	
	
	private boolean showStatus;
	private String currentText;
	
	public Skin skin;
	public Stage stage; // XXX temp
	protected Table panel;
	protected Table buttons;
	protected Table outline;
	protected TabPane global;
	private Table superGlobal;
	private VerticalGroup pinStack;
	
	public EditorRegistry registry; // TODO move to a system ?
	
	private InputMultiplexer toolDelegator;
	
	private ToolGroup mainToolGroup;
	
	/** tools displayed as button when selection change (contextual tools) */
	private Array<Tool> mainTools = new Array<Tool>();
	
	
	private Array<Tool> autoTools = new Array<Tool>();
	
	public GameScreen game;
	
	private EditorCamera editorCamera;
	
	final public EditorAssetManager assets;
	final public Engine entityEngine;
	
	/**
	 * Set non null value to spawn a tool at editor startup.
	 */
	public Tool defaultTool;

	private Label status;

	private Array<Button> contextualButtons = new Array<Button>();

	private ObjectSet<EditorListener> listeners = new ObjectSet<EditorListener>();
	
	final private EditorConfiguration config;
	
	public EditorScreen(EditorConfiguration config, GameScreen screen, EditorAssetManager assets, Engine engine) {
		super(screen);
		this.config = config;
		this.game = screen;
		this.game.registry = config.registry;
		this.entityEngine = engine;
		this.assets = assets;
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
		skin = new Skin(Gdx.files.classpath("uiskin.json"));
		
		stage = new Stage(config.viewport);
		
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
		UndoTool undoTool = new UndoTool(this);
		createToolGroup().addProcessor(undoTool);
		
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
		
		// TODO do it else where
		registry.inject(entityEngine, this);
		
		editor.addTools(prependTools);
		
		// make the all tools list
		Array<Tool> allTools = new Array<Tool>();
		allTools.addAll(mainToolGroup.tools);
		allTools.addAll(globalTools);
		// special injection for undoTool (not registered like others)
		allTools.add(undoTool);
		
		// inject in all tools and set this !
		for(Tool tool : allTools){
			registry.inject(entityEngine, tool);
			tool.setEditor(this);
		}

		// inject in systems
		for(EntitySystem system : entityEngine.getSystems()){
			registry.inject(entityEngine, system);
		}
		
		
		
		
		
		global.addTab("Tools", new ScrollPane(buttons, skin));
		global.addTab("Components", new ScrollPane(outline, skin, "light"));
		for(java.util.Map.Entry<String, EngineEditor> entry : entityEngine.getSystem(EditorSystem.class).globalEditors.entrySet()){
			global.addTab(entry.getKey(), entry.getValue().createEditor(this.entityEngine, assets, skin));
		}
		
		// listener for component add/remove
		EntityListener selectionListener = new EntityListener() {
			
			@Override
			public void entityRemoved(Entity entity) {
				if(selection.contains(entity)){
					selection.invalidate();
				}
			}
			
			@Override
			public void entityAdded(Entity entity) {
				if(entity == selection.selected()) selection.invalidate();
			}
		};
		
		for(Class<? extends Component> type : registry.editablePlugins.keySet()){
			entityEngine.addEntityListener(Family.one(type).get(), selectionListener);
		}
		
		// listener for entity add/remove
		entityEngine.addEntityListener(new EntityListener() {
			
			@Override
			public void entityRemoved(Entity entity) {
				if(selection.contains(entity)){
					selection.remove(entity);
				}
			}
			
			@Override
			public void entityAdded(Entity entity) {
			}
		});
		
		
		// build GUI
		updateSelection();
		
		// prepend tools and stage: order is first editor stage then tools then others
		Kit.inputs.addProcessor(0, toolDelegator);
		Kit.inputs.addProcessor(0, stage);
		
		if(defaultTool != null){
			mainToolGroup.setActiveTool(defaultTool);
		}
	}
	
	private ToolGroupHandler toolGroupHandler = new ToolGroupHandler() {
		@Override
		public void onToolChanged(Tool tool) {
			toolOutline.clear();
			if(tool != null){
				Table table = new Table(skin);
				Table view = new EntityEditor(tool, true, skin);
				table.setBackground(skin.getDrawable("default-rect"));
				table.add(tool.name).row();
				table.add(view).row();
				toolOutline.add(table);
			}
		}
	};

	private Array<ToolGroup> prependTools = new Array<ToolGroup>();
	
	public ToolGroup createToolGroup() 
	{
		ToolGroup g = new ToolGroup(toolGroupHandler);
		toolDelegator.addProcessor(g);
		prependTools.add(g);
		return g;
	}
	private Array<Tool> globalTools = new Array<Tool>();
	
	public void addGlobalTool(Tool tool) 
	{
		ToolGroup g = createToolGroup();
		g.setActiveTool(tool);
		globalTools.add(tool);
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
		if(selection.validate())
		{
			updateSelection();
		}
		
		current.render(deltaTime);

		if(displayEnabled){
			stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			stage.act();
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
	
	// TODO will be part of system save !
	public final Array<EntitySystem> pinnedSystems = new Array<EntitySystem>();
	
	private void updateSelection() 
	{
		final Entity entity = selection.size() > 0 ? selection.last() : null;
		
		for(Button button : contextualButtons ){
			mainToolGroup.removeButton(button);
		}
		contextualButtons.clear();
		
		buttons.clear();
		outline.clear();
		outline.setBackground((Drawable)null);
		
		buttons.add(String.valueOf(selection.size()) + " entities").expandX().fill().row();
			
		
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
				selection.invalidate();
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
			boolean allowed = tool.allowed(selection.selection);
			accepted &= pluginFilter == null || pluginFilter.isEmpty() || pluginFilter.equals(registry.getTag(tool));
			accepted &= tool.activator == null || (entity != null && tool.activator.matches(entity));
			if(accepted && (allowed || showAllTools))
			{
				Button button = createToolButton(tool.name, mainToolGroup, tool, allowed);
				contextualButtons.add(button);
				buttons.add(button).fill().row();
			}
		}
		
		// Display all entity components (unique entity only)
		if(selection.size() == 1)
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
	
	private void pinEditor(Entity entity, Component component) 
	{
		pinStack.addActor(createPinEditor(entity, component));
		
	}
	public void pinEditor(EntitySystem system) 
	{
		if(!pinnedSystems.contains(system, true)){
			pinnedSystems.add(system);
			pinStack.addActor(createPinEditor(system));
		}
	}
	private void pinEditors(Array<EntitySystem> systems) {
		for(EntitySystem system : systems)
		{
			pinEditor(system);
		}
	}
	
	private void unpinEditor(Actor editor){
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
	
	
	private boolean displayEnabled = true; // true by default

	public Table toolOutline;


	public void addTool(Tool tool) {
		mainToolGroup.tools.add(tool);
		mainTools.add(tool);
	}
	public void addSuperTool(Tool tool) {
		mainToolGroup.tools.add(tool);
		superGlobal.add(createToolButton(tool, true));
	}
	
	public Button createToolButton(final Tool tool, boolean enabled) 
	{
		return createToolButton(tool.name, mainToolGroup, tool, enabled); // all groups
	}
	
	public static class ToolButton extends ImageButton implements Learnable
	{
		private Tool tool;
		public ToolButton(Tool tool, Skin skin, String styleName) {
			super(skin, styleName);
			this.tool = tool;
		}
		
		@Editable
		public void execute(){
			setChecked(!isChecked());
		}

		@Override
		public Accessor accessorToBind() {
			return new VoidAccessor(this, "execute");
		}
		
		@Override
		public String bindKey() {
			Storable storable = tool.getClass().getAnnotation(Storable.class);
			if(storable != null)
				return storable.value() + "#activate";
			// Warning : type mapping ...
			return tool.getClass().getName() + "#activate";
		}
		
	}
	
	private Button createToolButton(String name, final ToolGroup group, final Tool tool, boolean enabled) 
	{
		final Button btTool = new ToolButton(tool, skin, "toggle");
				// new TextButton("", skin, "toggle");
		btTool.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(btTool.isChecked()) group.setActiveTool(tool);
				else group.setActiveTool(null);
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

	public <T> T loadAssetNow(String fileName, Class<T> type) {
		return AssetHelper.loadAssetNow(assets, fileName, type, registry.getDefaultLoaderParameter(type));
	}
	public <T> T loadAssetNow(String fileName, Class<T> type, AssetLoaderParameters<T> parameters) {
		return AssetHelper.loadAssetNow(assets, fileName, type, parameters);
	}

	public Vector2 unproject(float screenX, float screenY) {
		return Tool.unproject(getGameCamera(), screenX, screenY);
	}

	/**
	 * @deprecated inject {@link SelectionSystem} instead
	 */
	@Deprecated
	public void addSelector(SelectorPlugin selector) {
		entityEngine.getSystem(SelectionSystem.class).selectors.add(selector);
	}
	
	public void reset() 
	{
		entityEngine.removeAllEntities();
		
		// clear all assets used by engine (not these loaded by editor).
		assets.clear();
		
		editorCamera.reset();
		
		selection.clear();
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

	public void addSystem(EntitySystem system) {
		addSystem(system, true);
	}
	public void addSystem(EntitySystem system, boolean processing) {
		entityEngine.addSystem(system);
		system.setProcessing(processing);
	}

}
