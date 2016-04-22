package hw5;

import java.util.*;
import java.io.*;
import com.jogamp.opengl.*;

class GLObject {
    List<Vertex> vlist;
    List<Vertex> nlist;
    List<Face> flist;
    Map<Vertex,List<Face>> facemap;

    GLObject(){
	vlist = new ArrayList<Vertex>();
	nlist = new ArrayList<Vertex>();
	flist = new ArrayList<Face>();
	facemap = new HashMap<Vertex,List<Face>>();
    }

    GLObject(List<Vertex> vlist, List<Face> flist){
	this.vlist = vlist;
	this.flist = flist;
	facemap = new HashMap<Vertex,List<Face>>();
    }

    public List<Vertex> getVlist(){
	return vlist;
    }

    public List<Face> getFlist(){
	return flist;
    }

    public void render(GLAutoDrawable drawable){
	render(drawable,GL2.GL_FLAT);
    }

    public void render(GLAutoDrawable drawable, int mode){
    int[] temps = {0,0,1,0,1,1,0,1};
	GL2 gl = drawable.getGL().getGL2();
	gl.glShadeModel(mode);
	if(mode==GL2.GL_FLAT){
	    for(Face face : flist){
		gl.glNormal3d(face.normal[0],face.normal[1],face.normal[2]);
		gl.glBegin(GL2.GL_POLYGON);
		int count = 0;
		for(Vertex v : face.getVlist()){
			gl.glTexCoord2f(v.x,v.y);
		    gl.glVertex3f(v.x,v.y,v.z);
//		    if(count < temps.length-1){
//		    	count+=2;
//		    }
		}
		gl.glEnd();
	    }
	}
	else { // smooth shading
	    for(Face face : flist){
		gl.glBegin(GL2.GL_POLYGON);
		int count = 0;
		for(Vertex v : face.getVlist()){
		    gl.glNormal3d(v.nx,v.ny,v.nz);
		    gl.glTexCoord2f(v.x,v.y);
		    gl.glVertex3f(v.x,v.y,v.z);
//		    if(count < temps.length-1){
//		    	count+=2;
//		    }
		}
		gl.glEnd();
	    }
	}
    }

    public float getMinX(){
	float min = Float.POSITIVE_INFINITY;
	for(Vertex v : vlist){
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

    public void computeNormals(int mode){
	if(nlist.isEmpty()){
	    if(mode==GL2.GL_FLAT){
		for(Face f : flist){
		    f.computeNormal();
		}
	    }
	    else {
		for(Face f : flist){
		    f.computeNormal();
		}
		for(Vertex v : vlist){
		    v.computeNormal(facemap.get(v));
		}
	    }
	}
    }

    GLObject(String fname) throws FileNotFoundException,
	InputMismatchException,
	IndexOutOfBoundsException {

	this();

	Scanner scanner = new Scanner(new File(fname));

	while(scanner.hasNextLine()){
	    String line = scanner.nextLine();
	    Scanner scan = new Scanner(line);
	    if(scan.hasNext()){
		String command = scan.next();
		if(command.equals("v")){
		    float x = scan.nextFloat();
		    float y = scan.nextFloat();
		    float z = scan.nextFloat();
		    vlist.add(new Vertex(x,y,z));
		}
		else if(command.equals("vn")){
		    float x = scan.nextFloat();
		    float y = scan.nextFloat();
		    float z = scan.nextFloat();
		    nlist.add(new Vertex(x,y,z));
		}
		else if(command.equals("f")){
		    Face face = new Face();
		    while(scan.hasNext()){
			String s = scan.next();
			String[] ss = s.split("/");
			if(ss.length<3){
			    int vx = Integer.parseInt(ss[0]);
			    Vertex v = vlist.get(vx-1);
			    face.addVertex(v);
			    List<Face> faces = facemap.get(v);
			    if(faces==null){
				faces = new ArrayList<Face>();
				facemap.put(v,faces);
			    }
			    faces.add(face);
			}
			else {
			    int vx = Integer.parseInt(ss[0]);
			    Vertex v = vlist.get(vx-1);
			    int nx = Integer.parseInt(ss[2]);
			    Vertex n = nlist.get(nx-1);
			    v.setNormal(n);
			    face.addVertex(vlist.get(vx-1));
			}
		    }
		    flist.add(face);
		}
	    }
	}
	computeNormals(GL2.GL_FLAT);
    }
}