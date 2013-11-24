package com.project.harbinger.gameObject;

import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Enemy extends GameObject {
	
	public Enemy(float pX, float pY, ITextureRegion region, VertexBufferObjectManager vbo) {
		super(pX, pY, region, vbo);
	}
}
