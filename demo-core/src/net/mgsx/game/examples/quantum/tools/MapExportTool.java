package net.mgsx.game.examples.quantum.tools;

import java.io.IOException;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.helpers.NativeService;
import net.mgsx.game.core.helpers.NativeService.DefaultCallback;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.examples.quantum.storage.MapSerializer;

public strictfp class MapExportTool extends Tool
{

	public MapExportTool(EditorScreen editor) {
		super("Quantum Map Export", editor);
	}
	
	@Override
	protected void activate() {
		super.activate();
		
		NativeService.instance.openSaveDialog(new DefaultCallback() {
			@Override
			public void selected(FileHandle selectedFile) {
				try {
					MapSerializer.writeMap(getEngine(), selectedFile);
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
