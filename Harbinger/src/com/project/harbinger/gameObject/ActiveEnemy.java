package com.project.harbinger.gameObject;

import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.project.harbinger.scene.GameScene;


public class ActiveEnemy extends Enemy {
	
	public static final String ACTIVE_USER_DATA = "active";
	public static final String ACTIVE_START_ME = "hmm";
	public static final String ACTIVE_TURN = "turn!";
	
	float xVelocity;
	float yVelocity;
	ActiveEnemyType type;
	boolean allowToShoot;
	int updatesCounter;
	GameScene gameScene;

	public ActiveEnemy(float pX, float pY, ITextureRegion region, VertexBufferObjectManager vbo, int id, GameScene gameScene) {
		super(pX, pY, region, vbo, id);
		allowToShoot = false;
		updatesCounter = 0;
		
		this.gameScene = gameScene;
	}
	
	public void changeSide() {
		xVelocity *= -1f;
		body.setLinearVelocity(xVelocity, yVelocity);
		body.setUserData(ACTIVE_USER_DATA);
	}
	
	public void start() {
		if (type == ActiveEnemyType.LEFT) {
        	xVelocity = 5f;
        } else {
        	xVelocity = -5f;
        }
		
		body.setLinearVelocity(xVelocity, yVelocity);
		
		body.setUserData(ACTIVE_USER_DATA);
		
		allowToShoot = true;
	}
	
	public enum ActiveEnemyType { LEFT, RIGHT }
}
