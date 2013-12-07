package com.project.harbinger.scene;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.color.Color;

import com.project.harbinger.manager.ResourcesManager;
import com.project.harbinger.manager.SceneManager.SceneType;
import com.project.harbinger.multiplayer.GameObjectInformation;

public class MultiplayerClientGameScene extends BaseScene {

	List<Sprite> objectsToRender;
	
	@Override
	public void createScene() {
		createBackground();
		
		objectsToRender = new ArrayList<Sprite>();
	}

	@Override
	public void onBackKeyPressed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_MULTIPLAYER_CILENT_GAME;
	}

	@Override
	public void disposeScene() {
		// TODO Auto-generated method stub
		
	}
	
	public void renderObjects(List<GameObjectInformation> objectsInformation) {
		Iterator<Sprite> it = objectsToRender.iterator();
		
		while (it.hasNext()) {
			Sprite next = it.next();
			detachChild(next);
			objectsToRender.remove(next);
		}
		
		for (GameObjectInformation goi : objectsInformation) {
			Sprite next;
			switch (goi.getType()) {
			case PLAYER1:
				next = new Sprite(goi.getX(), goi.getY(), ResourcesManager.getInstance().getPlayerRegion(), vbom);
				break;
			case PLAYER2:
				next = new Sprite(goi.getX(), goi.getY(), ResourcesManager.getInstance().getPlayerRegion(), vbom);
				break;
			case BULLET:
				next = new Sprite(goi.getX(), goi.getY(), ResourcesManager.getInstance().getBulletRegion(), vbom);
				break;
			case CRUISER:
				next = new Sprite(goi.getX(), goi.getY(), ResourcesManager.getInstance().getCruiserRegion(), vbom);
				break;
			case HEAVY_FIGHTER:
				next = new Sprite(goi.getX(), goi.getY(), ResourcesManager.getInstance().getHeavyFighterRegion(), vbom);
				break;
			case LIGHT_FIGHTER:
				next = new Sprite(goi.getX(), goi.getY(), ResourcesManager.getInstance().getLightFighterRegion(), vbom);
				break;
			case METEOR:
				next = new Sprite(goi.getX(), goi.getY(), ResourcesManager.getInstance().getMeteorRegion(), vbom);
				break;
			case MISSILE:
				next = new Sprite(goi.getX(), goi.getY(), ResourcesManager.getInstance().getMissileRegion(), vbom);
				break;
			default:
				next = null;
				break;
			}
			
			objectsToRender.add(next);
			attachChild(next);
		}
	}
	
	private void createBackground() {
		setBackground(new Background(Color.BLACK));
	}

}
