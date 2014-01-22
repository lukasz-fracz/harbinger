package com.project.harbinger.manager;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import android.graphics.Color;

import com.project.harbinger.MainMenuActivity;

/**
 * Klasa zawierająca wszystkie zasoby potrzebne w grze. Zaprojektowana jako singleton, więc dostęp do niej można uzyskać z każdeg miejsca.
 * 
 * @author Łukasz Frącz
 *
 */
public class ResourcesManager {

	/**Instancja klasy. */
	private static final ResourcesManager INSTANCE = new ResourcesManager();
	
	/**
	 * @return Instancję klasy
	 */
	public static ResourcesManager getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Metoda przygotowująca menadżer
	 * 
	 * @param engine Silnik gry
	 * @param activity Referencja do activity
	 * @param camera Kamera obsługująca grę
	 * @param vbom Menadżer objektów
	 */
	public static void prepareManager(Engine engine, MainMenuActivity activity, 
			Camera camera, VertexBufferObjectManager vbom) {
        getInstance().engine = engine;
        getInstance().activity = activity;
        getInstance().camera = camera;
        getInstance().vbom = vbom;
    }
	
	// --------
	
	/**Silnik aplikacji*/
	private Engine engine;
	/**Acitivity aplikacji*/
	private MainMenuActivity activity;
	/**Kamera w grze*/
	private Camera camera;
	/**Menadżer objektów*/
	private VertexBufferObjectManager vbom;
	
	// splash
	/**Tekstura występująca na splash screenie*/
	private ITextureRegion splashRegion;
	/**Atlas tekstur występujących na splash screenie*/
	private BitmapTextureAtlas splashTextureAtlas;
	
	/**
	 * Ładuje zasoby dla potrzeb splash screenu
	 */
	public void loadSplashScreen() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		splashTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(),
				480, 800, TextureOptions.BILINEAR);
		splashRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(splashTextureAtlas, activity, "splash.png",
				0, 0);
		splashTextureAtlas.load();
	}
	
	/**
	 * Usuwa z pamięci zasoby użytw w splash screenie.
	 */
	public void unloadSplashScreen() {
		splashTextureAtlas.unload();
		splashRegion = null;
	}
	
	/**
	 * @return Tekstura występująca na splash screenie
	 */
	public ITextureRegion getSplashRegion() {
		return splashRegion;
	}
	
	// menu
	/**Tekstura tła w menu*/
	private ITextureRegion menuBackgroundRegion;
	/**Tekstura przycisku "single player"*/
	private ITextureRegion singleButtonRegion;
	/**Tekstura przycisku "multi player"*/
	private ITextureRegion multiButtonRegion;
	/**Tekstura przycisku "start"*/
	private ITextureRegion startButtonRegion;
	/**Tekstura przycisku "back"*/
	private ITextureRegion backMenuButtonRegion;
	/**Tekstura przycisku "high scores"*/
	private ITextureRegion highScoresButtonRegion;
	/**Tekstura przycisku "host game"*/
	private ITextureRegion hostButtonRegion;
	/**Tekstura przycisku "join game"*/
	private ITextureRegion joinButtonRegion;
	/**Tekstura ikony oczekiwania*/
	private ITextureRegion waitIconRegion;
	/**Tekstura ikony znalezienia czegoś*/
	private ITextureRegion haveSomethingIconRegion;
	/**Teksura ikony startu*/
	private ITextureRegion goIconRegion;
	/**Atlas tekstur używanych w menu głównym*/
	private BuildableBitmapTextureAtlas menuTextureAtlas;
	
	/**
	 * @return Tekstura tła w menu
	 */
	public ITextureRegion getMenuBackgroundRegion() {
		return menuBackgroundRegion;
	}
	
	/**
	 * @return Tekstura przycisku "single player"
	 */
	public ITextureRegion getSingleButtonRegion() {
		return singleButtonRegion;
	}
	
	/**
	 * @return Tekstura przycisku "multi player"
	 */
	public ITextureRegion getMultiButtonRegion() {
		return multiButtonRegion;
	}
	 
	/**
	 * @return Tekstura przycisku "start"
	 */
	public ITextureRegion getStartButtonRegion() {
		return startButtonRegion;
	}
	
	/**
	 * @return Tekstura przycisku "back"
	 */
	public ITextureRegion getBackMenuButtonRegion() {
		return backMenuButtonRegion;
	}
	
	/**
	 * @return Tekstura przycisku "high scores"
	 */
	public ITextureRegion getHighScoresButtonRegion() {
		return highScoresButtonRegion;
	}
	
	/**
	 * @return Tekstura przycisku "host game"
	 */
	public ITextureRegion getHostButtonRegion() {
		return hostButtonRegion;
	}
	
	/**
	 * @return Tekstura przycisku "join game"
	 */
	public ITextureRegion getJoinButtonRegion() {
		return joinButtonRegion;
	}
	
	/**
	 * @return Tekstura ikony oczekiwania
	 */
	public ITextureRegion getWaitIconRegion() {
		return waitIconRegion;
	}
	
	/**
	 * @return Tekstura ikony znalezienia czegoś
	 */
	public ITextureRegion getHaveSomethingIconRegion() {
		return haveSomethingIconRegion;
	}
	
	/**
	 * @return Teksura ikony startu
	 */
	public ITextureRegion getGoIconRegion() {
		return goIconRegion;
	}
	
	/**
	 * Przygotowuje zasoby dla potrzeb menu głównego
	 */
	private void loadMenuGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
		menuTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(),
				1024, 1024, TextureOptions.BILINEAR);
		menuBackgroundRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				menuTextureAtlas, activity, "menu_background.png");
		singleButtonRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				menuTextureAtlas, activity, "single_button.gif");
		multiButtonRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				menuTextureAtlas, activity, "multi_button.gif");
		startButtonRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				menuTextureAtlas, activity, "start_button.gif");
		backMenuButtonRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				menuTextureAtlas, activity, "back_button.gif");
		highScoresButtonRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				menuTextureAtlas, activity, "high_scores_button.gif");
		hostButtonRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				menuTextureAtlas, activity, "host_button.gif");
		joinButtonRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				menuTextureAtlas, activity, "join_button.gif");
		haveSomethingIconRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				menuTextureAtlas, activity, "have_something_icon.png");
		waitIconRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				menuTextureAtlas, activity, "wait_icon.png");
		goIconRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				menuTextureAtlas, activity, "go_icon.png");
		
		try {
			menuTextureAtlas.build(
					new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			menuTextureAtlas.load();
		} catch (TextureAtlasBuilderException e) {
			Debug.e(e);
		}
	}
	
	/**
	 * Kasuje z pamięci zasoby użyte w menu głównym
	 */
	public void unloadMenuTextures() {
		menuTextureAtlas.unload();
	}
	
	/**
	 * Ładuje do pamięci zasoby potrzebne w menu głównym
	 */
	public void loadMenuTextures() {
		menuTextureAtlas.load();
	}
	
	/**
	 * Przygotowuje wszystkie zasoby potrzebne w menu głównym
	 */
	public void loadMenuResources() {
		loadMenuGraphics();
		loadMenuFonts();
	}
	
	/**Czcionka używana w grze*/
	private Font font;
	
	/**
	 * Ładuje czcionkę używaną w grze
	 */
	private void loadMenuFonts() {
		FontFactory.setAssetBasePath("fonts/");
		ITexture mainFontTexture = new BitmapTextureAtlas(activity.getTextureManager(),
				256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		
		font = FontFactory.createStrokeFromAsset(activity.getFontManager(),
				mainFontTexture, activity.getAssets(), "7TH.ttf", 30, true,
				Color.WHITE, 2, Color.BLACK);
		font.load();
	}
	
	/**
	 * @return Czcionka używana w grze
	 */
	public Font getFont() {
		return font;
	}
	
	// game
	/**Atlas tekstur obiektów występujących na ekranie gry*/
	private BuildableBitmapTextureAtlas gameTextureAtlas;
	/**Tekstura gracza*/
	private ITextureRegion playerRegion;
	/**Tekstura drugiego gracza*/
	private ITextureRegion player2Region;
	/**Tekstura meteoru*/
	private ITextureRegion meteorRegion;
	/**Tekstura wystrzału*/
	private ITextureRegion missileRegion;
	/**Tekstura pocisku*/
	private ITextureRegion bulletRegion;
	/**Tekstura lekkiego myśliwca*/
	private ITextureRegion lightFighterRegion;
	/**Tekstura ciężkiego myśliwca*/
	private ITextureRegion heavyFighterRegion;
	/**Tekstura krążownika*/
	private ITextureRegion cruiserRegion;
	/**Tekstura gałki analogowej*/
	private ITextureRegion analogRegion;
	/**Tekstura tła gałki analogowej*/
	private ITextureRegion analogBackgroundRegion;
	/**Tekstura przycisku wystrzału*/
	private ITextureRegion fireButtonRegion;
	/**Tekstura przycisku "resume"*/
	private ITextureRegion resumeButtonRegion;
	/**Tekstura przycisku "back to menu"*/
	private ITextureRegion backButtonRegion;
	/**Tekstura przycisku "yes"*/
	private ITextureRegion yesButtonRegion;
	/**Tekstura przycisku "no"*/
	private ITextureRegion noButtonRegion;
	/**Tekstura tła panelu kontrolnego*/
	private ITextureRegion gamepadBackgroundRegion;
	
	/**Atlas tekstur tła*/
	private BitmapTextureAtlas backgroundAtlas;
	/**Tekstury tła*/
	private TiledTextureRegion backgroundRegion;
	
	public BuildableBitmapTextureAtlas getGameTextureAtlas() {
		return gameTextureAtlas;
	}
	
	/**
	 * @return Tekstura gracza
	 */
	public ITextureRegion getPlayerRegion() {
		return playerRegion;
	}
	
	/**
	 * @return Tekstura drugiego gracza
	 */
	public ITextureRegion getPlayer2Region() {
		return player2Region;
	}
	
	/**
	 * @return Tekstura meteoru
	 */
	public ITextureRegion getMeteorRegion() {
		return meteorRegion;
	}
	
	/**
	 * @return Tekstura wystrzału
	 */
	public ITextureRegion getMissileRegion() {
		return missileRegion;
	}
	
	/**
	 * @return Tekstura pocisku
	 */
	public ITextureRegion getBulletRegion() {
		return bulletRegion;
	}
	
	/**
	 * @return Tekstura lekkiego myśliwca
	 */
	public ITextureRegion getLightFighterRegion() {
		return lightFighterRegion;
	}
	
	/**
	 * @return Tekstura ciężkiego myśliwca
	 */
	public ITextureRegion getHeavyFighterRegion() {
		return heavyFighterRegion;
	}
	
	/**
	 * @return Tekstura krążownika
	 */
	public ITextureRegion getCruiserRegion() {
		return cruiserRegion;
	}
	
	/**
	 * @return Tekstura gałki analogowej
	 */
	public ITextureRegion getAnalogRegion() {
		return analogRegion;
	}
	
	/**
	 * @return Tekstura tła gałki analogowej
	 */
	public ITextureRegion getAnalogBackgroundRegion() {
		return analogBackgroundRegion;
	}
	
	/**
	 * @return Tekstura przycisku wystrzału
	 */
	public ITextureRegion getFireButtonRegion() {
		return fireButtonRegion;
	}
	
	/**
	 * @return Tekstura przycisku "resume"
	 */
	public ITextureRegion getResumeButtonRegion() {
		return resumeButtonRegion;
	}
	
	/**
	 * @return Tekstura przycisku "back to menu"
	 */
	public ITextureRegion getBackButtonRegion() {
		return backButtonRegion;
	}
	
	/**
	 * @return Tekstura przycisku "yes"
	 */
	public ITextureRegion getYesButtonRegion() {
		return yesButtonRegion;
	}
	
	/**
	 * @return Tekstura przycisku "no"
	 */
	public ITextureRegion getNoButtonRegion() {
		return noButtonRegion;
	}
	
	/**
	 * @return Tekstura tła panelu kontrolnego
	 */
	public ITextureRegion getGamepadBackgroundRegion() {
		return gamepadBackgroundRegion;
	}

	/**
	 * @return Tekstury tła
	 */
	public TiledTextureRegion getBackgroundRegion() {
		return backgroundRegion;
	}
	
	/**
	 * Przygotowuje zasoby do użycia w grze
	 */
	private void loadGameGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
	    gameTextureAtlas = new BuildableBitmapTextureAtlas(
	    		activity.getTextureManager(), 2048, 2048, TextureOptions.BILINEAR);
	    
	    playerRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
	    		gameTextureAtlas, activity, "player.gif");
	    player2Region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
	    		gameTextureAtlas, activity, "player2.gif");
	    meteorRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
	    		gameTextureAtlas, activity, "meteor.gif");
	    missileRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
	    		gameTextureAtlas, activity, "missile.gif");
	    bulletRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
	    		gameTextureAtlas, activity, "bullet.gif");
	    lightFighterRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
	    		gameTextureAtlas, activity, "fighter_light.gif");
	    heavyFighterRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
	    		gameTextureAtlas, activity, "fighter_heavy.gif");	
	    cruiserRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
	    		gameTextureAtlas, activity, "cruiser.gif");
	    analogRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
	    		gameTextureAtlas, activity, "buttons/analog.png");
	    analogBackgroundRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
	    		gameTextureAtlas, activity, "buttons/analogBackground.png");
	    fireButtonRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
	    		gameTextureAtlas, activity, "buttons/fire.gif");
	    resumeButtonRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
	    		gameTextureAtlas, activity, "buttons/resume.gif");
	    backButtonRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
	    		gameTextureAtlas, activity, "buttons/back.gif");
	    yesButtonRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
	    		gameTextureAtlas, activity, "buttons/yes.gif");
	    noButtonRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
	    		gameTextureAtlas, activity, "buttons/no.gif");
	    gamepadBackgroundRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
	    		gameTextureAtlas, activity, "buttons/gamepad.png");
	    
	    backgroundAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 960, 800, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
	    backgroundRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(backgroundAtlas, activity.getAssets(),
	    	    "background.jpg", 0, 0, 2, 1);
	    
	    try  {
	        gameTextureAtlas.build(new 
	        		BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
	        gameTextureAtlas.load();
	        backgroundAtlas.load();
	    } 
	    catch (TextureAtlasBuilderException e) {
	        Debug.e(e);
	    }
	}
	
	/**
	 * Ładuje zasoby potrzebne w grze
	 */
	public void loadGameResources() {
		loadGameGraphics();
	}
	
	/**
	 * Usuwa z pamięci zasoby używane w grze
	 */
	public void unloadGameResources() {
		gameTextureAtlas.unload();
        backgroundAtlas.unload();
	}
	
	/**
	 * @return Silnik aplikacji
	 */
	public Engine getEngine() {
		return engine;
	}
	
	/**
	 * @return Activity
	 */
	public MainMenuActivity getActivity() {
		return activity;
	}
	
	/**
	 * @return Kamera gry
	 */
	public Camera getCamera() {
		return camera;
	}
	
	/**
	 * @return Menadżer objektów
	 */
	public VertexBufferObjectManager getVbom() {
		return vbom;
	}
}
