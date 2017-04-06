package net.mgsx.game.plugins.g3d.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SharedLibraryLoader;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.helpers.NativeService;
import net.mgsx.game.core.helpers.NativeService.DefaultCallback;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.plugins.g3d.components.G3DModel;

@Editable
public class ImportFbxTool extends Tool
{
	private final static boolean installed = checkInstall();

	private static boolean checkInstall() {
		if(SharedLibraryLoader.isLinux){
			ProcessBuilder pb = new ProcessBuilder("which", "fbx-conv");
			pb.redirectError();
			pb.inheritIO();
			try {
				Process process = pb.start();
				return process.waitFor() == 0;
			} catch (InterruptedException e) {
				// silently fail
			} catch (IOException e) {
				Gdx.app.error("KIT", "???", e);
			}
		}
		return false;
	}
	
	private FileHandle fbxFile;
	
	@Editable
	public boolean flipV = true;
	@Editable
	public boolean packColor = true;
	@Editable
	public boolean verbose = true;
	@Editable
	public int maxSize = 32767;
	@Editable
	public int maxBones = 12;
	@Editable
	public int maxWeights = 4;
	
	@Editable(value="Convert to...")
	public void convertAndImport(){
		NativeService.instance.openSaveDialog(new DefaultCallback() {
			@Override
			public void selected(FileHandle g3dFile) 
			{
				try {
					convertTo(g3dFile);
				} catch (IOException e) {
					Gdx.app.error("KIT", "failure see console", e);
					desactivate();
					return;
				}
				
				Model model = editor.loadAssetNow(g3dFile.path(), Model.class);
				ModelInstance modelInstance = new ModelInstance(model);
				Entity entity = currentEntity();
				G3DModel data = new G3DModel();
				data.modelInstance = modelInstance;

				// TODO if animations not empty ?
				data.animationController = new AnimationController(modelInstance);
				
				
				entity.add(data);
				
				desactivate();
			}
			@Override
			public boolean match(FileHandle file) {
				return file.extension().equals("g3dj") || file.extension().equals("g3db");
			}
			@Override
			public String description() {
				return "Model files (g3dj, g3db)";
			}
		});
	}
	
	public ImportFbxTool(EditorScreen editor) {
		super("Import FBX", editor);
	}
	
	@Override
	public boolean allowed(Array<Entity> selection) {
		return installed;
	}
	
	@Override
	protected void activate() {
		super.activate();
		chooseFile();
	}
	
	@Editable(value="Install fbx-conv")
	public void downloadFbxConv() {
		Gdx.net.openURI("https://github.com/libgdx/fbx-conv");
	}
	
	private void chooseFile(){
		NativeService.instance.openLoadDialog(new DefaultCallback() {
			@Override
			public void selected(FileHandle file) {
				fbxFile = file;
			}
			@Override
			public boolean match(FileHandle file) {
				return file.extension().equals("fbx");
			}
			@Override
			public String description() {
				return "FBX files (fbx)";
			}
		});
	}
	
	private void convertTo(FileHandle g3dFile) throws IOException{
		boolean binaryFormat = g3dFile.extension().equals("g3db");
		List<String> args = new ArrayList<String>();
		args.add("fbx-conv");
		args.add("-o");
		args.add(binaryFormat ? "G3DB" : "G3DJ");
		if(flipV)
			args.add("-f");
		if(packColor)
			args.add("-p");
		if(verbose)
			args.add("-v");
		args.add("-m");
		args.add(String.valueOf(maxSize));
		args.add("-b");
		args.add(String.valueOf(maxBones));
		args.add("-w");
		args.add(String.valueOf(maxWeights));
		args.add(fbxFile.file().getAbsolutePath());
		args.add(g3dFile.file().getAbsolutePath());
		
		ProcessBuilder pb = new ProcessBuilder(args);
		pb.directory(fbxFile.file().getParentFile());
		pb.redirectError();
		pb.inheritIO();
		Process process = pb.start();
		try {
			process.waitFor();
		} catch (InterruptedException e) {
			// silently fail
		}
	}

}
