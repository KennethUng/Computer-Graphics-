/***************************************************************
* file: Chunk.java
* authors: Kenneth L. Ung, Dongri Zhu, Rafael Soto-Salinas
* class: CS 4450.01 – Computer Graphics
*
* assignment: semester project
* date last modified: 4/30/2019
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
    private int[] start = new int[3];
    private int[][] heights;
    private float[][] heightAtXZ = new float[CHUNK_SIZE][CHUNK_SIZE];
    private int StartX, StartY, StartZ;
    int nextHeight = 0;
    float [] heightOfTerrain = new float[CHUNK_SIZE*CHUNK_SIZE];    
    
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
    public Chunk(int startX, int startY, int startZ, int choice) {
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
        storeHeight();
        System.out.println("Chunk is placed at (X: " + startX + ") and (Z: " + startZ + ")");        
        if(choice == 15) {
            teamName(startX,startY,startZ);
        }
        else {
        rebuildMesh(startX, startY, startZ, choice);
        }
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
    public int[] getStart() {
        return this.start;
    }
    
    public float[][] getHeights() {
        return this.heightAtXZ;
    }
    
    
    /**
     * Every time there is a change to our Chunks, we need to rebuild the mesh. This method
     * is responsible for rebuilding the Chunks.
     * @param startX
     * @param startY
     * @param startZ 
     */
    public void rebuildMesh(float startX, float startY, float startZ, int terrainChoice) {

        /**
         * Simplex Noise requires 3 things : Feature, Persistence (the variation) and a random number generator.
         * Simplex then generates octaves between 1 and largest feature.
         * Persistence = closer to 1 = rocky mountains, closer to 0 = varying slopes
         * Random seed = random number that's it.
         */
        /**
         * start in the middle of the chunk.
         */
        float lowest = CHUNK_SIZE; //Chunk Size is the heighest a point can ever reach, we will use this as default for now.
        start[0] = 0;
        start[1] = 0;
        start[2] = CHUNK_SIZE/2;
        /**
         * default 3.5f, .9f
         */
        float[][] test = calcNoise(3.5f,.9f);
        VBOTextureHandle = glGenBuffers();
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        FloatBuffer VertexTextureData = BufferUtils.createFloatBuffer((CHUNK_SIZE*CHUNK_SIZE*CHUNK_SIZE)*6*12);
        FloatBuffer VertexPositionData = BufferUtils.createFloatBuffer(CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 6 * 12);
        FloatBuffer VertexColorData = BufferUtils.createFloatBuffer(CHUNK_SIZE*CHUNK_SIZE*CHUNK_SIZE*6*12);
        heights = new int[CHUNK_SIZE][CHUNK_SIZE];
        int count = 0;
        for(float x = 0; x < CHUNK_SIZE; x++) {
            for(float z = 0; z < CHUNK_SIZE; z++) {
                float height = test[(int)x][(int)z] * 25;
                if(height < 0) {
                    height *= -1;
                }
                if(height > 30) {
                    height = 29;
                }
                if(height < lowest) {
                    lowest = height;
                }
                if(height > start[1]) {
                    start[0] = (int)x;
                    start[1] = (int) height;
                    start[2] = (int)z;
                }
//                heights[(int)x][(int)z] = (int)height;                
                //for(float y = 0 ; y <= height; y++) {
                for(float y = 0; y <= heightOfTerrain[count]; y++) {
                    int choice = r.nextInt(3);
                    switch (terrainChoice) {
                        case 0:
                            createSplitBiome((int)y,(int)x,(int)z,heightOfTerrain[count]);
                            //setBlockType(y,x,z,heightOfTerrain[count],choice);
                            break;
                        case 1:
                            setUpGrassLand((int)y,(int)x,(int)z, (int)heightOfTerrain[count]);
                            break;
                        case 2:
                            setUpStone((int)y,(int)x,(int)z);
                            break;
                        case 3:
                            setUpSand((int)y,(int)x,(int)z);
                            break;
                        case 4:
                            setBlockType1(y,x,z,heightOfTerrain[count] , choice);
                            break;
                        case 5:
                            setBlockType2(y,x,z,heightOfTerrain[count], choice);
                            break;                    
                        case 6:
                            setBlockType3(y,x,z,heightOfTerrain[count], choice);
                            break;
                        case 7:
                            setBlockType4(y,x,z,heightOfTerrain[count], choice);
                            break;
                        case 8:
                            setBlockType5(y,x,z,heightOfTerrain[count], choice);
                            break;
                        case 9:
                            setBlockType6(y,x,z,heightOfTerrain[count], count);
                            break;
                        case 10:
                            setBlockType(y,x,z,heightOfTerrain[count],choice);
                        default:
                            break;
                    }
                    Blocks[(int)x][(int)y][(int)z].setActive(true);
                    VertexTextureData.put(createTexCube((float) 0, (float) 0, Blocks[(int)x][(int)y][(int)z]));
                    VertexPositionData.put(createCube((float)(startX + x * CUBE_LENGTH), (float)(y * CUBE_LENGTH + ((int)(CHUNK_SIZE*.8))), (float) (startZ + z * CUBE_LENGTH)));
                    VertexColorData.put(createCubeVertexCol(getCubeColor(Blocks[(int) x][(int) y][(int) z]))); 
                }
                heights[(int)x][(int)z] = (int)heightOfTerrain[count];                
                count++;
                heightAtXZ[(int)x][(int)z] = height;
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
    /**
     * Simplex Noise 2d Noise generation for random terrain. This method essentially
     * calculates the noise for us at each x,z coordinate and we will pass in the
     * frequency and weight we want to use.
     * @param freq
     * @param weightMult
     * @return 
     */
    private float[][] calcNoise(float freq, float weightMult) {
        Random rd = new Random();
        SimplexNoise_octave noise = new SimplexNoise_octave(rd.nextInt(5000));
        float[][] answer = new float[CHUNK_SIZE][CHUNK_SIZE];
        float layerF = 0.003f;
        float weight = 1f;
        for(int i = 0; i < 3; i++) {
            for(int x = 0; x < CHUNK_SIZE; x++) {
                for(int y = 0; y < CHUNK_SIZE; y++) {
                    answer[x][y] += (float)noise.noise(x * layerF, y * layerF) * weight;
                }
            }
            layerF *= freq;
            weight *= weightMult;
        }
        return answer;
    }
    /**
     * method to generate the Cube Color
     * @param CubeColorArray
     * @return 
     */
    private float[] createCubeVertexCol(float[] CubeColorArray) {
        float[] cubeColors = new float[CubeColorArray.length * 4 * 6];
        for(int i = 0; i < cubeColors.length; i++) {
            cubeColors[i] = CubeColorArray[i % CubeColorArray.length];
        }
        return cubeColors;
    }
    /**
     * Physically creating/materializing a cube.
     * @param x
     * @param y
     * @param z
     * @return 
     */
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
    
    /**
     * Return color of cube, no longer used since we are doing texture.
     * @param block
     * @return 
     */
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
                         
            case 7: //glass
                return new float[]{
                    // Bottom --> Order goes Right, Left, Left, Right, Lowest --> Highest
                    x + (offset * 4), y + (offset * 14),
                    x + (offset * 3), y + (offset * 14),
                    x + (offset * 3), y + (offset * 13),
                    x + (offset * 4), y + (offset * 13),
                    
                    //Top
                    x + (offset * 4), y + (offset * 14),
                    x + (offset * 3), y + (offset * 14),
                    x + (offset * 3), y + (offset * 13),
                    x + (offset * 4), y + (offset * 13),
                    
                    // Front , now working left to right, lowest to highest
                    x + (offset * 3), y + (offset * 13),
                    x + (offset * 4), y + (offset * 13),
                    x + (offset * 4), y + (offset * 14),
                    x + (offset * 3), y + (offset * 14),
                    
                    // Back , Same concept as Bottom and Top
                    x + (offset * 4), y + (offset * 14),
                    x + (offset * 3), y + (offset * 14),
                    x + (offset * 3), y + (offset * 13),
                    x + (offset * 4), y + (offset * 13), 
                    
                    //Left Back to concept of Front
                    x + (offset * 3), y + (offset * 13),
                    x + (offset * 4), y + (offset * 13),
                    x + (offset * 4), y + (offset * 14),
                    x + (offset * 3), y + (offset * 14),
                    
                    //Right same concept
                    x + (offset * 3), y + (offset * 13),
                    x + (offset * 4), y + (offset * 13),
                    x + (offset * 4), y + (offset * 14),
                    x + (offset * 3), y + (offset * 14)                    
                    
                };
            case 8: // blue
                return new float[]{
                    // Bottom --> Order goes Right, Left, Left, Right, Lowest --> Highest
                    x + (offset * 15), y + (offset * 1),
                    x + (offset * 14), y + (offset * 1),
                    x + (offset * 14), y + (offset * 0),
                    x + (offset * 15), y + (offset * 0),
                    
                    //Top
                    x + (offset * 15), y + (offset * 1),
                    x + (offset * 14), y + (offset * 1),
                    x + (offset * 14), y + (offset * 0),
                    x + (offset * 15), y + (offset * 0),
                    
                    // Front , now working left to right, lowest to highest
                    x + (offset * 14), y + (offset * 0),
                    x + (offset * 15), y + (offset * 0),
                    x + (offset * 15), y + (offset * 1),
                    x + (offset * 14), y + (offset * 1),
                    
                    // Back , Same concept as Bottom and Top
                    x + (offset * 15), y + (offset * 1),
                    x + (offset * 14), y + (offset * 1),
                    x + (offset * 14), y + (offset * 0),
                    x + (offset * 15), y + (offset * 0), 
                    
                    //Left Back to concept of Front
                    x + (offset * 14), y + (offset * 0),
                    x + (offset * 15), y + (offset * 0),
                    x + (offset * 15), y + (offset * 1),
                    x + (offset * 14), y + (offset * 1),
                    
                    //Right same concept
                    x + (offset * 14), y + (offset * 0),
                    x + (offset * 15), y + (offset * 0),
                    x + (offset * 15), y + (offset * 1),
                    x + (offset * 14), y + (offset * 1)                    
                };
            case 9: //lines 
                return new float[]{
                    // Bottom --> Order goes Right, Left, Left, Right, Lowest --> Highest
                    x + (offset * 2), y + (offset * 5),
                    x + (offset * 1), y + (offset * 5),
                    x + (offset * 1), y + (offset * 4),
                    x + (offset * 2), y + (offset * 4),
                    
                    //Top
                    x + (offset * 2), y + (offset * 5),
                    x + (offset * 1), y + (offset * 5),
                    x + (offset * 1), y + (offset * 4),
                    x + (offset * 2), y + (offset * 4),
                    
                    // Front , now working left to right, lowest to highest
                    x + (offset * 1), y + (offset * 4),
                    x + (offset * 2), y + (offset * 4),
                    x + (offset * 2), y + (offset * 5),
                    x + (offset * 1), y + (offset * 5),
                    
                    // Back , Same concept as Bottom and Top
                    x + (offset * 2), y + (offset * 5),
                    x + (offset * 1), y + (offset * 5),
                    x + (offset * 1), y + (offset * 4),
                    x + (offset * 2), y + (offset * 4), 
                    
                    //Left Back to concept of Front
                    x + (offset * 1), y + (offset * 4),
                    x + (offset * 2), y + (offset * 4),
                    x + (offset * 2), y + (offset * 5),
                    x + (offset * 1), y + (offset * 5),
                    
                    //Right same concept
                    x + (offset * 1), y + (offset * 4),
                    x + (offset * 2), y + (offset * 4),
                    x + (offset * 2), y + (offset * 5),
                    x + (offset * 1), y + (offset * 5)                     
                };
            case 10:  // whiteLines
                return new float[]{
                    // Bottom --> Order goes Right, Left, Left, Right, Lowest --> Highest
                    x + (offset * 2), y + (offset * 4),
                    x + (offset * 1), y + (offset * 4),
                    x + (offset * 1), y + (offset * 3),
                    x + (offset * 2), y + (offset * 3),
                    
                    //Top
                    x + (offset * 2), y + (offset * 4),
                    x + (offset * 1), y + (offset * 4),
                    x + (offset * 1), y + (offset * 3),
                    x + (offset * 2), y + (offset * 3),
                    
                    // Front , now working left to right, lowest to highest
                    x + (offset * 1), y + (offset * 3),
                    x + (offset * 2), y + (offset * 3),
                    x + (offset * 2), y + (offset * 4),
                    x + (offset * 1), y + (offset * 4),
                    
                    // Back , Same concept as Bottom and Top
                    x + (offset * 2), y + (offset * 4),
                    x + (offset * 1), y + (offset * 4),
                    x + (offset * 1), y + (offset * 3),
                    x + (offset * 2), y + (offset * 3), 
                    
                    //Left Back to concept of Front
                    x + (offset * 1), y + (offset * 3),
                    x + (offset * 2), y + (offset * 3),
                    x + (offset * 2), y + (offset * 4),
                    x + (offset * 1), y + (offset * 4),
                    
                    //Right same concept
                    x + (offset * 1), y + (offset * 3),
                    x + (offset * 2), y + (offset * 3),
                    x + (offset * 2), y + (offset * 4),
                    x + (offset * 1), y + (offset * 4)                       
                };
            case 11 : //bricks
                return new float[]{
                    // Bottom --> Order goes Right, Left, Left, Right, Lowest --> Highest
                    x + (offset * 7), y + (offset * 4),
                    x + (offset * 6), y + (offset * 4),
                    x + (offset * 6), y + (offset * 3),
                    x + (offset * 7), y + (offset * 3),
                    
                    //Top
                    x + (offset * 7), y + (offset * 4),
                    x + (offset * 6), y + (offset * 4),
                    x + (offset * 6), y + (offset * 3),
                    x + (offset * 7), y + (offset * 3),
                    
                    // Front , now working left to right, lowest to highest
                    x + (offset * 6), y + (offset * 3),
                    x + (offset * 7), y + (offset * 3),
                    x + (offset * 7), y + (offset * 4),
                    x + (offset * 6), y + (offset * 4),
                    
                    // Back , Same concept as Bottom and Top
                    x + (offset * 7), y + (offset * 4),
                    x + (offset * 6), y + (offset * 4),
                    x + (offset * 6), y + (offset * 3),
                    x + (offset * 7), y + (offset * 3), 
                    
                    //Left Back to concept of Front
                    x + (offset * 6), y + (offset * 3),
                    x + (offset * 7), y + (offset * 3),
                    x + (offset * 7), y + (offset * 4),
                    x + (offset * 6), y + (offset * 4),
                    
                    //Right same concept
                    x + (offset * 6), y + (offset * 3),
                    x + (offset * 7), y + (offset * 3),
                    x + (offset * 7), y + (offset * 4),
                    x + (offset * 6), y + (offset * 4)                       
                };
            case 12 : //old bricks
                return new float[]{
                    // Bottom --> Order goes Right, Left, Left, Right, Lowest --> Highest
                    x + (offset * 7), y + (offset * 1),
                    x + (offset * 6), y + (offset * 1),
                    x + (offset * 6), y + (offset * 0),
                    x + (offset * 7), y + (offset * 0),
                    
                    //Top
                    x + (offset * 7), y + (offset * 1),
                    x + (offset * 6), y + (offset * 1),
                    x + (offset * 6), y + (offset * 0),
                    x + (offset * 7), y + (offset * 0),
                    
                    // Front , now working left to right, lowest to highest
                    x + (offset * 4), y + (offset * 2),
                    x + (offset * 5), y + (offset * 2),
                    x + (offset * 5), y + (offset * 3),
                    x + (offset * 4), y + (offset * 3),
                    
                    // Back , Same concept as Bottom and Top
                    x + (offset * 5), y + (offset * 3),
                    x + (offset * 4), y + (offset * 3),
                    x + (offset * 4), y + (offset * 2),
                    x + (offset * 5), y + (offset * 2), 
                    
                    //Left Back to concept of Front
                    x + (offset * 4), y + (offset * 2),
                    x + (offset * 5), y + (offset * 2),
                    x + (offset * 5), y + (offset * 3),
                    x + (offset * 4), y + (offset * 3),
                    
                    //Right same concept
                    x + (offset * 4), y + (offset * 2),
                    x + (offset * 5), y + (offset * 2),
                    x + (offset * 5), y + (offset * 3),
                    x + (offset * 4), y + (offset * 3)                       
                };
            case 13 : //alterBlock
                return new float[]{
                    // Bottom --> Order goes Right, Left, Left, Right, Lowest --> Highest
                    x + (offset * 12), y + (offset * 3),
                    x + (offset * 11), y + (offset * 3),
                    x + (offset * 11), y + (offset * 2),
                    x + (offset * 12), y + (offset * 2),
                    
                    //Top
                    x + (offset * 12), y + (offset * 3),
                    x + (offset * 11), y + (offset * 3),
                    x + (offset * 11), y + (offset * 2),
                    x + (offset * 12), y + (offset * 2),
                    
                    // Front , now working left to right, lowest to highest
                    x + (offset * 9), y + (offset * 1),
                    x + (offset * 10), y + (offset * 1),
                    x + (offset * 10), y + (offset * 2),
                    x + (offset * 9), y + (offset * 2),
                    
                    // Back , Same concept as Bottom and Top
                    x + (offset * 10), y + (offset * 2),
                    x + (offset * 9), y + (offset * 2),
                    x + (offset * 9), y + (offset * 1),
                    x + (offset * 10), y + (offset * 1), 
                    
                    //Left Back to concept of Front
                    x + (offset * 9), y + (offset * 1),
                    x + (offset * 10), y + (offset * 1),
                    x + (offset * 10), y + (offset * 2),
                    x + (offset * 9), y + (offset * 2),
                    
                    //Right same concept
                    x + (offset * 9), y + (offset * 1),
                    x + (offset * 10), y + (offset * 1),
                    x + (offset * 10), y + (offset * 2),
                    x + (offset * 9), y + (offset * 2)  
                    
                };
            case 14: //scary tree face
                return new float[] {
                    x + (offset * 7), y + (offset * 7),
                    x + (offset * 6), y + (offset * 7),
                    x + (offset * 6), y + (offset * 6),
                    x + (offset * 7), y + (offset * 6),
                                       
                    x + (offset * 6), y + (offset * 2),
                    x + (offset * 5), y + (offset * 2),
                    x + (offset * 5), y + (offset * 1),
                    x + (offset * 6), y + (offset * 1),
                    
                    // Front , now working left to right, lowest to highest
                    x + (offset * 7), y + (offset * 7),
                    x + (offset * 8), y + (offset * 7),
                    x + (offset * 8), y + (offset * 8),
                    x + (offset * 7), y + (offset * 8),
                    
                    // Back , Same concept as Bottom and Top
                    x + (offset * 8), y + (offset * 8),
                    x + (offset * 7), y + (offset * 8),
                    x + (offset * 7), y + (offset * 7),
                    x + (offset * 8), y + (offset * 7), 
                    
                    //Left Back to concept of Front
                    x + (offset * 8), y + (offset * 8),
                    x + (offset * 7), y + (offset * 8),
                    x + (offset * 7), y + (offset * 7),
                    x + (offset * 8), y + (offset * 7),
                    
                    //Right same concept
                    x + (offset * 8), y + (offset * 8),
                    x + (offset * 7), y + (offset * 8),
                    x + (offset * 7), y + (offset * 7),
                    x + (offset * 8), y + (offset * 7)
                };
            case 15: //TNT
                return new float[] {                    
                    // Bottom --> Order goes Right, Left, Left, Right, Lowest --> Highest
                    x + (offset * 11), y + (offset * 1),
                    x + (offset * 10), y + (offset * 1),
                    x + (offset * 10), y + (offset * 0),
                    x + (offset * 11), y + (offset * 0),
                                       
                    x + (offset * 11), y + (offset * 1),
                    x + (offset * 10), y + (offset * 1),
                    x + (offset * 10), y + (offset * 0),
                    x + (offset * 11), y + (offset * 0),
                    
                    // Front , now working left to right, lowest to highest
                    x + (offset * 8), y + (offset * 0),
                    x + (offset * 9), y + (offset * 0),
                    x + (offset * 9), y + (offset * 1),
                    x + (offset * 8), y + (offset * 1),
                    
                    // Back , Same concept as Bottom and Top
                    x + (offset * 9), y + (offset * 1),
                    x + (offset * 8), y + (offset * 1),
                    x + (offset * 8), y + (offset * 0),
                    x + (offset * 9), y + (offset * 0), 
                    
                    //Left Back to concept of Front
                    x + (offset * 8), y + (offset * 0),
                    x + (offset * 9), y + (offset * 0),
                    x + (offset * 9), y + (offset * 1),
                    x + (offset * 8), y + (offset * 1),
                    
                    //Right same concept
                    x + (offset * 9), y + (offset * 1),
                    x + (offset * 8), y + (offset * 1),
                    x + (offset * 8), y + (offset * 0),
                    x + (offset * 9), y + (offset * 0)
                };                
                
        }
        return new float[] {1,1,1};
    }
    /**
     * Fill the blocks with default values
     */
    private void fillBlocks() {
        Blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        for(int i = 0; i < CHUNK_SIZE; i++) {
            for(int j = 0; j < CHUNK_SIZE; j++) {
                for(int k = 0; k < CHUNK_SIZE; k++) {
                    if(j == 0) {
                        Blocks[i][j][k] = new Block(Block.BlockType.Bedrock);
                    }
                    else {
                        if(r.nextFloat() > 0.9f) { 
                            Blocks[i][j][k] = new Block(Block.BlockType.Grass); //Grass
                        }
                        else if(r.nextFloat() > 0.8f){
                            Blocks[i][j][k] = new Block(Block.BlockType.Sand); //Sand?
                        }
                        else if (r.nextFloat() > 0.7f) {
                            Blocks[i][j][k] = new Block(Block.BlockType.Water); //Water
                        }
                        else if(r.nextFloat() > 0.6f) {
                            Blocks[i][j][k] = new Block(Block.BlockType.Stone);
                        }
                        else if(r.nextFloat() > 0.5f) {
                            Blocks[i][j][k] = new Block(Block.BlockType.Bedrock);
                        }
                        else if(r.nextFloat() > 0.4f) {
                            Blocks[i][j][k] = new Block(Block.BlockType.Wood);
                        }
                        else {
                            Blocks[i][j][k] = new Block(Block.BlockType.Dirt);
                        }
                    }
                }
            }
        }
    }
    /**
     * Returns the startX for a Chunk, used for dynamic allocation
     * @return 
     */
    public float getX() {
        return this.StartX;
    }
    /**
     * Returns the startZ for a chunk, which is used for dynamic allocation.
     * @return 
     */
    public float getZ() {
        return this.StartZ;
    }
    /**
     * Purpose of this was to print out our team name, unfinished.
     * @param startX
     * @param startY
     * @param startZ 
     */
    public void teamName(float startX, float startY, float startZ) {
        VBOTextureHandle = glGenBuffers();
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        FloatBuffer VertexTextureData = BufferUtils.createFloatBuffer((CHUNK_SIZE*CHUNK_SIZE*CHUNK_SIZE)*6*12);
        FloatBuffer VertexPositionData = BufferUtils.createFloatBuffer(CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 6 * 12);
        FloatBuffer VertexColorData = BufferUtils.createFloatBuffer(CHUNK_SIZE*CHUNK_SIZE*CHUNK_SIZE*6*12);
        /**
         * Creates the T
         */
        for(int y = 13; y < 18; y++) {
            Blocks[2][y][15].setType(15);
            VertexTextureData.put(createTexCube((float) 0, (float) 0, Blocks[2][y][15]));
            VertexPositionData.put(createCube((float)(startX + 2 * CUBE_LENGTH), (float)(y * CUBE_LENGTH + ((int)(CHUNK_SIZE*.8))), (float) (startZ + 15 * CUBE_LENGTH)));
            VertexColorData.put(createCubeVertexCol(getCubeColor(Blocks[2][y][15])));            
        }
        for(int x = 0; x < 5; x++) {
            Blocks[x][18][15].setType(15);
            VertexTextureData.put(createTexCube((float) 0, (float) 0, Blocks[x][18][15]));
            VertexPositionData.put(createCube((float)(startX + x * CUBE_LENGTH), (float)(18 * CUBE_LENGTH + ((int)(CHUNK_SIZE*.8))), (float) (startZ + 15 * CUBE_LENGTH)));
            VertexColorData.put(createCubeVertexCol(getCubeColor(Blocks[x][18][15])));              
        }
        for(int y = 15; y < 17; y++) {
            Blocks[6][y][15].setType(15);
            VertexTextureData.put(createTexCube((float) 0, (float) 0, Blocks[6][y][15]));
            VertexPositionData.put(createCube((float)(startX + 6 * CUBE_LENGTH), (float)(y * CUBE_LENGTH + ((int)(CHUNK_SIZE*.8))), (float) (startZ + 15 * CUBE_LENGTH)));
            VertexColorData.put(createCubeVertexCol(getCubeColor(Blocks[6][y][15])));             
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
    /**
     * Main test to check if our code was working, same as rebuildMesh.
     * @param startX
     * @param startY
     * @param startZ 
     */
    public void collisionDetection(float startX, float startY, float startZ) {
        float[][] test = calcNoise(3.5f,.9f);
        VBOTextureHandle = glGenBuffers();
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        FloatBuffer VertexTextureData = BufferUtils.createFloatBuffer((CHUNK_SIZE*CHUNK_SIZE*CHUNK_SIZE)*6*12);
        FloatBuffer VertexPositionData = BufferUtils.createFloatBuffer(CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 6 * 12);
        FloatBuffer VertexColorData = BufferUtils.createFloatBuffer(CHUNK_SIZE*CHUNK_SIZE*CHUNK_SIZE*6*12);
        
        heights = new int[CHUNK_SIZE][CHUNK_SIZE];
        
        for(float x = 0; x < CHUNK_SIZE; x++) {
            for(float z = 0; z < CHUNK_SIZE; z++) {
                float height = test[(int)x][(int)z] * 25;
                if(height < 0) {
                    height *= -1;
                }
                if(height > 30) {
                    height = 29;
                }
                
                heights[(int)x][(int)z] = (int)height;
              
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
    /**
     * Returns a 2d array containing the maximum heights of our Chunk.
     * @return 
     */
    public int[][] getHeights1(){
    	return heights;
    }
    /**
     * generates random heights for each index.
     */
    public void storeHeight(){
        int rand = r.nextInt(5);
        int rand2 = r.nextInt(5);
        float[] persistChoice = {1.5f,3.5f, .5f, 5.5f, 4.5f};
        float[] weightChoice = {.1f,.9f,1.5f, .5f, .3f};
        float[][] test = calcNoise(3.5f,.9f);
        for(int x = 0; x < CHUNK_SIZE; x++){
            for(int z = 0; z < CHUNK_SIZE; z++){
                float height = test[(int)x][(int)z] * 25;
                if(height < 0) {
                    height *= -1;
                }
                if(height > 30) {
                    height = 29;
                }
                
                heightOfTerrain[nextHeight++] = height;
                
            }
        }
        
    } 
    /**
     * Sets up a GrassLand Biome.
     * @param y
     * @param x
     * @param z
     * @param height 
     */
    private void setUpGrassLand(int y, int x, int z, int height) {
        if(y == 0) {
            Blocks[x][y][z].setType(5);
        }
        else if(y < height) {
            int choice = r.nextInt(2);
            if(choice == 0) {
                Blocks[x][y][z].setType(3);
            }
            else {
                Blocks[x][y][z].setType(4);
            }
        }
        else {
            Blocks[x][y][z].setType(0);
        }
        
    }
    /**
     * Sets up a Sand Biome
     * @param y
     * @param x
     * @param z 
     */
    private void setUpSand(int y, int x, int z) {
        if(y == 0) {
            Blocks[x][y][z].setType(5);
        }
        else {
            Blocks[x][y][z].setType(1);
        }
        
    } 
    /**
     * Sets up a Ocean Biome.
     * @param y
     * @param x
     * @param z 
     */
    private void setUpOcean(int y, int x, int z) {
        if(y == 0) {
            Blocks[x][y][z].setType(5);
        }
        else {
            Blocks[x][y][z].setType(2);
        }
        
    }
    /**
     * Sets up a Stone Biome.
     * @param y
     * @param x
     * @param z 
     */
    private void setUpStone(int y, int x, int z) {
        if(y == 0) {
            Blocks[x][y][z].setType(5);
        }
        else {
            Blocks[x][y][z].setType(4);
        }
        
    }    
    /**
     * Creates a Terrain where every Quadrant is a different Biome.
     * @param y
     * @param x
     * @param z
     * @param height 
     */
    private void createSplitBiome(int y, int x, int z, float height) {
        /**
         * GrassLand
         */
        if(x < 7 && z < 7) {
            setUpGrassLand(y,x,z,(int)height);
        }
        /**
         * Sand
         */
        else if(x < 14 && z < 14) {
            setUpSand(y,x,z);

        }
        /**
         * Waterfall
         */
        else if(x < 21 && z < 21) {
            setUpOcean(y,x,z);

        }
        /**
         * Stone
         */
        else {
            setUpStone(y,x,z);

        }
    }
    /**
     * Default World.
     * @param y
     * @param x
     * @param z
     * @param height
     * @param choice 
     */
   public void setBlockType(float y, float x, float z, float height, int choice){
        if(y == 0) {
                        Blocks[(int)x][(int)y][(int)z].setType(5);
                    }
        if((( y > 1 && y + 1 > height) || y == height) && (height != 0)) {
            if(choice == 0) {
                Blocks[(int)x][(int)y][(int)z].setType(0);
            }
            else if(choice == 1) {
                Blocks[(int)x][(int)y][(int)z].setType(1);
            }
            else
                Blocks[(int)x][(int)y][(int)z].setType(2);
        }
        else if(y != 0) {
            choice = r.nextInt(2);
            if(choice == 0)
                Blocks[(int)x][(int)y][(int)z].setType(3);
            else
                Blocks[(int)x][(int)y][(int)z].setType(4);
        }
    }
   /**
    * World with Glass texture
    * @param y
    * @param x
    * @param z
    * @param height
    * @param choice 
    */
    public void setBlockType1(float y, float x, float z, float height, int choice){
            
        Blocks[(int)x][(int)y][(int)z].setType(7);
  
    }
    /**
     * World with Blue Texture.
     * @param y
     * @param x
     * @param z
     * @param height
     * @param choice 
     */
    public void setBlockType2(float y, float x, float z, float height, int choice){
        Blocks[(int)x][(int)y][(int)z].setType(8);
    }
    /**
     * World with Lines as Texture.
     * @param y
     * @param x
     * @param z
     * @param height
     * @param choice 
     */
    public void setBlockType3(float y, float x, float z, float height, int choice){
        Blocks[(int)x][(int)y][(int)z].setType(9);
    }
    /**
     * World with more defined lines as texture
     * @param y
     * @param x
     * @param z
     * @param height
     * @param choice 
     */
    public void setBlockType4(float y, float x, float z, float height, int choice){
        Blocks[(int)x][(int)y][(int)z].setType(10);        
    }  
    /**
     * Combination of BlockType 2,3,4.
     * @param y
     * @param x
     * @param z
     * @param height
     * @param choice 
     */
    public void setBlockType5(float y, float x, float z, float height, int choice){
        if(x== 0 || z == 0 || x == CHUNK_SIZE-1 || z == CHUNK_SIZE-1)
            Blocks[(int)x][(int)y][(int)z].setType(8);
        else  if (x == 1 || z == 1 || x == CHUNK_SIZE -2 || z == CHUNK_SIZE -2)
            Blocks[(int)x][(int)y][(int)z].setType(9);
        else  if(x == 2 || z == 2 || x == CHUNK_SIZE -3 || z == CHUNK_SIZE -3)
            Blocks[(int)x][(int)y][(int)z].setType(7);
        else if((( y > 1 && y + 1 > height) || y == height) && (height != 0))
            Blocks[(int)x][(int)y][(int)z].setType(8);
        else 
            Blocks[(int)x][(int)y][(int)z].setType(10);
    }    
    /**
     * Custom Texture.
     * @param y
     * @param x
     * @param z
     * @param height
     * @param index 
     */
    public void setBlockType6(float y, float x, float z, float height, int index){
        if( x== 0 || z == 0 || x == CHUNK_SIZE-1 || z == CHUNK_SIZE-1)
            Blocks[(int)x][(int)y][(int)z].setType(11);
        else if(y == (float)heightOfTerrain[index] - 1){
            Blocks[(int)x][(int)y][(int)z].setType(13);
        }
        else if ( y == 0 )
            Blocks[(int)x][(int)y][(int)z].setType(8);
        else if(y == 5 )
            Blocks[(int)x][(int)y][(int)z].setType(0);
        else if( y < 5){
            
            if (y == height)
                Blocks[(int)x][(int)y][(int)z].setType(0);
            else 
                Blocks[(int)x][(int)y][(int)z].setType(3);
        }
        else 
            Blocks[(int)x][(int)y][(int)z].setType(12);
      
    } 
}
