package teamtnt;

/***************************************************************
* file: FPCameraController.java
* authors: Kenneth L. Ung, Dongri Zhu, Rafael Soto-Salinas
* class: CS 4450.01 – Computer Graphics
*
* assignment: semester project
* date last modified: 4/30/2019
*
* purpose: camera controller class where the camera is created and 
* method to move camera are provided. this is where the rendering is also
* located for the cube.
* also adds keyboard inputs available
* We added extra functionality in this project, where if you press "E", we will enter into collision mode.
* We have also binded the keys F1-F5, to change the terrain to different textures.
* Finally we made the world dynamically generated as soon as you pass a certain X or Z value. 
*
****************************************************************/ 


import java.util.ArrayList;
import java.util.Random;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.Sys;

public class FPCameraController {
    private Vector3f position = null;
    private Vector3f IPosition = null;   
    private int currentChunk = 0;
    private float yaw = 0;
    private float pitch = 0;
    private long currentTime = 0;
    private ArrayList<Chunk> gameWorld = new ArrayList<Chunk>();
    private Chunk world;
    private Chunk test;
    private int CHUNK_SIZE = world.CHUNK_SIZE * world.CUBE_LENGTH;
    private int maxX = 60;
    private int maxZ = 60;
    private int minX = 0;
    private int minZ = 0;
    private boolean dynamic = false;
    /**
     * Constructor for a Camera Controller, we set the position of the Camera at the X,Y,Z location.
     * @param x starting X
     * @param y starting Y
     * @param z starting Z
     */
    public FPCameraController(float x, float y, float z){
        position = new Vector3f(x,y,z);
        IPosition = new Vector3f(x,y,z);
        IPosition.x = 0f;
        IPosition.y = 15f;
        IPosition.z = 0f;
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
        glRotatef(pitch,1.0f,0f,0f);
        glRotatef(yaw,0f,1.0f,0f);
        glTranslatef(position.x, position.y, position.z);
    }
    // method: gameLoop
    // purpose: applies methods above and calls rendering
    // also adds keyboard inputs available
    // We added extra functionality in this project, where if you press "E", we will enter into collision mode.
    // We have also binded the keys F1-F5, to change the terrain to different textures.
    // Finally we made the world dynamically generated as soon as you pass a certain X or Z value.
    public void gameLoop() {
        world = new Chunk(0,0,0,0); //Default
        test = new Chunk(0,0,0,10); //Default
        gameWorld.add(world);
        int[] playerPosition = world.getStart(); //Used later to set the User to the highest point on the terrain.
        FPCameraController camera = new FPCameraController(-30f,-100f,-30f);
        //FPCameraController camera = new FPCameraController(-playerPosition[0] * 2,-playerPosition[1] * world.CUBE_LENGTH - (world.CHUNK_SIZE - 3.5f),-playerPosition[2] * world.CUBE_LENGTH);
        camera.world = world;
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
            if(Keyboard.isKeyDown(Keyboard.KEY_F1)){
                for(int i = 0; i < gameWorld.size(); i++) {
                    gameWorld.get(i).rebuildMesh(0, 0, 0, 0);
                }
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_F2)){
                for(int i = 0; i < gameWorld.size(); i++) {
                    gameWorld.get(i).rebuildMesh(0, 0, 0, 5);
                }            }
            if(Keyboard.isKeyDown(Keyboard.KEY_F3)){
                for(int i = 0; i < gameWorld.size(); i++) {
                    gameWorld.get(i).rebuildMesh(0, 0, 0, 6);
                }            }
            if(Keyboard.isKeyDown(Keyboard.KEY_F4)){
                for(int i = 0; i < gameWorld.size(); i++) {
                    gameWorld.get(i).rebuildMesh(0, 0, 0, 7);
                }            }
            if(Keyboard.isKeyDown(Keyboard.KEY_F5)){
                for(int i = 0; i < gameWorld.size(); i++) {
                    gameWorld.get(i).rebuildMesh(0, 0, 0, 8);
                }            }            
            if(Keyboard.isKeyDown(Keyboard.KEY_F6)){
                for(int i = 0; i < gameWorld.size(); i++) {
                    gameWorld.get(i).rebuildMesh(0, 0, 0, 9);
                }            }            
            if(Keyboard.isKeyDown(Keyboard.KEY_F7)){
                for(int i = 0; i < gameWorld.size(); i++) {
                    gameWorld.get(i).rebuildMesh(0, 0, 0, 10);
                }  
            }
	    if(Keyboard.isKeyDown(Keyboard.KEY_E)) {
	        //camera = new FPCameraController(0,-86,0);
	        camera = new FPCameraController(-20,-86,-30);
	        test.collisionDetection(0, 0, 0);
	        int blockHeights[][] = test.getHeights1();
	        	   
	        	   
                while(!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
		   	           
                    time = Sys.getTime();
		   
                    lastTime = time;

		   
                    dx = Mouse.getDX();
		   
                    dy = Mouse.getDY();
		   
                    camera.yaw(dx * mouseSensitivity);
		   
                    camera.pitch(dy * mouseSensitivity);
		   
                    
		   
                    if(Keyboard.isKeyDown(Keyboard.KEY_W) || Keyboard.isKeyDown(Keyboard.KEY_UP)){
		   
                        camera.collisionWalkForward(movementSpeed, blockHeights);
		   	
                    }
		   
                    if(Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_DOWN)){
		  
                        camera.collisionWalkBackwards(movementSpeed, blockHeights);

                    }
		   
                    if(Keyboard.isKeyDown(Keyboard.KEY_A) || Keyboard.isKeyDown(Keyboard.KEY_LEFT)){
		   
                        camera.collisionStrafeLeft(movementSpeed, blockHeights);
		   	
                    }
		   
                    if(Keyboard.isKeyDown(Keyboard.KEY_D) || Keyboard.isKeyDown(Keyboard.KEY_RIGHT)){
		   
                        camera.collisionStrafeRight(movementSpeed, blockHeights);
		   	
                    }
		   
                    if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
		   
                        float powerJump = 0.8f;
		   	
                        camera.collisionJump(powerJump);

                    }
		   
                    if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
		   
                        camera.collisionMoveDown(movementSpeed, blockHeights);
		   	
                    }
            if(Keyboard.isKeyDown(Keyboard.KEY_F1)){
                test.rebuildMesh(0, 0, 0, 0);
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_F2)){
                test.rebuildMesh(0,0,0,5);
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_F3)){
                test.rebuildMesh(0, 0, 0, 6);
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_F4)){
                test.rebuildMesh(0, 0, 0, 7);
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_F5)){
                test.rebuildMesh(0, 0, 0, 8);
            }            
            if(Keyboard.isKeyDown(Keyboard.KEY_F6)){
                test.rebuildMesh(0, 0, 0, 9);
            }            
            if(Keyboard.isKeyDown(Keyboard.KEY_F7)){
                test.rebuildMesh(0, 0, 0, 10);
            }                      
		   	            
		    //Gravity
		   
                                 
                    camera.gravity(movementSpeed, blockHeights);
		   	            
		   
                    glLoadIdentity();
		   
                    camera.lookThrough();
		   
                    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		   
                    test.render();
		   	            
		   
                    Display.update();
		   
                    Display.sync(60);                   
		
                }
	        
                Display.destroy();
		
            }            
            glLoadIdentity();
            camera.lookThrough();
            //test.render();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            for(int i = 0; i < gameWorld.size(); i++) {
                gameWorld.get(i).render();
            }
            checkEndOfWorld(camera);
            setMinandMax(camera);
            System.out.println("Camera Position X: " + camera.position.x + "\nCamera Position Z: " + camera.position.z);
            System.out.println("Current MaxX: " + maxX + "\nCurrent MinX: " + minX + 
                    "\nCurrent MaxZ: " + maxZ + "\nCurrent MinZ: " + minZ);
            Display.update();
            Display.sync(60);
        }
        Display.destroy();
        
    }




    /**
     * Checks if we have reached the end of a Chunk, we do this by checking it with the
     * corresponding X and Z values. If there was a Chunk created at that place, then we should just
     * ignore it and create nothing. 
     * @param camera we are passing the user in to get their current position on the world.
     */
    private void checkEndOfWorld(FPCameraController camera) {
        Random r = new Random();
        /**
         * Choose between 0-3
         */
        int choice = r.nextInt(4);
        /**
         * We know that the Camera is negative, so the Maximum X has to be positive.
         * Now we check if we are getting close to an edge case and if we are, we check to make sure
         * that there are no Chunk already defined there, if there isn't we build a new Chunk.
         */
        /**
         * Currently only positive numbers can be stored, negative instances don't work.
         */
        if(camera.position.x <= 0) {
            if(Math.abs(camera.position.x) + 4 >= maxX) {
                if(!checkIfChunk(maxX,minZ)) {
                    gameWorld.add(new Chunk(maxX,0,minZ, choice));
                }
            }
            /**
             * Assuming we are moving negative direction, if it is lower than the 
             * Minimum, we would build a new chunk.
             */
            else if(Math.abs(camera.position.x) - 4 <= minX) {
                if(!checkIfChunk(minX - CHUNK_SIZE,minZ)) {
                    gameWorld.add(new Chunk(minX - CHUNK_SIZE,0,minZ, choice));
                }
            }
        }
        else if(camera.position.x >= 0) {
            /**
             * Coord System --> X in negative direction. We check to see if we are close to minimum X value.
             */
            if(-camera.position.x - 4 <= minX) {
                if(!checkIfChunk(minX - CHUNK_SIZE,minZ)) {
                    gameWorld.add(new Chunk(minX - CHUNK_SIZE, 0, minZ, choice));
                }
            }
            else if(-camera.position.x + 4 >= maxX) {
                if(!checkIfChunk(maxX,minZ)) {
                    gameWorld.add(new Chunk(maxX, 0, minZ, choice));
                }
            }
        }
        if(camera.position.z <= 0) {
            if(Math.abs(camera.position.z) + 4 >= maxZ) {
                if(!checkIfChunk(minX,maxZ)) {
                    gameWorld.add(new Chunk(minX,0,maxZ, choice));
                }
            }
            /**
             * Assuming we are moving negative direction, if it is lower than the 
             * Minimum, we would build a new chunk.
             */
            else if(Math.abs(camera.position.z) - 4 <= minZ) {
                if(!checkIfChunk(minX,minZ - CHUNK_SIZE)) {
                    gameWorld.add(new Chunk(minX,0,minZ - CHUNK_SIZE, choice));
                }
            }
        }
        else if(camera.position.z >= 0) {
            /**
             * Coord System --> X in negative direction. We check to see if we are close to minimum X value.
             */
            if(-camera.position.z - 4 <= minZ) {
                if(!checkIfChunk(minX,minZ - CHUNK_SIZE)) {
                    gameWorld.add(new Chunk(minX, 0, minZ - CHUNK_SIZE, choice));
                }
            }
            else if(-camera.position.z + 4 >= maxZ) {
                if(!checkIfChunk(minX,maxZ)) {
                    gameWorld.add(new Chunk(minX, 0, maxZ, choice));
                }
            }
        }        

    } 
    /**
     * Checks if there is already a Chunk generated at the current location.
     * @param positionX
     * @param positionZ
     * @return 
     */
    private boolean checkIfChunk(int positionX, int positionZ) {
        for(int i = 0; i < gameWorld.size(); i++) {
            if((gameWorld.get(i).getX() == positionX) && (gameWorld.get(i).getZ() == positionZ)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Checks our current position and compares it to the current Minimum and Maximum values of X and Z.
     * If we are greater than or less than, we will update accordingly.
     * @param camera the players current position on the world.
     */
    private void setMinandMax(FPCameraController camera) {
        /**
         * This portion first checks if our X value is less than or equal to 0.
         * If we are less than or equal to 0, that means that our X value is in the positive direction (Coord System)
         * We now know that since X is in the positive direction, we take the abs value (convert to Coord System), and check to see if
         * that value is greater than our current Maximum, if it is, we know that we are in a new Chunk. That Chunk Maximum X and Minimum X
         * value would need to be updated.
         * Example :
         * We currently have Max X : 60, Min X : 0, we move past 60 --> Math.abs(-61), since we are above 60, we update the Maximum and Minimum
         * to 60 higher. 
         * ----------------------------------------------------------------------------------------------------------
         * Now we also know just because the camera is in the positive x direction (Coordinate System), it doesn't mean that we are going towards a Maximum,
         * we could also be going towards a minimum.
         * So now we check that if our current position is less than the Minimum Value, if it is we would decrement accordingly.
         */
        if((camera.position.x <= 0)) { // Positive X direction (coord), right now we know that X is negative (positive in Coord
            if(Math.abs(camera.position.x) > maxX) {
                maxX += CHUNK_SIZE;
                minX += CHUNK_SIZE;
            }
            else if(Math.abs(camera.position.x) < minX ) {
                minX -= CHUNK_SIZE;
                maxX -= CHUNK_SIZE;
            }

        }
        else if((camera.position.x >= 0)) { // We know that we are moving left, Coord( - X )
            if(-camera.position.x < minX) { 
                maxX -= CHUNK_SIZE;
                minX -= CHUNK_SIZE;
            }
            else if(-camera.position.x > maxX) {
                maxX += CHUNK_SIZE;
                minX += CHUNK_SIZE;
            }
        }
        if((camera.position.z <= 0)) { // Positive X direction (coord), right now we know that X is negative (positive in Coord
            if(Math.abs(camera.position.z) > maxZ) {
                maxZ += CHUNK_SIZE;
                minZ += CHUNK_SIZE;
            }
            else if(Math.abs(camera.position.z) < minZ ) {
                minZ -= CHUNK_SIZE;
                maxZ -= CHUNK_SIZE;
            }

        }
        else if((camera.position.z >= 0)) { // We know that we are moving left, Coord( - X )
            if(-camera.position.z < minZ) { 
                maxZ -= CHUNK_SIZE;
                minZ -= CHUNK_SIZE;
            }
            else if(-camera.position.z > maxZ) {
                maxZ += CHUNK_SIZE;
                minZ += CHUNK_SIZE;
            }
        }              
    }

    /**
     * Movement for Collision.
     * @param distance
     * @param blockHeights 
     */

	public void collisionWalkForward(float distance, int[][] blockHeights){
        float xOffset = distance *(float) Math.sin(Math.toRadians(yaw));
        float zOffset = distance *(float) Math.cos(Math.toRadians(yaw));
        position.x -= xOffset;
        position.z += zOffset;
        
        // wall collision detection
        wallCollisionDetection(blockHeights);
        
        // can not pass the edges of the world
        cannotPassEdges();
    }

        // Movement for collision
    public void collisionWalkBackwards(float distance, int[][] blockHeights){
        float xOffset = distance *(float) Math.sin(Math.toRadians(yaw));
        float zOffset = distance *(float) Math.cos(Math.toRadians(yaw));
        position.x += xOffset;
        position.z -= zOffset;   
        
        // wall collision detection
        wallCollisionDetection(blockHeights);
        
    	// can not pass the edges of the world
        cannotPassEdges();
    }

    // MOvement for collision.
    public void collisionStrafeLeft(float distance, int[][] blockHeights){
        float xOffset = distance *(float) Math.sin(Math.toRadians(yaw - 90));
        float zOffset = distance *(float) Math.cos(Math.toRadians(yaw - 90));
        position.x -= xOffset;
        position.z += zOffset; 
        
        // wall collision detection
        wallCollisionDetection(blockHeights);
        
        // can not pass the edges of the world
        cannotPassEdges();
    }

    // Movement for Collision
    public void collisionStrafeRight(float distance, int[][] blockHeights){
        float xOffset = distance *(float) Math.sin(Math.toRadians(yaw + 90));
        float zOffset = distance *(float) Math.cos(Math.toRadians(yaw + 90));
        position.x -= xOffset;
        position.z += zOffset;
        
        // wall collision detection
        wallCollisionDetection(blockHeights);
    
        // can not pass the edges of the world
        cannotPassEdges();
    }
    
    /**
     * Checks to see if the next Block is a wall or not.
     * @param blockHeights 
     */
	private void wallCollisionDetection(int[][] blockHeights) {
		
		int leftBlockHeight=-90, rightBlockHeight=-90, frontBlockHeight=-90, backBlockHeight=-90;

        if(position.x > -57)
        	leftBlockHeight = getBlockHeight(position.x-2, position.z, blockHeights);
        if(position.x < -1)
        	rightBlockHeight = getBlockHeight(position.x+2, position.z, blockHeights);
        if(position.z > -56)
        	frontBlockHeight = getBlockHeight(position.x, position.z-2, blockHeights);
        if(position.z < 0)
        	backBlockHeight = getBlockHeight(position.x, position.z+2, blockHeights);
        
        
        int midPointX = Math.round(position.x/2)*2;
        
        if(position.y > leftBlockHeight) {
        	if(position.x < midPointX-0.5f)
        		position.x = midPointX-0.5f;
        }
        
        if(position.y > rightBlockHeight) {
        	if(position.x > midPointX-0.5f)
        		position.x = midPointX-0.5f;
        }
        
        int midPointZ = Math.round((position.z-1)/2)*2 + 1;
        
        if(position.y > frontBlockHeight) {
        	if(position.z < midPointZ-0.5f)
        		position.z = midPointZ-0.5f;
        }
        
        if(position.y > backBlockHeight) {
        	if(position.z > midPointZ-0.5f)
        		position.z = midPointZ-0.5f;
        }
		
	}

        /**
         * calculating Jumping
         * */
    public void collisionJump(float distance){
        position.y -= distance;
        
        // can not pass the edges of the world
        if (position.y < -90)
        	position.y = -90;
    }
    /**
     * movement for collision.
     * @param distance
     * @param blockHeights 
     */
    private void collisionMoveDown(float distance, int[][] blockHeights){
    	position.y += distance;
    	

    	int blockHeight = getBlockHeight(position.x, position.z, blockHeights);

        
        if (position.y > blockHeight)
        	position.y = blockHeight;
    }
    
    /**
     * Sets the gravity of the user.
     * @param distance
     * @param blockHeights 
     */
    private void gravity(float distance, int[][] blockHeights) {
    	float gravity = 1.15f;
    	position.y += distance*gravity;
    	
    	// collision detection
    	int blockHeight = getBlockHeight(position.x, position.z, blockHeights);     
        if (position.y > blockHeight)
        	position.y = blockHeight;
		
	}
   /**
    * Gets the height of the current Block the user is on.
    * @param x
    * @param z
    * @param blockHeights
    * @return 
    */
    private int getBlockHeight(float x, float z, int[][] blockHeights) {
    	int blockX = 0, blockZ = 0;
    	
    	// our chunks in the X-axis are from 1 to -59
    	// 1,0 are stored in int[0][] blockHeights, -1,-2 are stored in int[1][], and so on, since each block size is 2
    	// 2 is the cube size
    	if(position.x < 0)
    		blockX = Math.round(Math.round(x)/(-2));
    	
    	// our chunks in the Z-axis are from 2 to -58
    	// 2,1 are stored in int[][0] blockHeights, 0,-1 are stored in int[][1], and so on, since each block size is 2
    	// 2 is the cube size
    	if(position.z < 1)
    		blockZ = Math.round((Math.round(z)-1)/(-2));
    	
    	// 28 is the height of the camera
		return (-2)*blockHeights[blockX][blockZ] - 28;
	}
    
    /**
     * Makes sure the user cannot pass an edge and bounds them.
     */
    private void cannotPassEdges() {
    	if(position.x > 0)
        	position.x = 0;
        if(position.z > 1)
        	position.z = 1;
        if(position.x < -58)
        	position.x = -58;
        if(position.z < -57)
        	position.z = -57;
	}
    
 
}
