package com.example.particlesimulation;

import javafx.scene.paint.Color;

import static com.example.particlesimulation.CONSTANTS.*;
import static com.example.particlesimulation.MyUtils.*;

public class Particle
{
    // Physics:
    Vector2 pos;
    Vector2 vel;
    double mass;
    double radius;
    boolean isStatic = false; // Does the particle stay in one place?


    // Color settings:
    boolean isRainbow = false;
    double hue = random(0, 360);
    double saturation = 1;
    double brightness = 1;

    public Particle()
    {
        this.pos = new Vector2(0, 0);
        this.vel = new Vector2(0, 0);
        this.mass = 1;
        this.radius = 10;
    }

    public Particle(Vector2 pos, double mass, double radius)
    {
        this.pos = pos;
        this.vel = new Vector2(0, 0);
        this.mass = mass;
        this.radius = radius;
    }

    public Particle(Vector2 pos, double mass, double radius, boolean isStatic)
    {
        this.pos = pos;
        this.vel = new Vector2(0, 0);
        this.mass = mass;
        this.radius = radius;
        this.isStatic = isStatic;
    }

    public Particle(Vector2 pos, Vector2 vel)
    {
        this.pos = pos;
        this.vel = vel;
        this.mass = 1;
        this.radius = 10;
    }

    public Particle(Vector2 pos, Vector2 vel, double mass, double radius)
    {
        this.pos = pos;
        this.vel = vel;
        this.mass = mass;
        this.radius = radius;
    }

    public void SolveInsideBoxCollision(Vector2 boxStart, Vector2 boxEnd)
    {
        // Vertical collision check
        if (this.pos.y + this.radius > boxEnd.y)
        {
            this.pos.y = boxEnd.y - this.radius - 0.1;
            this.vel.y *= -CONSTANTS.COLLISION_DAMPING;
        }
        if (this.pos.y - this.radius < boxStart.y)
        {
            this.pos.y = boxStart.y + this.radius;
            this.vel.y *= -CONSTANTS.COLLISION_DAMPING;
        }

        // Horizontal collision check
        if (this.pos.x + this.radius > boxEnd.x)
        {
            this.pos.x = boxEnd.x - this.radius;
            this.vel.x *= -CONSTANTS.COLLISION_DAMPING;
        }
        if (this.pos.x - this.radius < boxStart.x)
        {
            this.pos.x = boxStart.x + this.radius;
            this.vel.x *= -CONSTANTS.COLLISION_DAMPING;
        }
    }

    public void SolveOutsideBoxCollision(Vector2 boxStart, Vector2 boxEnd)
    {
        double bottomY = this.pos.y + this.radius;
        double topY = this.pos.y - this.radius;
        double rightX = this.pos.x + this.radius;
        double leftX = this.pos.x - this.radius;
        boolean tTopLeftC = isTouchingPoint(boxStart.x, boxStart.y);
        boolean tTopRightC = isTouchingPoint(boxEnd.x, boxStart.y);
        boolean tBottomRightC = isTouchingPoint(boxEnd.x, boxEnd.y);
        boolean tBottomLeftC = isTouchingPoint(boxStart.x, boxEnd.y);

        Vector2 boxCenter = boxStart.add(boxEnd).div(2);
        double xDiff = Math.abs(boxCenter.x - this.pos.x);
        double yDiff = Math.abs(boxCenter.y - this.pos.y);

        if (xDiff > yDiff) // If it is a collision with the sides
        {
            boolean tUpAndDown = bottomY > boxStart.y && topY < boxEnd.y;

            if (this.pos.x < boxCenter.x) // If the collision is to the left of the center
            {
                // Left wall collision
                if (rightX > boxStart.x && rightX < boxEnd.x && tUpAndDown)// && tUpAndDown && !tTopLeftC && !tBottomLeftC)
                {
                    this.pos.x = boxStart.x - this.radius;
                    this.vel.x *= -CONSTANTS.COLLISION_DAMPING;
                }
            }
            else // If it is to the right of the center or at the center
            {
                // Right wall collision
                if (leftX < boxEnd.x && leftX > boxStart.x && tUpAndDown)// && tUpAndDown && !tTopRightC && ! tBottomRightC)
                {
                    this.pos.x = boxEnd.x + this.radius;
                    this.vel.x *= -CONSTANTS.COLLISION_DAMPING;
                }
            }
        }
        else
        {
            // Up & down
            boolean tLeftAndRight = rightX > boxStart.x && leftX < boxEnd.x;

            // Top wall collision
            if (bottomY > boxStart.y && bottomY < boxEnd.y && tLeftAndRight)// && !tTopLeftC && !tTopRightC)
            {
                this.pos.y = boxStart.y - this.radius - 0.1;
                this.vel.y *= -CONSTANTS.COLLISION_DAMPING;
            }
            // Bottom wall collision
            else if (topY < boxEnd.y && topY > boxStart.y && tLeftAndRight)// && !tBottomLeftC && !tBottomRightC)
            {
                this.pos.y = boxEnd.y + this.radius;
                this.vel.y *= -CONSTANTS.COLLISION_DAMPING;
            }
        }


        // Corners
        if (tTopLeftC)
        {
//            double xDiff = boxStart.x - this.pos.x;
//            double yDiff = boxStart.y - this.pos.y;
//            boolean moveOnX = xDiff > yDiff;
//            if (moveOnX)
//            {
//                double amount = radius - xDiff;
//                //this.pos.x -= amount;
//            }
//            else
//            {
//                double amount = radius - yDiff;
//                //this.pos.y -= amount;
//            }
        }
        else if (tTopRightC)
        {

        }
        else if (tBottomRightC)
        {

        }
        else if (tBottomLeftC)
        {
//            double xDiff = boxEnd.x - this.pos.x;
//            double yDiff = boxEnd.y - this.pos.y;
//            boolean moveOnX = xDiff > yDiff;
//            if (moveOnX)
//            {
//                double amount = radius - xDiff;
//                //this.pos.x += amount;
//            }
//            else
//            {
//                double amount = radius - yDiff;
//                //this.pos.y += amount;
//            }
        }
    }

    public void SolveCircleCollision(Vector2 center, double radius)
    {
        double sqrDist = center.subtract(this.pos).sqrMagnitude();
        if (sqrDist + this.radius > radius * radius)
        {
            this.vel = this.vel.inverse();
            // TODO
            // Fix it, it doesn't work. Maybe the ball needs to roll
            Vector2 dir = center.subtract(this.pos);
            dir = dir.normalized().multiply(this.radius);
            this.pos = this.pos.add(dir);
        }
    }

    public void SolveParticleCollision(Particle other)
    {
        // Set the values to change (as we will need use the before-collision data while updating the one after)
        Vector2 new_p1 = this.pos.copy();
        Vector2 new_v1 = this.vel.copy();
        Vector2 new_p2 = other.pos.copy();
        Vector2 new_v2 = other.vel.copy();

        // Get the squared distance between the centers and check if they are colliding
        double x = this.pos.x - other.pos.x;
        double y = this.pos.y - other.pos.y;
        double dist = x * x + y * y;

        double r = this.radius + other.radius;
        if (dist < r * r)
        {
            // Update the distance to not be squared
            // As there are n objects, we would have needed to preform n * (n - 1) sqrt operations.
            // Re-calculating the distance ensures we will preform fewer operations if there are more objects
            dist = Math.sqrt(dist);

            // Update positions (move the particles away from each other):
            Vector2 change = this.pos.subtract(other.pos).div(dist);
            double overlap = r - dist;
            if (!this.isStatic) new_p1 = this.pos.add(change.multiply(overlap / 2));
            if (!other.isStatic) new_p2 = other.pos.subtract(change.multiply(overlap / 2));

            // Updating the velocities after collision

            // Calculate the first particle's velocity
            if (!this.isStatic)
            {
                Vector2 relativeVelocity = this.vel.subtract(other.vel);
                Vector2 relativePosition = this.pos.subtract(other.pos);

                double frac1 = 2 * other.mass / (this.mass + other.mass);
                double frac2 = relativeVelocity.dot(relativePosition);
                double mag = relativePosition.sqrMagnitude();
                frac2 = (mag == 0) ? frac2 : frac2 / mag;

                new_v1 = this.vel.subtract(relativePosition.multiply(frac1).multiply(frac2));
            }

            // Calculate the second particle's velocity
            if (!other.isStatic)
            {
                Vector2 relativeVelocity = other.vel.subtract(this.vel);
                Vector2 relativePosition = other.pos.subtract(this.pos);

                double frac1 = 2 * this.mass / (this.mass + other.mass);
                double frac2 = relativeVelocity.dot(relativePosition);
                double mag = relativePosition.sqrMagnitude();
                frac2 = (mag == 0) ? frac2 : frac2 / mag;

                new_v2 = other.vel.subtract(relativePosition.multiply(frac1).multiply(frac2));
            }

            // And, finally, update the actual values
            this.pos = new_p1;
            this.vel = new_v1;
            other.pos = new_p2;
            other.vel = new_v2;
        }
    }

    public boolean isTouchingPoint(Vector2 point)
    {
        double dist = this.pos.subtract(point).sqrMagnitude();
        return dist < this.radius * this.radius;
    }
    public boolean isTouchingPoint(double x, double y)
    {
        double dist = this.pos.subtract(new Vector2(x, y)).sqrMagnitude();
        return dist < this.radius * this.radius;
    }

    public void AutoSetMass()
    {
        this.mass = this.radius * RADIUS_TO_MASS;
    }

    public void SetColor(Color color)
    {
        this.hue = color.getHue();
        this.brightness = color.getBrightness();
        this.saturation = color.getSaturation();
    }
}
