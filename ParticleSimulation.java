package com.example.particlesimulation;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.Arrays;

import static com.example.particlesimulation.MyUtils.*;

public class ParticleSimulation extends Application {
    // Variable declaration & initialization

    // Bounds & window settings
    Vector2 boxStart = new Vector2(10, 10);
    Vector2 boxEnd = new Vector2(510, 510);
    Vector2 windowSize = new Vector2(630, 520);
    Color bgColor = Color.LIGHTGREY;

    // References
    ParticleUpdateHandler pUpdater = new ParticleUpdateHandler(boxStart, boxEnd); // Create a particle updater
    Pane root;

    // Data and UI lists
    private Particle[] particles = pUpdater.particles; // Import the particles data list
    private Circle[] ui_circles; // Create a list of displayed circle elements
    private Wall[] walls = pUpdater.walls;
    private Rectangle[] ui_walls;

    // Particle creation & collision checks
    private final String collisionCheckMode = "FAST"; // The circles collision check mode: FAST or PRECISE
    private final int maxParticles = 1000;
    private final int maxWalls = 100;

    // Mouse control
    private boolean isDragging = false;
    private Vector2 currentMousePos = new Vector2(0, 0);
    private Vector2 clickPosition;

    // Manual CC mode variables
    private boolean randomCircleCreation = false;
    private boolean creatingStaticCircles = false;
    private Circle newCircle;
    private boolean creatingCircle = false;

    // Colors
    private boolean paintModeOn = false; // Are you painting
    private Color selectedColor = Color.AQUA;
    private final double colorChangeSpeed = 1;
    private boolean isRainbowPainting = false;

    // Wall placing
    private boolean wallPlacingModeEnabled = false;
    private Rectangle newWall;
    private boolean creatingWall;

    // Selection
    private Circle selection;
    private double selectionScale = 1.1; // Relative to the selected circle
    private Color deleteModeColor = Color.rgb(189, 45, 36);
    private Color paintModeColor = Color.AQUAMARINE;
    private Color selectionColor = deleteModeColor;


    @Override
    public void start(Stage primaryStage) {
        PrintWelcomeMessage();

        // Create a window and a root
        root = new Pane();
        Scene scene = new Scene(root, windowSize.x, windowSize.y, Color.BLACK);
        root.setStyle("-fx-background-color: lightgrey;");

        // Create the handlers:
        root.setOnMouseMoved(event -> {
            currentMousePos = new Vector2(event.getX(), event.getY());
        });
        root.setOnMouseDragged(event -> {
            if (isDragging)
            {
                if (wallPlacingModeEnabled)
                {
                    double newX = Math.min(event.getX(), clickPosition.x);
                    double newY = Math.min(event.getY(), clickPosition.y);

                    double width = Math.abs(event.getX() - clickPosition.x);
                    double height = Math.abs(event.getY() - clickPosition.y);

                    newWall.setX(newX);
                    newWall.setY(newY);
                    newWall.setWidth(width);
                    newWall.setHeight(height);
                }
                else
                {
                    Vector2 d = new Vector2(event.getX(), event.getY()).subtract(clickPosition);
                    double radius = d.magnitude();

                    newCircle.setRadius(radius);
                }
            }
        });
        root.setOnMouseReleased(event -> {
            if (creatingCircle)
            {
                creatingCircle = false;
                isDragging = false;

                Vector2 pos = new Vector2(newCircle.getCenterX(), newCircle.getCenterY());
                Particle p = new Particle(pos, 1, newCircle.getRadius());
                pUpdater.AddParticle(p);
                p.SetColor(selectedColor);
                if (isRainbowPainting) p.isRainbow = true;
                if (creatingStaticCircles) p.isStatic = true;

                particles = pUpdater.particles;
                int i = particles.length - 1;
                ui_circles = Arrays.copyOf(ui_circles, particles.length);
                ui_circles[i] = newCircle;
                newCircle = null;
            }
            if (creatingWall)
            {
                creatingWall = false;
                isDragging = false;

                double startX = Math.min(clickPosition.x, event.getX());
                double startY = Math.min(clickPosition.y, event.getY());
                double endX = Math.max(clickPosition.x, event.getX());
                double endY = Math.max(clickPosition.y, event.getY());

                Vector2 start = new Vector2(startX, startY);
                Vector2 end = new Vector2(endX, endY);

                Wall w = new Wall(start, end);
                //System.out.println("Start: " + start);
                //System.out.println("End: " + end);
                pUpdater.AddWall(w);

                walls = pUpdater.walls;
                int i = walls.length - 1;
                ui_walls = Arrays.copyOf(ui_walls, walls.length);
                ui_walls[i] = newWall;
                newCircle = null;
            }
        });
        root.setOnMousePressed(event -> {
            // If the event is "LMB down"
            if (event.getButton().name().equals("PRIMARY"))
            {
                if (wallPlacingModeEnabled)
                {
                    if (walls.length > maxWalls-1) return;

                    creatingWall = true;
                    isDragging = true;
                    double x = event.getX();
                    double y = event.getY();

                    clickPosition = new Vector2(x, y);

                    newWall = new Rectangle(clickPosition.x, clickPosition.y, 10, 10);

                    newWall.setFill(bgColor);
                    root.getChildren().add(newWall);
                }
                else // If we are creating or painting circles
                {
                    if (paintModeOn)
                    {
                        int index = pUpdater.GetCircleIndex(event.getX(), event.getY());

                        if (index != -1)
                        {
                            if (isRainbowPainting) particles[index].isRainbow = !particles[index].isRainbow;
                            particles[index].SetColor(selectedColor);
                            ui_circles[index].setFill(selectedColor);
                        }
                    }
                    else
                    {
                        if (randomCircleCreation)
                        {
                            if (particles.length > maxParticles-1) return;

                            // Create a new particle if possible
                            // Create a new particle in the ParticleUpdateHandler particles list
                            pUpdater.HandleLMBClick(event.getX(), event.getY());
                            // Get a new list from there (as it re-assigns a list, not modifies it)
                            particles = pUpdater.particles;



                            // Get the last particle's data from the list (we still need to create the UI circle)
                            int i = particles.length - 1;
                            double x = particles[i].pos.x;
                            double y = particles[i].pos.y;
                            double radius = particles[i].radius;
                            Color color = Color.rgb(rnd(0, 255), rnd(0, 255), rnd(0, 255)); // Color is random
                            if (isRainbowPainting) particles[i].isRainbow = true;

                            // Extend the UI circle list
                            ui_circles = Arrays.copyOf(ui_circles, particles.length);

                            // Create a new circle with the values that we got and add it to the list and canvas
                            Circle circle = new Circle(x, y, radius, color);
                            ui_circles[i] = circle;
                            root.getChildren().add(circle);
                        }
                        else
                        {
                            if (particles.length > maxParticles-1) return;

                            creatingCircle = true;
                            isDragging = true;
                            double x = event.getX();
                            double y = event.getY();

                            clickPosition = new Vector2(x, y);

                            newCircle = new Circle(x, y, 10);

                            newCircle.setFill(selectedColor);
                            root.getChildren().add(newCircle);
                        }
                    }
                }
            }
            // If the event is "RMB down"
            if (event.getButton().name().equals("SECONDARY"))
            {
                if (wallPlacingModeEnabled) // If we are deleting walls
                {
                    Object[] results = pUpdater.DeleteWall(event.getX(), event.getY());
                    int i = (int) results[0];
                    boolean foundAny = (boolean) results[1];

                    if (foundAny)
                    {
                        walls = pUpdater.walls;

                        root.getChildren().remove(ui_walls[i]);
                        ui_walls = removeElement(ui_walls, i);
                    }
                }
                else // If we are deleting circles
                {
                    // Delete a circle, if clicked one

                    // Get the results after handling the RMB click in the ParticleUpdateHandler
                    // The results is { int, boolean }:
                    //  - int is the index of the found circle in the list
                    //  - boolean is true if we found a circle and need to delete it
                    Object[] results = pUpdater.DeleteCircle(event.getX(), event.getY());
                    int i = (int) results[0];
                    boolean foundAny = (boolean) results[1];

                    if (foundAny)
                    {
                        // If found any circles, remove the UI element and a Circle from the list
                        particles = pUpdater.particles; // The list reassigned, not modified, again

                        root.getChildren().remove(ui_circles[i]);
                        ui_circles = removeElement(ui_circles, i);
                    }
                }
            }
        });

        // Draw the bounds box
        Rectangle boundsRect = new Rectangle(boxStart.x, boxStart.y, boxEnd.x - boxStart.x, boxEnd.y - boxStart.y);
        boundsRect.setFill(Color.WHITE);
        root.getChildren().add(boundsRect);


        // Add the UI elements
        // Delete circles button
        Button deleteCirclesButton = new Button("DELETE CIRCLES");
        deleteCirclesButton.setOnAction(this::ClearCircleLists);
        deleteCirclesButton.setLayoutX(520);
        deleteCirclesButton.setLayoutY(10);
        deleteCirclesButton.setMinWidth(100);
        root.getChildren().add(deleteCirclesButton);

        // Delete walls button
        Button deleteWallsButton = new Button("DELETE WALLS");
        deleteWallsButton.setOnAction(this::ClearWallLists);
        deleteWallsButton.setLayoutX(520);
        deleteWallsButton.setLayoutY(45);
        deleteWallsButton.setMinWidth(100);
        root.getChildren().add(deleteWallsButton);

        // The wall placing mode checkbox is between those elements

        // Circle creation mode
        CheckBox randomModeCheckbox = new CheckBox("Rand. circles");
        randomModeCheckbox.setOnAction(event -> randomCircleCreation = randomModeCheckbox.isSelected());
        randomModeCheckbox.setLayoutX(520);
        randomModeCheckbox.setLayoutY(110);
        randomModeCheckbox.setMaxWidth(100);
        root.getChildren().add(randomModeCheckbox);

        // Line separation
        Line lineSeparator = new Line();
        lineSeparator.setStartX(510);
        lineSeparator.setEndX(630);
        lineSeparator.setStartY(138);
        lineSeparator.setEndY(138);
        lineSeparator.setOpacity(0.2);
        root.getChildren().add(lineSeparator);

        // Color picker
        ColorPicker colorPicker = new ColorPicker(selectedColor);
        colorPicker.setOnAction(event -> selectedColor = colorPicker.getValue());
        colorPicker.setLayoutX(520);
        colorPicker.setLayoutY(148);
        colorPicker.setMaxWidth(100);
        root.getChildren().add(colorPicker);

        // Rainbow color
        CheckBox rainbowPaintingCheckbox = new CheckBox("Rainbow");
        rainbowPaintingCheckbox.setOnAction(event -> isRainbowPainting = rainbowPaintingCheckbox.isSelected());
        rainbowPaintingCheckbox.setLayoutX(520);
        rainbowPaintingCheckbox.setLayoutY(183);
        rainbowPaintingCheckbox.setMaxWidth(100);
        root.getChildren().add(rainbowPaintingCheckbox);

        // Paint mode
        CheckBox paintModeCheckbox = new CheckBox("Paint mode");
        paintModeCheckbox.setOnAction(event -> paintModeOn = paintModeCheckbox.isSelected());
        paintModeCheckbox.setLayoutX(520);
        paintModeCheckbox.setLayoutY(212);
        paintModeCheckbox.setMaxWidth(100);
        root.getChildren().add(paintModeCheckbox);

        // Wall placing mode (Y pos is 80!)
        CheckBox wallModeCheckbox = new CheckBox("Wall mode");
        wallModeCheckbox.setOnAction(event -> {
            boolean isSelected = wallModeCheckbox.isSelected();
            wallPlacingModeEnabled = isSelected;

            // Hide other elements
            randomModeCheckbox.setVisible(!isSelected);
            lineSeparator.setVisible(!isSelected);
            colorPicker.setVisible(!isSelected);
            rainbowPaintingCheckbox.setVisible(!isSelected);
            paintModeCheckbox.setVisible(!isSelected);
        });
        wallModeCheckbox.setLayoutX(520);
        wallModeCheckbox.setLayoutY(80);
        wallModeCheckbox.setMaxWidth(100);
        root.getChildren().add(wallModeCheckbox);

        // Line separation 2
        Line lineSeparator2 = new Line();
        lineSeparator2.setStartX(510);
        lineSeparator2.setEndX(630);
        lineSeparator2.setStartY(243);
        lineSeparator2.setEndY(243);
        lineSeparator2.setOpacity(0.2);
        root.getChildren().add(lineSeparator2);

        // Paint mode
        CheckBox staticModeCheckbox = new CheckBox("Static circles");
        staticModeCheckbox.setOnAction(event -> creatingStaticCircles = staticModeCheckbox.isSelected());
        staticModeCheckbox.setLayoutX(520);
        staticModeCheckbox.setLayoutY(255);
        staticModeCheckbox.setMaxWidth(100);
        root.getChildren().add(staticModeCheckbox);



        selection = new Circle(0, 0, 0, selectionColor);
        selection.setVisible(false);
        root.getChildren().add(selection);

        // Particle initialization
        pUpdater.RandomizeVelocities(-5, 5);

        // Initialize circles based on the particles list
        ui_circles = new Circle[particles.length];

        // Loop over all the particles
        for (int i = 0; i < particles.length; i++) {
            // Get their values
            double x = particles[i].pos.x;
            double y = particles[i].pos.y;
            double radius = particles[i].radius;

            // And create a circle based on those parameters and add it to the list
            Circle circle = new Circle(x, y, radius, Color.rgb(rnd(0, 255), rnd(0, 255), rnd(0, 255)));
            ui_circles[i] = circle;
            root.getChildren().add(circle);
        }
        // Initialize boxes
        ui_walls = new Rectangle[walls.length];

        for (int i = 0; i < walls.length; i++)
        {
            Rectangle rect = new Rectangle(walls[i].startPos.x, walls[i].startPos.y,
                    walls[i].endPos.x - walls[i].startPos.x, walls[i].endPos.y - walls[i].startPos.y);
            rect.setFill(bgColor);
            ui_walls[i] = rect;
            root.getChildren().add(rect);
        }


        // Update loop
        // empty comment
        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Update particles data (apply gravity, solve collisions)
                pUpdater.UpdateParticles(collisionCheckMode);
                particles = pUpdater.particles;

                // Update circle's positions and their colors on the screen
                for (int i = 0; i < ui_circles.length; i++) {
                    // Get the data from the list
                    double x = particles[i].pos.x;
                    double y = particles[i].pos.y;
                    double radius = particles[i].radius;

                    // And update it for each particle
                    ui_circles[i].setCenterX(x);
                    ui_circles[i].setCenterY(y);
                    ui_circles[i].setRadius(radius);

                    // Change the color slightly
                    if (particles[i].isRainbow)
                    {
                        particles[i].hue += colorChangeSpeed; // Adjust for speed of color change
                        if (particles[i].hue > 360.0) {
                            particles[i].hue -= 360.0;
                        }

                        Color newColor = Color.hsb(particles[i].hue, particles[i].saturation, particles[i].brightness);
                        ui_circles[i].setFill(newColor);
                    }
                }

                // Move the selection if needed
                int index = pUpdater.GetCircleIndex(currentMousePos.x, currentMousePos.y);

                if (index != -1 && !wallPlacingModeEnabled)
                {
                    if (paintModeOn) selectionColor = paintModeColor;
                    else selectionColor = deleteModeColor;

                    selection.setVisible(true);
                    selection.setFill(selectionColor);
                    selection.setCenterX(particles[index].pos.x);
                    selection.setCenterY(particles[index].pos.y);
                    selection.setRadius(particles[index].radius * selectionScale);

                }
                else
                {
                    selection.setVisible(false);
                }
            }
        };

        // Start the application
        animationTimer.start();

        primaryStage.setTitle("Particle Simulation");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void ClearCircleLists(ActionEvent actionEvent) {
        particles = new Particle[0];
        pUpdater.particles = new Particle[0];
        for (Circle c : ui_circles)
        {
            root.getChildren().remove(c);
        }
        ui_circles = new Circle[0];
    }
    private void ClearWallLists(ActionEvent actionEvent) {
        walls = new Wall[0];
        pUpdater.walls = new Wall[0];
        for (Rectangle r : ui_walls)
        {
            root.getChildren().remove(r);
        }
        ui_walls = new Rectangle[0];
    }
    private void PrintWelcomeMessage()
    {
        String RESET = "\u001B[0m";
        String BLUE = "\u001B[34m";
        String GREEN = "\u001B[32m";
        String YELLOW = "\u001B[33m";
        String RED = "\u001B[31m";

        String text = BLUE +
                """
                Hello! This is a particle simulation.

                Right now it only supports circles and static boxes.
                It also doesn't support rotation, friction, and angular velocities.
                But, it has many other functions:
                
                Firstly, you can""" + GREEN + " create circles by dragging your mouse. " + BLUE + """
                They will be created with the settings you have enabled - rainbow, static, current color, etc.
                
                If you don't want to create many circles manually, you can enable""" + GREEN + " \"Rand. circles\" " + BLUE + """
                This will create them randomly with each click.
                
                Now, you may find that you have too many circles! But you don't want to delete all of them,
                because there is your best, favorite one, right in that corner!
                If that is the case, you can""" + GREEN + " right click to erase " + BLUE + """
                the circle under the cursor!
                
                You can also""" + GREEN + " change circle's colors by enabling \"Paint mode\". " + BLUE + """
                This will allow you to paint your circles with the selected color by pressing LMB.
                If you have "Rainbow mode" enabled, then painting circles will make them rainbow!
                Circles that already are rainbow will become regular.
                
                """ + GREEN + " There's also a function that allows you to place WALLS, " + BLUE + """
                like the one in the middle.
                But they were tricky to make, and are still under development, so they are quite buggy.
                You can still use them, but do note that:
                    - Corner collisions work strangely
                    - Thin walls teleport circles
                    - Circles can go through walls (sometimes, I don't know when)
                    - Circles also collide with thin walls even when they are not touching them
                    - And many more bugs that I don't yet know about!
                So, be careful)
                
                Circle collisions work fine, but static circle collisions may "remove" the velocity of the circle
                they are colliding with. I know why, but it is hard to fix, so in this version they are "bad circles".
                
                Now, that is all! I plan on adding many new features (you may find some of them unfinished in the code),
                such as time stopping, fixing walls, dragging balls, exploding balls, and many more!
                
                Have fun :)
                """ + RESET;

        System.out.println(text);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

