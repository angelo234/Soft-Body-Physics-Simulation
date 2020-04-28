package com.angelo.springdampersim.physics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import com.angelo.springdampersim.Display;
import com.angelo.springdampersim.IRendering;

public class Spring implements IRendering{
	
	//1 is positive y side of spring
	//2 is negative y side of spring
	public PointMass pointMass1;
	public PointMass pointMass2;
	public Point2d fixedPoint;
	public double k;
	public double dampingRatio;
	public double restLength;
	public double x;
	public Vector2d oldX;
	
	public Spring(PointMass pointMass1, PointMass pointMass2, Point2d fixedPoint, double k, double dampingRatio, double restLength) {
		this.pointMass1 = pointMass1;
		this.pointMass2 = pointMass2;
		this.fixedPoint = fixedPoint;
		this.k = k;
		this.dampingRatio = dampingRatio;
		this.restLength = restLength;
		this.oldX = new Vector2d();
	}
	
	public void calculateForces(double delta) {
		if(connectedToFixedPoint()) {
			Point2d pointMass1Pos = new Point2d(pointMass1.state.position);
			pointMass1Pos.add(new Point2d(PointMass.LENGTH / 2, -PointMass.LENGTH / 2));
			
			//Vector from fixed point to cube point representing unstretched spring
			Vector2d unstretchedVec = new Vector2d(pointMass1Pos);
			unstretchedVec.sub(fixedPoint);
			unstretchedVec.normalize();	
			unstretchedVec.scale(restLength);
			
			//Vector from fixed point to cube representing spring
			Vector2d fixedPointToCube = new Vector2d(pointMass1Pos);		
			fixedPointToCube.sub(fixedPoint);
					
			//Vector representing hooke's law (F = k * x)
			Vector2d hookesLawVec = new Vector2d(unstretchedVec);
			hookesLawVec.sub(fixedPointToCube);	

			//Vector representing damping (B * v)
			Vector2d damper = new Vector2d(hookesLawVec);
			damper.sub(oldX);
			damper.scale(1.0 / delta);
			damper.scale(-dampingRatio);
			
			oldX = hookesLawVec;
			
			this.x = pointMass1Pos.distance(fixedPoint) - restLength;
			
			hookesLawVec.scale(k);
			
			//k * x + B * v
			hookesLawVec.add(damper);
			
			pointMass1.force.add(hookesLawVec);
		}
		else {
			Point2d pointMass1Pos = new Point2d(pointMass1.state.position);
			Point2d pointMass2Pos = new Point2d(pointMass2.state.position);
			pointMass1Pos.add(new Point2d(PointMass.LENGTH / 2, -PointMass.LENGTH / 2));
			pointMass2Pos.add(new Point2d(PointMass.LENGTH / 2, -PointMass.LENGTH / 2));
			
			//Vector representing unstretched spring
			Vector2d unstretchedVec = new Vector2d(pointMass2Pos);
			unstretchedVec.sub(pointMass1Pos);
			unstretchedVec.normalize();	
			unstretchedVec.scale(restLength);

			//Vector from point on cube 2 to cube 1 representing spring
			Vector2d cube2ToCube1 = new Vector2d(pointMass2Pos);		
			cube2ToCube1.sub(pointMass1Pos);	

			//Actual length subtracted by unstretched length
			Vector2d hookesLawVec = new Vector2d(cube2ToCube1);
			hookesLawVec.sub(unstretchedVec);				
			
			this.x = pointMass1Pos.distance(pointMass2Pos) - restLength;
			
			//Vector representing damping (B * v)
			Vector2d damper = new Vector2d(hookesLawVec);
			
			damper.sub(oldX);
			
			oldX = new Vector2d(hookesLawVec);
			
			//Displacement into Velocity
			damper.scale(1.0 / delta);	
			
			damper.scale(dampingRatio);
			
			//Vector representing hooke's law (F = k * x)
			hookesLawVec.scale(k);
			
			Vector2d hookesLawVec2 = new Vector2d(hookesLawVec);
			//k * x + B * v
			hookesLawVec.add(damper);				
			
			pointMass1.force.add(hookesLawVec);

			//k * x + B * v		
			hookesLawVec2.negate();	
			damper.negate();
			hookesLawVec2.add(damper);
			pointMass2.force.add(hookesLawVec2);	
			
		
		}		
	}
	
	public boolean connectedToFixedPoint() {
		return fixedPoint != null;
	}

	boolean renderSimpleSpring = true;
	
	@Override
	public void render(Graphics2D g2) {	
		if(this.x >= 0) {
			g2.setColor(Color.RED);
		}
		else {
			g2.setColor(Color.GREEN);
		}
		
		Point2d pointMass1Pos = new Point2d(pointMass1.state.position);
		pointMass1Pos.add(new Point2d(PointMass.LENGTH / 2, -PointMass.LENGTH / 2));
		
		double x1 = 0;
		double y1 = 0;
		
		if(connectedToFixedPoint()) {
			x1 = fixedPoint.x * Display.PIXELS_PER_METER;
			y1 = -fixedPoint.y * Display.PIXELS_PER_METER;
		}
		else {
			Point2d pointMass2Pos = new Point2d(pointMass2.state.position);		
			pointMass2Pos.add(new Point2d(PointMass.LENGTH / 2, -PointMass.LENGTH / 2));
			
			x1 = pointMass2Pos.x * Display.PIXELS_PER_METER;
			y1 = -pointMass2Pos.y * Display.PIXELS_PER_METER;
		}
		
		double x2 = pointMass1Pos.x * Display.PIXELS_PER_METER;
		double y2 = -pointMass1Pos.y * Display.PIXELS_PER_METER;
		double w = 15 * PointMass.LENGTH;
		int N = 10;
		
		if(renderSimpleSpring) {
			Line2D line = new Line2D.Double(x1, y1, x2, y2);
			
			g2.draw(line);
		}
		else {
			// vector increment
			double inv = 0.25 / N;
			double dx = (x2 - x1) * inv, dy = (y2 - y1) * inv;

			// perpendicular direction
			double inv2 = w / Math.sqrt(dx * dx + dy * dy);
			double px = dy * inv2, py = -dx * inv2;

			// loop
			double x = x1, y = y1;
			for (int i = 0; i < N; i++) {
				Line2D line1 = new Line2D.Double(x, y, x + dx + px, y + dy + py);
				Line2D line2 = new Line2D.Double(x + dx + px, y + dy + py, x + 3.0 * dx - px, y + 3.0 * dy - py);
				Line2D line3 = new Line2D.Double(x + 3.0 * dx - px, y + 3.0 * dy - py, x + 4.0 * dx, y + 4.0 * dy);

				g2.draw(line1);
				g2.draw(line2);
				g2.draw(line3);
				x += 4.0 * dx;
				y += 4.0 * dy;
			}

		}		
	}

}
