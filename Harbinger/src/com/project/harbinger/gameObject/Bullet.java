package com.project.harbinger.gameObject;

import org.andengine.engine.camera.Camera;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.project.harbinger.manager.ResourcesManager;

public class Bullet extends GameObject {
	
	public static final String BULLET_USER_DATA = "bullet";

	public Bullet(float pX, float pY, VertexBufferObjectManager vbo, Camera camera, PhysicsWorld physicsWorld) {
        super(pX, pY, ResourcesManager.getInstance().getBulletRegion(), vbo);
        
        score = 2;
        createPhysics(camera, physicsWorld);
    }
	
	private void createPhysics(final Camera camera, PhysicsWorld physicsWorld) {
		body = PhysicsFactory.createBoxBody(physicsWorld, 
				this, BodyType.KinematicBody, PhysicsFactory.createFixtureDef(0, 0, 0));
		
		body.setUserData(BULLET_USER_DATA);
		body.setFixedRotation(true);
		body.setLinearVelocity(0, 10);
		
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false) {
	        @Override
	        public void onUpdate(float pSecondsElapsed) {
	            super.onUpdate(pSecondsElapsed);
	        }
	    });
	}
}