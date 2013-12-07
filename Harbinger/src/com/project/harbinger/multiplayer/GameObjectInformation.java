package com.project.harbinger.multiplayer;

public class GameObjectInformation implements java.io.Serializable {

	private ObjectType type;
	private int x, y;
	
	public GameObjectInformation(ObjectType type, int x, int y) {
		this.type = type;
		this.x = x;
		this.y = y;
	}
	
	public ObjectType getType() {
		return type;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public enum ObjectType {
		PLAYER1, PLAYER2, BULLET, CRUISER, HEAVY_FIGHTER, LIGHT_FIGHTER, METEOR, MISSILE
	}
}
