package net.mgsx.game.core.ui.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.LocalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

public class FileBrowserWidget extends Table {

	private FileHandleResolver fileResolver;
	private FileHandle path;
	private TextField pathField;
	private Table fileList;
	
	public FileBrowserWidget(Skin skin) {
		this(Gdx.files.local(""), skin);
	}
	public FileBrowserWidget(FileHandle path, Skin skin) {
		super(skin);
		this.path = path;
		this.fileResolver = new LocalFileHandleResolver();
		
		add(pathField = new TextField(path.path(), skin)).expandX().fill().row();
		add(fileList = new Table(skin)).expand().fill().row();
		
		fileList.defaults().expandX().fill();
		
		pathField.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				FileHandle f = fileResolver.resolve(pathField.getText());
				if(f != null && f.exists() && f.isDirectory()){
					FileBrowserWidget.this.path = f;
					updateList();
				}
			}
		});
		
		updateList();
	}
	public void setPath(FileHandle path) {
		this.path = path;
		updatePath();
	}

	private void updatePath() {
		pathField.setProgrammaticChangeEvents(false);
		pathField.setText(path.path());
		pathField.setProgrammaticChangeEvents(true);
		updateList();
	}
	private void updateList() {
		fileList.clearChildren();
		if(!path.parent().path().equals(path.path())){
			fileList.add(createFileButton(path.parent(), "..")).row();
		}
		for(FileHandle file : path.list()){
			fileList.add(createFileButton(file)).row();
		}
	}
	private Actor createFileButton(FileHandle file) {
		return createFileButton(file, file.name());
	}
	private Actor createFileButton(final FileHandle file, String label) {
		String text = file.isDirectory() ? "d | " : "f | ";
		TextButton bt = new TextButton(text + label, getSkin());
		bt.getLabel().setAlignment(Align.left);
		bt.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(file.isDirectory()){
					setPath(file);
				}
			}
		});
		return bt;
	}

}
