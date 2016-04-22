package hw1;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;

public class DrawingAndAnimation implements GLEventListener, ChangeListener{
	private GLCanvas canvas;
	private GL2 gl;
	private GLU glu;
	private int numtriangles = 8;
	private float radius = 250.0f;
	private float myx = 0.00f;
	private float myy = 0.00f;
	private float twoPi = 2.0f * (float)Math.PI;
	private double red = 0.0;
	private double green = 0.3;
	private double blue = 0.0;
	private double colorchange = 0.15; //initial but will be changed in render
	private JCheckBox rotateBox;
	private boolean rotating = false;
	private float theta = 0.0f;
	private boolean reverse;
	private JSlider speedSlider;
	private float thetaInc = 0.02f;
	
	public static void main(String[] args){
		new DrawingAndAnimation();
	}
	
	DrawingAndAnimation(){
		GLProfile glp = GLProfile.getDefault();
		GLCapabilities caps = new GLCapabilities(glp);
		canvas = new GLCanvas(caps);
		//canvas.addEventListener( this );
		
		// create GUI components and widgets
		JFrame frame = new JFrame("2D Drawing and Animation");
		frame.add(canvas);
		JPanel topRow = new JPanel( new FlowLayout(FlowLayout.LEADING));
        JPanel north = new JPanel( new BorderLayout());
        JPanel northWest = new JPanel( new BorderLayout());

        rotateBox = new JCheckBox("Rotate                               ");
        rotateBox.setSelected(false);
        
        speedSlider = new JSlider(0, 100);
        speedSlider.addChangeListener(this);
        speedSlider.setValue(4);
        speedSlider.setMinorTickSpacing(5);
        speedSlider.setMajorTickSpacing(25);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        
        topRow.add(rotateBox);
        topRow.add(new JLabel("Slider Speed:"));
        topRow.add(speedSlider);
        
        northWest.add(topRow, BorderLayout.NORTH);       
        north.add(northWest, BorderLayout.WEST);
	    JPanel center = new JPanel(new GridLayout(1,1));
        center.add(canvas);

        frame.add(north,  BorderLayout.NORTH);
        frame.add(center, BorderLayout.CENTER);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE );
	    frame.setSize(500, 500);
	    //frame.setLayout(new BorderLayout());
	    frame.setVisible(true);
	    canvas.requestFocus();

        // create event listeners
	    canvas.addKeyListener(new KeyAdapter(){
		    public void keyPressed(KeyEvent e){
		    	DrawingAndAnimation.this.keyPressed(e);
		    }
		});
	    
	    canvas.addMouseListener(new MouseAdapter(){
		    public void mouseClicked(MouseEvent e){
		    	DrawingAndAnimation.this.mouseClicked(e);
		    }
		});

		//Allows for drawing and modification of canvas:
		canvas.addGLEventListener(this);
		FPSAnimator animator = new FPSAnimator(canvas, 30);
		animator.start();
	}

	// draws the scene on the canvas
	public void display(GLAutoDrawable drawable) {
		update( );  //  handles changes in variables for animation
		render( );  // does the actual drawing with OpenGL commands.
	}
	
    public void mouseClicked(MouseEvent e){
	if(e.getButton()==MouseEvent.BUTTON1){
		if(rotating){
			reverse = !reverse;
		}
	    }
	}
    
    public void keyPressed(KeyEvent e){
		switch(e.getKeyChar()){
			case 'r':
				red=1.0;
				green=0.0;
				blue = 0.0;
				break;
			case 'g':
				red=0.0;
				green=0.3;
				blue = 0.0;
				break;
			case 'b':
				red=0.0;
				green=0.0;
				blue = 1.0;
				break;
			case '+':
				numtriangles+=1;
				break;
			case '-':
				numtriangles-=1;
				break;
			case 'k':
				radius += 10.00f;
				break;
			case 'm':
				radius -= 10.00f;
				break;
		}
    }
	
	public void update(){
		if (rotateBox.isSelected()) {
			rotating = true;
			if(reverse){
				theta -= thetaInc;
			}
			else{
				theta += thetaInc; // incrementing for part 2 rotation
			}     
        }
        else{
        	theta += 0.0;
        	rotating = false;
        }
	}
	
	public void render(){
		green=0.3;
		gl.glClear(GL.GL_COLOR_BUFFER_BIT); // clear the canvas
		gl.glBegin(GL2.GL_TRIANGLES);
			//code to create triangle
		colorchange = (float)1/numtriangles;
		for(int i=1; i<=numtriangles; i++){	 
			 // determine color shading
	        if (i < numtriangles/2) {
	            green += colorchange;
	        }
	        // darken:
	        else {
	            green -= colorchange;
	        }
	        gl.glColor3d(red, green, blue);
	        gl.glVertex2f(myx,myy); // origin
			gl.glVertex2f(
					myx+(radius * (float)Math.cos(theta)),
					myy+(radius * (float)Math.sin(theta))
					);
			theta += twoPi / (float)numtriangles;
			gl.glVertex2f(
					myx+(radius * (float)Math.cos(theta)),
					myy+(radius * (float)Math.sin(theta))
					);
		}
			
		gl.glEnd();
			
	}

	// cleanup method called when the program exits
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		
	}

	// initialization
	public void init(GLAutoDrawable drawable) {
		gl = drawable.getGL( ).getGL2( );
	    glu = new GLU( );
	    //Example:
	    gl.glClearColor(1, 1, 1, 1); // set clear color to white
		
	}

	// called when the canvas is resized
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		myx=width/2;
		myy=height/2;
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluOrtho2D(0,width,height,0);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		thetaInc = (float) (speedSlider.getValue()/100.0);
		
	}
}
