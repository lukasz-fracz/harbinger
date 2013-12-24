package com.project.harbinger.manager;

import org.andengine.engine.Engine;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.ui.IGameInterface.OnCreateSceneCallback;

import com.project.harbinger.multiplayer.BluetoothConnection;
import com.project.harbinger.multiplayer.BluetoothServer;
import com.project.harbinger.scene.BaseScene;
import com.project.harbinger.scene.GameCompletedScene;
import com.project.harbinger.scene.GameScene;
import com.project.harbinger.scene.HighScoresScene;
import com.project.harbinger.scene.LoadingScene;
import com.project.harbinger.scene.MainMenuScene;
import com.project.harbinger.scene.MultiplayerOptionsScene;
import com.project.harbinger.scene.MultiplayerGameCompletedScene;
import com.project.harbinger.scene.MultiplayerGameScene;
import com.project.harbinger.scene.SinglePlayerOptionsScene;
import com.project.harbinger.scene.SplashScene;

public class SceneManager {
	
	private static final SceneManager INSTANCE = new SceneManager();
	
	public static SceneManager getInstance() {
		return INSTANCE;
	}
	
	// ---------------
	
	private BaseScene splashScene;
	private BaseScene menuScene;
	private BaseScene gameScene;
	private BaseScene loadingScene;
	private BaseScene gameCompletedScene;
	private BaseScene multiPlayerOptionsScene;
	private BaseScene singlePlayerOptionsScene;
	private BaseScene highScoresScene;
	private BaseScene multiplayerClientGameScene;
	private BaseScene multiplayerServerGameScene;
	private BaseScene multiplayerGameScene;
	private BaseScene multiplayerGameCompletedScene;
	
	public enum SceneType {
		SCENE_SPLASH,
		SCENE_MENU,
		SCENE_GAME,
		SCENE_LOADING,
		SCENE_GAME_COMPLETED,
		SCENE_MULTIPLAYER_OPTIONS,
		SCENE_SINGLEPLAYER_OPTIONS,
		SCENE_HIGH_SCORES,
		SCENE_MULTIPLAYER_CILENT_GAME,
		SCENE_MULTIPLAYER_SERVER_GAME,
		SCENE_MULTIPLAYER_GAME_COMPLETED
	}
	
	private SceneType currentSceneType = SceneType.SCENE_SPLASH;
	private BaseScene currentScene;
	private Engine engine = ResourcesManager.getInstance().getEngine();
	
	public void setScene(BaseScene scene) {
		engine.setScene(scene);
		currentScene = scene;
		currentSceneType = scene.getSceneType();
	}
	
	public void setScene(SceneType sceneType) {
		switch (sceneType) {
		case SCENE_MENU:
			setScene(menuScene);
			break;
		case SCENE_SPLASH:
			setScene(splashScene);
			break;
		case SCENE_GAME:
			setScene(gameScene);
			break;
		case SCENE_LOADING:
			setScene(loadingScene);
			break;
		case SCENE_GAME_COMPLETED:
			setScene(gameCompletedScene);
			break;
		case SCENE_MULTIPLAYER_OPTIONS:
			setScene(multiPlayerOptionsScene);
			break;
		case SCENE_SINGLEPLAYER_OPTIONS:
			setScene(singlePlayerOptionsScene);
			break;
		case SCENE_HIGH_SCORES:
			setScene(highScoresScene);
			break;
		case SCENE_MULTIPLAYER_CILENT_GAME:
			setScene(multiplayerClientGameScene);
			break;
		case SCENE_MULTIPLAYER_SERVER_GAME:
			setScene(multiplayerServerGameScene);
			break;
		case SCENE_MULTIPLAYER_GAME_COMPLETED:
			setScene(multiplayerGameCompletedScene);
			break;
		default:
			break;
		}
	}
	
	public void createSplashScreen(OnCreateSceneCallback pOnCreateSceneCallback) {
		ResourcesManager.getInstance().loadSplashScreen();
		splashScene = new SplashScene();
		currentScene = splashScene;
		pOnCreateSceneCallback.onCreateSceneFinished(splashScene);
	}
	
	private void disposeSplashScene() {
		ResourcesManager.getInstance().unloadSplashScreen();
		splashScene.disposeScene();
		splashScene = null;
	}
	
	public void createMenuScene() {
		ResourcesManager.getInstance().loadMenuResources();
		menuScene = new MainMenuScene();
		loadingScene = new LoadingScene();
		multiPlayerOptionsScene = new MultiplayerOptionsScene();
		singlePlayerOptionsScene = new SinglePlayerOptionsScene();
		highScoresScene = new HighScoresScene();
		setScene(menuScene);
		disposeSplashScene();
	}

	public void loadGameScene(final Engine mEngine) {
		setScene(loadingScene);
		ResourcesManager.getInstance().unloadMenuTextures();
		mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() 
	    {
	        public void onTimePassed(final TimerHandler pTimerHandler) 
	        {
	            mEngine.unregisterUpdateHandler(pTimerHandler);
	            ResourcesManager.getInstance().loadGameResources();
	            gameScene = new GameScene();
	            gameCompletedScene = new GameCompletedScene();
	            setScene(gameScene);
	            /*multiplayerServerGameScene = new MultiplayerServerGameScene();
	            setScene(multiplayerServerGameScene);*/
	        }
	    }));
	}
	
	public void loadMultiplayerGameScene(final Engine mEngine, final BluetoothConnection bluetoothConnection,
			final boolean isClient) {
		setScene(loadingScene);
		((MultiplayerOptionsScene) multiPlayerOptionsScene).onStop();
		ResourcesManager.getInstance().unloadMenuTextures();
		mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() 
	    {
	        public void onTimePassed(final TimerHandler pTimerHandler) 
	        {
	            mEngine.unregisterUpdateHandler(pTimerHandler);
	            ResourcesManager.getInstance().loadGameResources();
	            multiplayerGameScene = new MultiplayerGameScene(bluetoothConnection, isClient);
	            multiplayerGameCompletedScene = new MultiplayerGameCompletedScene();
	            setScene(multiplayerGameScene);
	        }
	    }));
	}
	
	public void loadMenuScene(final Engine mEngine) {
		setScene(loadingScene);
		gameScene.disposeScene();
		ResourcesManager.getInstance().unloadGameResources();
		mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() 
	    {
	        public void onTimePassed(final TimerHandler pTimerHandler) 
	        {
	            mEngine.unregisterUpdateHandler(pTimerHandler);
	            ResourcesManager.getInstance().loadMenuTextures();
	            setScene(menuScene);
	        }
	    }));
	}
	
	public void loadMenuSceneFromMultiplayer(final Engine mEngine) {
		setScene(loadingScene);
		multiplayerGameCompletedScene.disposeScene();
		ResourcesManager.getInstance().unloadGameResources();
		mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() 
	    {
	        public void onTimePassed(final TimerHandler pTimerHandler) 
	        {
	            mEngine.unregisterUpdateHandler(pTimerHandler);
	            ResourcesManager.getInstance().loadMenuTextures();
	            setScene(menuScene);
	        }
	    }));
	}
	
	public void loadMultiplayerGameCompletedScene(Engine mEngine, int myScore, int opponentScore) {
		((MultiplayerOptionsScene) multiPlayerOptionsScene).disableBluetooth();
		multiplayerGameScene.disposeScene();
		((MultiplayerGameCompletedScene) multiplayerGameCompletedScene).prepareScene(myScore, opponentScore);
		setScene(multiplayerGameCompletedScene);
		ResourcesManager.getInstance().unloadGameResources();
	}
	
	public void loadSingleplayerOptionsScene(Engine mEngine) {
		setScene(singlePlayerOptionsScene);
	}
	
	public void loadHighScoresScene(Engine mEngine) {
		setScene(highScoresScene);
	}
	
	public void loadGameCompletedScene(final Engine mEngine, int score) {
		((GameCompletedScene) gameCompletedScene).prepareScene(score);
		setScene(gameCompletedScene);
		gameScene.disposeScene();
		ResourcesManager.getInstance().unloadGameResources();
	}
	
	public void backToMenu() {
		setScene(menuScene);
		//multiPlayerOptionsScene.disposeScene();
	}
	
	public void loadMultiplayerOptionsScene(final Engine mEngine) {
		setScene(multiPlayerOptionsScene);
		((MultiplayerOptionsScene) multiPlayerOptionsScene).onStart();
		//menuScene.disposeScene();
	}
	
	public BaseScene getCurrentScene() {
		return currentScene;
	}
	
	public SceneType getCurrentSceneType() {
		return currentSceneType;
	}
	
}
