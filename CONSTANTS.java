package com.example.particlesimulation;

public final class CONSTANTS
{
    static Vector2 GRAVITY = new Vector2(0, 0.05); // 0.05 works the best
    static double COLLISION_DAMPING = 0.8;
    static double INTER_PARTICLE_COLLISION_DAMPING = 0.99;
    static double RADIUS_TO_MASS = 0.1; // Radius * RTM = mass
}
