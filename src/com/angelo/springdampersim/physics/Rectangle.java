package com.angelo.springdampersim.physics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;

public class Rectangle extends PhysicsObject{

	public Rectangle(double x, double y, double width, double height, double mass, double rotation) {
		super(x, y, width, height, mass, rotation);
		this.isStatic = true;
	}
	
	@Override
	public void render(Graphics2D g2) {
		g2.setColor(Color.GRAY);
		
		PathIterator pathIterator = shape.getPathIterator(AffineTransform.getRotateInstance(Math.toRadians(rotation)));
		
		GeneralPath path = new GeneralPath();
		path.append(pathIterator, true);
		
		g2.fill(path);
	}
}
