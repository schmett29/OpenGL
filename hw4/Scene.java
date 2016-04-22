package hw4;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;

public class Scene implements GLEventListener, ActionListener, ChangeListener, MouseListener, KeyListener{
	public static final String PATH = "hw4/"; //updatae the path so it points to the folder with the obj files (mine are in hw4)
	private static Scanner scanner;
	private int initialwidth = 750;
	private int initialheight = 750;
	private GLCanvas canvas;
	private GL2 gl2;
	private GLU glu;
	private int shade = GL2.GL_SMOOTH;
	GLUT glut;
	boolean wire = false;
	private GLObject object1;
	private GLObject object2;
	private float theta, angle1, angle2, zoom;
	public static final float colors[][] = new float[][] {
            {1f,0f,0f}, {0f, 1f, 0f}, {0f, 0f, 1f},
            {0f, 1f, 1f}, {1f, 0f, 1f}, {1f, 1f,0f}
    };
    private JSlider ambientSlider, diffuseSlider, 
	specularSlider, shininessSlider, divisionsSlider;
    private JRadioButton light0Button, light1Button, shading;
    private JLabel divisionsLabel;
    boolean light0On = true;
    boolean light1On = true;

    int subdivisions = 10;
    private float lightAmbient[] = {.3f, .3f, .3f, 1f};
    private float lightDiffuse[] = {.7f, .7f, .7f, .7f};
    private float lightSpecular[] = {1f, 1f, 1f, 1f};
    private float light0Position[] = {20f, 0f, -5f, 1f};
    private float light1Position[] = {0f, 10f, 10f, 1f};
    private float white[] = {0f,0f,0f};
    private float red[] = {1f,0f,0f};
    private float sphere1Ambient[] = {0, 0.3f, 0};
    private float sphere1Diffuse[] = {0, 0.9f, 0};
    
    private float backAmbient[] = {0f, 0.3f, 0.3f};
    private float backDiffuse[] = {0f, 0.9f, 0.9f};
    
    private float materialSpecular[] = {0.9f, 0.9f, 0.9f};
    private int materialShininess = 32;
    
    public Scene(){
    	try {
			object1 = new GLObject(PATH + "gourd.obj.txt");
			object2 = new GLObject(PATH + "teapot.obj.txt");
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
         
         JFrame sliderFrame = new JFrame("Modify Attributes");
     	sliderFrame.setLayout(new GridLayout(6,1));
     	sliderFrame.setSize(300, 600);
     	sliderFrame.setAlwaysOnTop(true);
     	sliderFrame.setLocation(1000, 50);
     	
     	JPanel row0 = new JPanel(new FlowLayout(FlowLayout.LEADING));
     	light0Button = new JRadioButton("Light 0",true);
     	light0Button.addActionListener(this);
     	row0.add(light0Button);
     	light1Button = new JRadioButton("Light 1",true);
     	light1Button.addActionListener(this);
     	row0.add(light1Button);
     	shading = new JRadioButton("Smooth/Flat", true);
     	shading.addActionListener(this);
     	row0.add(shading);
     	sliderFrame.add(row0);

     	JPanel row1 = new JPanel(new BorderLayout());
     	ambientSlider = newSlider(row1, 0, 100, 25, "Ambient");
     	ambientSlider.setValue(50);
     	sliderFrame.add(row1);

     	JPanel row2 = new JPanel(new BorderLayout());
     	diffuseSlider = newSlider(row2, 0, 100, 25, "Diffuse");
     	diffuseSlider.setValue(60);
     	sliderFrame.add(row2);

     	JPanel row3 = new JPanel(new BorderLayout());
     	specularSlider = newSlider(row3, 0, 100, 25, "Specular");
     	specularSlider.setValue(50);
     	sliderFrame.add(row3);

     	JPanel row4 = new JPanel(new BorderLayout());
     	shininessSlider = newSlider(row4, 0, 100, 25, "Shininess");
     	shininessSlider.setValue(30);
     	sliderFrame.add(row4);

     	JPanel row5 = new JPanel(new BorderLayout());
     	divisionsSlider = newSlider(row5, 10, 100, 20, "Divisions");
     	divisionsLabel = new JLabel(""+subdivisions);
     	divisionsSlider.setValue(subdivisions);
     	row5.add(divisionsLabel, BorderLayout.EAST);
     	sliderFrame.add(row5);
         
         frame.setVisible(true);
         sliderFrame.setVisible(true);
         this.canvas.addGLEventListener(this);
         this.canvas.addMouseListener(this);
         this.canvas.addKeyListener(this);
         this.angle1 = 0f;
         this.angle2 = (float)Math.PI/-2;
         this.zoom = 1;
         FPSAnimator animator = new FPSAnimator(canvas, 60);
         animator.start();
	}
    
	public static void main(String[] args) {
		Scene s = new Scene();
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
		gl2.glShadeModel(shade);
        gl2.glMatrixMode(GL2.GL_MODELVIEW);
        gl2.glLoadIdentity();
        
        float xLook = (float) (Math.cos(angle1) * Math.cos(angle2));
        float yLook = (float) Math.sin(angle1);
        float zLook = -1 * (float) (Math.cos(angle1) * Math.sin(angle2));
        glu.gluLookAt(0, 1, 0, xLook, yLook+1, zLook, 0, 1, 0); // view transformation
        gl2.glScalef(zoom, zoom, 1.0f);
        
     // turn the lights on or off
    	if(light0On)
    	    gl2.glEnable(GL2.GL_LIGHT0);
    	else
    	    gl2.glDisable(GL2.GL_LIGHT0);
    	if(light1On)
    	   gl2.glEnable(GL2.GL_LIGHT1);
    	else
    	    gl2.glDisable(GL2.GL_LIGHT1);

    	for(int light=GL2.GL_LIGHT0; light<=GL2.GL_LIGHT1; light++){
    	    gl2.glLightfv(light,  GL2.GL_AMBIENT, lightAmbient, 0);
    	    gl2.glLightfv(light,  GL2.GL_DIFFUSE, lightDiffuse, 0);
    	    gl2.glLightfv(light,  GL2.GL_SPECULAR, lightSpecular, 0);
    	}
        gl2.glPushMatrix();
        //Drawing floor
        gl2.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
        gl2.glBegin(GL2.GL_QUADS);
        
        for (int i = -24 / 2; i < 24 / 2; i++) {
            for (int k = -24; k < 24; k++) {
                if ((i + k) % 2 == 0){
                	gl2.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, red, 0);
        			gl2.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, red, 0);
        			gl2.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, materialSpecular, 0);
        			gl2.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, materialShininess);
                }
                else{
                	gl2.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, white, 0);
        			gl2.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, white, 0);
        			gl2.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, materialSpecular, 0);
        			gl2.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, materialShininess);
                }
                gl2.glVertex3f(i, -1, k);
                gl2.glVertex3f(i + 1, -1, k);
                gl2.glVertex3f(i + 1, -1, k + 1);
                gl2.glVertex3f(i, -1, k + 1);
            }
        }
        gl2.glEnd();
        gl2.glPopMatrix();
        
        for(int x = 0; x < 4; x++){
        	for(int y = 0; y < 2; y++){
        		for(int z = -6; z < 6; z++){
        			GLObject object = (Math.abs(z+x) % 2 == 0) ? object1 : object2;
        			gl2.glPushMatrix();
        			gl2.glTranslatef(4 * x - 6.1f, 4 * y, 4 * z - 2.1f);
        			float scale = 1f/(object.getMaxX() - object.getMinX()); // makes all shapes same size so large shapes don't take the entire canvas
        			gl2.glScalef(scale, scale, scale);
        			gl2.glRotatef(y==0 ? 0 : theta, 1, 0, 0); //rotate theta around x axis or rest on floor
        			gl2.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, sphere1Ambient, 0);
        			gl2.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, sphere1Diffuse, 0);
        			gl2.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, materialSpecular, 0);
        			gl2.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, materialShininess);
        			drawObj(object);
        			gl2.glPopMatrix();
        			
        		}
        	}
        }
        theta += 2f;	
	}
	
	public void drawObj(GLObject obj){
		// set the fill color
		gl2.glEnable(GL2.GL_POLYGON_OFFSET_FILL);
        gl2.glPolygonOffset(1.0f, 1.0f);
        gl2.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
        // draw the object here
        obj.render(canvas);
        gl2.glDisable(GL2.GL_POLYGON_OFFSET_FILL);
        gl2.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
        // draw the object again
        obj.render(canvas);
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
        glut = new GLUT();
        gl2.glShadeModel(shade);
        float dir [] = {-1, 0, 0};
    	float pos[] = {2, 0, 0};
    	gl2.glEnable(GL2.GL_LIGHTING);
    	gl2.glEnable(GL2.GL_LIGHT0);
    	gl2.glLightfv(GL2.GL_LIGHT0,  GL2.GL_POSITION, light0Position, 0);
    	gl2.glEnable(GL2.GL_LIGHT1);
    	gl2.glLightfv(GL2.GL_LIGHT1,  GL2.GL_POSITION, light1Position, 0);
        glu.gluLookAt(0, 0, 0, 0, 0, -1, 0, 1, 0); // looking in the negative z direction
        gl2.glEnable(GL2.GL_NORMALIZE);
		
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
	
	// This assumes the parent is a panel using BorderLayout.
    private JSlider newSlider(JPanel parent, int min, int max, int step, String label) {
	JSlider slider = new JSlider(min, max);
	slider.setMajorTickSpacing(step);
	slider.setPaintTicks(true);
	slider.setPaintLabels(true); 
	slider.addChangeListener(this);
	JLabel name = new JLabel(label);
	parent.add(name, BorderLayout.WEST); 
	parent.add(slider, BorderLayout.CENTER);
	return slider;
    }
    
    public void actionPerformed(ActionEvent event) {
    	if (event.getSource() == light0Button)
    	    light0On = !light0On;
    	else if (event.getSource() == light1Button) {
    	    light1On = !light1On;
    	}
    	else if(event.getSource() == shading){
    		if(shade == GL2.GL_FLAT){
    			shade = GL2.GL_SMOOTH;
    		}
    		else if(shade == GL2.GL_SMOOTH){
    			shade = GL2.GL_FLAT;
    		}
    	}
        }
        
        public void stateChanged(ChangeEvent e) {
    	if (e.getSource() == ambientSlider) {
    	    lightAmbient[0]=lightAmbient[1]=lightAmbient[2]=
    		ambientSlider.getValue()/100.0f;
    	}
    	if (e.getSource() == diffuseSlider) {
    	    lightDiffuse[0]=lightDiffuse[1]=lightDiffuse[2]=
    		diffuseSlider.getValue()/100.0f;
    	}
    	if (e.getSource() == specularSlider) {
    	    lightSpecular[0]=lightSpecular[1]=lightSpecular[2]=
    		specularSlider.getValue()/100.0f;
    	}
    	if (e.getSource() == shininessSlider) {
    	    materialShininess=shininessSlider.getValue();
    	}
    	else if (e.getSource() == divisionsSlider) {
    	    subdivisions = divisionsSlider.getValue();
    	    divisionsLabel.setText(String.format( "%5.0f     ", new Float(subdivisions)));
    	}
        }

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			int x=e.getX();
		    int y=e.getY();
		    System.out.println(x+","+y);//these co-ords are relative to the component
		    if(x < initialwidth - 575){ // go left
		    	angle2 += 0.1;
		    	System.out.println("left");
		    }
		    else if(x > initialwidth-175){ //go right
		    	angle2 -= 0.1;
		    	System.out.println("right");
		    }
		    else if(y < initialheight/2){ //go up
		    	angle1 += 0.1;
		    	System.out.println("up");
		    }
		    else if(y > initialheight/2){ // go down
		    	angle1 -= 0.1;
		    	System.out.println("down");
		    }
		    	
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void keyPressed(KeyEvent e) {
			char key = e.getKeyChar();
			if(key == '+' || key == '='){
				zoom+=0.5f;
			}
			if(key == '-' || key == '_'){
				if(zoom != 1){
					zoom-=0.5f;
				}
			}
			
		}

		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}

}
