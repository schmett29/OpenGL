package hw3;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.Scanner;

import javax.swing.JFrame;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;

public class Scene implements GLEventListener{
	public static final String PATH = "hw3/"; //updatae the path so it points to the folder with the obj files (mine are in hw3)
	private static Scanner scanner;
	private int initialwidth = 750;
	private int initialheight = 750;
	private GLCanvas canvas;
	private GL2 gl2;
	private GLU glu;
	private DotObj object1;
	private DotObj object2;
	private float theta;
	public static final float colors[][] = new float[][] {
            {1f,0f,0f}, {0f, 1f, 0f}, {0f, 0f, 1f},
            {0f, 1f, 1f}, {1f, 0f, 1f}, {1f, 1f,0f}
    };
    
    public Scene(){
    	try {
	    	File file1 = new File(PATH + "skyscraper.obj.txt");
			object1 = readObjFile(file1);
			File file2 = new File(PATH + "gourd.obj.txt");
			object2 = readObjFile(file2);
    	} catch(FileNotFoundException e){
    		e.printStackTrace();
    	}
    	 GLProfile glp = GLProfile.getDefault();
         GLCapabilities caps = new GLCapabilities(glp);
         caps.setDoubleBuffered(false);
         this.canvas = new GLCanvas(caps);
         JFrame frame = new JFrame("GLObjRender");
         frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         frame.setSize(initialwidth, initialheight);
         frame.setLocation(50, 50);
         frame.setLayout(new BorderLayout());
         frame.add(canvas);
         frame.setVisible(true);
         this.canvas.addGLEventListener(this);
         FPSAnimator animator = new FPSAnimator(canvas, 60);
         animator.start();
	}
    
	public static void main(String[] args) {
		Scene s = new Scene();
	}
	
	public DotObj readObjFile(File file) throws FileNotFoundException{
		scanner = new Scanner(file);
		DotObj object = new DotObj();
		while(scanner.hasNextLine()){
			String line = scanner.nextLine();
			String[] tokens = line.split("\\s+"); // split on spaces
			if(tokens.length == 0){
				continue;
			}
			switch(tokens[0]){
			//if vertex:
			case "v":
				float x = Float.parseFloat(tokens[1]);
				float y = Float.parseFloat(tokens[2]);
				float z = Float.parseFloat(tokens[3]);
				float[] vertex = new float[]{x,y,z};
				object.addVector(vertex);
				break;
			// if face:
			case "f":
				int[] token = new int[tokens.length-1];
				// add every face
				for(int i = 1; i < tokens.length; i++){
					String[] verts = tokens[i].split("/+"); // get rid of double /
					int vert = Integer.parseInt(verts[0]);
					token[i-1]=vert; // gets the first number 4//4 --> 4,4
				}
				object.createFace(token);
			}
		}
		return object;
	}

	@Override
	public void display(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		render();
		
	}

	private void render() {
		// TODO Auto-generated method stub
		gl2.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl2.glClearColor(0.196078f, 0.6f, 0.8f, 0f);
        gl2.glMatrixMode(GL2.GL_MODELVIEW);
        gl2.glLoadIdentity();
        
        //Drawing floor
        gl2.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
        gl2.glBegin(GL2.GL_QUADS);
        for (int i = -24 / 2; i < 24 / 2; i++) {
            for (int k = -24; k < 24; k++) {
                if ((i + k) % 2 == 0) gl2.glColor3f(1, 0, 0);
                else gl2.glColor3f(0, 0, 0);
                gl2.glVertex3f(i, -1, k);
                gl2.glVertex3f(i + 1, -1, k);
                gl2.glVertex3f(i + 1, -1, k + 1);
                gl2.glVertex3f(i, -1, k + 1);
            }
        }
        gl2.glEnd();
        
        for(int x = 0; x < 4; x++){
        	for(int y = 0; y < 2; y++){
        		for(int z = -6; z < 6; z++){
        			DotObj object = (Math.abs(z+x) % 2 == 0) ? object1 : object2;
        			gl2.glPushMatrix();
        			gl2.glTranslatef(4 * x - 6.1f, 4 * y, 4 * z - 2.1f);
        			float scale = 1f/(object.xmax - object.xmin); // makes all shapes same size so large shapes don't take the entire canvas
        			gl2.glScalef(scale, scale, scale);
        			gl2.glRotatef(y==0 ? 0 : theta, 1, 0, 0); //rotate theta around x axis or rest on floor
        			gl2.glColor3fv(colors[x],0);
        			drawObj(object);
        			gl2.glPopMatrix();
        			
        		}
        	}
        }
        theta += 2f;	
	}
	
	public void drawObj(DotObj obj){
		// set the fill color
		gl2.glEnable(GL2.GL_POLYGON_OFFSET_FILL);
        gl2.glPolygonOffset(1.0f, 1.0f);
        gl2.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
        // draw the object here
        obj.draw(gl2);
        gl2.glDisable(GL2.GL_POLYGON_OFFSET_FILL);

        gl2.glColor3f(0, 0, 0);  // use black for the wire frame
        gl2.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
        // draw the object again
        obj.draw(gl2);
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		gl2 = arg0.getGL().getGL2();
        glu = new GLU();
        gl2.glShadeModel(GL2.GL_FLAT);
        glu.gluLookAt(0, 0, 0, 0, 0, -1, 0, 1, 0); // looking in the negative z direction
		
	}

	@Override
	public void reshape(GLAutoDrawable arg0, int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		float aspect = width / (float) height;
        gl2.glEnable(GL2.GL_DEPTH_TEST);
        gl2.glMatrixMode(GL2.GL_PROJECTION);
        gl2.glLoadIdentity();
        glu.gluPerspective(30, aspect, 1, 100);
		
	}

}
