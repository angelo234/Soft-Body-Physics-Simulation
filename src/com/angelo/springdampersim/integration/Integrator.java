package com.angelo.springdampersim.integration;

import javax.vecmath.Vector2d;

public class Integrator {

	public static void RK4(State state, Vector2d acceleration, double timeElapsed, double deltaTime) {
		Derivative a,b,c,d;

        a = evaluate(state, acceleration, timeElapsed, 0.0f, new Derivative());
        b = evaluate(state, acceleration, timeElapsed, deltaTime * 0.5f, a);
        c = evaluate(state, acceleration, timeElapsed, deltaTime * 0.5f, b);
        d = evaluate(state, acceleration, timeElapsed, deltaTime, c);

        Vector2d dxdt = new Vector2d();
        
        dxdt.setX(1.0f / 6.0f * ( a.dx.x + 2.0f * ( b.dx.x + c.dx.x ) + d.dx.x));
        dxdt.setY(1.0f / 6.0f * ( a.dx.y + 2.0f * ( b.dx.y + c.dx.y ) + d.dx.y));
        
        Vector2d dvdt = new Vector2d();
        
        dvdt.setX(1.0f / 6.0f * ( a.dv.x + 2.0f * ( b.dv.x + c.dv.x ) + d.dv.x));
        dvdt.setY(1.0f / 6.0f * ( a.dv.y + 2.0f * ( b.dv.y + c.dv.y ) + d.dv.y));
        
        dxdt.scale(deltaTime);
        dvdt.scale(deltaTime);
        
        state.position.add(dxdt);
        state.velocity.add(dvdt);
	}
	
	private static Derivative evaluate(State initialState, Vector2d acceleration, double timeElapsed, double deltaTime, Derivative derivative) {
		State state = new State(0, 0);
		
		state.position = new Vector2d(derivative.dx);
		state.position.scale(deltaTime);
		state.position.add(initialState.position);
		
		state.velocity = new Vector2d(derivative.dv);
		state.velocity.scale(deltaTime);
		state.velocity.add(initialState.velocity);

        Derivative output = new Derivative();
        output.dx = state.velocity;
        output.dv = acceleration;
        return output;
		
	}
}
