package net.mgsx.game.plugins.core.tools;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.mgsx.game.core.helpers.NativeService;
import net.mgsx.game.core.helpers.NativeService.DefaultCallback;
import net.mgsx.game.core.storage.EntityGroupRef;
import net.mgsx.game.core.storage.EntityGroupStorage;
import net.mgsx.game.core.storage.LoadConfiguration;
import net.mgsx.game.core.ui.FieldEditor;
import net.mgsx.game.core.ui.accessors.Accessor;

public class EntityGroupEditor implements FieldEditor
{

	private LoadConfiguration config;
	
	public EntityGroupEditor(LoadConfiguration config) {
		super();
		this.config = config;
	}

	@Override
	public Actor create(final Accessor accessor, Skin skin) 
	{
		EntityGroupRef ref = (EntityGroupRef)accessor.get();
		TextButton btOpen = new TextButton(ref == null ? "..." : ref.ref, skin);
		btOpen.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				NativeService.instance.openSaveDialog(new DefaultCallback() {
					@Override
					public void selected(FileHandle file) {
						EntityGroupRef ref = new EntityGroupRef();
						ref.ref = file.path();
						ref.group = EntityGroupStorage.loadNow(file.path(), config);
						accessor.set(ref);
					}
					@Override
					public boolean match(FileHandle file) {
						return file.extension().equals("json");
					}
					@Override
					public String description() {
						return "Patch files (json)";
					}
				});
			}
		});
			
		return btOpen;
	}

}
