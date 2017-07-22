package net.mgsx.game.examples.openworld.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Scaling;

import net.mgsx.game.core.helpers.RemoteImage;
import net.mgsx.game.examples.openworld.model.OpenWorldRepository;
import net.mgsx.game.services.gapi.Achievement;
import net.mgsx.game.services.gapi.GAPI;
import net.mgsx.game.services.gapi.Leaderboard;

public class ConnectionView extends Table
{
	private TextField accountField;

	public ConnectionView(Skin skin) {
		super(skin);
		setBackground("default-window");
		
		accountField = new TextField("", skin);
		
		buildForm();
	}
	
	private void buildForm(){
		TextButton btConntext = new TextButton("Connect", getSkin());
		
		Table main = this;
		
		
		main.add("Google Game Account");
		main.add(accountField);
		main.add(btConntext).colspan(2).fill();
		main.row();
		
		btConntext.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				connect();
			}
		});
	}

	private void connect() {
		final String user = accountField.getText().trim();
		
		clearChildren();
		add("Connecting, please wait...");
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				bgConnect(user);
			}
		}).start();
	}

	protected void bgConnect(String user) {
		try {
			GAPI.service.init("OpenWorld");
			GAPI.service.connect(user);
			
			OpenWorldRepository.achievements = GAPI.service.fetchAchievements();
			OpenWorldRepository.leaderboards = GAPI.service.fetchLeaderboards();
			
			
			
		} catch (GdxRuntimeException e) {
			Gdx.app.error("Remote", "Google Game Service Error", e);
			postError();
			
		}
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				displayOK();
			}
		});
	}

	private void postError() {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				displayError();
			}
		});
	}

	protected void displayOK() {
		clearChildren();
		
		for(Achievement a : OpenWorldRepository.achievements)
		{
			add(new RemoteImage(a.getIconUrl(), 64, 64, Scaling.fit)).size(64);
			if(a.isHidden()){
				add("---");
				add("---");
				add("---");
				add("---");
			}else{
				
				add(a.name);
				add(a.type);
				add(a.description);
				if(a.isIncremental())
					add(a.currentSteps + " " + a.totalSteps);
				else
					add();
			}
			add(a.state);
			row();
		}
		
		for(Leaderboard lb : OpenWorldRepository.leaderboards){
			add(new RemoteImage(lb.iconUrl, 64, 64, Scaling.fit)).size(64);
			add(lb.name);
			add(lb.order);
			row();
		}
		
		
	}
	
	protected void displayError() {
		clearChildren();
		TextButton bt = new TextButton("OK", getSkin());
		bt.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				buildForm();
			}
		});
		
		add("Cannot connect to Google Game Service, Please").row();
		add("Please check your internet connection and try again").row();
		add(bt).fill().row();
		
	}
	
}
