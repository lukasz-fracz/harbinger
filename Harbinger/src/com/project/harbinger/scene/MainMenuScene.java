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

public class MainMenuScene extends BaseScene implements IOnMenuItemClickListener {

	private MenuScene menuChildScene;
	private final int MENU_SINGLE = 0;
	private final int MENU_MULTI = 1;
	
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
		
		singleItem.setPosition(singleItem.getX(), singleItem.getY() + 10);
		multiItem.setPosition(multiItem.getX(), multiItem.getY() + 10);
		
		menuChildScene.setOnMenuItemClickListener(this);
		
		setChildScene(menuChildScene);
	}
	
	@Override
	public void createScene() {
		createBackground();
		createMenuChildScene();
	}

	@Override
	public void onBackKeyPressed() {
		System.exit(0);
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_MENU;
	}

	@Override
	public void disposeScene() {
		// TODO Auto-generated method stub
		
	}
	
	private void createBackground() {
		attachChild(new Sprite(0, 0, resourcesManager.getMenuBackgroundRegion(), vbom){
			
			protected void preDraw(GLState pGLState, Camera pCamera) {
	            super.preDraw(pGLState, pCamera);
	            pGLState.enableDither();
	        }
		});
	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		switch (pMenuItem.getID()) {
		case MENU_SINGLE:
			SceneManager.getInstance().loadGameScene(engine);
			return true;
		case MENU_MULTI:
			return true;
		default:
			return false;
		}
	}

}
