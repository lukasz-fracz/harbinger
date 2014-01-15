package com.project.harbinger.gameObject;

import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.project.harbinger.scene.GameScene;


/**
 * Class that represent active enemies. Active enemies are those enemies, that can shoot or turn.
 * 
 * @author Łukasz Frącz
 *
 */
public class ActiveEnemy extends Enemy {
	
	/**User data that identifies an active enemy*/
	public static final String ACTIVE_USER_DATA = "active";
	/**Active enemy needs to be activate. Activation means, it will start shooting and/or moving left/right*/
	public static final String ACTIVE_START_ME = "hmm";
	/**Enemy needs to turn. Use after colliding with wall or other enemy.*/
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
