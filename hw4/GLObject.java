package hw4;

import java.util.*;
import java.io.*;
import com.jogamp.opengl.*;

class GLObject {

    List<Vertex> vlist;
    List<Face> flist;
    List<Vertex> vNormals;
    List<Face> fNormals;

    GLObject(){
	vlist = new ArrayList<Vertex>();
	flist = new ArrayList<Face>();
	vNormals = new ArrayList<Vertex>();
	fNormals = new ArrayList<Face>();
    }

    GLObject(List<Vertex> vlist, List<Face> flist){
	this.vlist = vlist;
	this.flist = flist;
    }

    public List<Vertex> getVlist(){
	return vlist;
    }

    public List<Face> getFlist(){
	return flist;
    }

    public void render(GLAutoDrawable drawable){
	render(drawable,GL2.GL_POLYGON);
    }

    public void render(GLAutoDrawable drawable, int mode){
	GL2 gl = drawable.getGL().getGL2();
	int count =0;
	for(Face face : flist){
		Face normal = null;
	    gl.glBegin(mode);
	    if(!fNormals.isEmpty()){
	    	normal = fNormals.get(count);
	    }
	    int count2 = 0;
	    for(Vertex v : face.getVlist()){
	    	float xTemp = v.x-1f;
	    	float yTemp = v.y-1f;
	    	float zTemp = v.z-1f;
	    	if(!vNormals.isEmpty()){
	    		if(normal== null){
	    			xTemp = vNormals.get(count2).x-1f;
	    			yTemp = vNormals.get(count2).y-1f;
	    			zTemp = vNormals.get(count2).z-1f;
	    			float[] theGL = new float[]{xTemp, yTemp, zTemp};
	    	    	gl.glNormal3fv(theGL, 0);
	    		}
	    	}
	    	gl.glVertex3f(v.x,v.y,v.z);
	    	count2++;
	    }
	    gl.glEnd();
	    count++;
	}
    }

    public float getMinX(){
	float min = Float.POSITIVE_INFINITY;
	for(Vertex v : vlist){
	    //System.out.println(v);
	    if(v.x<min)
		min = v.x;
	}
	return min;
    }

    public float getMinY(){
	float min = Float.POSITIVE_INFINITY;
	for(Vertex v : vlist){
	    if(v.y<min)
		min = v.y;
	}
	return min;
    }

    public float getMinZ(){
	float min = Float.POSITIVE_INFINITY;
	for(Vertex v : vlist){
	    if(v.z<min)
		min = v.z;
	}
	return min;
    }

    public float getMaxX(){
	float max = Float.NEGATIVE_INFINITY;
	for(Vertex v : vlist){
	    if(v.x>max)
		max = v.x;
	}
	return max;
    }

    public float getMaxY(){
	float max = Float.NEGATIVE_INFINITY;
	for(Vertex v : vlist){
	    if(v.y>max)
		max = v.y;
	}
	return max;
    }

    public float getMaxZ(){
	float max = Float.NEGATIVE_INFINITY;
	for(Vertex v : vlist){
	    if(v.z>max)
		max = v.z;
	}
	return max;
    }

    GLObject(String fname) throws FileNotFoundException,
	InputMismatchException,
	IndexOutOfBoundsException {

	this();

	Scanner scanner = new Scanner(new File(fname));

	while(scanner.hasNextLine()){
	    String line = scanner.nextLine();
	    //System.out.println(line);
	    Scanner scan = new Scanner(line);
	    if(scan.hasNext()){
		String command = scan.next();
		if(command.equals("v")){
		    float x = scan.nextFloat();
		    float y = scan.nextFloat();
		    float z = scan.nextFloat();
		    vlist.add(new Vertex(x,y,z));
		}
		if(command.equals("f")){
		    Face face = new Face();
		    while(scan.hasNext()){
			String s = scan.next();
			int n;
			int k = s.indexOf("/");
			if(k<0)
			    n = Integer.parseInt(s);
			else
			    n = Integer.parseInt(s.substring(0,k));
			face.addVertex(vlist.get(n-1));
		    }
		    Vertex p1 = face.getVlist().get(0);
		    Vertex p2 = face.getVlist().get(1);
		    Vertex p3 = face.getVlist().get(2);
		    	Vertex a = new Vertex(0,0,0);
		    	Vertex b = new Vertex(0,0,0);
		    	Vertex n = new Vertex(0,0,0);
		    	a.x = p2.x - p1.x;
		    	a.y = p2.y - p1.y;
		    	a.z = p2.z - p1.z;

		    	b.x = p3.x - p1.x;
		    	b.y = p3.y - p1.y;
		    	b.z = p3.z - p1.z;

		    	n.x = (a.y * b.z) - (a.z * b.y);
		    	n.y = (a.z * b.x) - (a.x * b.z);
		    	n.z = (a.x * b.y) - (a.y * b.x);

		    	// Normalize (divide by root of dot product)
		    	float l = (float) Math.sqrt(n.x * n.x + n.y * n.y + n.z * n.z);
		    	n.x /= l;
		    	n.y /= l;
		    	n.z /= l;
		    	Vertex normalized = new Vertex(n.x,n.y,n.z);
		    	vNormals.add(normalized);
		    
		    flist.add(face);
		}
	    }
	}
    }
}