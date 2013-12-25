package com.project.harbinger.gameObject;

import org.andengine.engine.camera.Camera;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.project.harbinger.gameObject.Missile.MissileType;
import com.project.harbinger.manager.ResourcesManager;
import com.project.harbinger.scene.GameScene;

public class Player extends GameObject {

	public static final String PLAYER_USER_DATA = "player";
	public static final String PLAYER_IMMORTAL_DATA = "haha";
	
	private boolean fire, killed;
	private GameScene gameScene;
	
	public Player(float pX, float pY, VertexBufferObjectManager vbo, Camera camera, PhysicsWorld physicsWorld, GameScene gameScene) {
        super(pX, pY, ResourcesManager.getInstance().getPlayerRegion(), vbo, 1);
        createPhysics(camera, physicsWorld);
        
        this.gameScene = gameScene;
        
        fire = killed = false;
    }
	
	private void createPhysics(final Camera camera, PhysicsWorld physicsWorld) {
		body = PhysicsFactory.createBoxBody(physicsWorld, 
				this, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(0, 0, 0));
		
		body.setUserData(PLAYER_USER_DATA);
		body.setFixedRotation(true);
		
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false) {
	        
			float x = 0, y = 0;
			
			@Override
	        public void onUpdate(float pSecondsElapsed) {
	            super.onUpdate(pSecondsElapsed);
	            
	            x += pSecondsElapsed;
	            
	            if (fire) {
	            	if (x >= 0.5) {
	            		x = 0;
	            		gameScene.creteMissile(getX() + 20, getY() - 50, MissileType.PLAYER1);
	            		fire = false;
	            	}
	            }
	            if (killed) {
	            	y += pSecondsElapsed;
		            if (y >= 1.0) {
		            	setVisible(true);
		            	body.setUserData(PLAYER_USER_DATA);
		            	killed = false;
		            	y = 0;
		            }		            
	            }
	        }
	    });
	}
	
	public void setVelocity(float dx, float dy) {
		if (killed) {
			return;
		}
		body.setLinearVelocity(dx, dy);
	}
	
	public void fire() {
		fire = true;
	}
	
	public void setToImmortal() {
		body.setUserData(PLAYER_IMMORTAL_DATA);
		setVisible(false);
		killed = true;
		body.setLinearVelocity(0, 0);
	}
}
