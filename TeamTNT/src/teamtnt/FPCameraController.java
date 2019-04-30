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


import java.util.ArrayList;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.Sys;

public class FPCameraController {
    private Vector3f position = null;
    private Vector3f IPosition = null;
    private static long lastFrameTime;
    private static float delta;    
    
    private float yaw = 0;
    private float pitch = 0;
    private boolean solid = false;
    private long currentTime = 0;
    private boolean falling = false;
    private float speed = 0;
    private long airTime = 0;
    private ArrayList<Chunk> gameWorld = new ArrayList<Chunk>();
    private Chunk world;
    private Chunk test;
    private int maxX = 60;
    private int maxZ = 60;
    private int minX = 0;
    private int minZ = 0;
    
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
        
        if(solid) {
            if(currLocation(xOffset * -1, zOffset)) {
                if(!falling) {
                    falling = true;
                    airTime = Sys.getTime();
                }
            }
        }
        else {
            position.x -= xOffset;
            position.z += zOffset;
        }
    }
    // method: walkBackwards
    // purpose: moves camera position backwards
    public void walkBackwards(float distance){
        float xOffset = distance *(float) Math.sin(Math.toRadians(yaw));
        float zOffset = distance *(float) Math.cos(Math.toRadians(yaw));
        if(solid) {
            if(currLocation(xOffset, zOffset * -1)) {
                if(!falling) {
                    falling = true;
                    airTime = Sys.getTime();
                }
            }
        }
        else {
            position.x += xOffset;
            position.z -= zOffset;  
        }
    }
    // method: strafeLeft
    // purpose: moves camera position towards the left
    public void strafeLeft(float distance){
        float xOffset = distance *(float) Math.sin(Math.toRadians(yaw - 90));
        float zOffset = distance *(float) Math.cos(Math.toRadians(yaw - 90));
        if(solid) {
            if(currLocation(xOffset * -1, zOffset)) {
                if(!falling) {
                    falling = true;
                    airTime = Sys.getTime();
                }
            }
        }
        else {
            position.x -= xOffset;
            position.z += zOffset; 
        }
    }
    // method: strageRight
    // purpose: moves camera position towards the right
    public void strafeRight(float distance){
        float xOffset = distance *(float) Math.sin(Math.toRadians(yaw + 90));
        float zOffset = distance *(float) Math.cos(Math.toRadians(yaw + 90));
        if(solid) {
            if(currLocation(xOffset * -1, zOffset)) {
                if(!falling) {
                    falling = true;
                    airTime = Sys.getTime();
                }
            }
        }
        else {
            position.x -= xOffset;
            position.z += zOffset;  
        }
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
    public void gameLoop() {
        Chunk world2 = new Chunk(60,0,0);
        Chunk world3 = new Chunk(120,0,0);
        world = new Chunk(0,0,0);
        test = new Chunk(0,0,0);
        gameWorld.add(world);
        Chunk[] hello = new Chunk[3];
        hello[0] = world;
        hello[1] = world2;
        hello[2] = world3;
        int[] playerPosition = world.getStart();
        FPCameraController camera = new FPCameraController(-30f,-100f,-30f);
        //FPCameraController camera = new FPCameraController(-playerPosition[0] * 2,-playerPosition[1] * world.CUBE_LENGTH - (world.CHUNK_SIZE - 3.5f),-playerPosition[2] * world.CUBE_LENGTH);
        camera.world = world;
        for(int i = 0; i < 3; i++) {
            System.out.println(camera.position.x);
            System.out.println(camera.position.y);
            System.out.println(camera.position.z);            
        }
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

            if(Keyboard.isKeyDown(Keyboard.KEY_Q)) {                    
                checkPlayerStatus(camera, playerPosition);
            }
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
            if(camera.falling) {
                camera.drop(camera.speed);
            }
            glLoadIdentity();
            camera.lookThrough();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            //world.render();
            //world2.render();
            //world3.render();
            for(int i = 0; i < gameWorld.size(); i++) {
                gameWorld.get(i).render();
            }
            checkEndOfWorld(camera);
            setMinandMax(camera);
            System.out.println("Camera Position X: " + camera.position.x);
            System.out.println("Current MaxX: " + maxX + "\nCurrent MinX: " + minX + 
                    "\nCurrent MaxZ: " + maxZ + "\nCurrent MinZ: " + minZ);
            Display.update();
            Display.sync(60);
        }
        Display.destroy();
        
    }
    private boolean currLocation(float x, float z) {
        int playerX = (int) (Math.ceil(Math.abs(position.x)) / Chunk.CUBE_LENGTH);
        int playerZ = (int) (Math.ceil(Math.abs(position.z - 1)) / Chunk.CUBE_LENGTH);
        int changeX = (int) (Math.ceil(Math.abs(position.x + x)) / Chunk.CUBE_LENGTH);
        int changeZ = (int) (Math.ceil(Math.abs(position.z + z - 1)) / Chunk.CUBE_LENGTH);
        float currY;
        if(position.x + x >= -Chunk.CHUNK_SIZE * Chunk.CUBE_LENGTH && position.x + x < 1 && position.z + z >= -Chunk.CHUNK_SIZE * Chunk.CUBE_LENGTH && position.z + z < 2) {
            try {
                currY = -(world.getHeights()[changeX][changeZ] * Chunk.CUBE_LENGTH - (Chunk.CHUNK_SIZE - 3.5f));
                if(position.y <= currY) {
                    position.x += x;
                    position.z += z;
                    return true;
                } 
            } catch (ArrayIndexOutOfBoundsException outZ) {
                try {
                    currY = -(world.getHeights()[playerX][changeZ] * Chunk.CUBE_LENGTH - (Chunk.CHUNK_SIZE - 3.5f));
                    if(position.y <= currY) {
                        position.z += z;
                        return true;
                    }
                } catch(ArrayIndexOutOfBoundsException outX) {
                    try {
                        currY = -(world.getHeights()[changeX][playerZ] * Chunk.CUBE_LENGTH - (Chunk.CHUNK_SIZE - 3.5f));
                        if(position.y <= currY) {
                            position.x += x;
                            return true;
                        }
                    } catch (ArrayIndexOutOfBoundsException no) {
                        return false;
                    }
                }
            }
        }
        return false; 
   }
    public void drop(float velocity) {
        currentTime = Sys.getTime();
        float dt = (currentTime - airTime) / 1000.0f;
        float dy = (float) ((velocity * dt) + ((1.0f/2) * -.5f * Math.pow(dt, 2)));
        int x = (int) (Math.ceil(Math.abs(position.x)) / Chunk.CUBE_LENGTH);
        int z = (int) (Math.ceil(Math.abs(position.z - 1))/ Chunk.CUBE_LENGTH);
        if(position.y - dy <= (-world.getHeights()[x][z]) * world.CUBE_LENGTH - (world.CHUNK_SIZE - 3.5f)) {
            position.y -= dy;
        }
        else {
            position.y = -(world.getHeights()[x][z]) * world.CUBE_LENGTH - (world.CHUNK_SIZE - 3.5f);
            falling = false;
            speed = 0;
        }
    }
    private void checkPlayerStatus(FPCameraController camera, int[] startLocation) {
        if(camera.solid) {
            camera.setStatus(false);
        }
        else {
            camera.setStatus(true);
            camera.position.x = (-startLocation[0] * world.CUBE_LENGTH);
            camera.position.y = (-startLocation[1] * world.CUBE_LENGTH - (world.CHUNK_SIZE - 3.5f));
            camera.position.z = (-startLocation[2] * world.CUBE_LENGTH);
            //FPCameraController camera = new FPCameraController(-playerPosition[0] * 2,-playerPosition[1] * world.CUBE_LENGTH - (world.CHUNK_SIZE - 3.5f),-playerPosition[2] * world.CUBE_LENGTH);

        }
    }
    private void setStatus(boolean state) {
        solid = state;
    }
    
    private void checkEndOfWorld(FPCameraController camera) {
        if((camera.position.x < -1) && Math.abs(camera.position.x) + 10 >= maxX) {
            if(!checkIfChunk(maxX,minZ)) {
                gameWorld.add(new Chunk((int)maxX,0,(int)minZ));
                maxX += world.CHUNK_SIZE * world.CUBE_LENGTH;
                minX += world.CHUNK_SIZE * world.CUBE_LENGTH;
            }
        }
        if((camera.position.x < -1) && Math.abs(camera.position.x) - 10 <= minX) {
            if(!checkIfChunk(minX,minZ)) {
                minX -= 60;
                maxX -= 60;
                gameWorld.add(new Chunk(minX, 0, minZ));
            }
        }
        if((camera.position.x > 1) && camera.position.x - 10 <= minX) {
            if(!checkIfChunk(minX,minZ)) {
                minX -= world.CHUNK_SIZE * world.CUBE_LENGTH;
                maxX -= 60;
                gameWorld.add(new Chunk(minX,0,minZ));
            }
        }
        if((camera.position.x > 1) && camera.position.x + 10 >= maxX) {
            if(!checkIfChunk(maxX,minZ)) {
                gameWorld.add(new Chunk(maxX,0,minZ));
                minX += 60;
                maxX += 60;
            }
        }
        if((camera.position.z < -1) && Math.abs(camera.position.z) + 10 >= maxZ) {
            if(!checkIfChunk(minX,maxZ)) {
                gameWorld.add(new Chunk(minX,0,maxZ));
                maxZ += 60;
                minZ += 60;
            }
        }
        if((camera.position.z < -1) && Math.abs(camera.position.z) - 10 <= minZ) {
            if(!checkIfChunk(minX,minZ)) {
                minZ -= 60;
                maxZ -= 60;
                gameWorld.add(new Chunk(minX,0, minZ));
            }
        }
        if((camera.position.z > 1) && camera.position.z - 10 <= minZ) {
            if(!checkIfChunk(minX,minZ)) {
               minZ -= 60;
               maxZ -= 60;
               gameWorld.add(new Chunk(minX,0,minZ)); 
            }
        if((camera.position.z > 1) && camera.position.z + 10 >= maxZ) {
            if(!checkIfChunk(minX,maxZ)) {
                gameWorld.add(new Chunk(minX,0,maxZ));
                minZ += 60;
                maxZ += 60;
            }
        }
        }
        
//        if(Math.abs(camera.position.x) + 10 >= maxX) {
//            gameWorld.add(new Chunk((int)maxX,0,(int)maxZ));
//            gameWorld.add(new Chunk((int)maxX,0,(int)minZ));
//            maxX += world.CHUNK_SIZE * world.CUBE_LENGTH;
//        } 
//        else if(camera.position.x - 30 <= minX) {
//            minX -= world.CHUNK_SIZE * world.CUBE_LENGTH;
//            gameWorld.add(new Chunk((int)minX,0,(int)maxZ));
//            gameWorld.add(new Chunk((int)minX,0,(int)minZ));
//        }
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
    private void setMinandMax(FPCameraController camera) {
        if((camera.position.x < -1) && (Math.abs(camera.position.x) > maxX)) {
            maxX += 60;
            minX += 60;
        }
        else if((camera.position.x < -1) && (Math.abs(camera.position.x) < minX)) {
            maxX -= 60;
            minX -= 60;
        }
        else if((camera.position.x > 1) && -camera.position.x < minX) {
            maxX -= 60;
            minX -= 60;
        }
        else if((camera.position.x > 1) && -camera.position.x > maxX) {
            maxX += 60;
            minX += 60;
        }
        else if((camera.position.z < -1) && (Math.abs(camera.position.z) > maxZ)) {
            maxZ += 60;
            minZ += 60;
        }
        else if((camera.position.z < -1) && (Math.abs(camera.position.z) < minZ)) {
            maxZ -= 60;
            minZ -= 60;
        }        
        else if( (camera.position.z > 1) && (-camera.position.z < minZ)) {
            maxZ -= 60;
            minZ -= 60;
        }
        else if( (camera.position.z > 1) && (-camera.position.z > maxZ)) {
            maxZ += 60;
            minZ += 60;
        }        
    }
    /******************************************************************************************************/
    /**********************************************added functions*****************************************/
    /******************************************************************************************************/

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
        
        //System.out.println("lH: " + leftBlockHeight + " rH: " + rightBlockHeight + " fH: " + frontBlockHeight + " bH: " + backBlockHeight);
        
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

    public void collisionJump(float distance){
        position.y -= distance;
        
        // can not pass the edges of the world
        if (position.y < -90)
        	position.y = -90;
    }
    
    private void collisionMoveDown(float distance, int[][] blockHeights){
    	position.y += distance;
    	
    	/*
    	for(int i=0; i<blockHeights.length; i++) {
    		for(int j=0; j<blockHeights[i].length; j++) {
    			System.out.println(" x: "+ i + " z: " + j + " height: " + blockHeights[i][j]);
    		}
    	}
    	*/
    	
    	//System.out.println("blockX: " + blockX + " blockZ: " + blockZ);
    	int blockHeight = getBlockHeight(position.x, position.z, blockHeights);

    	//System.out.println("block heights: " + blockHeight);
        
        if (position.y > blockHeight)
        	position.y = blockHeight;
    }
    
    private void gravity(float distance, int[][] blockHeights) {
    	float gravity = 1.15f;
    	position.y += distance*gravity;
    	
    	// collision detection
    	int blockHeight = getBlockHeight(position.x, position.z, blockHeights);     
        if (position.y > blockHeight)
        	position.y = blockHeight;
		
	}
   
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
    
    /******************************************************************************************************/
    /**********************************************until Here**********************************************/
    /******************************************************************************************************/    
}
