package net.mgsx.game.plugins.spline.tools;

import com.badlogic.gdx.files.FileHandle;

import net.mgsx.SplineTest.AbstractBlenderCurve;
import net.mgsx.SplineTest.BlenderCurve;
import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.helpers.NativeService;
import net.mgsx.game.core.helpers.NativeService.DefaultCallback;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.plugins.spline.components.PathComponent;

// TODO really work ?
public class ImportSplineTool extends Tool {
	public ImportSplineTool(EditorScreen editor) {
		super("Import Spline", editor);
	}

	@Override
	protected void activate() {
		NativeService.instance.openLoadDialog(new DefaultCallback() {
			@Override
			public void selected(FileHandle file) 
			{
				BlenderCurve curve = editor.loadAssetNow(file.path(), BlenderCurve.class);
				for(AbstractBlenderCurve c : curve.splines){
					PathComponent path = new PathComponent();
					path.path = c.toPath();
					editor.entityEngine.addEntity(editor.entityEngine.createEntity().add(path));
				}
				end();
			}
			@Override
			public boolean match(FileHandle file) {
				return file.extension().equals("json");
			}
			@Override
			public String description() {
				return "Spline files (json)";
			}
		});
	}
}