package com.project.harbinger.scene;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

import com.project.harbinger.manager.ResourcesManager;
import com.project.harbinger.manager.SceneManager.SceneType;
import com.project.harbinger.multiplayer.GameObjectInformation;

public class MultiplayerClientGameScene extends BaseScene {

	List<Sprite> objectsToRender, objectsToDetach;
	
	@Override
	public void createScene() {
		createBackground();
		
		objectsToRender = new ArrayList<Sprite>();
		objectsToDetach = new ArrayList<Sprite>();
		
		registerUpdateHandler(createServerUpdateHandler());
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
	
	private IUpdateHandler createServerUpdateHandler() {
		IUpdateHandler iUpdateHandler = new IUpdateHandler() {

			float x = 0;
			float a = 1f / 25f;
			
			@Override
			public void onUpdate(float pSecondsElapsed) {
				x += pSecondsElapsed;
				if (x >= a) {
					for (Sprite sprite : objectsToDetach) {
						detachChild(sprite);
					}
					
					objectsToDetach = new ArrayList<Sprite>();
					x = 0;
				}	
			}

			@Override
			public void reset() {
			}
			
		};
		
		return iUpdateHandler;
	}
	
	public void renderObjects(List<GameObjectInformation> objectsInformation) {
		for (Sprite sprite : objectsToRender) {
			objectsToDetach.add(sprite);
		}
		
		objectsToRender = new ArrayList<Sprite>();
		
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
