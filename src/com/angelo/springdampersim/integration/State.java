package com.angelo.springdampersim.integration;

import javax.vecmath.Vector2d;

public class State {

	public Vector2d position = new Vector2d();
	public Vector2d velocity = new Vector2d();
	
	
	public State(double x, double y) {
		this.position.x = x;
		this.position.y = y;
	}
	
	
}
