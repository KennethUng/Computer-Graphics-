/***************************************************************
* file: Block.java
* authors: Kenneth L. Ung, Dongri Zhu, Rafael Soto-Salinas
* class: CS 4450.01 – Computer Graphics
*
* assignment: semester project
* date last modified: 4/30/2019
*
* purpose: Holds the definition of what a Block is. It enumerates the block into different types.
* We currently are using only 6 basic types. Will be expanded to add more.
*
****************************************************************/ 
package teamtnt;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author kennethung
 */
public class Block {
    private boolean isActive;
    private BlockType Type;
    private float x,y,z;
    
    public enum BlockType {
        Grass(0), 
        Sand(1), 
        Water(2),
        Dirt(3), 
        Stone(4), 
        Bedrock(5),
        Wood(6), 
        Glass(7),
        Blue(8),
        Lines(9), 
        WhiteLines(10), 
        Bricks(11),
        OldBricks(12), 
        altar(13), 
        TreeFace(14), 
        TNT(15);
        
        private int BlockID;
        
        BlockType(int i) {
            BlockID = i;
        }
        
        public int GetID() {
            return BlockID;
        }
        
        public void setID(int i) {
            BlockID = i;
        }
    }
    public Block() {
        
    }
    public Block(BlockType type) {
        this.Type = type;
    }
    
    public void setCoords(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public boolean isActive() {
        return this.isActive;
    }
    
    public void setActive(boolean active) {
        this.isActive = active;
    }
    
    public int getID() {
        return Type.GetID();
    }
    
    public void setType(int type) {
        Type.setID(type);
    }
    
}
