package com.project.harbinger.multiplayer;

public class GameObjectInformation implements java.io.Serializable {

	private ObjectType type;
	private float x, y;
	
	public GameObjectInformation(ObjectType type, float x, float y) {
		this.type = type;
		this.x = x;
		this.y = y;
	}
	
	public ObjectType getType() {
		return type;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public enum ObjectType {
		PLAYER1, PLAYER2, BULLET, CRUISER, HEAVY_FIGHTER, LIGHT_FIGHTER, METEOR, MISSILE
	}
}
