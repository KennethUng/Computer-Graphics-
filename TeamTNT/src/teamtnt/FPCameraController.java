package teamtnt;

/***************************************************************
* file: FPCameraController.java
* authors: Kenneth L. Ung, Dongri Zhu, Rafael Soto-Salinas
* class: CS 4450.01 – Computer Graphics
*
* assignment: semester project
* date last modified: 3/11/2019
*
* purpose: camera controller class where the camera is created and 
* method to move camera are provided. this is where the rendering is also
* located for the cube.
*
****************************************************************/ 


import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.Sys;

public class FPCameraController {
    private Vector3f position = null;
    private Vector3f IPosition = null;
    
    private float yaw = 0;
    private float pitch = 0;
    private Vector3Float me;
    
    public FPCameraController(float x, float y, float z){
        position = new Vector3f(x,y,z);
        IPosition = new Vector3f(x,y,z);
        IPosition.x = 0;
        IPosition.y = 15;
        IPosition.z = 0;
    }
    // method: yaw
    // purpose: increments the camera's yaw rotation
    public void yaw(float amount){
        yaw += amount;
    }
    // method: pitch
    // purpose: increments the camera's pitch rotation
    public void pitch(float amount){
        pitch -= amount;
    }
    // method: walkForward
    // purpose: moves camera position forward
    public void walkForward(float distance){
        float xOffset = distance *(float) Math.sin(Math.toRadians(yaw));
        float zOffset = distance *(float) Math.cos(Math.toRadians(yaw));
        position.x -= xOffset;
        position.z += zOffset;
    }
    // method: walkBackwards
    // purpose: moves camera position backwards
    public void walkBackwards(float distance){
        float xOffset = distance *(float) Math.sin(Math.toRadians(yaw));
        float zOffset = distance *(float) Math.cos(Math.toRadians(yaw));
        position.x += xOffset;
        position.z -= zOffset;    
    }
    // method: strafeLeft
    // purpose: moves camera position towards the left
    public void strafeLeft(float distance){
        float xOffset = distance *(float) Math.sin(Math.toRadians(yaw - 90));
        float zOffset = distance *(float) Math.cos(Math.toRadians(yaw - 90));
        position.x -= xOffset;
        position.z += zOffset; 
    }
    // method: strageRight
    // purpose: moves camera position towards the right
    public void strafeRight(float distance){
        float xOffset = distance *(float) Math.sin(Math.toRadians(yaw + 90));
        float zOffset = distance *(float) Math.cos(Math.toRadians(yaw + 90));
        position.x -= xOffset;
        position.z += zOffset;     
    }
    // method: moveUp
    // purpose: moves camera position up
    public void moveUp(float distance){
        position.y -= distance;
    }
    // method: moveDown
    // purpose: moves camera position down
    public void moveDown(float distance){
        position.y += distance;
    }
    // method: lookThrough
    // purpose: makes it look through the camera by 
    // providing the rotations and translations
    public void lookThrough(){
        glRotatef(pitch,1,0,0);
        glRotatef(yaw,0,1,0);
        glTranslatef(position.x, position.y, position.z);
    }
    // method: gameLoop
    // purpose: applies methods above and calls rendering
    // also adds keyboard inputs available
    public void gameLoop() {
        Chunk test = new Chunk(0,0,0);
        FPCameraController camera = new FPCameraController(0,0,-30);
        float dx = 0;
        float dy = 0;
        float dt = 0;
        float lastTime = 0;
        long time = 0;
        float mouseSensitivity = 0.09f;
        float movementSpeed = .35f;
        Mouse.setGrabbed(true);
        glEnable(GL_DEPTH_TEST); // to not make sides transparent
        while(!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
            time = Sys.getTime();
            lastTime = time;
            
            dx = Mouse.getDX();
            dy = Mouse.getDY();
            camera.yaw(dx * mouseSensitivity);
            camera.pitch(dy * mouseSensitivity);
            
            if(Keyboard.isKeyDown(Keyboard.KEY_W) || Keyboard.isKeyDown(Keyboard.KEY_UP)){
                camera.walkForward(movementSpeed);
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_DOWN)){
                camera.walkBackwards(movementSpeed);
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_A) || Keyboard.isKeyDown(Keyboard.KEY_LEFT)){
                camera.strafeLeft(movementSpeed);
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_D) || Keyboard.isKeyDown(Keyboard.KEY_RIGHT)){
                camera.strafeRight(movementSpeed);
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
                camera.moveUp(movementSpeed);
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
                camera.moveDown(movementSpeed);
            }
            glLoadIdentity();
            camera.lookThrough();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            test.render();
            //render();
            
            Display.update();
            Display.sync(60);
        }
        Display.destroy();
    }
    // method: render
    // purpose: what's to be drawn 
    private void render(){
            try{
                
                glBegin(GL_QUADS);
                
                //top
                glColor3f(1.0f,1.0f,0.0f); 
                glVertex3f(2.0f, 2.0f, -2.0f);
                glVertex3f(-2.0f, 2.0f, -2.0f);
                glVertex3f(-2.0f, 2.0f, 2.0f);
                glVertex3f(2.0f, 2.0f, 2.0f);
                
                //bottom
                glColor3f(1.0f,1.0f,1.0f); 
                glVertex3f(2.0f, -2.0f, 2.0f);
                glVertex3f(-2.0f, -2.0f, 2.0f);
                glVertex3f(-2.0f, -2.0f, -2.0f);
                glVertex3f(2.0f, -2.0f, -2.0f);
                
                //front
                glColor3f(1.0f,0.0f,0.0f); 
                glVertex3f(2.0f, 2.0f, 2.0f);
                glVertex3f(-2.0f, 2.0f, 2.0f);
                glVertex3f(-2.0f, -2.0f, 2.0f);
                glVertex3f(2.0f, -2.0f, 2.0f);
                
                //back
                glColor3f(0.5f,0.5f,0.5f); 
                glVertex3f(2.0f, -2.0f, -2.0f);
                glVertex3f(-2.0f, -2.0f, -2.0f);
                glVertex3f(-2.0f, 2.0f, -2.0f);
                glVertex3f(2.0f, 2.0f, -2.0f);                
                
                //left
                glColor3f(0.0f,0.0f,1.0f); 
                glVertex3f(-2.0f, 2.0f, 2.0f);
                glVertex3f(-2.0f, 2.0f, -2.0f);
                glVertex3f(-2.0f, -2.0f, -2.0f);
                glVertex3f(-2.0f, -2.0f, 2.0f);                
                
                //right
                glColor3f(0.0f,1.0f,1.0f); 
                glVertex3f(2.0f, 2.0f, -2.0f);
                glVertex3f(2.0f, 2.0f, 2.0f);
                glVertex3f(2.0f, -2.0f, 2.0f);
                glVertex3f(2.0f, -2.0f, -2.0f);      
                glEnd();
                      
               glBegin(GL_LINE_LOOP);
                glColor3f(1.0f,1.0f,0.0f); 
                glVertex3f(2.0f, 2.0f, -2.0f);
                glVertex3f(-2.0f, 2.0f, -2.0f);
                glVertex3f(-2.0f, 2.0f, 2.0f);
                glVertex3f(2.0f, 2.0f, 2.0f);
                glEnd();
                
                
               glBegin(GL_LINE_LOOP);
                glColor3f(1.0f,1.0f,1.0f); 
                glVertex3f(2.0f, -2.0f, 2.0f);
                glVertex3f(-2.0f, -2.0f, 2.0f);
                glVertex3f(-2.0f, -2.0f, -2.0f);
                glVertex3f(2.0f, -2.0f, -2.0f);
                glEnd();
                
               glBegin(GL_LINE_LOOP);
                glColor3f(1.0f,0.0f,0.0f); 
                glVertex3f(2.0f, 2.0f, 2.0f);
                glVertex3f(-2.0f, 2.0f, 2.0f);
                glVertex3f(-2.0f, -2.0f, 2.0f);
                glVertex3f(2.0f, -2.0f, 2.0f);
                glEnd();
                
                
               glBegin(GL_LINE_LOOP);
                glColor3f(0.5f,0.5f,0.5f); 
                glVertex3f(2.0f, -2.0f, -2.0f);
                glVertex3f(-2.0f, -2.0f, -2.0f);
                glVertex3f(-2.0f, 2.0f, -2.0f);
                glVertex3f(2.0f, 2.0f, -2.0f); 
                glEnd();
                
                glBegin(GL_LINE_LOOP);
                glColor3f(0.0f,0.0f,1.0f); 
                glVertex3f(-2.0f, 2.0f, 2.0f);
                glVertex3f(-2.0f, 2.0f, -2.0f);
                glVertex3f(-2.0f, -2.0f, -2.0f);
                glVertex3f(-2.0f, -2.0f, 2.0f);    
                glEnd();
                
                glBegin(GL_LINE_LOOP);
                glColor3f(0.0f,1.0f,1.0f); 
                glVertex3f(2.0f, 2.0f, -2.0f);
                glVertex3f(2.0f, 2.0f, 2.0f);
                glVertex3f(2.0f, -2.0f, 2.0f);
                glVertex3f(2.0f, -2.0f, -2.0f);   
                glEnd();
            }
            catch(Exception e){
            }
    }    
}
