package com.project.harbinger.gameObject;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.Body;

public class GameObject extends Sprite {

	public static final String DESTROY_USER_DATA = "destroy";
	
	protected Body body;
	
	public GameObject(float pX, float pY, ITextureRegion region, VertexBufferObjectManager vbo) {
		super(pX, pY, region, vbo);
	}
	
	public Body getBody() {
		return body;
	}
}
