
import java.io.IOException;
import java.nio.FloatBuffer;

import objects3D.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.util.glu.GLU;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import GraphicsObjects.Arcball;
import GraphicsObjects.Utils;



public class MainWindow {

	private boolean MouseOnepressed = true;
	private boolean dragMode = false;
	private boolean BadAnimation = false;
	private boolean Earth = false;
	/** position of pointer */
	float x = 400, y = 300;
	/** angle of rotation */
	float rotation = 0;
	/** time at last frame */
	long lastFrame;
	/** frames per second */
	int fps;
	/** last fps time */
	long lastFPS;

	long myDelta = 0; // to use for animation
	float Alpha = 0; // to use for animation
	long StartTime; // beginAnimiation

	Arcball MyArcball = new Arcball();

	boolean DRAWGRID = false;
	boolean waitForKeyrelease = true;
	/** Mouse movement */
	int LastMouseX = -1;
	int LastMouseY = -1;

	float pullX = 0.0f; // arc ball X cord.
	float pullY = 0.0f; // arc ball Y cord.

	int OrthoNumber = 1200; // using this for screen size, making a window of 1200 x 800 so aspect ratio 3:2
							// // do not change this for assignment 3 but you can change everything for your
							// project

	// basic colours
	static float black[] = { 0.0f, 0.0f, 0.0f, 1.0f };
	static float white[] = { 1.0f, 1.0f, 1.0f, 1.0f };

	static float grey[] = { 0.5f, 0.5f, 0.5f, 1.0f };
	static float spot[] = { 0.1f, 0.1f, 0.1f, 0.5f };

	// primary colours
	static float red[] = { 1.0f, 0.0f, 0.0f, 1.0f };
	static float green[] = { 0.0f, 1.0f, 0.0f, 1.0f };
	static float blue[] = { 0.0f, 0.0f, 1.0f, 1.0f };

	// secondary colours
	static float yellow[] = { 1.0f, 1.0f, 0.0f, 1.0f };
	static float magenta[] = { 1.0f, 0.0f, 1.0f, 1.0f };
	static float cyan[] = { 0.0f, 1.0f, 1.0f, 1.0f };

	// other colours
	static float orange[] = { 1.0f, 0.5f, 0.0f, 1.0f, 1.0f };
	static float brown[] = { 0.5f, 0.25f, 0.0f, 1.0f, 1.0f };
	static float dkgreen[] = { 0.0f, 0.5f, 0.0f, 1.0f, 1.0f };
	static float pink[] = { 1.0f, 0.6f, 0.6f, 1.0f, 1.0f };

	float paceX1 = 0.0f;
	float paceY1 = 0.0f;
	float paceZ1 = 0.0f;
	float paceX2 = 0.0f;
	float paceY2 = 0.0f;
	float paceZ2 = 0.0f;
	float currentAngle = 0.0f;
	boolean turnOnLaserEffect = false;
	private float humanSpeed = 0.2f; // Adjust as necessary for movement speed
	private int humanDirection = 1; // 1 for one direction, -1 for the opposite
	float initialHumanX = 2785;
	float initialHumanY = 380;
	float initialHumanZ = 200;

	float initialCybermanX = 285;
	float initialCybermanY = 380;
	float initialCybermanZ = 0;

	private boolean laserHit = false;
	private long laserHitTime = 0;
	private final long resetDelay = 500;

	float spaceMinZ = -29.0f;
	float spaceMaxZ = 100000;

	private World world;

	private boolean isRotating = false;
	private boolean isCameraFollowing = false;


	// static GLfloat light_position[] = {0.0, 100.0, 100.0, 0.0};

	// support method to aid in converting a java float array into a Floatbuffer
	// which is faster for the opengl layer to process

	public void start() {

		StartTime = getTime();
		try {
			Display.setDisplayMode(new DisplayMode(1200, 800));
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}

		initGL(); // init OpenGL
		getDelta(); // call once before loop to initialise lastFrame
		lastFPS = getTime(); // call before loop to initialise fps timer

		while (!Display.isCloseRequested()) {
			int delta = getDelta();
			update(delta);
			renderGL();
			Display.update();
			Display.sync(120); // cap fps to 120fps
			long passed = getTime() - StartTime;
//			System.out.println("Time passed: " + passed);
		}

		Display.destroy();
	}

	public void update(int delta) {
		// rotate quad
		// rotation += 0.01f * delta;

		int MouseX = Mouse.getX();
		int MouseY = Mouse.getY();
		int WheelPostion = Mouse.getDWheel();

		boolean MouseButonPressed = Mouse.isButtonDown(0);

		if (MouseButonPressed && !MouseOnepressed) {
			MouseOnepressed = true;
			MyArcball.startBall(MouseX, MouseY, 1200, 800);
			dragMode = true;
			turnOnLaserEffect = true;
			System.out.println("Mouse Pressed");

		} else if (!MouseButonPressed) {
			MouseOnepressed = false;
			dragMode = false;
			turnOnLaserEffect = false;

		}

		if (dragMode) {
			MyArcball.updateBall(MouseX, MouseY, 1200, 800);
		}

		if (WheelPostion > 0) {
			OrthoNumber += 10;

		}

		if (WheelPostion < 0) {
			OrthoNumber -= 10;
			if (OrthoNumber < 610) {
				OrthoNumber = 610;
			}


		}

		/** rest key is R */
		if (Keyboard.isKeyDown(Keyboard.KEY_R))
			MyArcball.reset();

		/* bad animation can be turn on or off using A key) */

		if (Keyboard.isKeyDown(Keyboard.KEY_A))
			BadAnimation = !BadAnimation;

		if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
			// move to the east
			paceX1 += 0.35f;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			// move to the west
			paceX1 -= 0.35f;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_I)) {
			// move upward
			paceY1 += 0.35f;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_O)) {
			// move downward
			paceY1 -= 0.35f;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_N)) {
			// move to the north
			if (!isCollidingWithSpace(paceZ1)) {
				System.out.println("Colliding Happened");
			} else {
				paceZ1 -=0.35f;
			}
			System.out.println("NewPaceZ1: " + paceZ1 + " SpaceMinZ: " + spaceMinZ + " SpaceMaxZ: " + spaceMaxZ);
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			// move to the south
			paceZ1 += 0.35f;
			System.out.println("NewPaceZ1: " + paceZ1 + " SpaceMinZ: " + spaceMinZ + " SpaceMaxZ: " + spaceMaxZ);
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_Q))
		{
			isRotating = true;
			currentAngle += 0.35f * delta;
		} else {
			isRotating = false;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_C)) {
			isCameraFollowing = !isCameraFollowing;
		}

//		if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
//			Earth = !Earth;
//		}


		updateHumanPosition(delta);

		// keep quad on the screen
		if (x < 0)
			x = 0;
		if (x > 1200)
			x = 1200;
		if (y < 0)
			y = 0;
		if (y > 800)
			y = 800;


		if (turnOnLaserEffect) {
			boolean isLaserHit = checkLaserCollision();
			if (isLaserHit && !laserHit) {
				laserHit = true;
				laserHitTime = getTime(); // Record the time when the laser hit
				human.setSpecialEffect();
			}
			System.out.println("Laser Collision: " + isLaserHit);
		} else {
			laserHit = false;
			human.forceTurnOffSpecialEffect();
		}

		if (laserHit && getTime() - laserHitTime > resetDelay) {
			resetHumanPosition(); // Reset human position after delay
			laserHit = false; // Reset the flag
		}

		// Check if the laser hits the ball
		if (turnOnLaserEffect) {
			if (checkLaserBallCollision() == 0) {
				world.resetSecondBallMovement();
			} else if (checkLaserBallCollision() == 1) {
				world.resetOtherBallMovement();
			}
//			world.resetBallMovement();
		}

		// Update the physics of the World
		float deltaTimeInSeconds = delta / 1000.0f; // Convert delta to seconds
		world.updatePhysicsSecondBall(deltaTimeInSeconds);
		world.updatePhysicsOtherBall(deltaTimeInSeconds);

		updateFPS(); // update FPS Counter

		LastMouseX = MouseX;
		LastMouseY = MouseY;
	}

	/**
	 * Calculate how many milliseconds have passed since last frame.
	 * 
	 * @return milliseconds passed since last frame
	 */
	public int getDelta() {
		long time = getTime();
		int delta = (int) (time - lastFrame);
		lastFrame = time;

		return delta;
	}

	/**
	 * Get the accurate system time
	 * 
	 * @return The system time in milliseconds
	 */
	public long getTime() {
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}

	/**
	 * Calculate the FPS and set it in the title bar
	 */
	public void updateFPS() {
		if (getTime() - lastFPS > 1000) {
			Display.setTitle("FPS: " + fps);
			fps = 0;
			lastFPS += 1000;
		}
		fps++;
	}

	public void initGL() {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		changeOrth();
		MyArcball.startBall(0, 0, 1200, 800);
		glMatrixMode(GL_MODELVIEW);
		FloatBuffer lightPos = BufferUtils.createFloatBuffer(4);
		lightPos.put(10000f).put(1000f).put(1000).put(0).flip();

		FloatBuffer lightPos2 = BufferUtils.createFloatBuffer(4);
		lightPos2.put(0f).put(1000f).put(0).put(-1000f).flip();

		FloatBuffer lightPos3 = BufferUtils.createFloatBuffer(4);
		lightPos3.put(-10000f).put(1000f).put(1000).put(500).flip();

		FloatBuffer lightPos4 = BufferUtils.createFloatBuffer(4);
		lightPos4.put(-10000f).put(10000f).put(10000f).put(10000).flip();

		glLight(GL_LIGHT0, GL_POSITION, lightPos); // specify the
													// position
													// of the
													// light
		// glEnable(GL_LIGHT0); // switch light #0 on // I've setup specific materials
		// so in real light it will look abit strange

		glLight(GL_LIGHT1, GL_POSITION, lightPos2); // specify the
													// position
													// of the
													// light
		glEnable(GL_LIGHT1); // switch light #0 on
		glLight(GL_LIGHT1, GL_AMBIENT, Utils.ConvertForGL(spot));

		glLight(GL_LIGHT2, GL_POSITION, lightPos3); // specify
													// the
													// position
													// of the
													// light
		glEnable(GL_LIGHT2); // switch light #0 on
		glLight(GL_LIGHT2, GL_DIFFUSE, Utils.ConvertForGL(grey));

		glLight(GL_LIGHT3, GL_POSITION, lightPos4); // specify
													// the
													// position
													// of the
													// light
		glEnable(GL_LIGHT3); // switch light #0 on
		glLight(GL_LIGHT3, GL_DIFFUSE, Utils.ConvertForGL(grey));

		glEnable(GL_LIGHTING); // switch lighting on
		glEnable(GL_DEPTH_TEST); // make sure depth buffer is switched
									// on
		glEnable(GL_NORMALIZE); // normalize normal vectors for safety
		glEnable(GL_COLOR_MATERIAL);

		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		try {
			init();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // load in texture

	}

	public void changeOrth() {

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(1200 - OrthoNumber, OrthoNumber, (800 - (OrthoNumber * 0.66f)), (OrthoNumber * 0.66f), 100000, -100000);
		glMatrixMode(GL_MODELVIEW);

		FloatBuffer CurrentMatrix = BufferUtils.createFloatBuffer(16);
		glGetFloat(GL_MODELVIEW_MATRIX, CurrentMatrix);

		// if(MouseOnepressed)
		// {

		MyArcball.getMatrix(CurrentMatrix);
		// }

		glLoadMatrix(CurrentMatrix);

	}

	/*
	 * You can edit this method to add in your own objects / remember to load in
	 * textures in the INIT method as they take time to load
	 * 
	 */
	public void renderGL() {
		changeOrth();

		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		glColor3f(0.5f, 0.5f, 1.0f);

		myDelta = getTime() - StartTime;
		float delta = ((float) myDelta) / 10000;

		// code to aid in animation
		float theta = (float) (delta * 2 * Math.PI);
		float thetaDeg = delta * 360;
		float posn_x = (float) Math.cos(theta); // same as your circle code in your notes
		float posn_y = (float) Math.sin(theta);



		if (isCameraFollowing) {
			// Calculate camera position based on the cyberman's position
			float cameraDistance = 600.0f; // Distance behind the cyberman
			float cameraHeight = 1500.0f; // Height above the cyberman

			// Calculate the camera's position relative to the cyberman
			float angleRadians = (float) Math.toRadians(currentAngle + 270); // Convert angle to radians
			float cameraOffsetX = (float) Math.sin(angleRadians) * cameraDistance;
			float cameraOffsetZ = (float) Math.cos(angleRadians) * cameraDistance;

			float cameraX;
			float cameraY;
			float cameraZ;
			if (isRotating) {
				cameraX = paceZ1 + cameraOffsetX;
				cameraY = paceY1 + cameraHeight;
				cameraZ = paceX1 + cameraOffsetZ;
			} else {
				cameraX = paceZ1 * 100;
				cameraY = paceY1 + cameraHeight;
				cameraZ = paceX1 * 100;
			}

			System.out.println("paceX1: " + paceX1 + " paceZ1: " + paceZ1);

			// Cyberman's position (where the camera should look at)
			float cybermanX = paceX1 + 100;
			float cybermanY = paceY1 + 1500;
			float cybermanZ = paceZ1 - 50;

//		System.out.println("CybermanX: " + cybermanX);

			// Set the camera
			glMatrixMode(GL_PROJECTION);
			glLoadIdentity();
			GLU.gluPerspective(45f, 1.5f, 2.8f, 20000);
			glMatrixMode(GL_MODELVIEW);
			GLU.gluLookAt(cameraX, cameraY, cameraZ, // Camera position
					cybermanX, cybermanY, cybermanZ, // Look at position (cyberman position)
					0, 1, 0); // Up direction

		} else {
			glMatrixMode(GL_PROJECTION);
			glLoadIdentity();
			GLU.gluPerspective(45f, 1.5f, 2.8f, 20000);
			glMatrixMode(GL_MODELVIEW);
			GLU.gluLookAt(-350, 1250, -3500, 1000, 800, -500, 0, 1, 0);
		}





		// draw the sky and the land
//		World world = new World(texturesWorld);
		world.drawWorld();




		// Hibernation Chamber of the cyberman in the screen
		glPushMatrix();
		TexCube hibernationChamber = new TexCube();
		glColor3f(white[0], white[1], white[2]);
		glTranslatef(300, 300, 0);
		glScalef(100f, 400f, 100f);


		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);

		// Bind the texture to the surface
		Color.white.bind();
		textureCube.bind();
		glEnable(GL_TEXTURE_2D);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glClearColor(0.8f, 0.8f, 0.8f, 0.0f);

		hibernationChamber.drawTexCube();
		glPopMatrix();



		// doorway to BJUT
		glPushMatrix();
		TexCube doorway = new TexCube();
		glColor3f(white[0], white[1], white[2]);
//		glMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, Utils.ConvertForGL(pink));
		glTranslatef(800, 600, -4500);
		glScalef(500f, 500f, 8f);
		glRotatef(90, 0, 0, 0);


		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);

		// Bind the texture to the surface
		Color.white.bind();
		textureCube2.bind();
		glEnable(GL_TEXTURE_2D);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glClearColor(0.8f, 0.8f, 0.8f, 0.0f);

		doorway.drawTexCube();
		glPopMatrix();






		// draw the shadow of the cyberman
		glPushMatrix();
		Shadow shadow = new Shadow();
		glTranslatef(105, 240, 0);
		glScalef(90f, 90f, 90f);
		glRotatef(-90.0f, 0, 1, 0);
		if (!BadAnimation) {
			glTranslatef(paceX1, paceY1, paceZ1);

		} else {

			// bad animation version
//			glTranslatef(posn_x * 3.0f, 0.0f, posn_y * 3.0f);
		}

		shadow.drawShadow(delta, !BadAnimation); // give a delta for the Human object to be animated

		glPopMatrix();


		glPushMatrix();
		glTranslatef(initialCybermanX, initialCybermanY, initialCybermanZ);
		glScalef(90f, 90f, 90f);
		glRotatef(-90.0f, 0, 1, 0);



		if (!BadAnimation) {


			glTranslatef(paceX1, paceY1, paceZ1);
			glRotatef(currentAngle, 0, 1, 0);
			if (turnOnLaserEffect) {
				cyberman.setLaserEffect();
			} else {
				cyberman.forceTurnOffLaserEffect();

			}




		}

		cyberman.drawCyberman(delta, !BadAnimation); // give a delta for the Human object ot be animated

		glPopMatrix();




		glPushMatrix();
		glTranslatef(initialHumanX, initialHumanY, initialHumanZ);
		glScalef(90f, 90f, 90f);
		glRotatef(90.0f, 0, 1, 0);


		if (!BadAnimation) {

			glTranslatef(paceX2, paceY2, paceZ2);

		}

		human.drawHuman(delta, !BadAnimation); // give a delta for the Human object ot be animated

		glPopMatrix();



	}
	private boolean checkLaserCollision() {


		float cybermanX = paceX1;
		float humanX = paceX2;

		float cybermanY = paceY1;
		float humanY = paceY2;

		float cybermanZ = paceZ1;
		float humanZ = paceZ2;

		float currentAngleCyberman = calculateCybermanCurrentAngle();
		float angleToHuman = calculateHumanCurrentAngle();

		// Adjust for 360-degree wraparound
		float angleDifference = Math.abs(currentAngleCyberman - angleToHuman);
		angleDifference = (angleDifference + 180) % 360 - 180; // Normalize difference to [-180, 180]

		System.out.println("CybermanAngle: " + currentAngleCyberman + " AngleToHuman: " + angleToHuman + " AngleDifference: " + angleDifference);
		System.out.println("CybermanY: " + cybermanY + " HumanY: " + humanY);

		// Check if Cyberman is facing the Human within a certain tolerance
		return Math.abs(cybermanY - humanY) < 3 && Math.abs(angleDifference) < 20;
	}

	private void updateHumanPosition(int delta) {

		paceX2 += humanSpeed * humanDirection;


		if (paceX2 > 20 || paceX2 < -20) {
			humanDirection *= -1;
		}
	}

	private float calculateHumanCurrentAngle() {
		// Calculate the actual position of the Cyberman and the Human
		float cybermanPosX = initialCybermanX + paceX1; // Cyberman's current X position
		float cybermanPosZ = initialCybermanZ + paceZ1; // Cyberman's current Z position
		float humanPosX = initialHumanX + paceX2  + 2498;       // Human's current X position
		float humanPosZ = initialHumanZ + paceZ2 - 228;       // Human's current Z position

		// Calculate the differences in positions
		float deltaX = humanPosX - cybermanPosX; // Difference in X from Cyberman to Human
		float deltaZ = humanPosZ - cybermanPosZ; // Difference in Z from Cyberman to Human

		// Debugging print statements (optional)
		System.out.println("Cyberman Position - X: " + cybermanPosX + ", Z: " + cybermanPosZ);
		System.out.println("Human Position - X: " + humanPosX + ", Z: " + humanPosZ);
		System.out.println("Delta - X: " + deltaX + ", Z: " + deltaZ);

		// Calculate the angle in radians
		float angleRadians = (float) Math.atan2(deltaZ, deltaX);

		// Convert radians to degrees and normalize
		float angleDegrees = (float) Math.toDegrees(angleRadians);
		angleDegrees = (angleDegrees + 360) % 360;

		return angleDegrees;
	}



	private float calculateCybermanCurrentAngle() {


		currentAngle = currentAngle % 360;
		float angleDegrees = currentAngle;

		// Normalize the angle
		if (angleDegrees < 0) {
			angleDegrees += 360;
		}

		return angleDegrees;
	}

	private void resetHumanPosition() {
		paceX2 = initialHumanX - 2785; // Reset human X position
		paceY2 = initialHumanY - 380;  // Reset human Y position
		paceZ2 = initialHumanZ - 200;  // Reset human Z position
	}
	private boolean isCollidingWithSpace(float z) {
		return z >= spaceMinZ && z <= spaceMaxZ;
	}

	private int checkLaserBallCollision() {
		float currentAngleCyberman = calculateCybermanCurrentAngle();


		float angleDifference = Math.abs(currentAngleCyberman - 270);
//		angleDifference = (angleDifference + 180) % 360 - 180; // Normalize difference to [-180, 180]

		System.out.println("CybermanAngle: " + currentAngleCyberman + " AngleDifference: " + angleDifference);

		if (Math.abs(angleDifference) <= 8 && Math.abs(angleDifference) > 0) {
			return 0;
		} else if (Math.abs(angleDifference) <= 20 && Math.abs(angleDifference) > 8) {
			return 1;
		} else {
			return -1;
		}


		// Check if Cyberman is facing the Human within a certain tolerance
//		return Math.abs(angleDifference) < 20;

	}


	public static void main(String[] argv) {
		MainWindow hello = new MainWindow();
		hello.start();
	}

	Texture texture;
	Texture textureCube;
	Texture textureCube2;

	// An array for storing textures to make it easier to pass it to a Human object.
	Texture[] texturesHumanoid = new Texture[20];
	Texture[] texturesWorld = new Texture[5];

	Cyberman cyberman;
	Human human;




	public void init() throws IOException {

		texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/earthspace.png"));
		System.out.println("Texture loaded okay ");

		textureCube = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/cabinShell.png"));
		System.out.println("TextureCube loaded okay ");
		textureCube2 = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/bjut_picture.png"));
		System.out.println("TextureCube2 loaded okay ");

		texturesHumanoid[0] = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/texture2.png"));
		System.out.println("Texture0 for the Cyberman loaded okay ");

		texturesHumanoid[1] = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/texture5.png"));
		System.out.println("Texture1 for the Cyberman loaded okay ");

		texturesHumanoid[2] = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/texture6.png"));
		System.out.println("Texture2 for the Cyberman loaded okay ");

		texturesHumanoid[3] = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/texture4.png"));
		System.out.println("Texture3 for the Cyberman loaded okay ");
		texturesHumanoid[4] = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/textureSkin.png"));
		System.out.println("Texture0 for the Human loaded okay ");
		texturesHumanoid[5] = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/textureCloth1.png"));
		System.out.println("Texture1 for the Human loaded okay ");
		texturesHumanoid[6] = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/textureCloth2.png"));
		System.out.println("Texture2 for the Human loaded okay ");


		texturesWorld[0] = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/cityLand.png"));
		System.out.println("textureWorld0 loaded okay ");
		texturesWorld[1] = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/burningSky2.png"));
		System.out.println("textureWorld1 loaded okay ");
		texturesWorld[2] = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/burningSky2.png"));
		System.out.println("textureWorld2 loaded okay ");
		texturesWorld[3] = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/Project_Texture_2023.png"));
		System.out.println("textureWorld3 loaded okay ");
		texturesWorld[4] = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/galaxy.png"));
		System.out.println("textureWorld4 loaded okay ");

		world = new World(texturesWorld);

		cyberman = new Cyberman(texturesHumanoid);
		human = new Human(texturesHumanoid);



	}
}
