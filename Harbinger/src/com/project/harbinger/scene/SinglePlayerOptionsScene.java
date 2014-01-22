package com.project.harbinger.scene;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.util.color.Color;

import com.project.harbinger.manager.SceneManager;
import com.project.harbinger.manager.SceneManager.SceneType;

/**Menu trybu dla jednego gracza.
 * @author Łukasz Frącz
 *
 */
public class SinglePlayerOptionsScene extends BaseScene implements IOnMenuItemClickListener  {

	/**Scena dziecko zawierająca przyciski*/
	private MenuScene menuChildScene;
	/**Identyfikacja przycisku "start"*/
	private final int MENU_START = 0;
	/**Identyfikacja przycisku "high scores"*/
	private final int MENU_HIGH_SCORES = 1;
	/**Identyfikacja przycisku "back"*/
	private final int MENU_BACK = 2;
	
	/**Tworzy scenę
	 * @see com.project.harbinger.scene.BaseScene#createScene()
	 */
	@Override
	public void createScene() {
		createBackground();
		
		menuChildScene = new MenuScene(camera);
		menuChildScene.setPosition(0, 0);
		
		IMenuItem startItem = new ScaleMenuItemDecorator(new SpriteMenuItem(
				MENU_START, resourcesManager.getStartButtonRegion(), vbom),
				1.2f, 1);
		IMenuItem highScoresItem = new ScaleMenuItemDecorator(new SpriteMenuItem(
				MENU_HIGH_SCORES, resourcesManager.getHighScoresButtonRegion(), vbom),
				1.2f, 1);
		IMenuItem backItem = new ScaleMenuItemDecorator(new SpriteMenuItem(
				MENU_BACK, resourcesManager.getBackMenuButtonRegion(), vbom),
				1.2f, 1);
		
		menuChildScene.addMenuItem(startItem);
		menuChildScene.addMenuItem(highScoresItem);
		menuChildScene.addMenuItem(backItem);
		
		menuChildScene.buildAnimations();
		menuChildScene.setBackgroundEnabled(false);
		
		startItem.setPosition(startItem.getX(), startItem.getY() + 10);
		highScoresItem.setPosition(highScoresItem.getX(), highScoresItem.getY() + 10);
		backItem.setPosition(backItem.getX(), backItem.getY() + 30);
		
		menuChildScene.setOnMenuItemClickListener(this);
		
		setChildScene(menuChildScene);		
	}

	/**Metoda wywoływana po naciśnięciu przycisku "back". Powraca do menu głównego.
	 * @see com.project.harbinger.scene.BaseScene#onBackKeyPressed()
	 */
	@Override
	public void onBackKeyPressed() {
		SceneManager.getInstance().backToMenu();
	}

	/**
	 * @see com.project.harbinger.scene.BaseScene#getSceneType()
	 * @return Typ sceny (menu trybu dla jednego gracza)
	 */
	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_SINGLEPLAYER_OPTIONS;
	}

	/**Niszczy scenę
	 * @see com.project.harbinger.scene.BaseScene#disposeScene()
	 */
	@Override
	public void disposeScene() {
		detachSelf();
		dispose();
	}

	/**
	 * Tworzy tło
	 */
	private void createBackground() {
		setBackground(new Background(Color.BLACK));
	}

	/**Interfejs uruchamiany po naciśnięciu przycisku w menu
	 * @see org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener#onMenuItemClicked(org.andengine.entity.scene.menu.MenuScene, org.andengine.entity.scene.menu.item.IMenuItem, float, float)
	 */
	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		switch (pMenuItem.getID()) {
		case MENU_START:
			SceneManager.getInstance().loadGameScene();
			return true;
		case MENU_HIGH_SCORES:
			SceneManager.getInstance().loadHighScoresScene();
			((HighScoresScene) SceneManager.getInstance().getCurrentScene()).refresh();
			return true;
		case MENU_BACK:
			SceneManager.getInstance().backToMenu();
			return true;
		default:
			return false;
		}
	}
}
