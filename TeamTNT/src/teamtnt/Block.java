/***************************************************************
* file: Block.java
* authors: Kenneth L. Ung, Dongri Zhu, Rafael Soto-Salinas
* class: CS 4450.01 – Computer Graphics
*
* assignment: semester project
* date last modified: 3/28/2019
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
        BlockType_Grass(0), BlockType_Sand(1), BlockType_Water(2),
        BlockType_Dirt(3), BlockType_Stone(4), BlockType_Bedrock(5),
        BlockType_Wood(6), BlockType_Glass(7);
        
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
    
    public void SetActive(boolean active) {
        this.isActive = active;
    }
    
    public int GetID() {
        return Type.GetID();
    }
}
