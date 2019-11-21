package com.angelo.springdampersim.physics;

import java.util.ArrayList;
import java.util.List;

public class Physics {

	public static final double GRAVITY = -9.81;
	
	public static List<PhysicsObject> physicsObjects = new ArrayList<PhysicsObject>();
	public static List<Spring> springs = new ArrayList<Spring>();
	
	public Physics() {		
		
			
	}
	
	public void calculateForces(double delta) {
		for(PhysicsObject physicsObject : physicsObjects) {
			physicsObject.force.set(0, 0);
		}	
		
		for(Spring spring : springs) {
			spring.calculateForces(delta);
		}
	}
	
	public void update(double delta, double timeElapsed) {	

		calculateForces(delta);

		for (PhysicsObject physicsObject : physicsObjects) {
			physicsObject.updatePhysics(delta, timeElapsed);	
		}		
		
		for (PhysicsObject physicsObject : physicsObjects) {
			if(physicsObject.objectContacted != null) {
				physicsObject.updateFriction(delta, timeElapsed);
			}
		}		
	}
	
}
