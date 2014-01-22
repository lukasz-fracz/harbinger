package com.project.harbinger.manager;

import org.andengine.engine.Engine;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.ui.IGameInterface.OnCreateSceneCallback;

import com.project.harbinger.multiplayer.BluetoothConnection;
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

/**Klasa zarządzająca scenami. Ładuje, usuwa i zmienia aktualnie wyświetlaną scenę.
 * Zaprojektowana jako singleton.
 * 
 * @author Łukasz Frącz
 *
 */
/**
 * @author lukaszSA
 *
 */
public class SceneManager {
	
	/**Instancja klasy*/
	private static final SceneManager INSTANCE = new SceneManager();
	
	/**
	 * @return Instancja klasy
	 */
	public static SceneManager getInstance() {
		return INSTANCE;
	}
	
	// ---------------
	
	/**Splash screen*/
	private BaseScene splashScene;
	/**Menu główne*/
	private BaseScene menuScene;
	/**Scena gry*/
	private BaseScene gameScene;
	/**Ekran ładowania*/
	private BaseScene loadingScene;
	/**Ekran zakończenia gry*/
	private BaseScene gameCompletedScene;
	/**Menu trybu wiloosobowego*/
	private BaseScene multiPlayerOptionsScene;
	/**Menu trybu dla jednego gracza*/
	private BaseScene singlePlayerOptionsScene;
	/**Ekran tablicy wyników*/
	private BaseScene highScoresScene;
	/**Scena gry wieloosobowej*/
	private BaseScene multiplayerGameScene;
	/**Ekran zakończenia gry wieloosobowej*/
	private BaseScene multiplayerGameCompletedScene;
	
	/**Typ sceny
	 * @author Łukasz Frącz
	 *
	 */
	public enum SceneType {
		/**Splash screen*/
		SCENE_SPLASH,
		/**Menu główne*/
		SCENE_MENU,
		/**Scena gry*/
		SCENE_GAME,
		/**Ekran ładowania*/
		SCENE_LOADING,
		/**Ekran zakończenia gry*/
		SCENE_GAME_COMPLETED,
		/**Menu trybu wieloosobowego*/
		SCENE_MULTIPLAYER_OPTIONS,
		/**Menu trybu dla jednego gracza*/
		SCENE_SINGLEPLAYER_OPTIONS,
		/**Ekran tablicy wyników*/
		SCENE_HIGH_SCORES,
		/**Scena gry wieloosobowej*/
		SCENE_MULTIPLAYER_GAME,
		/**Ekran zakończenie gry wieloosobowej*/
		SCENE_MULTIPLAYER_GAME_COMPLETED
	}
	
	/**Typ aktualnie wyświetlanej sceny*/
	private SceneType currentSceneType = SceneType.SCENE_SPLASH;
	/**Aktualnie wyświetlana scena*/
	private BaseScene currentScene;
	/**Silnik aplikacji*/
	private Engine engine = ResourcesManager.getInstance().getEngine();
	
	/**Zmienia aktualną scenę
	 * @param scene Nowa scena
	 */
	private void setScene(BaseScene scene) {
		engine.setScene(scene);
		currentScene = scene;
		currentSceneType = scene.getSceneType();
	}	
	
	/**Tworzy splash screen
	 * @param pOnCreateSceneCallback
	 */
	public void createSplashScreen(OnCreateSceneCallback pOnCreateSceneCallback) {
		ResourcesManager.getInstance().loadSplashScreen();
		splashScene = new SplashScene();
		currentScene = splashScene;
		pOnCreateSceneCallback.onCreateSceneFinished(splashScene);
	}
	
	/**
	 * Niszczy splash screen
	 */
	private void disposeSplashScene() {
		ResourcesManager.getInstance().unloadSplashScreen();
		splashScene.disposeScene();
		splashScene = null;
	}
	
	/**
	 * Tworzy menu główne
	 */
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

	/**
	 * Ładuje ekran gry
	 */
	public void loadGameScene() {
		setScene(loadingScene);
		ResourcesManager.getInstance().unloadMenuTextures();
		engine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() 
	    {
	        public void onTimePassed(final TimerHandler pTimerHandler) 
	        {
	            engine.unregisterUpdateHandler(pTimerHandler);
	            ResourcesManager.getInstance().loadGameResources();
	            gameScene = new GameScene();
	            gameCompletedScene = new GameCompletedScene();
	            setScene(gameScene);
	        }
	    }));
	}
	
	/**Ładuje ekran gry wieloosobowej
	 * @param bluetoothConnection Objekt zarządzający połączeniem bluetooth
	 * @param isClient 'false' jeśli scenę ładuje gra hostująca tryb wieloosobowy. 'true' w przeciwnym przypadku 
	 */
	public void loadMultiplayerGameScene(final BluetoothConnection bluetoothConnection,
			final boolean isClient) {
		setScene(loadingScene);
		((MultiplayerOptionsScene) multiPlayerOptionsScene).onStop();
		ResourcesManager.getInstance().unloadMenuTextures();
		engine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() 
	    {
	        public void onTimePassed(final TimerHandler pTimerHandler) 
	        {
	            engine.unregisterUpdateHandler(pTimerHandler);
	            ResourcesManager.getInstance().loadGameResources();
	            multiplayerGameScene = new MultiplayerGameScene(bluetoothConnection, isClient);
	            multiplayerGameCompletedScene = new MultiplayerGameCompletedScene();
	            setScene(multiplayerGameScene);
	        }
	    }));
	}
	
	/**
	 * Ładuje menu główne
	 */
	public void loadMenuScene() {
		setScene(loadingScene);
		gameScene.disposeScene();
		ResourcesManager.getInstance().unloadGameResources();
		engine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() 
	    {
	        public void onTimePassed(final TimerHandler pTimerHandler) 
	        {
	            engine.unregisterUpdateHandler(pTimerHandler);
	            ResourcesManager.getInstance().loadMenuTextures();
	            setScene(menuScene);
	        }
	    }));
	}
	
	/**
	 * Ładuje menu główne po zakończeniu gry wieloosobowej
	 */
	public void loadMenuSceneFromMultiplayer() {
		setScene(loadingScene);
		multiplayerGameCompletedScene.disposeScene();
		ResourcesManager.getInstance().unloadGameResources();
		engine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() 
	    {
	        public void onTimePassed(final TimerHandler pTimerHandler) 
	        {
	            engine.unregisterUpdateHandler(pTimerHandler);
	            ResourcesManager.getInstance().loadMenuTextures();
	            setScene(menuScene);
	        }
	    }));
	}
	
	/**Ładuje ekran zakończenia gry wieloosobowej
	 * @param myScore Wynik gracza
	 * @param opponentScore Wynik drugiego gracza
	 */
	public void loadMultiplayerGameCompletedScene(int myScore, int opponentScore) {
		((MultiplayerOptionsScene) multiPlayerOptionsScene).disableBluetooth();
		multiplayerGameScene.disposeScene();
		((MultiplayerGameCompletedScene) multiplayerGameCompletedScene).prepareScene(myScore, opponentScore);
		setScene(multiplayerGameCompletedScene);
		ResourcesManager.getInstance().unloadGameResources();
	}
	
	/**
	 * Ładuje menu trybu dla jednego gracza
	 */
	public void loadSingleplayerOptionsScene() {
		setScene(singlePlayerOptionsScene);
	}
	
	/**
	 * Ładuje ekran najlepszych wyników
	 */
	public void loadHighScoresScene() {
		setScene(highScoresScene);
	}
	
	/**Ładuje ekran zakończenia gry jednoosobowej
	 * @param score Wynik gracza
	 */
	public void loadGameCompletedScene(int score) {
		((GameCompletedScene) gameCompletedScene).prepareScene(score);
		setScene(gameCompletedScene);
		gameScene.disposeScene();
		ResourcesManager.getInstance().unloadGameResources();
	}
	
	/**
	 * Ładuje spowrotem menu główne
	 */
	public void backToMenu() {
		setScene(menuScene);
	}
	
	/**
	 * Ładuje menu trybu dla wielu graczy
	 */
	public void loadMultiplayerOptionsScene() {
		setScene(multiPlayerOptionsScene);
		((MultiplayerOptionsScene) multiPlayerOptionsScene).onStart();
	}
	
	/**
	 * @return Aktualna scena
	 */
	public BaseScene getCurrentScene() {
		return currentScene;
	}
	
	/**
	 * @return Typ aktualnej sceny
	 */
	public SceneType getCurrentSceneType() {
		return currentSceneType;
	}
	
}
