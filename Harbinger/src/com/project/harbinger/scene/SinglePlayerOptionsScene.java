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

public class SinglePlayerOptionsScene extends BaseScene implements IOnMenuItemClickListener  {

	private MenuScene menuChildScene;
	private final int MENU_START = 0;
	private final int MENU_HIGH_SCORES = 1;
	private final int MENU_BACK = 2;
	
	@Override
	public void createScene() {
		createBackground();
		
		menuChildScene = new MenuScene(camera);
		menuChildScene.setPosition(0, 0);
		
		IMenuItem startItem = new ScaleMenuItemDecorator(new SpriteMenuItem(
				MENU_START, resourcesManager.getSingleButtonRegion(), vbom),
				1.2f, 1);
		IMenuItem highScoresItem = new ScaleMenuItemDecorator(new SpriteMenuItem(
				MENU_HIGH_SCORES, resourcesManager.getMultiButtonRegion(), vbom),
				1.2f, 1);
		IMenuItem backItem = new ScaleMenuItemDecorator(new SpriteMenuItem(
				MENU_BACK, resourcesManager.getSingleButtonRegion(), vbom),
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

	@Override
	public void onBackKeyPressed() {
		SceneManager.getInstance().backToMenu();
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_SINGLEPLAYER_OPTIONS;
	}

	@Override
	public void disposeScene() {
		detachSelf();
		dispose();
	}

	private void createBackground() {
		setBackground(new Background(Color.BLACK));
	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		switch (pMenuItem.getID()) {
		case MENU_START:
			SceneManager.getInstance().loadGameScene(engine);
			return true;
		case MENU_HIGH_SCORES:
			SceneManager.getInstance().loadHighScoresScene(engine);
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
