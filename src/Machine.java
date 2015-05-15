import java.lang.Math;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Matrix4f;

import java.util.ArrayList;

public class Machine {
	
	public boolean gameOn = true;

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

	//Panel variables
	public Vector3f SW = new Vector3f(width/2.0f, 0.0f, -length);        
	public Vector3f SE = new Vector3f(0.0f, 0.0f, -length);        
	public Vector3f NE = new Vector3f(0.0f, height/2.0f, -length);        
	public Vector3f NW = new Vector3f(width/2.0f, height/2.0f, -length);        

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
	
	public void draw() {
		drawMachine();
		drawBall();
		drawPanel();
	}

	public void intersection() {

		for (float[] wall : surfs)	{

			Vector3f norm = new Vector3f(wall[0], wall[1], wall[2]);

			//Front and paddle detection
			if (pos.z < wall[3]+size && wall[3] < 0) {
				if (pos.x > SW.x || pos.x < SE.x || pos.y > NW.y || pos.y < SW.y) {
					gameOn = false;
					vel.set(0, 0, 0);
					return;
				}
				
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
				pos.y += 0.1f;
				bounce(norm);
				return;
			//Top	
			} else if (pos.y > wall[4]-size && wall[4] > 0) {
				pos.y -= 0.1f;
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
	
	public void drawMachine() {
		GL11.glPushMatrix();

		//Draw boundary walls of play field
		GL11.glBegin(GL11.GL_QUADS);

        GL11.glColor4f(0.0f, 0.0f, 1.0f, 0.7f); //Make back blue

        //Back
        GL11.glVertex3f(width, height, length);
        GL11.glVertex3f(-width, height, length);
        GL11.glVertex3f(-width, -height, length);
        GL11.glVertex3f(width, -height, length);

        GL11.glEnd();

        GL11.glLineWidth(2.0f);

        GL11.glBegin(GL11.GL_LINES);

		GL11.glColor4f(0.0f, 1.0f, 0.0f, 0.5f); //Make floor green

		for (int i = 0; i <= 20; i+=2) {
	        //Top
	        GL11.glVertex3f(-width, height, length - i);
	        GL11.glVertex3f(width, height, length - i);
	        
	        //Bottom
	        GL11.glVertex3f(width, -height, length - i);
	        GL11.glVertex3f(-width, -height, length - i);

        }

        GL11.glColor4f(1.0f, 0.0f, 0.0f, 0.3f); //Make walls red

        for (int i = 0; i <= 20; i+=2) {
	        //Left
	        GL11.glVertex3f(-width, height, length - i);
	        GL11.glVertex3f(-width, -height, length - i);
	        
	        //Right
	        GL11.glVertex3f(width, -height, length - i);
	        GL11.glVertex3f(width, height, length - i);

        }

        //Top Right Rail
        GL11.glVertex3f(-width, height, length);
        GL11.glVertex3f(-width, height, -length);
        
        //Bottom Left Rail
        GL11.glVertex3f(-width, -height, -length);
        GL11.glVertex3f(-width, -height, length);

        //Top Left Rail
        GL11.glVertex3f(width, height, -length);
        GL11.glVertex3f(width, height, length);

        //Bottom Right Rail
        GL11.glVertex3f(width, -height, length);
        GL11.glVertex3f(width, -height, -length);

		GL11.glEnd();
		GL11.glPopMatrix();
	}
	public void movePanel(char in) {
		switch(in) {
			case 'U':
				if (NE.y + 0.1f > height)
					return;
				SW.y+=0.1f; SE.y+=0.1f; NE.y+=0.1f; NW.y+=0.1f;
				break;
			case 'D':
				if (SE.y - 0.1f < -height)
					return;
				SW.y-=0.1f; SE.y-=0.1f; NE.y-=0.1f; NW.y-=0.1f;
				break;
			case 'L':
				if (NW.x + 0.1f > width)
					return;
				SW.x+=0.1f; SE.x+=0.1f; NE.x+=0.1f; NW.x+=0.1f;
				break;
			case 'R':
				if (NE.x - 0.1f < -width)
					return;
				SW.x-=0.1f; SE.x-=0.1f; NE.x-=0.1f; NW.x-=0.1f;
		}
		
	}
	
	public void drawPanel() {
		GL11.glPushMatrix();

		//Draw boundary walls of play field
		GL11.glBegin(GL11.GL_QUADS);

		GL11.glColor4f(0.5f, 0.5f, 0.5f, 0.8f); 

        GL11.glVertex3f(SW.x, SW.y, SW.z);
        GL11.glVertex3f(SE.x, SE.y, SE.z);
        GL11.glVertex3f(NE.x, NE.y, NE.z);
        GL11.glVertex3f(NW.x, NW.y, NW.z);
        
		GL11.glEnd();

		GL11.glBegin(GL11.GL_LINE_LOOP);

		GL11.glColor3f(0.2f, 0.2f, 0.2f); 

		//X Mark
        GL11.glVertex3f(SW.x, SW.y, SW.z);
        GL11.glVertex3f(NE.x, NE.y, NE.z);
        
        GL11.glVertex3f(SE.x, SE.y, SE.z);
        GL11.glVertex3f(NW.x, NW.y, NW.z);


        
		GL11.glEnd();
		GL11.glPopMatrix();
		
	}
	public void drawBall() {
		GL11.glPushMatrix();

		Vector3f.add(vel, g, vel);
		Vector3f.add(pos, vel, pos);

		GL11.glTranslatef(pos.x, pos.y, pos.z);

		if (gameOn == true) {
			GL11.glColor3f(0.2f, 0.2f, 0.2f);
		} else {
			GL11.glColor3f(1.0f, 1.0f, 1.0f);
		}	
		Sphere s = new Sphere();
		s.draw(size, 20, 20);

		GL11.glPopMatrix();
	}

	public void resetBall() {
		pos.x = 0;
		pos.y = 0;
		pos.z = 0;
		gameOn = true;
		vel.set((float)Math.random()/10, (float)Math.random()/10, (float)Math.random()/3);
	}

}