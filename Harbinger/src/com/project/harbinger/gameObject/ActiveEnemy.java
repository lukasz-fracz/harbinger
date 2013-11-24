package com.project.harbinger.gameObject;

import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;


public class ActiveEnemy extends Enemy {
	
	public static final String ACTIVE_USER_DATA = "active";
	public static final String ACTIVE_START_ME = "hmm";
	public static final String ACTIVE_TURN = "turn!";
	
	protected float xVelocity;
	protected float yVelocity;
	protected ActiveEnemyType type;
	protected boolean allowToShoot;
	protected int updatesCounter;

	public ActiveEnemy(float pX, float pY, ITextureRegion region, VertexBufferObjectManager vbo) {
		super(pX, pY, region, vbo);
		allowToShoot = false;
		updatesCounter = 0;
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
