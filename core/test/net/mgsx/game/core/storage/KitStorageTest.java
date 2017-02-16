package net.mgsx.game.core.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;

import org.junit.Before;
import org.junit.Test;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import net.mgsx.game.core.GameRegistry;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.components.Repository;
import net.mgsx.game.core.meta.ClassRegistry;
import net.mgsx.game.core.meta.CollectionClassRegistry;

public class KitStorageTest {

	public static class TestFileHandle extends FileHandle
	{
		final private byte[] content;
		public TestFileHandle(byte[] content){
			this.content = content;
		}
		@Override
		public InputStream read() {
			return new ByteArrayInputStream(content);
		}
		// TODO same for write : write in memory and assert on data ... ?
	}
	
	public static class TestFileHandleResolver implements FileHandleResolver {
		private ObjectMap<String, byte[]> data = new ObjectMap<String, byte[]>();
		
		public TestFileHandleResolver() {
		}
		
		public void register(String fileName, String content)
		{
			register(fileName, content.getBytes());
		}
		public void register(String fileName, byte[] content)
		{
			data.put(fileName, content);
		}

		@Override
		public FileHandle resolve(String fileName) 
		{
			byte[] content = data.get(fileName);
			if(content != null){
				return new TestFileHandle(content);
			}
			return null;
		}
	}

	@Storable("test.1")
	public static class SystemTest1 extends EntitySystem{
		@Editable
		public int a = 5;
	}
	
	public static class TestAsset{
	}
	
	@Storable("test.cmp.1")
	public static class ComponentTest1 implements Component{
		@Editable
		public int v = 0;
	}
	
	@Storable("test.cmp.2")
	public static class ComponentTest2 implements Component{
		@Editable
		public TestAsset asset;
	}
	
	public static class TestAssetManager extends AssetManager
	{
		
		public TestAssetManager(FileHandleResolver resolver) {
			super(resolver);
		}
		private ObjectMap<String, Object> objects = new ObjectMap<String, Object>();
		public void load(String ref, Object object){
			objects.put(ref, object);
		}
		@Override
		public synchronized <T> String getAssetFileName(T asset) {
			return objects.findKey(asset, true);
		}
		@Override
		public synchronized Class getAssetType(String fileName) {
			return objects.get(fileName).getClass();
		}
		@Override
		public synchronized <T> T get(String fileName, Class<T> type) {
			return (T)objects.get(fileName);
		}
	}
	
	SaveConfiguration saveConfig;
	LoadConfiguration loadConfig;
	TestAssetManager assets;
	CollectionClassRegistry classRegistry;
	StringWriter writer;
	TestFileHandleResolver resolver;
	
	@Before
	public void setup()
	{
		resolver = new TestFileHandleResolver();
		
		assets = new TestAssetManager(resolver);
		
		// setup default save configuration
		saveConfig = new SaveConfiguration();
		saveConfig.assets = assets;
		saveConfig.engine = new Engine();
		saveConfig.pretty = false; // condense form for test assertion
		saveConfig.registry = new GameRegistry();
		saveConfig.saveSystems = true;
		saveConfig.saveViews = true;
		saveConfig.stripPaths = false; // XXX strip path require files exists (not available in tests)... maybe configure a resolver !
		
		ClassRegistry.instance = classRegistry = new CollectionClassRegistry();
		
		writer = new StringWriter();
		
		loadConfig = new LoadConfiguration();
		loadConfig.assets = assets;
		loadConfig.engine = new Engine();
		loadConfig.failSafe = false;
		loadConfig.registry = new GameRegistry();
	}
	
	@Test
	public void testSaveNominal()
	{
		// register classes
		classRegistry.add(ComponentTest1.class, ComponentTest2.class);
		
		// add serializer for our assets
		saveConfig.registry.addSerializer(TestAsset.class, new AssetSerializer<TestAsset>(TestAsset.class));
		
		// initialize registry
		saveConfig.registry.scanPackages();
		
		// add some systems to engine
		SystemTest1 system1 = new SystemTest1();
		saveConfig.engine.addSystem(system1);
		
		// add some views to save
		saveConfig.visibleSystems.add(system1);
		
		// add entities
		Entity e;
		e = saveConfig.engine.createEntity();
		saveConfig.engine.addEntity(e);
		
		// with classic component
		ComponentTest1 c1 = saveConfig.engine.createComponent(ComponentTest1.class);
		c1.v = 8;
		e.add(c1);
		
		// with asset component
		ComponentTest2 c2 = saveConfig.engine.createComponent(ComponentTest2.class);
		c2.asset = new TestAsset();
		assets.load("folder/file.test", c2.asset);
		e.add(c2);
		
		// with repository component
		Repository r = saveConfig.engine.createComponent(Repository.class);
		e.add(r);
		
		
		EntityGroupStorage.save(writer, saveConfig);
		
		assertEquals("{entities:[{id:0,test.cmp.1:{v:8},test.cmp.2:{asset:folder/file.test}}],systems:[{type:test.1,enabled:true,a:5}],views:[test.1],assets:[{type:net.mgsx.game.core.storage.KitStorageTest$TestAsset,name:folder/file.test}]}", writer.toString());
	}
	
	@Test
	public void testLoadNominal()
	{
		final String json = "{entities:[{id:0,test.cmp.1:{v:8},test.cmp.2:{asset:folder/file.test}}],systems:[{type:test.1,enabled:true,a:5}],views:[test.1],assets:[{type:net.mgsx.game.core.storage.KitStorageTest$TestAsset,name:folder/file.test}]}";
		
		// register classes
		classRegistry.add(ComponentTest1.class, ComponentTest2.class);
		
		// add serializer for our assets
		loadConfig.registry.addSerializer(TestAsset.class, new AssetSerializer<TestAsset>(TestAsset.class));
		
		loadConfig.registry.scanPackages();
		
		// test load
		final String filename = "test.json";
		resolver.register(filename, json);
		
		assets.setLoader(EntityGroup.class, new EntityGroupLoader(resolver));
		
		// add system
		SystemTest1 system1 = new SystemTest1();
		system1.a = 88;
		loadConfig.engine.addSystem(system1);
		
		
		
		EntityGroupLoader egl = new EntityGroupLoader(resolver);
		EntityGroupLoaderParameters parameter = new EntityGroupLoaderParameters();
		parameter.config = loadConfig;
		Array<AssetDescriptor> deps = egl.getDependencies(filename, resolver.resolve(filename), parameter);
		
		// check dependencies
		assertEquals(1, deps.size);
		AssetDescriptor dep = deps.first();
		assertEquals("folder/file.test", dep.fileName);
		assertSame(TestAsset.class, dep.type);
		
		// bypass asset manager by loading manually dependant asset
		final TestAsset asset = new TestAsset();
		assets.load("folder/file.test", asset);
		
		egl.loadAsync(assets, filename, resolver.resolve(filename), parameter);
		EntityGroup eg = egl.loadSync(assets, filename, resolver.resolve(filename), parameter);
		
		assertEquals(1, eg.entities.size);
		Entity e = eg.entities.first();
		
		assertEquals(2, e.getComponents().size());
		ComponentTest1 c1 = e.getComponent(ComponentTest1.class);
		ComponentTest2 c2 = e.getComponent(ComponentTest2.class);
		
		assertNotNull(c1);
		assertNotNull(c2);
		
		assertEquals(8, c1.v);
		assertSame(asset, c2.asset);
		
		// check systems
		assertEquals(5, system1.a);
		
		// check visible systems
		assertEquals(1, loadConfig.visibleSystems.size);
		assertSame(system1, loadConfig.visibleSystems.first());
		
		
	}

	
	@Test
	public void testSaveNothing()
	{
		saveConfig.saveSystems = false;
		saveConfig.saveViews = false;
		EntityGroupStorage.save(writer, saveConfig);
		
		assertEquals("{}", writer.toString());
	}
	
	@Test
	public void testSaveSystemSettings()
	{
		SystemTest1 system1 = new SystemTest1();
		saveConfig.engine.addSystem(system1);
		saveConfig.saveSystems = true;
		saveConfig.saveViews = false;
		EntityGroupStorage.save(writer, saveConfig);
		
		assertEquals("{systems:[{type:test.1,enabled:true,a:5}]}", writer.toString());
	}
}
