import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Matrix4f;

import java.util.ArrayList;

public class Machine {
	
	//Ball's position
	public Vector3f pos = new Vector3f(0.0f, 0.0f, 0.0f);
	//Ball's velocity
	public Vector3f vel = new Vector3f(0.1f, 0.1f, 0.1f);
	//Ball's size
	public float size = 0.5f;
	
	//Gravity constant
	public static Vector3f g = new Vector3f(0.0f, 0.0f, 0.0f);


	//Wall dimensions
	public float height = 5.0f;
	public float width = 5.0f;
	public float length = 10.0f;
	public float thickness = 0.1f;

	//Flipper variable
	public float flipRest = -length;
	public float flipLToggle = flipRest;
	public float flipRToggle = flipRest;

	//Collection of surfaces w/ normal and length e.g. (normal x, normal y, normal z, len x, len y, len z)
	public ArrayList<float[]> surfs = new ArrayList<float[]>();

	public Machine() {
		//Add Front wall
		float [] front = {0, 0, -1.0f, -length, 0, 0};
		surfs.add(front);

		//Add Back wall
		float [] back = {0, 0, 1.0f, length, 0, 0};
		surfs.add(back);

		//Add Left wall
		float [] left = {1.0f, 0, 0, 0, 0, -width};
		surfs.add(left);

		//Add Right wall
		float [] right = {-1.0f, 0 ,0, 0, 0, width};
		surfs.add(right);

		//Add Bottom Wall
		float [] bottom = {0, 1.0f, 0, 0, -height, 0};
		surfs.add(bottom);

		//Add Top wall
		float [] top = {0, -1.0f, 0, 0, height, 0};
		surfs.add(top);
	}

	public void flip(char c) {
		//TODO: rotate flipper about y instead of moving points?
		switch(c) {
			case 'L':
				flipLToggle = -length+size;
				break;
			case 'R':
				flipRToggle = -length+size;
				break;
			case 'X':
				flipLToggle = -length+size;
				flipRToggle = -length+size;
		}
	}
	
	public void resetFlip() {
		flipLToggle = flipRest;
		flipRToggle = flipRest;
	}
	
	public void draw() {
		drawMachine();
		drawBall();
		// drawFlippers();
	}

	public void intersection() {

		for (float[] wall : surfs)	{

			Vector3f norm = new Vector3f(wall[0], wall[1], wall[2]);

			//Front
			if (pos.z < wall[3]+size && wall[3] < 0) {
				bounce(norm);
				return;
			//Back
			} else if (pos.z > wall[3]-size && wall[3] > 0) { 
				bounce(norm);
				return;
			//Left
			} else if (pos.x > wall[5]-size && wall[5] > 0) { 
				bounce(norm);
				return;
			//Right
			} else if (pos.x < wall[5]+size && wall[5] < 0) { 
				bounce(norm);
				return;
			//Bottom	
			} else if (pos.y < wall[4]+size && wall[4] < 0) {
				bounce(norm);
				return;
			//Top	
			} else if (pos.y > wall[4]-size && wall[4] > 0) {
				bounce(norm);
				return;
			}
			
		}
	}

	public void bounce(Vector3f n){
		//bounce formula v' = 2*(vI dot N)*N - vI
		//no scalar multiplication?
		Vector3f vI = new Vector3f();
		vel.negate(vI);
		float temp = 2*Vector3f.dot(vI, n);
		Vector3f bounce = new Vector3f(temp*n.x, temp*n.y, temp*n.z);
		vel.sub(bounce, vI, vel);
	}
	
	public void drawFlippers() {
		GL11.glPushMatrix();
		GL11.glBegin(GL11.GL_TRIANGLES);

		GL11.glColor3f(1.0f, 1.0f, 0.0f);		
		
		//Left
        GL11.glVertex3f(size, -height, flipLToggle);
        GL11.glVertex3f(width/2, -height, -length-size);
        GL11.glVertex3f(width/2, height, -length-size);

        GL11.glVertex3f(size, -height, flipLToggle);
        GL11.glVertex3f(width/2, -height, -length);
        GL11.glVertex3f(width/2, height, -length);        
        
        GL11.glVertex3f(size, -height, flipLToggle);
        GL11.glVertex3f(width/2, height, -length);
        GL11.glVertex3f(width/2, height, -length-size);  
        
        //Right
        GL11.glVertex3f(-size, -height, flipRToggle);
        GL11.glVertex3f(-width/2, -height, -length-size);
        GL11.glVertex3f(-width/2, height, -length-size);
        
        GL11.glVertex3f(-size, -height, flipRToggle);
        GL11.glVertex3f(-width/2, -height, -length);
        GL11.glVertex3f(-width/2, height, -length);        

        GL11.glVertex3f(-size, -height, flipRToggle);
        GL11.glVertex3f(-width/2, height, -length);
        GL11.glVertex3f(-width/2, height, -length-size);  

		GL11.glEnd();
		GL11.glPopMatrix();
	}
	
	public void drawMachine() {
		GL11.glPushMatrix();

		//Draw boundary walls of play field
		GL11.glBegin(GL11.GL_QUADS);

		GL11.glColor3f(0.0f, 1.0f, 0.0f); //Make floor green

		//Floor
        GL11.glVertex3f(width, -height, length);
        GL11.glVertex3f(-width, -height, length);
        GL11.glVertex3f(-width, -height, -length);
        GL11.glVertex3f(width, -height, -length);

        //Top
        GL11.glVertex3f(width, height, length);
        GL11.glVertex3f(-width, height, length);
        GL11.glVertex3f(-width, height, -length);
        GL11.glVertex3f(width, height, -length);

        GL11.glColor3f(1.0f, 0.0f, 0.0f); //Make walls red

        //Back
        GL11.glVertex3f(width, height, length);
        GL11.glVertex3f(-width, height, length);
        GL11.glVertex3f(-width, -height, length);
        GL11.glVertex3f(width, -height, length);

        //Left
        GL11.glVertex3f(-width, height, length);
        GL11.glVertex3f(-width, height, -length);
        GL11.glVertex3f(-width, -height, -length);
        GL11.glVertex3f(-width, -height, length);

        //Right
        GL11.glVertex3f(width, height, -length);
        GL11.glVertex3f(width, height, length);
        GL11.glVertex3f(width, -height, length);
        GL11.glVertex3f(width, -height, -length);

		GL11.glEnd();
		GL11.glPopMatrix();
	}

	public void drawBall() {
		GL11.glPushMatrix();

		Vector3f.add(vel, g, vel);
		Vector3f.add(pos, vel, pos);

		GL11.glTranslatef(pos.x, pos.y, pos.z);

		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		Sphere s = new Sphere();
		s.draw(size, 20, 20);

		GL11.glPopMatrix();
	}

	public void resetBall() {
		pos.x = 0;
		pos.y = 0;
		pos.z = 0;
	}

}