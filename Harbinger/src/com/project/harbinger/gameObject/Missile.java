package com.project.harbinger.gameObject;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.project.harbinger.manager.ResourcesManager;

public class Missile extends Sprite {
	
	public static final String MISSILE_USER_DATA = "missile";
	
	private Body body;
	private MissileType type;
	
	public Missile(float pX, float pY, VertexBufferObjectManager vbo, Camera camera, PhysicsWorld physicsWorld,
			MissileType type) {
        super(pX, pY, ResourcesManager.getInstance().getMissileRegion(), vbo);
        
        this.type = type;
        
        createPhysics(camera, physicsWorld);
    }
	
	private void createPhysics(final Camera camera, PhysicsWorld physicsWorld) {
		body = PhysicsFactory.createBoxBody(physicsWorld, 
				this, BodyType.KinematicBody, PhysicsFactory.createFixtureDef(0, 0, 0));
		
		body.setUserData(MISSILE_USER_DATA);
		body.setFixedRotation(true);
		body.setBullet(true);
		if (type == MissileType.PLAYER) {
			body.setLinearVelocity(0, -10);
		} else {
			body.setLinearVelocity(0, 10);
		}
		
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false) {
	        @Override
	        public void onUpdate(float pSecondsElapsed) {
	            super.onUpdate(pSecondsElapsed);
	        }
	    });
	}
	
	public enum MissileType { PLAYER, ENEMY };
}
