/***************************************************************
* file: Chunk.java
* authors: Kenneth L. Ung, Dongri Zhu, Rafael Soto-Salinas
* class: CS 4450.01 – Computer Graphics
*
* assignment: semester project
* date last modified: 3/11/2019
*
* purpose: Generates a chunk of blocks of size 30 x 30. New render method is in here and 
* all of the drawing is done in this class. We also use this class in order to generate the terrain
* and apply texture mapping onto each individual block.
*
****************************************************************/ 
package teamtnt;


import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

public class Chunk {
    private int VBOTextureHandle;
    private Texture texture;
    static final int CHUNK_SIZE = 30;
    static final int CUBE_LENGTH = 2;
    private Block[][][] Blocks;
    private int VBOVertexHandle;
    private int VBOColorHandle;
    private int StartX, StartY, StartZ;
    
    private Random r = new Random();
    /**
     * Constructor for Chunk class, takes the starting X,Y, and Z coordinates.
     * In this constructor, we are loading the image file to apply texture mapping to it. 
     * We are also generating the blocks, which can contain anywhere from 1-30 blocks in a X,Z plane.
     * 
     * @param startX
     * @param startY
     * @param startZ 
     */
    public Chunk(int startX, int startY, int startZ) {
        try {
            texture = TextureLoader.getTexture("PNG",ResourceLoader.getResourceAsStream("terrain.png"));
        }
        catch (Exception e) {
            System.out.println("File not found");
        }
        fillBlocks();
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        StartX = startX;
        StartY = startY;
        StartZ = startZ;
        rebuildMesh(startX, startY, startZ);
    }
    /**
     * Draws onto the screen.
     */
    public void render() {
        glPushMatrix();
        glPushMatrix();
        glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
        glVertexPointer(3, GL_FLOAT, 0, 0L);
        glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
        glColorPointer(3, GL_FLOAT, 0, 0L);
        glBindBuffer(GL_ARRAY_BUFFER,VBOTextureHandle);
        glBindTexture(GL_TEXTURE_2D,1);
        glTexCoordPointer(2,GL_FLOAT,0,0L);
        glDrawArrays(GL_QUADS, 0, CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 24);
        glPopMatrix();
    }
    /**
     * Every time there is a change to our Chunks, we need to rebuild the mesh. This method
     * is responsible for rebuilding the Chunks.
     * @param startX
     * @param startY
     * @param startZ 
     */
    public void rebuildMesh(float startX, float startY, float startZ) {
        /**
         * Simplex Noise requires 3 things : Feature, Persistence (the variation) and a random number generator.
         * Simplex then generates octaves between 1 and largest feature.
         * Persistence = closer to 1 = rocky mountains, closer to 0 = varying slopes
         * Random seed = random number that's it.
         */
        //SimplexNoise generator = new SimplexNoise(CHUNK_SIZE,0.2, 3135);
        float[][] test = calcNoise();
        VBOTextureHandle = glGenBuffers();
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        FloatBuffer VertexTextureData = BufferUtils.createFloatBuffer((CHUNK_SIZE*CHUNK_SIZE*CHUNK_SIZE)*6*12);
        FloatBuffer VertexPositionData = BufferUtils.createFloatBuffer(CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 6 * 12);
        FloatBuffer VertexColorData = BufferUtils.createFloatBuffer(CHUNK_SIZE*CHUNK_SIZE*CHUNK_SIZE*6*12);
        for(float x = 0; x < CHUNK_SIZE; x++) {
            for(float z = 0; z < CHUNK_SIZE; z++) {
//                float noise = (float)generator.getNoise(((int)x), 0, (int)z); // Should return a value between -1 and 1
//                if(noise < 0){
//                    noise = noise * -1; //This just mean the height is negative.
//                }
//                float height = noise * 100 * CUBE_LENGTH; //Should never exceed Chunk_Size
//                
//                System.out.println(height);
//                if(height > 29) {
//                    height = 29;
//                }
                float height = test[(int)x][(int)z] * 25;
                if(height < 0) {
                    height *= -1;
                }
                if(height > 30) {
                    height = 29;
                }
                for(float y = 0 ; y <= height; y++) {
                    int choice = r.nextInt(3);
                    if(y == 0) {
                        Blocks[(int)x][(int)y][(int)z].setType(5);
                    }
                    if((( y > 1 && y + 1 > height) || y == height) && (height != 0)) {
                        if(choice == 0) {
                            Blocks[(int)x][(int)y][(int)z].setType(0);
                            //Blocks[(int)x][(int)y][(int)z] = new Block(Block.BlockType.BlockType_Grass);
                        }
                        else if(choice == 1) {
                            Blocks[(int)x][(int)y][(int)z].setType(1);
                            //Blocks[(int)x][(int)y][(int)z] = new Block(Block.BlockType.BlockType_Sand);
                        }
                        else
                            Blocks[(int)x][(int)y][(int)z].setType(2);
                            //Blocks[(int)x][(int)y][(int)z] = new Block(Block.BlockType.BlockType_Water);
;       
                    }
                    else if(y != 0) {
                        choice = r.nextInt(2);
                        if(choice == 0)
                            Blocks[(int)x][(int)y][(int)z].setType(3);
                            //Blocks[(int)x][(int)y][(int)z] = new Block(Block.BlockType.BlockType_Dirt);
                        else
                            Blocks[(int)x][(int)y][(int)z].setType(4);
                            //Blocks[(int)x][(int)y][(int)z] = new Block(Block.BlockType.BlockType_Stone);                     
                    }
                    Blocks[(int)x][(int)y][(int)z].setActive(true);
                    VertexTextureData.put(createTexCube((float) 0, (float) 0, Blocks[(int)x][(int)y][(int)z]));
                    VertexPositionData.put(createCube((float)(startX + x * CUBE_LENGTH), (float)(y * CUBE_LENGTH + ((int)(CHUNK_SIZE*.8))), (float) (startZ + z * CUBE_LENGTH)));
                    VertexColorData.put(createCubeVertexCol(getCubeColor(Blocks[(int) x][(int) y][(int) z]))); 
                }
            }
        }
        VertexColorData.flip();
        VertexPositionData.flip();
        VertexTextureData.flip();
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexTextureData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER,0);
        glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexPositionData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexColorData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER,0);    
    }
    
    private float[][] calcNoise() {
        Random rd = new Random();
        SimplexNoise_octave noise = new SimplexNoise_octave(rd.nextInt(5000));
        float[][] answer = new float[CHUNK_SIZE][CHUNK_SIZE];
        float layerF = 0.003f;
        float weight = 1;
        for(int i = 0; i < 3; i++) {
            for(int x = 0; x < CHUNK_SIZE; x++) {
                for(int y = 0; y < CHUNK_SIZE; y++) {
                    answer[x][y] += (float)noise.noise(x * layerF, y * layerF) * weight;
                }
            }
            layerF *= 3.5f;
            weight *= .9f;
        }
        return answer;
    }
    private float[] createCubeVertexCol(float[] CubeColorArray) {
        float[] cubeColors = new float[CubeColorArray.length * 4 * 6];
        for(int i = 0; i < cubeColors.length; i++) {
            cubeColors[i] = CubeColorArray[i % CubeColorArray.length];
        }
        return cubeColors;
    }
    
    public static float[] createCube(float x, float y, float z) {
        int offset = CUBE_LENGTH/2;
        return new float[] {
            //Top Quad
            x + offset, y + offset, z,
            x - offset, y + offset, z,
            x - offset, y + offset, z - CUBE_LENGTH,
            x + offset, y + offset, z - CUBE_LENGTH,
            //Bottom Quad
            x + offset, y - offset, z - CUBE_LENGTH,
            x - offset, y - offset, z - CUBE_LENGTH,
            x - offset, y - offset, z,
            x + offset, y - offset, z,
            // Front Quad
            x + offset, y + offset, z - CUBE_LENGTH,
            x - offset, y + offset, z - CUBE_LENGTH,
            x - offset, y - offset, z - CUBE_LENGTH,
            x + offset, y - offset, z - CUBE_LENGTH,
            //Back Quad
            x + offset, y - offset, z,
            x - offset, y - offset, z,
            x - offset, y + offset, z,
            x + offset, y + offset, z,
            //Left Quad
            x - offset, y + offset, z - CUBE_LENGTH,
            x - offset, y + offset, z,
            x - offset, y - offset, z,
            x - offset, y - offset, z - CUBE_LENGTH,
            //Right Quad
            x + offset, y + offset, z,
            x + offset, y + offset, z - CUBE_LENGTH,
            x + offset, y - offset, z - CUBE_LENGTH,
            x + offset, y - offset, z
        };
    }
    
    private float[] getCubeColor(Block block) {
        return new float[] {1,1,1};
    }
    /**
     * Texture Mapping portion, where we are mapping each Block Type to a texture.
     * @param x
     * @param y
     * @param block
     * @return 
     */
    public static float[] createTexCube(float x, float y, Block block) {
        float offset = (1024f/16)/1024f;
        switch(block.getID()) { //Remember Camera is flipped.
            case 0: 
                return new float[] {
                    // Bottom ? == Top
                    x + (offset * 3), y + (offset * 10), //Right Edge
                    x + (offset * 2), y + (offset * 10), // Left Edge
                    x + (offset * 2), y + (offset * 9), // Left edge
                    x + (offset * 3), y + (offset * 9), // Right Edge
                    
                    //Top ? == Bottom
                    x + (offset * 3), y + (offset * 1), //Right
                    x + (offset * 2), y + (offset * 1),
                    x + (offset * 2), y + (offset * 0),
                    x + (offset * 3), y + (offset * 0),
                    
                    // Front == Back
                    x + (offset * 3), y + (offset * 0), //Left
                    x + (offset * 4), y + (offset * 0), // Right
                    x + (offset * 4), y + (offset * 1),
                    x + (offset * 3), y + (offset * 1),
                    
                    //Back == Front
                    x + (offset * 4), y + (offset * 1),
                    x + (offset * 3), y + (offset * 1),
                    x + (offset * 3), y + (offset * 0),
                    x + (offset * 4), y + (offset * 0),
                    
                    //Left == Right
                    x + (offset * 3), y + (offset * 0),
                    x + (offset * 4), y + (offset * 0),
                    x + (offset * 4), y + (offset * 1),
                    x + (offset * 3), y + (offset * 1),
                    
                    //Right == Left
                    
                    x + (offset * 3), y + (offset * 0),
                    x + (offset * 4), y + (offset * 0),
                    x + (offset * 4), y + (offset * 1),
                    x + (offset * 3), y + (offset * 1)
                    
                };
            case 1: //Sand
                return new float[] {
                    // Bottom --> Order goes Right, Left, Left, Right, Lowest --> Highest
                    x + (offset * 3), y + (offset * 2),
                    x + (offset * 2), y + (offset * 2),
                    x + (offset * 2), y + (offset * 1),
                    x + (offset * 3), y + (offset * 1),
                    
                    //Top
                    x + (offset * 3), y + (offset * 2),
                    x + (offset * 2), y + (offset * 2),
                    x + (offset * 2), y + (offset * 1),
                    x + (offset * 3), y + (offset * 1),
                    
                    // Front , now working left to right, lowest to highest
                    x + (offset * 2), y + (offset * 1),
                    x + (offset * 3), y + (offset * 1),
                    x + (offset * 3), y + (offset * 2),
                    x + (offset * 2), y + (offset * 2),
                    
                    // Back , Same concept as Bottom and Top
                    x + (offset * 3), y + (offset * 2),
                    x + (offset * 2), y + (offset * 2),
                    x + (offset * 2), y + (offset * 1),
                    x + (offset * 3), y + (offset * 1),    
                    
                    //Left Back to concept of Front
                    x + (offset * 2), y + (offset * 1),
                    x + (offset * 3), y + (offset * 1),
                    x + (offset * 3), y + (offset * 2),
                    x + (offset * 2), y + (offset * 2),
                    
                    //Right same concept
                    x + (offset * 2), y + (offset * 1),
                    x + (offset * 3), y + (offset * 1),
                    x + (offset * 3), y + (offset * 2),
                    x + (offset * 2), y + (offset * 2)                    
                };
            case 2: //Water
                return new float[] {
                    //Bottom
                    x + (offset * 2), y + (offset * 12),
                    x + (offset * 1), y + (offset * 12),
                    x + (offset * 1), y + (offset * 11),
                    x + (offset * 2), y + (offset * 11),
                    //Top
                    x + (offset * 2), y + (offset * 12),
                    x + (offset * 1), y + (offset * 12),
                    x + (offset * 1), y + (offset * 11),
                    x + (offset * 2), y + (offset * 11),
                    //Front
                    x + (offset * 1), y + (offset * 11),
                    x + (offset * 2), y + (offset * 11),
                    x + (offset * 2), y + (offset * 12),
                    x + (offset * 1), y + (offset * 12),
                    // Back
                    x + (offset * 2), y + (offset * 12),
                    x + (offset * 1), y + (offset * 12),
                    x + (offset * 1), y + (offset * 11),
                    x + (offset * 2), y + (offset * 11),  
                    // Left
                    x + (offset * 1), y + (offset * 11),
                    x + (offset * 2), y + (offset * 11),
                    x + (offset * 2), y + (offset * 12),
                    x + (offset * 1), y + (offset * 12),
                    //Right
                    x + (offset * 1), y + (offset * 11),
                    x + (offset * 2), y + (offset * 11),
                    x + (offset * 2), y + (offset * 12),
                    x + (offset * 1), y + (offset * 12)                    
                };
            case 3: // Dirt
                return new float[] {
                    // Bottom --> Order goes Right, Left, Left, Right, Lowest --> Highest
                    x + (offset * 3), y + (offset * 1),
                    x + (offset * 2), y + (offset * 1),
                    x + (offset * 2), y + (offset * 0),
                    x + (offset * 3), y + (offset * 0),
                    
                    //Top
                    x + (offset * 3), y + (offset * 1),
                    x + (offset * 2), y + (offset * 1),
                    x + (offset * 2), y + (offset * 0),
                    x + (offset * 3), y + (offset * 0),
                    
                    // Front , now working left to right, lowest to highest
                    x + (offset * 2), y + (offset * 0),
                    x + (offset * 3), y + (offset * 0),
                    x + (offset * 3), y + (offset * 1),
                    x + (offset * 2), y + (offset * 1),
                    
                    // Back , Same concept as Bottom and Top
                    x + (offset * 3), y + (offset * 1),
                    x + (offset * 2), y + (offset * 1),
                    x + (offset * 2), y + (offset * 0),
                    x + (offset * 3), y + (offset * 0),    
                    
                    //Left Back to concept of Front
                    x + (offset * 2), y + (offset * 0),
                    x + (offset * 3), y + (offset * 0),
                    x + (offset * 3), y + (offset * 1),
                    x + (offset * 2), y + (offset * 1),
                    
                    //Right same concept
                    x + (offset * 2), y + (offset * 0),
                    x + (offset * 3), y + (offset * 0),
                    x + (offset * 3), y + (offset * 1),
                    x + (offset * 2), y + (offset * 1)   
                    
                };
            case 4: //Stone
                 return new float[] {
                    // Bottom --> Order goes Right, Left, Left, Right, Lowest --> Highest
                    x + (offset * 1), y + (offset * 2),
                    x + (offset * 0), y + (offset * 2),
                    x + (offset * 0), y + (offset * 1),
                    x + (offset * 1), y + (offset * 1),
                    
                    //Top
                    x + (offset * 1), y + (offset * 2),
                    x + (offset * 0), y + (offset * 2),
                    x + (offset * 0), y + (offset * 1),
                    x + (offset * 1), y + (offset * 1),
                    
                    // Front , now working left to right, lowest to highest
                    x + (offset * 0), y + (offset * 1),
                    x + (offset * 1), y + (offset * 1),
                    x + (offset * 1), y + (offset * 2),
                    x + (offset * 0), y + (offset * 2),
                    
                    // Back , Same concept as Bottom and Top
                    x + (offset * 1), y + (offset * 2),
                    x + (offset * 0), y + (offset * 2),
                    x + (offset * 0), y + (offset * 1),
                    x + (offset * 1), y + (offset * 1),  
                    
                    //Left Back to concept of Front
                    x + (offset * 0), y + (offset * 1),
                    x + (offset * 1), y + (offset * 1),
                    x + (offset * 1), y + (offset * 2),
                    x + (offset * 0), y + (offset * 2),
                    
                    //Right same concept
                    x + (offset * 0), y + (offset * 1),
                    x + (offset * 1), y + (offset * 1),
                    x + (offset * 1), y + (offset * 2),
                    x + (offset * 0), y + (offset * 2) 
                };
            case 5: //Bed Rock
                 return new float[] {
                    // Bottom --> Order goes Right, Left, Left, Right, Lowest --> Highest
                    x + (offset * 2), y + (offset * 2),
                    x + (offset * 1), y + (offset * 2),
                    x + (offset * 1), y + (offset * 1),
                    x + (offset * 2), y + (offset * 1),
                    
                    //Top
                    x + (offset * 2), y + (offset * 2),
                    x + (offset * 1), y + (offset * 2),
                    x + (offset * 1), y + (offset * 1),
                    x + (offset * 2), y + (offset * 1),
                    
                    // Front , now working left to right, lowest to highest
                    x + (offset * 1), y + (offset * 1),
                    x + (offset * 2), y + (offset * 1),
                    x + (offset * 2), y + (offset * 2),
                    x + (offset * 1), y + (offset * 2),
                    
                    // Back , Same concept as Bottom and Top
                    x + (offset * 2), y + (offset * 2),
                    x + (offset * 1), y + (offset * 2),
                    x + (offset * 1), y + (offset * 1),
                    x + (offset * 2), y + (offset * 1), 
                    
                    //Left Back to concept of Front
                    x + (offset * 1), y + (offset * 1),
                    x + (offset * 2), y + (offset * 1),
                    x + (offset * 2), y + (offset * 2),
                    x + (offset * 1), y + (offset * 2),
                    
                    //Right same concept
                    x + (offset * 1), y + (offset * 1),
                    x + (offset * 2), y + (offset * 1),
                    x + (offset * 2), y + (offset * 2),
                    x + (offset * 1), y + (offset * 2)
                }; 
            case 6: //Wood
                 return new float[] {
                    // Bottom --> Order goes Right, Left, Left, Right, Lowest --> Highest
                    x + (offset * 5), y + (offset * 1),
                    x + (offset * 5), y + (offset * 2),
                    x + (offset * 4), y + (offset * 1),
                    x + (offset * 4), y + (offset * 2),
                    
                    //Top
                    x + (offset * 5), y + (offset * 1),
                    x + (offset * 5), y + (offset * 2),
                    x + (offset * 4), y + (offset * 1),
                    x + (offset * 4), y + (offset * 2),
                    
                    // Front , now working left to right, lowest to highest
                    x + (offset * 4), y + (offset * 1),
                    x + (offset * 5), y + (offset * 1),
                    x + (offset * 5), y + (offset * 2),
                    x + (offset * 4), y + (offset * 2),
                    
                    // Back , Same concept as Bottom and Top
                    x + (offset * 5), y + (offset * 2),
                    x + (offset * 4), y + (offset * 2),
                    x + (offset * 4), y + (offset * 1),
                    x + (offset * 5), y + (offset * 1), 
                    
                    //Left Back to concept of Front
                    x + (offset * 4), y + (offset * 1),
                    x + (offset * 5), y + (offset * 1),
                    x + (offset * 5), y + (offset * 2),
                    x + (offset * 4), y + (offset * 2),
                    
                    //Right same concept
                    x + (offset * 4), y + (offset * 1),
                    x + (offset * 5), y + (offset * 1),
                    x + (offset * 5), y + (offset * 2),
                    x + (offset * 4), y + (offset * 2)
                };                  
                
        }
        return new float[] {1,1,1};
    }
    public void splitTerrain(float startX, float startY, float startZ) {
        /**
         * Simplex Noise requires 3 things : Feature, Persistence (the variation) and a random number generator.
         * Simplex then generates octaves between 1 and largest feature.
         * Persistence = closer to 1 = rocky mountains, closer to 0 = varying slopes
         * Random seed = random number that's it.
         */
        float[][] test = calcNoise();
        VBOTextureHandle = glGenBuffers();
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        FloatBuffer VertexTextureData = BufferUtils.createFloatBuffer((CHUNK_SIZE*CHUNK_SIZE*CHUNK_SIZE)*6*12);
        FloatBuffer VertexPositionData = BufferUtils.createFloatBuffer(CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 6 * 12);
        FloatBuffer VertexColorData = BufferUtils.createFloatBuffer(CHUNK_SIZE*CHUNK_SIZE*CHUNK_SIZE*6*12);
        for(float x = 0; x < CHUNK_SIZE; x++) {
            for(float z = 0; z < CHUNK_SIZE; z++) {
                float height = test[(int)x][(int)z] * 25;
                if(height < 0) {
                    height *= -1;
                }
                if(height > 30) {
                    height = 29;
                }
                for(float y = 0 ; y <= height; y++) {
                    int choice = r.nextInt(3);
                    if((( y > 1 && y + 1 > height) || y == height) && (height != 0)) {
                        if(choice == 0) {
                            Blocks[(int)x][(int)y][(int)z] = new Block(Block.BlockType.BlockType_Grass);
                            for(float change = 1; change < height - 1; change++) {
                                Blocks[(int)x][(int)change][(int)z] = new Block(Block.BlockType.BlockType_Dirt);
                            }
                        }
                        else if(choice == 1) {
                            Blocks[(int)x][(int)y][(int)z] = new Block(Block.BlockType.BlockType_Sand);
                            for(float change = 1; change < height - 1; change++) {
                                Blocks[(int)x][(int)change][(int)z] = new Block(Block.BlockType.BlockType_Sand);
                            }                            
                        }
                        else {
                            Blocks[(int)x][(int)y][(int)z] = new Block(Block.BlockType.BlockType_Water);
                              for(float change = 1; change < height - 1; change++) {
                                Blocks[(int)x][(int)change][(int)z] = new Block(Block.BlockType.BlockType_Water);
                            }                          
                        }

       
                    }
//                    else if(y != 0) {
//                        choice = r.nextInt(2);
//                        if(choice == 0)
//                            Blocks[(int)x][(int)y][(int)z] = new Block(Block.BlockType.BlockType_Dirt);
//                        else
//                            Blocks[(int)x][(int)y][(int)z] = new Block(Block.BlockType.BlockType_Stone);                     
//                    }
                    VertexTextureData.put(createTexCube((float) 0, (float) 0, Blocks[(int)x][(int)y][(int)z]));
                    VertexPositionData.put(createCube((float)(startX + x * CUBE_LENGTH), (float)(y * CUBE_LENGTH + ((int)(CHUNK_SIZE*.8))), (float) (startZ + z * CUBE_LENGTH)));
                    VertexColorData.put(createCubeVertexCol(getCubeColor(Blocks[(int) x][(int) y][(int) z]))); 
                }
            }
        }
        VertexColorData.flip();
        VertexPositionData.flip();
        VertexTextureData.flip();
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexTextureData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER,0);
        glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexPositionData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexColorData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER,0);    
    }    
    private void fillBlocks() {
        Blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        for(int i = 0; i < CHUNK_SIZE; i++) {
            for(int j = 0; j < CHUNK_SIZE; j++) {
                for(int k = 0; k < CHUNK_SIZE; k++) {
                    if(j == 0) {
                        Blocks[i][j][k] = new Block(Block.BlockType.BlockType_Bedrock);
                    }
                    else {
                        if(r.nextFloat() > 0.9f) { 
                            Blocks[i][j][k] = new Block(Block.BlockType.BlockType_Grass); //Grass
                        }
                        else if(r.nextFloat() > 0.8f){
                            Blocks[i][j][k] = new Block(Block.BlockType.BlockType_Sand); //Sand?
                        }
                        else if (r.nextFloat() > 0.7f) {
                            Blocks[i][j][k] = new Block(Block.BlockType.BlockType_Water); //Water
                        }
                        else if(r.nextFloat() > 0.6f) {
                            Blocks[i][j][k] = new Block(Block.BlockType.BlockType_Stone);
                        }
                        else if(r.nextFloat() > 0.5f) {
                            Blocks[i][j][k] = new Block(Block.BlockType.BlockType_Bedrock);
                        }
                        else if(r.nextFloat() > 0.4f) {
                            Blocks[i][j][k] = new Block(Block.BlockType.BlockType_Wood);
                        }
                        else {
                            Blocks[i][j][k] = new Block(Block.BlockType.BlockType_Dirt);
                        }
                    }
                }
            }
        }
    }
}
