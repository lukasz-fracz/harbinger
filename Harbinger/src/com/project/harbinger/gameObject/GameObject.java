package com.project.harbinger.gameObject;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.Body;

public class GameObject extends Sprite {

	public static final String DESTROY_USER_DATA = "destroy";
	public static final String DESTROY_BY_WALL_USER_DATA = "destroy1";
	
	protected Body body;
	protected int score;
	
	public GameObject(float pX, float pY, ITextureRegion region, VertexBufferObjectManager vbo) {
		super(pX, pY, region, vbo);
	}
	
	public Body getBody() {
		return body;
	}
	
	public int getScore() {
		return score;
	}
}
