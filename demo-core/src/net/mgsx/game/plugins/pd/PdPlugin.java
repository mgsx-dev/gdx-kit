package net.mgsx.game.plugins.pd;

import com.badlogic.gdx.audio.Music;

import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.Plugin;
import net.mgsx.pd.midi.MidiMusicLoader;
import net.mgsx.pd.patch.PatchLoader;
import net.mgsx.pd.patch.PdPatch;

@PluginDef
public class PdPlugin implements Plugin
{

	@Override
	public void initialize(GameScreen engine) {
		engine.assets.setLoader(Music.class, "mid", new MidiMusicLoader(engine.assets.getFileHandleResolver()));
		engine.assets.setLoader(PdPatch.class, "pd", new PatchLoader(engine.assets.getFileHandleResolver()));

	}

}
