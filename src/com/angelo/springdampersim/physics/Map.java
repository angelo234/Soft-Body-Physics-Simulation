package com.angelo.springdampersim.physics;

import java.awt.geom.Line2D;

import javax.vecmath.Point2d;

import com.angelo.springdampersim.Display;

public class Map {

	public Map() {
		
		//generateSquare(1.5, 0, 3, 5, 5, 100, 2);
		
		generateCircle(2, -1, 1, 20, 50, 40, 0.25);
		
		Physics.physicsObjects.add(new Rectangle(0, -7, 8, 1, 5, 0));
		//Physics.physicsObjects.add(new Rectangle(5, -7, 3, 1, 5, 0));
	}
	
	private void generateCircle(double xOffset, double yOffset, double radius, double mass, double numOfPointMass, double k, double b) {
		PointMass.MASS = mass / numOfPointMass;
		double restSpringLengthDiff = 0;
		
		double radiansBetweenPointMasses = 2 * Math.PI / numOfPointMass;
		
		for(int i = 0; i < numOfPointMass; i++) {
			double angle = radiansBetweenPointMasses * i;
			
			double x = Math.cos(angle) * radius + xOffset;
			double y = -Math.sin(angle) * radius + yOffset;
			
			Physics.physicsObjects.add(new PointMass(x, y));
		}
		
		for(int i = 0; i < Physics.physicsObjects.size(); i++) {
			if(Physics.physicsObjects.get(i) instanceof PointMass) {
				PointMass pointMass = (PointMass) Physics.physicsObjects.get(i);
				
				for(PhysicsObject otherPointMass : Physics.physicsObjects) {
					if(otherPointMass == pointMass || !(Physics.physicsObjects.get(i) instanceof PointMass)) {
						continue;
					}
								
					Point2d relativePoint = new Point2d(PointMass.LENGTH / 2, -PointMass.LENGTH / 2);
					
					Point2d actualPos1 = new Point2d(pointMass.state.position);
					actualPos1.add(relativePoint);
					
					Point2d actualPos2 = new Point2d(otherPointMass.state.position);
					actualPos2.add(relativePoint);
					
					Physics.springs.add(new Spring(pointMass, (PointMass)otherPointMass, null, k, b, actualPos1.distance(actualPos2) + restSpringLengthDiff));
				}
			}	
		}
		
	}
	
	private void generateSquare(double xOffset, double yOffset, double size, double mass, double pointMassPerRowColumn, double k, double b) {
		int numpointMassesPerRowColumn = 5;
		int numSpringsPerRowColumn = numpointMassesPerRowColumn - 1;
		PointMass.MASS = mass / (numpointMassesPerRowColumn * numpointMassesPerRowColumn);
					
		double sLength = size / numSpringsPerRowColumn;
		double restSpringLengthDiff = 0;
		
		for(int j = 0; j < numpointMassesPerRowColumn; j++) {
			for(int i = 0; i < numpointMassesPerRowColumn; i++) {
				double x = i * sLength + xOffset;
				double y = -j * sLength + yOffset;
				
				Physics.physicsObjects.add(new PointMass(x, y));
			}
		}
		
		System.out.println("Generated "+numpointMassesPerRowColumn * numpointMassesPerRowColumn+" Point Masses");
		
		int springs = 0;
		
		for(int i = 0; i < Physics.physicsObjects.size(); i++) {
			if(Physics.physicsObjects.get(i) instanceof PointMass) {
				PointMass pointMass = (PointMass) Physics.physicsObjects.get(i);
				
				int springsPerCube = 0;
				
				//Shoot raycast to check for adjacent pointMasses
				
				for(double angle = 135; angle <= 270; angle += 45) {
					double centerX = pointMass.shape.getBounds2D().getCenterX();
					double centerY = pointMass.shape.getBounds2D().getCenterY();
					
					Line2D raycast = new Line2D.Double(centerX, centerY, Math.cos(Math.toRadians(angle)) * (Math.sqrt(sLength * sLength + sLength * sLength )) * Display.PIXELS_PER_METER + centerX, Math.sin(Math.toRadians(angle)) *(Math.sqrt(sLength * sLength + sLength * sLength )) * Display.PIXELS_PER_METER + centerY);
					
					for(PhysicsObject otherPointMass : Physics.physicsObjects) {
						if(otherPointMass == pointMass || !(Physics.physicsObjects.get(i) instanceof PointMass)) {
							continue;
						}
									
						if(raycast.intersects(otherPointMass.shape.getBounds2D())) {
							
							Point2d relativePoint = new Point2d(PointMass.LENGTH / 2, -PointMass.LENGTH / 2);
							
							Point2d actualPos1 = new Point2d(pointMass.state.position);
							actualPos1.add(relativePoint);
							
							Point2d actualPos2 = new Point2d(otherPointMass.state.position);
							actualPos2.add(relativePoint);
							
							Physics.springs.add(new Spring(pointMass, (PointMass)otherPointMass, null, k, b, actualPos1.distance(actualPos2) + restSpringLengthDiff));
							
							springs++;
							springsPerCube++;
						}
					}
				}
				//System.out.println(springsPerCube);
			}	
		}
		
		System.out.println("Generated "+springs+" springs");
		
		//Physics.physicsObjects.add(new PointMass(4, -4));
		
		//Physics.physicsObjects.add(new Rectangle(2, -8.5, 8, 1, 10, -10));
		
		//springs.add(new Spring(pointMasses.get(0), null, new Point2d(3.5 + Cube.LENGTH / 2, 0), new Point2d(Cube.LENGTH / 2, 0), null, 1000, 10, 2));
		
	}
}
