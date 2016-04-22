package hw2;
/*
  This file contains classes representing geometric objects
*/

import com.jogamp.opengl.*;
import java.util.*;

//public List<ClipRectangle> list = new ArrayList<ClipRectangle>();

class Point {
    public int x,y;
    
    Point(int x,int y){
	this.x=x; this.y=y;
    }
}

class Polygon {
	List<Point> verticies = new ArrayList<Point>();
	
	Polygon(List<Point> verticies){
		this.verticies = verticies;
	}
	
	public void draw(GL2 gl, int how){
		gl.glLogicOp(how);
		gl.glBegin(GL2.GL_POLYGON);
		for(Point p : verticies){
			gl.glVertex2f(p.x, p.y);
		}
		gl.glEnd();
		gl.glFlush();
	}
    
}

class Line {
	Point p1;
	Point p2;
	byte outcode = 0;
	byte left = 1;
	byte right = 2;
	byte bottom = 3;
	byte top = 4;
	
	Line(Point p1, Point p2){
		this.p1 = p1;
		this.p2 = p2;
	}
	
	public Point intersection(Line l, Line l2){
		int x1 = l.p1.x;
		int x2 = l.p2.x;
		int x3 = l2.p1.x;
		int x4 = l2.p2.x;
		int y1 = l.p1.y;
		int y2 = l.p2.y;
		int y3 = l2.p1.y;
		int y4 = l2.p2.y;
		
		int d = ((x1 - x2)*(y3 - y4)) - ((y1-y2)*x3-x4);
		if(d == 0){
			// The lines don't intersect
			return null;
		}
		
		int newx = ( ((x3-x4)*(x1*y2-y1*x2)) - ((x1-x2)*(x3*y4-y3*x4)) )/d;
		int newy = ( ((y3-y4)*(x1*y2-y1*x2)) - ((y1-y2)*(x3*y4-y3*x4)) )/d;
		return new Point(newx,newy);
	}
	
	public byte getByteCode(Point p, ClipRectangle cr){
		if(p.x < cr.minX){
			outcode |= left;
		}
		else if (p.x > cr.maxX){
			outcode |= right;
		}
		
		if(p.y < cr.minY){
			outcode |= top;
		}
		else if (p.y > cr.maxY){
			outcode |= bottom;
		}
		return outcode;
	}
	
	public void draw(GL2 gl, int how, ClipRectangle cr){
		boolean trivial = false;
		if(cr != null){
			while(trivial != true){
				byte outcodePoint1 = getByteCode(p1,cr);
				byte outcodePoint2 = getByteCode(p2,cr);
				//Trivial check: line lies entirely within clip rectangle
				if((outcodePoint1 | outcodePoint2) == 0){
					//Line accepted:
					break;
				}
				//Trivial check: entire line lies outside one of the clipping edges
				if((outcodePoint1 & outcodePoint2) != 0){
					//Line Rejected:
					return;
				}
				//Nontrivial:
				//Select an endpoint with a nonzero outcode
				
				byte oc;
				if(outcodePoint1 != 0){
					oc = outcodePoint1;
				}
				else{
					oc = outcodePoint2;
				}
				
				//Select a clip rectangle edge which it is outside
				Point newpoint;
				if((oc & top) !=0){
					newpoint = intersection(this, new Line(new Point(0,cr.minY), new Point(10, cr.minY)));
				}
				else if((oc & bottom) != 0){
					newpoint = intersection(this, new Line(new Point(0, cr.maxY), new Point (10, cr.maxY)));
				}
				else if((oc & left) != 0){
					newpoint = intersection(this, new Line(new Point(cr.minX,0), new Point(cr.minX, 10)));
				}
				else{
					newpoint = intersection(this, new Line(new Point(cr.maxX,0), new Point(cr.maxX,10)));
				}
				//Replace the selected endpoint with the point of intersection found in the previous step.
				if(oc == outcodePoint1){
					p1 = newpoint;
				}
				else{
					p2 = newpoint;
				}
			}
		}
		gl.glLogicOp(how);
		gl.glLineWidth(5.0f); //makes the line thicker for visibility
		gl.glBegin(GL.GL_LINES);
		gl.glVertex2f((float)p1.x, (float)p1.y);
		gl.glVertex2f((float)p2.x, (float)p2.y);
		gl.glEnd();
		gl.glFlush();
	}

}

class FilledRectangle {
    // two points at opposite corners of a rectangle
    Point p1;
    Point p2;
    
    FilledRectangle(Point p1, Point p2){
	this.p1 = p1;
	this.p2 = p2;
    }
    
    // draw the rectangle as a polygon
    public void draw(GL2 gl, int how, ClipRectangle cr){
    	if(cr !=null){
    		Point maxX;
    		Point minX;
    		Point maxY;
    		Point minY;
    		if(p1.x > p2.x){
    			maxX = p1;
    			minX = p2;
    		}
    		else{
    			maxX = p2;
    			minX = p1;
    		}
    		
    		if(p1.y > p2.y){
    			maxY = p1;
    			minY = p2;
    		}
    		else{
    			maxY = p2;
    			minY = p1;
    		}
    		
    		
			if (minX.x < cr.minX) {
				minX.x = cr.minX;
			} else if (maxX.x > cr.maxX) {
				maxX.x = cr.maxX;
			}
			if (minY.y < cr.minY) {
				minY.y = cr.minY;
			} else if (maxY.y > cr.maxY) {
				maxY.y = cr.maxY;
			}
    	}
    	
	gl.glLogicOp(how);
	gl.glBegin(GL2.GL_POLYGON);
	gl.glVertex2f(p1.x,p1.y);
	gl.glVertex2f(p1.x,p2.y);
	gl.glVertex2f(p2.x,p2.y);
	gl.glVertex2f(p2.x,p1.y);
	gl.glEnd();
	gl.glFlush();
    }
}

class Rectangle {
	// two points at opposite corners of a rectangle
    Point p1;
    Point p2;
    
    Rectangle(Point p1, Point p2){
	this.p1 = p1;
	this.p2 = p2;
    }
    
    // draw the rectangle as a polygon
    public void draw(GL2 gl, int how, ClipRectangle cr){
    	if(cr !=null){
    		Point maxX;
    		Point minX;
    		Point maxY;
    		Point minY;
    		if(p1.x > p2.x){
    			maxX = p1;
    			minX = p2;
    		}
    		else{
    			maxX = p2;
    			minX = p1;
    		}
    		
    		if(p1.y > p2.y){
    			maxY = p1;
    			minY = p2;
    		}
    		else{
    			maxY = p2;
    			minY = p1;
    		}
    		
    		 if (minX.x < cr.minX){
    			 minX.x=cr.minX;
    		 }
    		 else if (maxX.x > cr.maxX){
            	 maxX.x = cr.maxX;
             }
             if (minY.y < cr.minY){
            	 minY.y = cr.minY;
             }
             else if (maxY.y > cr.maxY){
            	 maxY.y = cr.maxY;
             }
                 
    	}
    	
	gl.glLogicOp(how);
	gl.glLineWidth(5.0f); //makes the line thicker for visibility
	gl.glBegin(GL2.GL_LINE_LOOP);
	gl.glVertex2f(p1.x,p1.y);
	gl.glVertex2f(p1.x,p2.y);
	gl.glVertex2f(p2.x,p2.y);
	gl.glVertex2f(p2.x,p1.y);
	gl.glEnd();
	gl.glFlush();
    }

}

class ClipRectangle {
	// two points at opposite corners of a rectangle
    Point p1;
    Point p2;
    int maxX, minX, maxY, minY;
    
    
    ClipRectangle(Point p1, Point p2){
	this.p1 = p1;
	this.p2 = p2;
	this.maxX = Math.max(p1.x, p2.x); //right
	this.minX = Math.min(p1.x, p2.x); //left
	this.maxY = Math.max(p1.y, p2.y); //bottom
	this.minY = Math.min(p1.y, p2.y); //top
    }
    
    // draw the rectangle as a polygon
    public void draw(GL2 gl, int how, ClipRectangle cr){
	gl.glLogicOp(how);
	gl.glLineWidth(5.0f); //makes the line thicker for visibility
	gl.glBegin(GL2.GL_LINE_LOOP);
	gl.glVertex2f(p1.x,p1.y);
	gl.glVertex2f(p1.x,p2.y);
	gl.glVertex2f(p2.x,p2.y);
	gl.glVertex2f(p2.x,p1.y);
	gl.glEnd();
	gl.glFlush();
    }
}

