# Computer-Graphics-Project-2

An interactive animated scene built with Lightweight Java Game Library (LWJGL).

This OpenGL application is created based on my Project 1 for COMP3033J Computer Graphics, which was built upon my previous assignments and the foundational code provided by the course. Compared to the Project 1, this application allows the user to control and navigate the character's movements and actions.

## Manual for interaction

**Keyboard:**
+ W key: move to the west
+ E key: move to the east
+ N key: move to the north
+ S key: move to the south
+ I key: move upward
+ O key: move downward
+ Q key: rotate the character
+ C key: change the camera perspective

*Assuming the direction of the galaxy photo is the north.*

**Mouse:**
+ Click the mouse to fire laser
  + *If the laser follows the direction of the Human object, it will force the Human to back to the initial place; if it hits a balloon, the position of the balloon will be set to the initial value.*


## Screenshot

![screenshot.png](screenshot.png)

## Operating Env Requirements

**JDK 1.8.x, LWJGL 2.x**

*Note that ARM-based Macs may encounter unexpected weird errors when running this application.*