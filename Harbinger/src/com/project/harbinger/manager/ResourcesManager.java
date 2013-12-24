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
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import android.graphics.Color;

import com.project.harbinger.MainMenuActivity;

public class ResourcesManager {

	private static final ResourcesManager INSTANCE = new ResourcesManager();
	
	public static ResourcesManager getInstance() {
		return INSTANCE;
	}
	
	public static void prepareManager(Engine engine, MainMenuActivity activity, 
			Camera camera, VertexBufferObjectManager vbom) {
        getInstance().engine = engine;
        getInstance().activity = activity;
        getInstance().camera = camera;
        getInstance().vbom = vbom;
    }
	
	// --------
	
	private Engine engine;
	private MainMenuActivity activity;
	private Camera camera;
	private VertexBufferObjectManager vbom;
	
	// splash
	private ITextureRegion splashRegion;
	private BitmapTextureAtlas splashTextureAtlas;
	
	public void loadSplashScreen() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		splashTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(),
				480, 800, TextureOptions.BILINEAR);
		splashRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(splashTextureAtlas, activity, "splash.png",
				0, 0);
		splashTextureAtlas.load();
	}
	
	public void unloadSplashScreen() {
		splashTextureAtlas.unload();
		splashRegion = null;
	}
	
	// menu
	private ITextureRegion menuBackgroundRegion;
	private ITextureRegion singleButtonRegion;
	private ITextureRegion multiButtonRegion;
	private ITextureRegion startButtonRegion;
	private ITextureRegion backMenuButtonRegion;
	private ITextureRegion highScoresButtonRegion;
	private ITextureRegion hostButtonRegion;
	private ITextureRegion joinButtonRegion;
	private ITextureRegion waitIconRegion;
	private ITextureRegion haveSomethingIconRegion;
	private ITextureRegion goIconRegion;
	private BuildableBitmapTextureAtlas menuTextureAtlas;
	
	public ITextureRegion getMenuBackgroundRegion() {
		return menuBackgroundRegion;
	}
	
	public ITextureRegion getSingleButtonRegion() {
		return singleButtonRegion;
	}
	
	public ITextureRegion getMultiButtonRegion() {
		return multiButtonRegion;
	}
	
	public ITextureRegion getStartButtonRegion() {
		return startButtonRegion;
	}
	
	public ITextureRegion getBackMenuButtonRegion() {
		return backMenuButtonRegion;
	}
	
	public ITextureRegion getHighScoresButtonRegion() {
		return highScoresButtonRegion;
	}
	
	public ITextureRegion getHostButtonRegion() {
		return hostButtonRegion;
	}
	
	public ITextureRegion getJoinButtonRegion() {
		return joinButtonRegion;
	}
	
	public ITextureRegion getWaitIconRegion() {
		return waitIconRegion;
	}
	
	public ITextureRegion getHaveSomethingIconRegion() {
		return haveSomethingIconRegion;
	}
	
	public ITextureRegion getGoIconRegion() {
		return goIconRegion;
	}
	
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
	
	public void unloadMenuTextures() {
		menuTextureAtlas.unload();
	}
	
	public void loadMenuTextures() {
		menuTextureAtlas.load();
	}
	
	// loading
	private Font font;
	private void loadMenuFonts() {
		FontFactory.setAssetBasePath("fonts/");
		ITexture mainFontTexture = new BitmapTextureAtlas(activity.getTextureManager(),
				256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		
		font = FontFactory.createStrokeFromAsset(activity.getFontManager(),
				mainFontTexture, activity.getAssets(), "7TH.ttf", 30, true,
				Color.WHITE, 2, Color.BLACK);
		font.load();
	}
	
	public Font getFont() {
		return font;
	}
	
	// game
	private BuildableBitmapTextureAtlas gameTextureAtlas;
	private ITextureRegion playerRegion;
	private ITextureRegion player2Region;
	private ITextureRegion meteorRegion;
	private ITextureRegion missileRegion;
	private ITextureRegion missile2Region;
	private ITextureRegion bulletRegion;
	private ITextureRegion lightFighterRegion;
	private ITextureRegion heavyFighterRegion;
	private ITextureRegion cruiserRegion;
	private ITextureRegion analogRegion;
	private ITextureRegion analogBackgroundRegion;
	private ITextureRegion fireButtonRegion;
	private ITextureRegion resumeButtonRegion;
	private ITextureRegion backButtonRegion;
	private ITextureRegion yesButtonRegion;
	private ITextureRegion noButtonRegion;
	private ITextureRegion gamepadBackgroundRegion;
	
	public BuildableBitmapTextureAtlas getGameTextureAtlas() {
		return gameTextureAtlas;
	}
	
	public ITextureRegion getPlayerRegion() {
		return playerRegion;
	}
	
	public ITextureRegion getPlayer2Region() {
		return player2Region;
	}
	
	public ITextureRegion getMeteorRegion() {
		return meteorRegion;
	}
	
	public ITextureRegion getMissileRegion() {
		return missileRegion;
	}
	
	public ITextureRegion getMissile2Region() {
		return missile2Region;
	}
	
	public ITextureRegion getBulletRegion() {
		return bulletRegion;
	}
	
	public ITextureRegion getLightFighterRegion() {
		return lightFighterRegion;
	}
	
	public ITextureRegion getHeavyFighterRegion() {
		return heavyFighterRegion;
	}
	
	public ITextureRegion getCruiserRegion() {
		return cruiserRegion;
	}
	
	public ITextureRegion getAnalogRegion() {
		return analogRegion;
	}
	
	public ITextureRegion getAnalogBackgroundRegion() {
		return analogBackgroundRegion;
	}
	
	public ITextureRegion getFireButtonRegion() {
		return fireButtonRegion;
	}
	
	public ITextureRegion getResumeButtonRegion() {
		return resumeButtonRegion;
	}
	
	public ITextureRegion getBackButtonRegion() {
		return backButtonRegion;
	}
	
	public ITextureRegion getYesButtonRegion() {
		return yesButtonRegion;
	}
	
	public ITextureRegion getNoButtonRegion() {
		return noButtonRegion;
	}
	
	public ITextureRegion getGamepadBackgroundRegion() {
		return gamepadBackgroundRegion;
	}
	
	private void loadGameGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
	    gameTextureAtlas = new BuildableBitmapTextureAtlas(
	    		activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
	    
	    playerRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
	    		gameTextureAtlas, activity, "player.gif");
	    player2Region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
	    		gameTextureAtlas, activity, "player2.gif");
	    meteorRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
	    		gameTextureAtlas, activity, "meteor.gif");
	    missileRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
	    		gameTextureAtlas, activity, "missile.gif");
	    missile2Region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
	    		gameTextureAtlas, activity, "missile2.png");
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
	    
	    try  {
	        gameTextureAtlas.build(new 
	        		BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
	        gameTextureAtlas.load();
	    } 
	    catch (TextureAtlasBuilderException e) {
	        Debug.e(e);
	    }
	}
	
	public void loadMenuResources() {
		loadMenuGraphics();
		loadMenuFonts();
	}
	
	public void loadGameResources() {
		loadGameGraphics();
	}
	
	public void unloadGameResources() {
		
	}
	
	public Engine getEngine() {
		return engine;
	}
	
	public MainMenuActivity getActivity() {
		return activity;
	}
	
	public Camera getCamera() {
		return camera;
	}
	
	public VertexBufferObjectManager getVbom() {
		return vbom;
	}
	
	public ITextureRegion getSplashRegion() {
		return splashRegion;
	}
}
