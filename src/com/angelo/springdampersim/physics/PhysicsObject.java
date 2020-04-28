package com.angelo.springdampersim.physics;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;

import javax.vecmath.Vector2d;

import com.angelo.springdampersim.Display;
import com.angelo.springdampersim.IRendering;
import com.angelo.springdampersim.integration.Integrator;
import com.angelo.springdampersim.integration.State;

public class PhysicsObject implements IRendering{

	public boolean isRectangle;
	public State state;
	public Vector2d acceleration;
	public Shape shape;
	public Vector2d force;
	public double mass;
	public double width;
	public double height;
	public double rotation;
	public PhysicsObject objectContacted;
	public boolean isStatic = false;
	
	//Init Circle
	public PhysicsObject(double x, double y, double diameter, double mass) {
		state = new State(x, y);
		shape = new Ellipse2D.Double(x * Display.PIXELS_PER_METER, -y * Display.PIXELS_PER_METER, diameter * Display.PIXELS_PER_METER, diameter * Display.PIXELS_PER_METER);
		acceleration = new Vector2d();
		force = new Vector2d();
		this.mass = mass;
		this.width = diameter;
		this.height = diameter;
		
		isRectangle = false;
		
	}
	
	//Init Rectangle
	public PhysicsObject(double x, double y, double width, double height, double mass, double rotation) {
		state = new State(x, y);
		shape = new Rectangle2D.Double(x * Display.PIXELS_PER_METER, -y * Display.PIXELS_PER_METER, width * Display.PIXELS_PER_METER, height * Display.PIXELS_PER_METER);

		acceleration = new Vector2d();
		force = new Vector2d();
		this.mass = mass;
		this.width = width;
		this.height = height;
		this.rotation = rotation;
		
		isRectangle = true;
	}
	
	public void setPosition(double x, double y) {
		setPosition(new Vector2d(x, y));
	}
	
	public void setPosition(Vector2d pos) {
		state.position.set(pos.x, pos.y);
		
		if(isRectangle) {
			shape = new Rectangle2D.Double(pos.x * Display.PIXELS_PER_METER, -pos.y * Display.PIXELS_PER_METER, width * Display.PIXELS_PER_METER, height * Display.PIXELS_PER_METER);
		}
		else {
			shape = new Ellipse2D.Double(pos.x * Display.PIXELS_PER_METER, -pos.y * Display.PIXELS_PER_METER, width * Display.PIXELS_PER_METER, height * Display.PIXELS_PER_METER);
		}
		
		//shape.setFrame(x * Display.PIXELS_PER_METER, -y * Display.PIXELS_PER_METER, width * Display.PIXELS_PER_METER, height * Display.PIXELS_PER_METER);	
	}
	
	public void addPosition(double x, double y) {
		addPosition(new Vector2d(x, y));
	}
	
	public void addPosition(Vector2d delta) {
		state.position.add(delta);
		
		if(isRectangle) {
			shape = new Rectangle2D.Double(state.position.x * Display.PIXELS_PER_METER, -state.position.y * Display.PIXELS_PER_METER, width * Display.PIXELS_PER_METER, height * Display.PIXELS_PER_METER);
		}
		else {
			shape = new Ellipse2D.Double(state.position.x * Display.PIXELS_PER_METER, -state.position.y * Display.PIXELS_PER_METER, width * Display.PIXELS_PER_METER, height * Display.PIXELS_PER_METER);
		}
		
		//shape = new Ellipse2D.Double(x * Display.PIXELS_PER_METER, -y * Display.PIXELS_PER_METER, width * Display.PIXELS_PER_METER, height * Display.PIXELS_PER_METER);
		//shape.setFrame(state.position.x * Display.PIXELS_PER_METER, -state.position.y * Display.PIXELS_PER_METER, width * Display.PIXELS_PER_METER, height * Display.PIXELS_PER_METER);	
	}
	
	//Only gets called when (isGrounded == true)
	public void updateFriction(double delta, double timeElapsed) {
		//Weight + force on it = load
		Vector2d load = new Vector2d();
		
		int numOfGroundedMass = 0;
		
		for(PhysicsObject physicsObject : Physics.physicsObjects) {
			if(physicsObject instanceof PointMass) {
				if(physicsObject.objectContacted == null) {
					PointMass pointMass = (PointMass) physicsObject;
					
					load.add(pointMass.force);
					load.setY(load.getY() + pointMass.mass * Physics.GRAVITY);
				}
				else {
					numOfGroundedMass++;
				}
				
			}
			
		}
		
		Vector2d n = new Vector2d(Math.sin(Math.toRadians(objectContacted.rotation)), Math.cos(Math.toRadians(objectContacted.rotation)));
		
		//System.out.println(load.toString());
		
		load.scale(1.0 / numOfGroundedMass);
		
		//System.out.println(load.toString());
		
		load.setY(load.getY() + this.mass * Physics.GRAVITY);
		
		double sign = state.velocity.x >= 0 ? -1 : 1;
		
		Vector2d friction = new Vector2d(n.y * sign, n.x);
		
		friction.normalize();
		
		friction.scale(0.9 * load.length());

		force.add(friction);
		
		
		translateObject(delta, timeElapsed);
	}
	
	public void updatePhysics(double delta, double timeElapsed) {
		if(isStatic) {
			state.velocity.set(0, 0);
			acceleration.set(0, 0);
			force.set(0, 0);
			
			setPosition(state.position.x, state.position.y);
		}
		else {
			checkCollision();
			
			force.setY(force.getY() + Physics.GRAVITY * mass);
			
			if(objectContacted == null) {
				translateObject(delta, timeElapsed);
			}
			
		}
	}
	
	public GeneralPath getCollisionBox() {
		PathIterator pathIterator = shape.getPathIterator(AffineTransform.getRotateInstance(Math.toRadians(rotation)));
		
		GeneralPath path = new GeneralPath();
		path.append(pathIterator, true);

		return path;
	}
	
	public Vector2d vector = new Vector2d();
	
	private void checkCollision() {
		for(PhysicsObject physicsObject : Physics.physicsObjects) {
			if(physicsObject instanceof Rectangle) {
				Rectangle rectangle = (Rectangle) physicsObject;

				GeneralPath collisionBox = rectangle.getCollisionBox();
				
				if(collisionBox.intersects(shape.getBounds2D())) {
					
					objectContacted = rectangle;
					
					Vector2d n = new Vector2d(Math.sin(Math.toRadians(rectangle.rotation)), Math.cos(Math.toRadians(rectangle.rotation)));
					
					Vector2d rv = new Vector2d(state.velocity);
					rv.sub(rectangle.state.velocity);
					
					double velAlongNormal = rv.dot(n);

					if(velAlongNormal > 0) {
						return;
					}
					
					double e = 0.5;
					
					double j = -(1 + e) * velAlongNormal;
					
					j /= 1 / this.mass + 1 / rectangle.mass;
					
					Vector2d impulse = new Vector2d(n);
					impulse.scale(j);
					
					Vector2d a = new Vector2d(impulse);
					
					a.scale(1.0 / this.mass);
					
					this.state.velocity.add(a);
					
					//float percent = 0.2; // usually 20% to 80%
					//float slop = 0.01; // usually 0.01 to 0.1
					//Vec2 correction = max( penetration - slop, 0.0f ) / (A.inv_mass + B.inv_mass)) * percent * n
					//A.position -= A.inv_mass * correction
					//B.position += B.inv_mass * correction		  
				}	
				else {
					objectContacted = null;
				}
			}
		}
		
		
	}
	
	private void translateObject(double delta, double timeElapsed) {
		Vector2d acceleration = new Vector2d(force);	
		
		acceleration.scale(1.0 / mass);
		this.acceleration = acceleration;
		
		Integrator.RK4(state, acceleration, timeElapsed, delta);
		
		this.setPosition(state.position.x, state.position.y);	
	}

	@Override
	public void render(Graphics2D g2) {}
	
}
