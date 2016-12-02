package net.mgsx.game.examples.platformer.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;

import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.pd.midi.MidiMusicLoader;
import net.mgsx.pd.midi.PdMidiMusic;
import net.mgsx.pd.patch.PatchLoader;
import net.mgsx.pd.patch.PdPatch;

@EditableSystem
public class PlatformerMusicSystem extends EntitySystem
{
	private GameScreen game;
	
	private PdMidiMusic song;
	
	public PlatformerMusicSystem(GameScreen game) {
		super();
		this.game = game;
	};
	
	@Editable
	public void enableSound(){
		if(song != null) return;
		AssetManager assets = game.assets;
		
		assets.setLoader(Music.class, "mid", new MidiMusicLoader(assets.getFileHandleResolver()));
		assets.setLoader(PdPatch.class, "pd", new PatchLoader(assets.getFileHandleResolver()));
		
		AssetDescriptor<PdPatch> patchAsset = new AssetDescriptor<PdPatch>("../demo-platformer/assets/pdmidi/midiplayer.pd", PdPatch.class);
		AssetDescriptor<Music> songAsset = new AssetDescriptor<Music>("../demo-platformer/assets/MuteCity.mid", Music.class);
		
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
