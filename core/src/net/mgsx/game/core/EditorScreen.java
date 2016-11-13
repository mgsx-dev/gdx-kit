package net.mgsx.game.core;

import java.util.Map.Entry;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.assets.loaders.resolvers.ClasspathFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json.Serializer;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.commands.Command;
import net.mgsx.game.core.commands.CommandHistory;
import net.mgsx.game.core.components.Movable;
import net.mgsx.game.core.editors.AnnotationBasedComponentEditor;
import net.mgsx.game.core.helpers.AssetHelper;
import net.mgsx.game.core.helpers.AssetLookupCallback;
import net.mgsx.game.core.helpers.NativeService;
import net.mgsx.game.core.helpers.NativeService.DialogCallback;
import net.mgsx.game.core.helpers.ScreenDelegate;
import net.mgsx.game.core.plugins.EntityEditorPlugin;
import net.mgsx.game.core.plugins.GlobalEditorPlugin;
import net.mgsx.game.core.plugins.SelectorPlugin;
import net.mgsx.game.core.tools.ComponentTool;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.core.tools.ToolGroup;
import net.mgsx.game.core.ui.TabPane;
import net.mgsx.game.plugins.camera.components.CameraComponent;
import net.mgsx.game.plugins.camera.components.RenderingComponent;
import net.mgsx.game.plugins.camera.systems.CameraSystem;
import net.mgsx.game.plugins.core.tools.UndoTool;

/**
 * Base screen for game editor.
 * Work on Game screen (the screen to edit)
 * 
 * @author mgsx
 *
 */
public class EditorScreen extends ScreenDelegate implements EditorContext
{
	public CommandHistory history;
	protected Skin skin;
	protected Stage stage;
	protected Table panel;
	protected Table buttons;
	protected Table outline;
	protected TabPane global;
	private Table superGlobal;
	
	private SpriteBatch editorBatch;
	
	private EditorRegistry registry;
	
	private AssetManager editorAssets;
	
	private OrthographicCamera orthographicCamera;
	private PerspectiveCamera perspectiveCamera;
	
	public Array<SelectorPlugin> selectors = new Array<SelectorPlugin>();
	
	final private Array<ToolGroup> tools = new Array<ToolGroup>();
	
	private InputMultiplexer toolDelegator;
	
	private ToolGroup mainToolGroup;
	
	/** tools displayed as button when selection change (contextual tools) */
	private Array<Tool> mainTools = new Array<Tool>();
	
	
	private Array<Tool> autoTools = new Array<Tool>();
	public ShapeRenderer shapeRenderer;
	private GameScreen game;
	
	private boolean orthoMode;
	
	final public AssetManager assets;
	final public Engine entityEngine;
	
	public final ObjectMap<Class, Serializer> serializers;

	private Entity gameCamera, editorCamera;
	
	public EditorScreen(EditorConfiguration config, GameScreen screen) {
		super();
		this.game = screen;
		this.game.registry = config.registry;
		this.entityEngine = game.entityEngine;
		this.assets = game.assets;
		this.serializers = config.registry.serializers;
		this.registry = config.registry;
		this.current = this.game;
		
		init();
		
		createCamera();
	}
	
	private void createCamera()
	{
		gameCamera = entityEngine.getSystem(CameraSystem.class).getRenderCamera();
		
		editorCamera = entityEngine.createEntity();
		
		CameraComponent camera = entityEngine.createComponent(CameraComponent.class);
		RenderingComponent render = entityEngine.createComponent(RenderingComponent.class);
		
		PerspectiveCamera pc = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		pc.position.set(0, 0, 10);
		pc.up.set(0,1,0);
		pc.lookAt(0,0,0);
		pc.near = 1f;
		pc.far = 3000f;
		pc.update();
		camera.camera = pc;
		
		perspectiveCamera = pc;
		
		orthographicCamera = new OrthographicCamera();
		
		editorCamera.add(camera);
		editorCamera.add(render);
		
		entityEngine.addEntity(editorCamera);
	}
	
	public void zoom(float rate) {
		
		rate *= Tool.pixelSize(perspectiveCamera).x * Gdx.graphics.getWidth(); // 100 world unit per pixel TODO pixelSize !!
		
		if(orthoMode){
			perspectiveCamera.position.set(orthographicCamera.position);
			perspectiveCamera.translate(0, 0, -rate);
			perspectiveCamera.update(false);
			syncOrtho(true);
			
		}else{
			perspectiveCamera.translate(0, 0, -rate);
		}
	}
	
	public void fov(float rate) 
	{
		rate *= 360; // degree scale
		
		syncPerspective(false);
		
		// translate camera according to FOV changes (keep sprite plan unchanged !)
		
		float oldFOV = perspectiveCamera.fieldOfView * MathUtils.degreesToRadians * 0.5f;
		
		float hWorld = (float)Math.tan(oldFOV) * getRenderCamera().position.z;
		
		perspectiveCamera.fieldOfView += rate;
		perspectiveCamera.update(true);
		
		float newFOV = perspectiveCamera.fieldOfView * MathUtils.degreesToRadians * 0.5f;
		
		float distWorld = hWorld / (float)Math.tan(newFOV);
		
		float deltaZ = distWorld - getRenderCamera().position.z;
		perspectiveCamera.translate(0, 0, deltaZ);
		perspectiveCamera.update(false);
		
		
		syncOrtho(false);
	}
	
	private void syncPerspective(boolean force)
	{
		if(orthoMode || force){
			perspectiveCamera.position.set(orthographicCamera.position);
			perspectiveCamera.update(true);
		}
	}
	private void syncOrtho(boolean force)
	{
		if(!orthoMode || force)
		{
			// sync sprite plan for ortho (working !)
			Vector3 objectDepth = perspectiveCamera.project(new Vector3());
			
			Vector3 a = perspectiveCamera.unproject(new Vector3(0, 0, objectDepth.z));
			Vector3 b = perspectiveCamera.unproject(new Vector3(1, 1, objectDepth.z));
			b.sub(a);
			
			float w = Math.abs(b.x) * Gdx.graphics.getWidth();
			float h = Math.abs(b.y) * Gdx.graphics.getHeight();
			
			orthographicCamera.setToOrtho(false, w, h);
			orthographicCamera.position.set(perspectiveCamera.position);
			
			orthographicCamera.update(true);
		}
	}

	public void switchCamera()
	{
		CameraComponent camera = CameraComponent.components.get(editorCamera);
		if(!orthoMode)
		{
			syncOrtho(true);
			
			
			camera.camera = orthographicCamera;
			orthoMode = true;
		}
		else
		{
			syncPerspective(true);
			
			camera.camera = perspectiveCamera;
			orthoMode = false;
		}
	}
	
	public void switchCameras()
	{
		Entity currentCamera = entityEngine.getSystem(CameraSystem.class).getRenderCamera();
		currentCamera.remove(RenderingComponent.class);
		
		if(currentCamera == editorCamera)
		{
			currentCamera = gameCamera;
		}else{
			currentCamera = editorCamera;
		}
		currentCamera.add(entityEngine.createComponent(RenderingComponent.class));
	}
	
	public Movable getMovable(Entity entity)
	{
		// temporarily use Movable component ... for now
		return Movable.components.get(entity);
	}
	
	private void init()
	{
		editorBatch = new SpriteBatch();
		
		shapeRenderer = new ShapeRenderer();
		
		editorAssets = new AssetManager(new ClasspathFileHandleResolver());
		
		skin = AssetHelper.loadAssetNow(editorAssets, "data/uiskin.json", Skin.class);
		
		stage = new Stage(new ScreenViewport());
		history = new CommandHistory();
		
		toolDelegator = new InputMultiplexer();
		
		Gdx.input.setInputProcessor(new InputMultiplexer(stage, toolDelegator));

		panel = new Table(skin);
		
		
		// TODO add menu
		global = new TabPane(skin);
		buttons = new Table(skin); buttons.setBackground(skin.getDrawable("default-rect"));
		outline = new Table(skin); 
		superGlobal = new Table(skin);
		
		
		Table grp = new Table();
		grp.add(buttons).fill().row();
		grp.add(outline).fill().row();
		
		ScrollPane scroll = new ScrollPane(grp, skin, "light");
		
		Table globalBlock = new Table(skin);
		globalBlock.setBackground(skin.getDrawable("default-window-body"));
		
		globalBlock.add(superGlobal).row();
		globalBlock.add(global).row();
		
		panel.add(globalBlock).row();
		panel.add(scroll).left().row();
		
		Table main = new Table();
		main.add(panel).expand().left().top();
		
		main.setFillParent(true);
		stage.addActor(main);
		
		createToolGroup().addProcessor(new UndoTool(this));
		
		mainToolGroup = createToolGroup();

		registry.init(this);

		for(final Class<? extends Component> type : registry.components){
			EditableComponent config = type.getAnnotation(EditableComponent.class);
			if(config != null){
				registerPlugin(type, new AnnotationBasedComponentEditor(type));
				Family family = null;
				if(config.all().length > 0 || config.one().length > 0 || config.exclude().length > 0){
					family = Family.all(config.all()).one(config.one()).exclude(config.exclude()).get();
				}
				String name = config.name().isEmpty() ? type.getSimpleName() : config.name();
				autoTools.add(new ComponentTool(name, this, family){
					@Override
					protected Component createComponent(Entity entity) {
						return entityEngine.createComponent(type);
					}
				});
				
			}
		}
		
		for(Tool tool : autoTools){
			addTool(tool);
		}
		

		global.addTab("Off", new Table(skin));
		for(Entry<String, GlobalEditorPlugin> entry : registry.globalEditors.entrySet()){
			global.addTab(entry.getKey(), entry.getValue().createEditor(this, skin));
		}
		
		EntityListener selectionListener = new EntityListener() {
			
			@Override
			public void entityRemoved(Entity entity) {
				if(entity == getSelected()){
					if(!entityEngine.getEntities().contains(entity, true)){
						selection.removeValue(entity, true);
					}
					invalidateSelection(); // TODO or contains ?
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
		entityEngine.addEntityListener(selectionListener); // TODO maybe not the same listener
		
		
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
	
	@Override
	public void render(float deltaTime) 
	{
		getRenderCamera().update(true);
		if(selectionDirty){
			selectionDirty = false;
			updateSelection();
		}
		
		shapeRenderer.setProjectionMatrix(getRenderCamera().combined);
		
		// TODO some code should be placed in engine ...
//		batch.setProjectionMatrix(camera.combined);
//		batch.enableBlending();
//		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		
		current.render(deltaTime);

		stage.act();
		
		
		// TODO use systems instead (used by sprite tools ...)
		
		editorBatch.setProjectionMatrix(getRenderCamera().combined);
		editorBatch.begin();
		for(ToolGroup g : tools){
			g.render(editorBatch);
		}
		editorBatch.end();
		
		for(ToolGroup g : tools){
			g.render(shapeRenderer);
		}
		
		if(displayEnabled){

			
			stage.draw();
		}
		
		
	}
	
	public Camera getCullingCamera(){
		return game.getCullingCamera();
	}
	
	
	@Override
	public void resize(int width, int height) 
	{
		super.resize(width, height);
		
		syncPerspective(false);
		
		perspectiveCamera.viewportWidth = Gdx.graphics.getWidth();
		perspectiveCamera.viewportHeight = Gdx.graphics.getHeight();
		perspectiveCamera.update(true);

		syncOrtho(false);
		
		stage.getViewport().update(width, height, true);
	}
	
	
	@Override
	public void dispose () {
	}

	private void updateSelection() 
	{
		final Entity entity = selection.size == 1 ? selection.first() : null;
		
		buttons.clear();
		outline.clear();
		outline.setBackground((Drawable)null);
		
		if(selection.size > 1)
		{
			buttons.add(String.valueOf(selection.size) + " entities").expandX().fill().row();
		}
		else
		{
			// Display all tools
			buttons.add("Tools").expandX().center().row();
			for(Tool tool : mainTools)
			{
				
				if(tool.activator == null || (entity != null && tool.activator.matches(entity)))
				{
					boolean handled = false;
					
					if(tool instanceof ComponentTool && entity != null){
						ComponentTool componentTool = ((ComponentTool) tool);
						Class<? extends Component> componentType = componentTool.getAssignableFor();
						if(componentType != null){
							Component component = entity.getComponent(componentType);
							if(component == null){
								buttons.add(createOutline(entity, component)).expandX().fill().row();
								handled = true;
							}
						}
					}
					if(!handled){
						buttons.add(createToolButton(tool.name, mainToolGroup, tool)).fill().row();
					}
				}
			}
			
			// Display all entity components
			if(entity != null)
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
	public boolean selectionDirty;
	public boolean displayEnabled = true; // true by default

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
		mainTools.add(tool);
	}
	public void addSuperTool(Tool tool) {
		mainToolGroup.tools.add(tool);
		superGlobal.add(createToolButton(tool));
	}
	
	public void addSubTool(Tool tool) {
		mainToolGroup.tools.add(tool);
	}
	
	
	public TextButton createToolButton(final Tool tool) 
	{
		return createToolButton(tool.name, mainToolGroup, tool); // all groups
	}
	protected TextButton createToolButton(String name, final ToolGroup group, final Tool tool) 
	{
		final TextButton btTool = new TextButton(name, skin, "toggle");
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
		return AssetHelper.loadAssetNow(assets, fileName, type);
	}
	public <T> T loadAssetNow(String fileName, Class<T> type, AssetLoaderParameters<T> parameters) {
		return AssetHelper.loadAssetNow(assets, fileName, type, parameters);
	}

	public Vector2 unproject(float screenX, float screenY) {
		return Tool.unproject(getRenderCamera(), screenX, screenY);
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
				tex.setWrap(TextureWrap.ClampToEdge, TextureWrap.ClampToEdge); // XXX maybe not !
				callback.selected(tex);
			}
			@Override
			public void cancel() {
			}
		});
	}

	public Entity createEntity() {
		return entityEngine.createEntity();
	}

	public Entity createAndAddEntity() {
		Entity e = createEntity();
		entityEngine.addEntity(e);
		return e;
	}

	public void performCommand(Command command) 
	{
		history.add(command);
	}

	public void addGlobalEditor(String name, GlobalEditorPlugin plugin) {
		registry.addGlobalEditor(name, plugin);
	}

	public void registerPlugin(Class<? extends Component> type, EntityEditorPlugin plugin) {
		registry.registerPlugin(type, plugin);
	}

	public Camera getRenderCamera() {
		return game.getRenderCamera();
	}

}
