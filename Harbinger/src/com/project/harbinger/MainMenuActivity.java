package com.project.harbinger;

import org.andengine.engine.Engine;
import org.andengine.engine.LimitedFPSEngine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.BaseGameActivity;

import com.project.harbinger.manager.ResourcesManager;

/**
 * @author Łukasz Frącz
 *
 */

public class MainMenuActivity extends BaseGameActivity {

	private Camera camera;
	private ResourcesManager resourcesManager;
	
	public Engine onCreateEngine(EngineOptions pEngineOptions) {
	    return new LimitedFPSEngine(pEngineOptions, 60);
	}
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		camera = new Camera(0, 0, 480, 800);
		
		EngineOptions engineOptions = new EngineOptions(true, 
				ScreenOrientation.PORTRAIT_FIXED, 
				new RatioResolutionPolicy(480, 800), camera);
		engineOptions.getAudioOptions().setNeedsMusic(true).setNeedsSound(true);
		engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
		
		return engineOptions;
	}

	@Override
	public void onCreateResources(
			OnCreateResourcesCallback pOnCreateResourcesCallback)
			throws Exception {
		ResourcesManager.prepareManager(mEngine, this, camera, getVertexBufferObjectManager());
		resourcesManager = ResourcesManager.getInstance();
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
