package com.project.harbinger.gameObject;

import org.andengine.engine.camera.Camera;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.project.harbinger.manager.ResourcesManager;

public class LightFighter extends GameObject {
	
	public static final String LIGHT_FIGHTER_USER_DATA = "fighter";
	
	float xVelocity;
	float yVelocity;
	FighterType type;

	public LightFighter(float pX, float pY, VertexBufferObjectManager vbo, Camera camera, PhysicsWorld physicsWorld,
			FighterType type) {
        super(pX, pY, ResourcesManager.getInstance().getLightFighterRegion(), vbo);
        
        score = 3;
        this.type = type;
        
        if (type == FighterType.LEFT) {
        	xVelocity = 10f;
        } else {
        	xVelocity = -10f;
        }
        
        yVelocity = 5f;
        createPhysics(camera, physicsWorld);
    }
	
	private void createPhysics(final Camera camera, PhysicsWorld physicsWorld) {
		body = PhysicsFactory.createBoxBody(physicsWorld, 
				this, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(0, 0, 0));
		
		body.setUserData(LIGHT_FIGHTER_USER_DATA);
		body.setFixedRotation(true);
		body.setLinearVelocity(xVelocity, yVelocity);
		
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false) {
	        @Override
	        public void onUpdate(float pSecondsElapsed) {
	            super.onUpdate(pSecondsElapsed);
	        }
	    });
	}
	
	public void changeSide() {
		xVelocity *= -1f;
		body.setLinearVelocity(xVelocity, yVelocity);
	}
	
	public void start() {
		body.setLinearVelocity(xVelocity, yVelocity);
	}
	
	public enum FighterType { LEFT, RIGHT }
}
