package com.project.harbinger;

import org.andengine.engine.Engine;
import org.andengine.engine.LimitedFPSEngine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.BaseGameActivity;

import android.view.KeyEvent;

import com.project.harbinger.manager.ResourcesManager;
import com.project.harbinger.manager.SceneManager;

/**Klasa reprezentująca activity. 
 * 
 * @author Łukasz Frącz
 *
 */
public class MainMenuActivity extends BaseGameActivity {

	/**Kamera. Decyduje o tym, co wyświetla się na ekranie. W zasadzie nieużywana w grze.*/
	private Camera camera;
	
	/** 
	 * @see org.andengine.ui.activity.BaseGameActivity#onCreateEngine(org.andengine.engine.options.EngineOptions)
	 * 
	 * Tworzy silnik obsługujący całą aplikację. Wywoływana automatycznie
	 * @param pEngineOptions ustawienia silnika
	 * 
	 * @return Obiekt silnika
	 */
	public Engine onCreateEngine(EngineOptions pEngineOptions) {
	    return new LimitedFPSEngine(pEngineOptions, 30);
	}
	
	/** 
	 * @see org.andengine.ui.IGameInterface#onCreateEngineOptions()
	 * 
	 * Tworzy ustawienia potrzebne do stworzenia silnika.
	 * 
	 * @return Ustawienia silnika
	 */
	@Override
	public EngineOptions onCreateEngineOptions() {
		camera = new Camera(0, 0, 480, 800);
		
		EngineOptions engineOptions = new EngineOptions(true, 
				ScreenOrientation.PORTRAIT_FIXED, 
				new RatioResolutionPolicy(480, 800), camera);
		engineOptions.getAudioOptions().setNeedsMusic(true).setNeedsSound(true);
		engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
		engineOptions.getTouchOptions().setNeedsMultiTouch(true);
		
		return engineOptions;
	}

	/**
	 * @see org.andengine.ui.IGameInterface#onCreateResources(org.andengine.ui.IGameInterface.OnCreateResourcesCallback)
	 * 
	 * Przygotowuje zasoby gry. Wywoływana atutomatycznie.
	 * 
	 * @param pOnCreateResourcesCallback
	 */
	@Override
	public void onCreateResources(
			OnCreateResourcesCallback pOnCreateResourcesCallback)
			throws Exception {
		ResourcesManager.prepareManager(mEngine, this, camera, getVertexBufferObjectManager());
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	/**
	 * @see org.andengine.ui.IGameInterface#onCreateScene(org.andengine.ui.IGameInterface.OnCreateSceneCallback)
	 * 
	 * Tworzy pierwszą scenę. Wywoływana automatycznie.
	 * 
	 * @param
	 */
	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)
			throws Exception {
		SceneManager.getInstance().createSplashScreen(pOnCreateSceneCallback);
	}

	/**
	 * @see org.andengine.ui.IGameInterface#onPopulateScene(org.andengine.entity.scene.Scene, org.andengine.ui.IGameInterface.OnPopulateSceneCallback)
	 * Tworzy menu główne
	 */
	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
		mEngine.registerUpdateHandler(new TimerHandler(2f, new ITimerCallback() 
	    {
	            public void onTimePassed(final TimerHandler pTimerHandler) 
	            {
	                mEngine.unregisterUpdateHandler(pTimerHandler);
	                SceneManager.getInstance().createMenuScene();
	            }
	    }));
	    pOnPopulateSceneCallback.onPopulateSceneFinished();		
	}
	
	/**
	 * @see org.andengine.ui.activity.BaseGameActivity#onPauseGame()
	 * Metoda wywoływana automatycznie przy zatrzymaniu aplikacji (np. gdy ktoś dzwoni na telefon).
	 */
	public void onPauseGame() {
		super.onPauseGame();
		
		SceneManager.getInstance().getCurrentScene().onHomeKeyPressed();
	}
	
	/**
	 * @see org.andengine.ui.activity.BaseGameActivity#onDestroy()
	 * 
	 * Metoda wywoływana przy zamykaniu aplikacji
	 */
	public void onDestroy() {
		super.onDestroy();
		System.exit(0);
	}
	
	/**
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 * 
	 * Metoda wywoływana przy naciśnięciu któregoś z przycisków.
	 * W tym przypadku jedynym obsługiwanym przyciskiem jest przycisk back.
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			SceneManager.getInstance().getCurrentScene().onBackKeyPressed();
		}
		
		return false;
	}
}
