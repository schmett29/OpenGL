package hw3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

public class DotObj {
	public List<int[]> faces;
	public List<float[]> verticies;
	/*
	When you read in a .obj file, it's a good idea to determine
	the minimum and maximum values of x, y, and z in the model.
	That will be helpful in determining how to position it,
	whether or not it needs to be scaled, etc.
	*/
	public float xmin, xmax, ymin, ymax, zmin, zmax;
	
	public DotObj(){
		this.faces = new ArrayList<>();
		this.verticies = new ArrayList<>();
	}
	
	public void createFace(int[] face){
		this.faces.add(face);
	}
	
	public void addVector(float[] vertices){
		float x = vertices[0];
	   	float y = vertices[1];
	   	float z = vertices[2];
	   	if(x > xmax){
	   		xmax = x;
	   	}
	    else if(x < xmin){
	    	xmin = x;
	    }
	    if(y > ymax){
	    	ymax = y;
	    }
	    else if(y < ymin){
	    	ymin = y;
	    }
	    if (z > zmax){
	    	zmax = z;
	    }
	    else if(z < zmin){
	    	zmin = z;
	    }
	    this.verticies.add(vertices);
	}
	
	public void draw(GL2 gl2){
		gl2.glBegin(GL2.GL_POLYGON);
		//for every face draw the vectors in the vector list
		for(int[] f : faces){
			//System.out.println(Arrays.toString(f));
			for(int i = 0; i < f.length; i++){
				gl2.glVertex3fv(verticies.get(f[i]-1),0);
			}	
		}
		gl2.glEnd();
	}

}
