package com.project.harbinger.gameObject;

import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class StaticEnemy extends Enemy {
	
	public static final String STATIC_USER_DATA = "static";

	public StaticEnemy(float pX, float pY, ITextureRegion region, VertexBufferObjectManager vbo, int id) {
		super(pX, pY, region, vbo, id);
	}	
}
