package com.project.harbinger.gameObject;

import org.andengine.engine.camera.Camera;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.project.harbinger.manager.ResourcesManager;

public class Missile extends GameObject {
	
	public static final String MISSILE_USER_DATA = "missile";
	public static final String MISSILE_PLAYER2_USER_DATA = "missile2";

	private MissileType type;
	
	public Missile(float pX, float pY, VertexBufferObjectManager vbo, Camera camera, PhysicsWorld physicsWorld,
			MissileType type, int id) {
        super(pX, pY, ResourcesManager.getInstance().getMissileRegion(), vbo, id);
        
        this.type = type;
        createPhysics(camera, physicsWorld);
    }
	
	private void createPhysics(final Camera camera, PhysicsWorld physicsWorld) {
		body = PhysicsFactory.createBoxBody(physicsWorld, 
				this, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(0, 0, 0));
		
		if (type == MissileType.PLAYER2) {
			body.setUserData(MISSILE_PLAYER2_USER_DATA);
		} else {
			body.setUserData(MISSILE_USER_DATA);
		}
		body.setFixedRotation(true);
		if (type == MissileType.PLAYER1 || type == MissileType.PLAYER2) {
			body.setLinearVelocity(0, -15);
		} else {
			body.setLinearVelocity(0, 15);
		}
		
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false) {
	        @Override
	        public void onUpdate(float pSecondsElapsed) {
	            super.onUpdate(pSecondsElapsed);
	        }
	    });
	}
	
	public enum MissileType { PLAYER1, PLAYER2, ENEMY }
}
