package net.mgsx.game.examples.platformer.systems;

import java.util.Comparator;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.examples.platformer.components.CavernSurrounding;
import net.mgsx.game.plugins.core.components.Transform2DComponent;
import net.mgsx.pd.Pd;
import net.mgsx.pd.midi.MidiMusicLoader;
import net.mgsx.pd.midi.PdMidiMusic;
import net.mgsx.pd.patch.PatchLoader;
import net.mgsx.pd.patch.PdPatch;

@EditableSystem
public class PlatformerMusicSystem extends EntitySystem
{
	private GameScreen game;
	private ImmutableArray<Entity> caverns;
	private Array<Entity> surroundings;
	private Vector2 position = new Vector2(), v1 = new Vector2(), v2 = new Vector2();
	private float lastDensity = 0;
	
	private PdMidiMusic song;
	
	public PlatformerMusicSystem(GameScreen game) {
		super();
		this.game = game;
	};
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		caverns = game.entityEngine.getEntitiesFor(Family.all(CavernSurrounding.class, Transform2DComponent.class).get());
		surroundings = new Array<Entity>();
	}
	
	@Override
	public void removedFromEngine(Engine engine) {
		caverns = null;
		surroundings = null;
		super.removedFromEngine(engine);
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		Vector3 cam = game.getCullingCamera().position;
		position.set(cam.x, cam.y);
		
		// check where we are
		surroundings.clear();
		for(Entity entity : caverns){
			Transform2DComponent transform = Transform2DComponent.components.get(entity);
			CavernSurrounding cavern = CavernSurrounding.components.get(entity);
			cavern.distance = position.dst(transform.position);
			surroundings.add(entity);
		}
		surroundings.sort(surroundingByDistance);
		
		
		float density;
		if(surroundings.size <= 0){
			// nothing : normal density
			density = 0;
		}
		else if(surroundings.size <= 1){
			// only one : entity density
			CavernSurrounding s = CavernSurrounding.components.get(surroundings.first());
			density = s.density;
		}
		else{
			// two or more
			Entity e1 = surroundings.first();
			Entity e2 = surroundings.get(1);
			CavernSurrounding s1 = CavernSurrounding.components.get(e1);
			CavernSurrounding s2 = CavernSurrounding.components.get(e2);
			
			Transform2DComponent t1 = Transform2DComponent.components.get(e1);
			Transform2DComponent t2 = Transform2DComponent.components.get(e2);
			
			// compute density based on orientation
			v1.set(t2.position).sub(t1.position);
			v2.set(position).sub(t1.position);
			float dot = v2.dot(v1) / (v1.len2());
			if(dot <= 0){
				// first is in between
				density = s1.density;
			}else if(dot >= 1){
				// second is in between
				density = s2.density;
			}else{
				// camera in between : lerp both density (fade transition)
				density = MathUtils.lerp(s1.density, s2.density, dot);
			}
		}
		if(lastDensity != density){
			lastDensity = density;
			Pd.audio.sendFloat("surrounding-cavern", density);
		}
	}

	private final static Comparator<Entity> surroundingByDistance = new Comparator<Entity>() {
		@Override
		public int compare(Entity o1, Entity o2) {
			CavernSurrounding s1 = CavernSurrounding.components.get(o1);
			CavernSurrounding s2 = CavernSurrounding.components.get(o2);
			return Float.compare(s1.distance, s2.distance);
		}
	};
	
	@Editable
	public void enableSound(){
		if(song != null) return;
		AssetManager assets = game.assets;
		
		assets.setLoader(Music.class, "mid", new MidiMusicLoader(assets.getFileHandleResolver()));
		assets.setLoader(PdPatch.class, "pd", new PatchLoader(assets.getFileHandleResolver()));
		
		AssetDescriptor<PdPatch> patchAsset = new AssetDescriptor<PdPatch>("pdmidi/midiplayer.pd", PdPatch.class);
		AssetDescriptor<Music> songAsset = new AssetDescriptor<Music>("MuteCity.mid", Music.class);
		
		assets.load(patchAsset);
		assets.load(songAsset);
		assets.finishLoading();
		
		song = (PdMidiMusic)assets.get(songAsset);
		
		song.play();
		
		//Pd.audio.sendFloat("volume", 1f);
		// Pd.audio.sendFloat("pan", 0f);
	}
	@Editable
	public void disableSound(){
		if(song != null){
			song.stop();
		}
		song = null;
	}
}
