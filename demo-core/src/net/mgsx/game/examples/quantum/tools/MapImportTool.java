package net.mgsx.game.examples.quantum.tools;

import java.io.IOException;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.helpers.NativeService;
import net.mgsx.game.core.helpers.NativeService.DefaultCallback;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.examples.quantum.storage.MapSerializer;

public strictfp class MapImportTool extends Tool
{

	public MapImportTool(EditorScreen editor) {
		super("Quantum Map Import", editor);
	}
	
	@Override
	protected void activate() {
		super.activate();
		
		NativeService.instance.openLoadDialog(new DefaultCallback() {
			@Override
			public void selected(FileHandle selectedFile) {
				try {
					MapSerializer.readMap(getEngine(), selectedFile);
				} catch (IOException e) {
					throw new GdxRuntimeException(e);
				}
			}
			@Override
			public boolean match(FileHandle file) {
				return file.extension().equals("map");
			}
			@Override
			public String description() {
				return "Map files (*.map)";
			}
			@Override
			public void cancel() {
				end();
			}
		});
		end();
	}

}
