package hw2;

/*
   This file contains the drawing pens used by the Painter
   application.

   All pens are subclasses of the abstract class Pen.

   The code defines how each type of pen responds to mouse events.
*/
import com.jogamp.opengl.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

abstract class Pen {
    protected static final int FIRST=1, CONTINUE=2;
    protected static ClipRectangle therect;
    protected int state;
    protected float r,g,b;
    protected GL2 gl;
    protected static ClipRectangle cr;
    
    Pen(GL2 gl, ClipRectangle cr){
	this.gl = gl;
	this.cr = cr;
	state = FIRST;
    }
    
    public void mouseDown(MouseEvent e){}
    public void mouseUp(MouseEvent e){}
    public void mouseDragged(MouseEvent e){}
    public void setColor(float r, float g, float b){
	this.r = r; this.g = g; this.b = b;
    }
}

class ClipRectanglePen extends Pen {
	Point p1,p2;
    ClipRectangle crct;

    ClipRectanglePen(GL2 gl){
    	super(gl, cr);
    }
    
    public void mouseDown(MouseEvent e){
    	int xnow = e.getX();
    	int ynow = e.getY();
    	p1 = p2 = new Point(xnow,ynow);
    	crct = new ClipRectangle(p1,p2);
    	gl.glColor3f(1,1,1);
    	crct.draw(gl, GL2.GL_XOR, cr);
        }
        
        public void mouseUp(MouseEvent e){
    	// erase the last rectangle
    	crct.draw(gl, GL2.GL_XOR, cr);
    	// get the new corner point
    	int xnow = e.getX();
    	int ynow = e.getY();
    	p2 = new Point(xnow,ynow);
    	crct = new ClipRectangle(p1,p2);
    	if(cr == null){
    		cr = crct;
    	}
    	else{
    		// Find intersection of Clip Rectangles

    		Point maxX;
    		Point minX;
    		Point maxY;
    		Point minY;
			if (p1.x > p2.x) {
				maxX = p1;
				minX = p2;
			} else {
				maxX = p2;
				minX = p1;
			}

			if (p1.y > p2.y) {
				maxY = p1;
				minY = p2;
			} else {
				maxY = p2;
				minY = p1;
			}
			if (minX.x < cr.minX) {
				crct.minX = cr.minX;
			}
			if (maxX.x > cr.maxX) {
				crct.maxX = cr.maxX;
			}
			if (minY.y < cr.minY) {
				crct.minY = cr.minY;
			}
			if (maxY.y > cr.maxY) {
				crct.maxY = cr.maxY;
			}   
    	}
    	cr = crct;
    	gl.glColor3f(0,0,0);
    	// draw the new version permanently
    	crct.draw(gl, GL2.GL_COPY, cr);
        }
        
        public void mouseDragged(MouseEvent e){
    	// erase the last rectangle
    	crct.draw(gl, GL2.GL_XOR, cr);
    	// get the new corner point
    	int xnow = e.getX();
    	int ynow = e.getY();
    	p2 = new Point(xnow,ynow);
    	crct = new ClipRectangle(p1,p2);
    	// draw the new version
    	crct.draw(gl, GL2.GL_XOR, cr);
        }
    
}

class RectanglePen extends Pen {
	Point p1,p2;
    Rectangle rct;

    RectanglePen(GL2 gl){
	super(gl, cr);
    }
    
    public void mouseDown(MouseEvent e){
    	int xnow = e.getX();
    	int ynow = e.getY();
    	p1 = p2 = new Point(xnow,ynow);
    	rct = new Rectangle(p1,p2);
    	gl.glColor3f(1-r,1-g,1-b);
    	rct.draw(gl, GL2.GL_XOR, cr);
        }
        
        public void mouseUp(MouseEvent e){
    	// erase the last rectangle
    	rct.draw(gl, GL2.GL_XOR, cr);
    	// get the new corner point
    	int xnow = e.getX();
    	int ynow = e.getY();
    	p2 = new Point(xnow,ynow);
    	rct = new Rectangle(p1,p2);
    	gl.glColor3f(r,g,b);
    	// draw the new version permanently
    	rct.draw(gl, GL2.GL_COPY, cr);
        }
        
        public void mouseDragged(MouseEvent e){
    	// erase the last rectangle
    	rct.draw(gl, GL2.GL_XOR, cr);
    	// get the new corner point
    	int xnow = e.getX();
    	int ynow = e.getY();
    	p2 = new Point(xnow,ynow);
    	rct = new Rectangle(p1,p2);
    	// draw the new version
    	rct.draw(gl, GL2.GL_XOR, cr);
        }
    
}

class FilledRectanglePen extends Pen {
    Point p1,p2;
    FilledRectangle rct;
    
    FilledRectanglePen(GL2 gl){
    	super(gl, cr);
    }

    public void mouseDown(MouseEvent e){
	int xnow = e.getX();
	int ynow = e.getY();
	p1 = p2 = new Point(xnow,ynow);
	rct = new FilledRectangle(p1,p2);
	gl.glColor3f(1-r,1-g,1-b);
	rct.draw(gl, GL2.GL_XOR, cr);
    }
    
    public void mouseUp(MouseEvent e){
	// erase the last rectangle
	rct.draw(gl, GL2.GL_XOR, cr);
	// get the new corner point
	int xnow = e.getX();
	int ynow = e.getY();
	p2 = new Point(xnow,ynow);
	rct = new FilledRectangle(p1,p2);
	gl.glColor3f(r,g,b);
	// draw the new version permanently
	rct.draw(gl, GL2.GL_COPY, cr);
    }
    
    public void mouseDragged(MouseEvent e){
	// erase the last rectangle
	rct.draw(gl, GL2.GL_XOR, cr);
	// get the new corner point
	int xnow = e.getX();
	int ynow = e.getY();
	p2 = new Point(xnow,ynow);
	rct = new FilledRectangle(p1,p2);
	// draw the new version
	rct.draw(gl, GL2.GL_XOR, cr);
    }
}

class LinePen extends Pen {
	Point p1, p2;
	Line line;

	LinePen(GL2 gl) {
		super(gl, cr);
	}

	public void mouseDown(MouseEvent e) {
		int xnow = e.getX();
		int ynow = e.getY();
		p1 = p2 = new Point(xnow, ynow);
		line = new Line(p1, p2);
		gl.glColor3f(1 - r, 1 - g, 1 - b);
		line.draw(gl, GL2.GL_XOR, cr);
	}

	public void mouseUp(MouseEvent e) {
		// erase the last rectangle
		line.draw(gl, GL2.GL_XOR, cr);
		// get the new corner point
		int xnow = e.getX();
		int ynow = e.getY();
		p2 = new Point(xnow, ynow);
		line = new Line(p1, p2);
		gl.glColor3f(r, g, b);
		// draw the new version permanently
		line.draw(gl, GL2.GL_COPY, cr);
	}

	public void mouseDragged(MouseEvent e) {
		// erase the last rectangle
		line.draw(gl, GL2.GL_XOR, cr);
		// get the new corner point
		int xnow = e.getX();
		int ynow = e.getY();
		p2 = new Point(xnow, ynow);
		line = new Line(p1, p2);
		// draw the new version
		line.draw(gl, GL2.GL_XOR, cr);
	}

}

class LineLoopPen extends Pen {
	Point p1, p2, p3;
	Line lineloop;
	int counter = 0;
	
    LineLoopPen(GL2 gl){
    	super(gl, cr);
    }
    
    public void mouseDown(MouseEvent e) {
		int xnow = e.getX();
		int ynow = e.getY();
		if (counter<1){
			p1 = p2 = p3 = new Point(xnow,ynow);
		}
		p2 = p3;
		p3 = new Point(xnow,ynow);
		lineloop = new Line(p2, p3);
		gl.glColor3f(1 - r, 1 - g, 1 - b);
		
		//Ending LineLoop
		if(p2.x == p3.x && p2.y == p3.y && counter > 0){
			p3 = p1;
			lineloop = new Line(p2, p3);
			lineloop.draw(gl, GL2.GL_XOR, cr);
			counter = 0;
		}
		else{
			lineloop.draw(gl, GL2.GL_XOR, cr);
			counter++;
		}
		
	}

	public void mouseUp(MouseEvent e) {
		// erase the last rectangle
		lineloop.draw(gl, GL2.GL_XOR, cr);
		gl.glColor3f(r, g, b);
		// draw the new version permanently
		lineloop.draw(gl, GL2.GL_COPY, cr);
		// get the new corner point
		int xnow = e.getX();
		int ynow = e.getY();
		p2 = new Point(xnow, ynow);
		lineloop = new Line(p2, p3);
	}

	public void mouseDragged(MouseEvent e) {
		// erase the last rectangle
		lineloop.draw(gl, GL2.GL_XOR, cr);
		// get the new corner point
		int xnow = e.getX();
		int ynow = e.getY();
		p3 = new Point(xnow, ynow);
		lineloop = new Line(p2, p3);
		// draw the new version
		lineloop.draw(gl, GL2.GL_XOR, cr);
	}

}

class FilledPolygonPen extends Pen {
	Point p1, p2, p3;
	Polygon polygon;
	int counter = 0;
	List<Point> verticies = new ArrayList<Point>();
    
    FilledPolygonPen(GL2 gl){
    	super(gl, cr);
    }
    
    public void mouseDown(MouseEvent e) {
		int xnow = e.getX();
		int ynow = e.getY();
		if (counter<1){
			p1 = p2 = p3 = new Point(xnow,ynow);
			verticies.add(p3);
		}
		p2 = p3;
		p3 = new Point(xnow,ynow);	
		
		//Ending Polygon
		if(p2.x == p3.x && p2.y == p3.y && counter > 1){
			gl.glColor3f(1 - r, 1 - g, 1 - b);
			polygon = new Polygon(verticies);
			polygon.draw(gl, GL2.GL_XOR);
			counter = 0;
			verticies = new ArrayList<Point>();
		}
		else{
			verticies.add(p3);
			counter++;
		}
		
	}

}

