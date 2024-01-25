package objects3D;

import GraphicsObjects.Utils;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;

import static org.lwjgl.opengl.GL11.*;

public class World {
    Texture[] texturesWorld;
    static float magenta[] = { 1.0f, 0.0f, 1.0f, 1.0f };
    private float secondBallY; // Vertical position of the second ball
    private float otherBallY; // Vertical position of the other ball

    private float secondVelocityY; // Vertical velocity of the second ball
    private float otherVelocityY; // Vertical velocity of the other ball

    private final float gravity = -10f; // Acceleration due to gravity (m/s^2)
    private final float initialHeight = 2500; // Initial height of the ball

    public World(Texture[] texturesWorld) {
        this.texturesWorld = texturesWorld;
        this.secondBallY = initialHeight;
        this.otherBallY = initialHeight;
        this.secondVelocityY = 150.0f;
        this.otherVelocityY = 150.0f;
    }

    public void drawWorld() {
        // Draw the land of the world
        GL11.glPushMatrix();
        TexCube land = new TexCube();
        GL11.glTranslatef(600,200,0);
        GL11.glScalef(5000f, 8f, 5000f);
        Color.white.bind();
        texturesWorld[0].bind();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glTexParameteri( GL11.GL_TEXTURE_2D,  GL11.GL_TEXTURE_MAG_FILTER,  GL11.GL_NEAREST);
        land.drawTexCube();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();

        // Draw the sky dome of the world
        GL11.glPushMatrix();
        InwardTexSphere sky = new InwardTexSphere();
        GL11.glTranslatef(600,200,0);
        GL11.glScalef(10000f, 10000f, 10000f);
        GL11.glRotatef(90.0f, 0.0f, 0.0f, 0.0f);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
        // Bind the texture to the surface
        Color.white.bind();
        texturesWorld[1].bind();
        glEnable(GL_TEXTURE_2D);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glClearColor(0.7f, 0.7f, 0.7f, 0.0f);
        sky.DrawTexSphere(0.5f, 12, 12, texturesWorld[1]);
        GL11.glPopMatrix();

        // Draw the first ball
        GL11.glPushMatrix();
        Sphere sphere1 = new Sphere();
        glColor3f(magenta[0], magenta[1], magenta[2]);
        glMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, Utils.ConvertForGL(magenta));

        glTranslatef(200, otherBallY, 3500);
        GL11.glScalef(18f, 18f, 18f);
        sphere1.drawSphere(15.0f, 32, 32);

        GL11.glPopMatrix();

        // Draw the second ball
        GL11.glPushMatrix();
        Sphere sphere2 = new Sphere();
        glColor3f(magenta[0], magenta[1], magenta[2]);
        glMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, Utils.ConvertForGL(magenta));

        glTranslatef(1200, secondBallY * 1.2f, 3500);
        GL11.glScalef(18f, 18f, 18f);
        sphere2.drawSphere(15.0f, 32, 32);

        GL11.glPopMatrix();

        // Draw the third ball
        GL11.glPushMatrix();
        Sphere sphere3 = new Sphere();
        glColor3f(magenta[0], magenta[1], magenta[2]);
        glMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, Utils.ConvertForGL(magenta));

        glTranslatef(2200, otherBallY * 1.1f, 3500);
        GL11.glScalef(18f, 18f, 18f);
        sphere3.drawSphere(15.0f, 32, 32);

        GL11.glPopMatrix();


        // Draw the outer space
        GL11.glPushMatrix();
        TexCube space = new TexCube();
        GL11.glTranslatef(3000,1200,-200);
        GL11.glScalef(8f, 3000f, 4000f);
        GL11.glRotatef(90.0f, 0.0f, 0.0f, 0.0f);
        Color.white.bind();
        texturesWorld[4].bind();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glTexParameteri( GL11.GL_TEXTURE_2D,  GL11.GL_TEXTURE_MAG_FILTER,  GL11.GL_NEAREST);
        space.drawTexCube();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();

    }

    public void updatePhysicsSecondBall(float deltaTime) {
        // Update the velocity
        secondVelocityY += gravity * deltaTime * 8;

        // Update the position
        secondBallY += secondVelocityY * deltaTime;

        // Check for collision with the ground and bounce
        if (secondBallY < 450) {
            secondBallY = 450;
            secondVelocityY = -secondVelocityY * 0.8f; // Reverse velocity and apply damping factor for bounce
        }
    }

    public void updatePhysicsOtherBall(float deltaTime) {
        // Update the velocity
        otherVelocityY += gravity * deltaTime * 8;

        // Update the position
        otherBallY += otherVelocityY * deltaTime;

        // Check for collision with the ground and bounce
        if (otherBallY < 450) {
            otherBallY = 450;
            otherVelocityY = -otherVelocityY * 0.8f; // Reverse velocity and apply damping factor for bounce
        }
    }

    public void resetSecondBallMovement() {
        secondBallY = initialHeight;
        secondVelocityY = 0;
    }

    public void resetOtherBallMovement() {
        otherBallY = initialHeight;
        otherVelocityY = 0;
    }
}
