package com.project.harbinger.scene;

import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.util.GLState;
import org.andengine.engine.camera.Camera;

import com.project.harbinger.manager.SceneManager;
import com.project.harbinger.manager.SceneManager.SceneType;

/**Menu główne gry.
 * @author Łukasz Frącz
 *
 */
public class MainMenuScene extends BaseScene implements IOnMenuItemClickListener {

	/**Scena dziecko zawierająca przyciski*/
	private MenuScene menuChildScene;
	/**Identyfikacja przycisku "single player"*/
	private final int MENU_SINGLE = 0;
	/**Identyfikacja przycisku "multi player"*/
	private final int MENU_MULTI = 1;
	
	/**
	 * Tworzy scenę dziecko zawierającą przyciski
	 */
	private void createMenuChildScene() {
		menuChildScene = new MenuScene(camera);
		menuChildScene.setPosition(0, 0);
		
		IMenuItem singleItem = new ScaleMenuItemDecorator(new SpriteMenuItem(
				MENU_SINGLE, resourcesManager.getSingleButtonRegion(), vbom),
				1.2f, 1);
		IMenuItem multiItem = new ScaleMenuItemDecorator(new SpriteMenuItem(
				MENU_MULTI, resourcesManager.getMultiButtonRegion(), vbom),
				1.2f, 1);
		
		menuChildScene.addMenuItem(singleItem);
		menuChildScene.addMenuItem(multiItem);
		
		menuChildScene.buildAnimations();
		menuChildScene.setBackgroundEnabled(false);
		
		singleItem.setPosition(singleItem.getX(), singleItem.getY() - 20);
		multiItem.setPosition(multiItem.getX(), multiItem.getY() + 40);
		
		menuChildScene.setOnMenuItemClickListener(this);
		
		setChildScene(menuChildScene);
	}
	
	/**Tworzy scenę
	 * @see com.project.harbinger.scene.BaseScene#createScene()
	 */
	@Override
	public void createScene() {
		createBackground();
		createMenuChildScene();
	}

	/**Metoda wywoływana po naciśnięciu przycisku "back". W tym przypadku nic nie robi
	 * @see com.project.harbinger.scene.BaseScene#onBackKeyPressed()
	 */
	@Override
	public void onBackKeyPressed() {
		
	}

	/**
	 * @see com.project.harbinger.scene.BaseScene#getSceneType()
	 * @return Typ sceny (menu)
	 */
	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_MENU;
	}

	/**Metoda wywoływana przy niszczeniu sceny. W tym przypadku niczego nie robi.
	 * @see com.project.harbinger.scene.BaseScene#disposeScene()
	 */
	@Override
	public void disposeScene() {
	}
	
	/**
	 * Tworzy tło
	 */
	private void createBackground() {
		attachChild(new Sprite(0, 0, resourcesManager.getMenuBackgroundRegion(), vbom){
			
			protected void preDraw(GLState pGLState, Camera pCamera) {
	            super.preDraw(pGLState, pCamera);
	            pGLState.enableDither();
	        }
		});
	}

	/**Interfejs wywoływany po naciśnięciu przycisków w menu
	 * @see org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener#onMenuItemClicked(org.andengine.entity.scene.menu.MenuScene, org.andengine.entity.scene.menu.item.IMenuItem, float, float)
	 */
	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		switch (pMenuItem.getID()) {
		case MENU_SINGLE:
			SceneManager.getInstance().loadSingleplayerOptionsScene();
			//SceneManager.getInstance().loadGameScene(engine);
			return true;
		case MENU_MULTI:
			SceneManager.getInstance().loadMultiplayerOptionsScene();
			return true;
		default:
			return false;
		}
	}

}
