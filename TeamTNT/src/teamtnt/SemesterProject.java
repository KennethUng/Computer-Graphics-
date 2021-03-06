package teamtnt;

/***************************************************************
* file: SemesterProject.java
* authors: Kenneth L. Ung, Dongri Zhu, Rafael Soto-Salinas
* class: CS 4450.01 – Computer Graphics
*
* assignment: semester project
* date last modified: 3/11/2019
*
* purpose: to show a cube with a camera view that is able to be controlled
* by the mouse. also, they may be able to navigate through using the keyboard.
*
****************************************************************/ 


import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.glu.GLU;

public class SemesterProject {
    
    private FloatBuffer lightPosition;
    private FloatBuffer whiteLight;
    
    private DisplayMode displayMode;
    
    public void start(){
        FPCameraController fp = new FPCameraController(0.0f,0.0f,0.0f);
        
        try{
            createWindow();
            initGL();
            fp.gameLoop();
            
        }
        catch(Exception e){
        }
    }
    private void createWindow() throws Exception {
        Display.setFullscreen(false);
        DisplayMode d[] = Display.getAvailableDisplayModes();
        for(int i =0; i < d.length; i++){
            if(d[i].getWidth() == 640 
                    && d[i].getHeight() == 480
                    && d[i].getBitsPerPixel() ==32)
            {
                displayMode = d[i];
                break;
            }
        }
        Display.setDisplayMode(displayMode);
        Display.setTitle("TeamTNT");
        Display.create();
    }  
    private void initGL() {
        initLightArrays();
        glLight(GL_LIGHT0,GL_POSITION,lightPosition);
        glLight(GL_LIGHT0,GL_SPECULAR,whiteLight);
        glLight(GL_LIGHT0,GL_DIFFUSE,whiteLight);
        glLight(GL_LIGHT0,GL_AMBIENT,whiteLight);
        
        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);
         glClearColor(0.0f,255.0f,255.0f,100.0f);
         glEnable(GL_TEXTURE_2D);
         glEnableClientState(GL_TEXTURE_COORD_ARRAY);
         glEnableClientState(GL_VERTEX_ARRAY);
         glEnableClientState(GL_COLOR_ARRAY);
         glEnable(GL_DEPTH_TEST);
         glMatrixMode(GL_PROJECTION);
         glLoadIdentity();
         
         GLU.gluPerspective(100.0f, (float)displayMode.getWidth()/
                 (float)displayMode.getHeight(), 0.1f, 300);
         glMatrixMode(GL_MODELVIEW);
         glHint(GL_PERSPECTIVE_CORRECTION_HINT,GL_NICEST);
         
    }
    
    private void initLightArrays() {
        lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(0.0f).put(0.0f).put(0.0f).put(1.0f).flip();
        
        whiteLight = BufferUtils.createFloatBuffer(4);
        whiteLight.put(1.0f).put(1.0f).put(1.0f).put(0.0f).flip();
    }
    public static void main(String[] args) {
        SemesterProject dis = new SemesterProject();
        dis.start();
    }
    
}
