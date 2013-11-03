package com.project.harbinger;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

/**
 * @author lukaszSA
 *
 */

public class MainMenuActivity extends SimpleBaseGameActivity {
	
	private Camera camera;
	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 800;

	@Override
	public EngineOptions onCreateEngineOptions() {
		camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
	    EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, 
	    new FillResolutionPolicy(), camera);
	    return engineOptions;
	}

	@Override
	protected void onCreateResources() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected Scene onCreateScene() {
		 Scene scene = new Scene();
	     scene.setBackground(new Background(0.09804f, 0.6274f, 0.8784f));
	     return scene;
	}


}
