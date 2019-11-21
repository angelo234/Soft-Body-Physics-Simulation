package com.angelo.springdampersim;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;

import javax.vecmath.Vector2d;

import com.angelo.springdampersim.physics.Physics;
import com.angelo.springdampersim.physics.PhysicsObject;
import com.angelo.springdampersim.physics.PointMass;

public class Input implements KeyListener, MouseListener, MouseMotionListener{
	
	public static boolean keys[] = new boolean[65536];	
	public static boolean isCursorHoldingPointMass = false;
	
	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		keys[e.getKeyCode()] = true;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keys[e.getKeyCode()] = false;
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public static Vector2d dirFromCursor = new Vector2d();
	public static PhysicsObject physicsObjectHeld = null;
	public static boolean isObjectStatic = false;
	
	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1) {
			if(!isCursorHoldingPointMass) {
				//If cursor is inside cube
				for(PhysicsObject physicsObject : Physics.physicsObjects) {
					GeneralPath path = physicsObject.getCollisionBox();
					
					if(path.contains(e.getPoint())) {						
						dirFromCursor.set(e.getPoint().x - physicsObject.shape.getBounds2D().getX(), e.getPoint().y - physicsObject.shape.getBounds2D().getY());
						
						physicsObjectHeld = physicsObject;
						isCursorHoldingPointMass = true;
						isObjectStatic = physicsObject.isStatic;
					}
				}	
			}
		}
		
		if(e.getButton() == MouseEvent.BUTTON2) {
			if(physicsObjectHeld != null) {
				physicsObjectHeld.isStatic = true;
				isCursorHoldingPointMass = false;
				physicsObjectHeld = null;
			}
			else {
				for(PhysicsObject pointMass : Physics.physicsObjects) {
					if(pointMass.shape.contains(e.getPoint())) {					
						pointMass.isStatic = false;				
					}
				}
			}
			
		}
	}

	boolean mouseReleased = false;
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1) {
			if(isCursorHoldingPointMass) {
				isCursorHoldingPointMass = false;
				physicsObjectHeld.isStatic = isObjectStatic;
				physicsObjectHeld = null;
				
				isObjectStatic = false;
			}
			
		}		
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {

		if(true) {	
			if(isCursorHoldingPointMass) {
				double x = e.getPoint().x - dirFromCursor.x;
				double y = e.getPoint().y - dirFromCursor.y;
				
				physicsObjectHeld.state.position.set(x / 100.0, -y / 100.0);
				physicsObjectHeld.isStatic = true;
				
				//pointMassHeld.velocity.set(0, 0);
				//pointMassHeld.acceleration.set(0, 0);
				//pointMassHeld.shape.setFrame(x, y, PointMass.LENGTH * Display.PIXELS_PER_METER, PointMass.LENGTH * Display.PIXELS_PER_METER);
	    	}	
		}
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		
		
	}

}
