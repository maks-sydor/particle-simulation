package com.example.particlesimulation;

public class Wall
{
    Vector2 startPos;
    Vector2 endPos;

    public Wall()
    {
        this.startPos = new Vector2(0, 0);
        this.endPos = new Vector2(50, 50);
    }
    public Wall(Vector2 start, Vector2 end)
    {
        this.startPos = start;
        this.endPos = end;
    }
    public Wall(double x1, double y1, double x2, double y2)
    {
        this.startPos = new Vector2(x1, y1);
        this.endPos = new Vector2(x2, y2);
    }

    public boolean isTouchingPoint(Vector2 point)
    {
        boolean x = point.x > startPos.x && point.x < endPos.x;
        boolean y = point.y > startPos.y && point.y < endPos.y;
        return x && y;
    }
}
