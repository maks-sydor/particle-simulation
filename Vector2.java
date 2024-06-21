package com.example.particlesimulation;

public class Vector2
{
    double x;
    double y;

    @Override
    public String toString()
    {
        return "Vector2 {" +
                "x = " + x +
                ", y = " + y +
                '}';
    }

    public Vector2 copy()
    {
        return new Vector2(this.x, this.y);
    }

    public Vector2()
    {
        this.x = 0;
        this.y = 0;
    }
    public Vector2(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public Vector2 add(Vector2 other)
    {
        return new Vector2(this.x + other.x, this.y + other.y);
    }
    public Vector2 subtract(Vector2 other)
    {
        return new Vector2(this.x - other.x, this.y - other.y);
    }
    public Vector2 multiply(Vector2 other)
    {
        return new Vector2(this.x * other.x, this.y * other.y);
    }
    public Vector2 multiply(double value)
    {
        return new Vector2(this.x * value, this.y * value);
    }
    public Vector2 div(Vector2 other)
    {
        return new Vector2(
                (other.x == 0) ? this.x : this.x / other.x,
                (other.y == 0) ? this.y : this.y / other.y);
    }
    public Vector2 div(double value)
    {
        if (value != 0) return new Vector2(this.x / value, this.y / value);
        else return this;
    }

    public Vector2 random(double min, double max)
    {
        double x = min + Math.random() * (max - min);
        double y = min + Math.random() * (max - min);

        return new Vector2(x, y);
    }

    public Vector2 inverse()
    {
        return new Vector2(this.x * -1, this.y * -1);
    }
    public Vector2 normalized()
    {
        double m = this.magnitude();
        return new Vector2(this.x / m, this.y / m);
    }

    public double sqrMagnitude()
    {
        return (this.x * this.x) + (this.y * this.y);
    }
    public double magnitude()
    {
        return (float) Math.sqrt(this.sqrMagnitude());
    }
    public double dot(Vector2 other)
    {
        return this.x * other.x + this.y * other.y;
    }
}
