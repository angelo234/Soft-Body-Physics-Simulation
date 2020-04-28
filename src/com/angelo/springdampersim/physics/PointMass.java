package com.angelo.springdampersim.physics;

import java.awt.Color;
import java.awt.Graphics2D;

public class PointMass extends PhysicsObject{

	public static final double LENGTH = 0.2;
	public static double MASS = 0.001;
	
	public PointMass(double x, double y) {
		super(x, y, LENGTH, MASS);
	}
	
	@Override
	public void render(Graphics2D g2) {
		g2.setColor(Color.GRAY);
		
		if(this.objectContacted != null) {
			g2.setColor(Color.MAGENTA);
		}
		g2.fill(shape);
		
	}

}
