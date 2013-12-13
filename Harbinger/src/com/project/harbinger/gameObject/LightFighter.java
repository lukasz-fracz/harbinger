package com.project.harbinger.gameObject;

import org.andengine.engine.camera.Camera;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.project.harbinger.manager.ResourcesManager;

public class LightFighter extends ActiveEnemy {

	public LightFighter(float pX, float pY, VertexBufferObjectManager vbo, Camera camera, PhysicsWorld physicsWorld,
			ActiveEnemyType type, int id) {
        super(pX, pY, ResourcesManager.getInstance().getLightFighterRegion(), vbo, id);
        
        score = 3;
        this.type = type;
        
        yVelocity = 5f;
        xVelocity = 0;
        createPhysics(camera, physicsWorld);
    }
	
	private void createPhysics(final Camera camera, PhysicsWorld physicsWorld) {
		body = PhysicsFactory.createBoxBody(physicsWorld, 
				this, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(0, 0, 0));
		
		body.setUserData(ACTIVE_USER_DATA);
		body.setFixedRotation(true);
		body.setLinearVelocity(xVelocity, yVelocity);
		
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false) {
	        @Override
	        public void onUpdate(float pSecondsElapsed) {
	            super.onUpdate(pSecondsElapsed);
	        }
	    });
	}
}
