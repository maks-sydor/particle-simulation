package com.example.particlesimulation;

import java.util.Objects;

public class ParticleUpdateHandler
{
    final int CYCLES = 1;

    Vector2 boxStart;
    Vector2 boxEnd;

    public ParticleUpdateHandler(Vector2 boxStart, Vector2 boxEnd)
    {
        this.boxStart = boxStart;
        this.boxEnd = boxEnd;
    }

    public Particle[] particles = {
            new Particle(new Vector2(100, 100), 1, 10),
            new Particle(new Vector2(200, 150), 1, 15),
            new Particle(new Vector2(400, 200), 1, 50, true),
            new Particle(new Vector2(150, 250), 1, 12),
            new Particle(new Vector2(250, 300), 1, 18)
    };

    public Wall[] walls = {
            //new Wall(10, 400, 450, 450), // Bottom line wall
            new Wall(150, 150, 350, 350) // Center square wall
    };

    public void RandomizeVelocities(float min, float max)
    {
        for (Particle p : particles)
        {
            p.vel = new Vector2().random(min, max);
        }
    }

    public void UpdateParticles(String MODE)
    {
        if (Objects.equals(MODE, "FAST")) UpdateParticlesFast();
        if (Objects.equals(MODE, "PRECISE")) UpdateParticlesPrecise();
    }

    public void UpdateParticlesFast()
    {
        for (Particle p : particles)
        {
            if (!p.isStatic)
            {
                p.vel = p.vel.add(CONSTANTS.GRAVITY.multiply(p.mass));
                p.pos = p.pos.add(p.vel);

                p.SolveInsideBoxCollision(boxStart, boxEnd);
                //p.SolveCircleCollision(new Vector2(60, 60), 100);

                for (Wall wall : walls)
                {
                    p.SolveOutsideBoxCollision(wall.startPos, wall.endPos);
                }
            }

            for (Particle p2 : particles)
            {
                if (p2 != p)
                {
                    p.SolveParticleCollision(p2);
                }
            }
        }
    }

    public void UpdateParticlesPrecise()
    {
        // Apply gravity for each particle
        for (Particle p : particles)
        {
            if (!p.isStatic)
            {
                p.vel = p.vel.add(CONSTANTS.GRAVITY.multiply(p.mass));
                p.pos = p.pos.add(p.vel);
            }
        }
        // Solve particle collisions for each particle "CYCLES" times
        for (int i = 0; i < CYCLES; i++)
        {
            for (Particle p : particles)
            {
                if (!p.isStatic)
                {
                    for (Particle p2 : particles)
                    {
                        if (p2 != p)
                        {
                            p.SolveParticleCollision(p2);
                        }
                    }
                }
            }
        }
        // Put all the particles in the bounds
        for (Particle p : particles)
        {
            if (!p.isStatic)
            {
                p.SolveInsideBoxCollision(boxStart, boxEnd);
            }
        }
    }

    public void HandleLMBClick(double x, double y)
    {
        double min = 10;
        double max = 10;
        double r = min + Math.random() * (max - min);
        Particle p = new Particle(new Vector2(x, y), 1, r);
        AddParticle(p);
    }

    public Object[] DeleteCircle(double x, double y)
    {
        int index = 0;
        boolean touchedAny = false;

        for (int i = 0; i < particles.length; i++)
        {
            if (particles[i].isTouchingPoint(new Vector2(x, y)))
            {
                particles = MyUtils.removeElement(particles, i);
                index = i;
                touchedAny = true;
                break;
            }
        }
        return new Object[]{index, touchedAny};
    }

    public Object[] DeleteWall(double x, double y)
    {
        int index = 0;
        boolean touchedAny = false;

        for (int i = 0; i < walls.length; i++)
        {
            if (walls[i].isTouchingPoint(new Vector2(x, y)))
            {
                walls = MyUtils.removeElement(walls, i);
                index = i;
                touchedAny = true;
                break;
            }
        }
        return new Object[]{index, touchedAny};
    }

    public int GetCircleIndex(double x, double y)
    {
        // Returns an index. -1 means "Didn't find any circles"
        int index = -1;

        for (int i = 0; i < particles.length; i++)
        {
            if (particles[i].isTouchingPoint(new Vector2(x, y)))
            {
                index = i;
                break;
            }
        }

        return index;
    }

    public void AddParticle(Particle particle)
    {
        //particle.AutoSetMass();
        Particle[] newParticles = new Particle[particles.length + 1];
        System.arraycopy(particles, 0, newParticles, 0, particles.length);
        newParticles[newParticles.length - 1] = particle;
        particles = newParticles;
    }

    public void AddWall(Wall wall)
    {
        Wall[] newWalls = new Wall[walls.length + 1];
        System.arraycopy(walls, 0, newWalls, 0, walls.length);
        newWalls[newWalls.length - 1] = wall;
        walls = newWalls;
    }
}
