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
	public Vector3f vel = new Vector3f(0.01f, 0.0f, 0.1f);
	//Ball's size
	public float size = 0.5f;
	
	//Gravity constant (need to fine tune)
	public static Vector3f g = new Vector3f(0.0f, 0.0f, -0.01f);

	//Wall dimensions
	public float height = 0.5f;
	public float width = 2.0f;
	public float length = 5.0f;

	//Collection of surfaces w/ normal and length e.g. (normal x, normal z, len x, len z)
	public ArrayList<Vector4f> surfs = new ArrayList<Vector4f>();

	public Machine() {
		//Don't need to add floor surface since it is perpendicular to the plane of motion

		//Add Front wall
		surfs.add(new Vector4f(0, -1.0f, width*2, 0));

		//Add Back wall
		surfs.add(new Vector4f(0, 1.0f, width*2, 0));

		//Add Left wall
		surfs.add(new Vector4f(1.0f, 0, 0, length*2));

		//Add Right wall
		surfs.add(new Vector4f(-1.0f, 0, 0, length*2));
	}


	public void draw() {
		drawMachine();
		drawBall();
	}

	public void intersection() {
		Vector3f normal = new Vector3f();

		for (Vector4f wall : surfs)	{
			//Front
			if (pos.z < -length+size) {
				normal.z = -1; 
				bounce(normal);
			//Back
			} else if (pos.z > length-size) { 
				normal.x = 1; 
				bounce(normal);
			//Left
			} else if (pos.x > width-size) { 
				normal.x = 1; 
				bounce(normal);
			//Right
			} else if (pos.x < -width+size) { 
				normal.x = -1; 
				bounce(normal);
			} 
			
			return;
		}
	}

	public void bounce(Vector3f N){
		//bounce formula v' = b * ( -2*(v dot N)*N + v )
		//no scalar multiplication?
		float temp = -2*Vector3f.dot(vel, N);
		Vector3f bounce = new Vector3f(
				temp*N.x, temp*N.y, temp*N.z);
		vel.add(bounce, vel, vel);
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

        GL11.glColor3f(1.0f, 0.0f, 0.0f); //Make walls red

        //Front
        GL11.glVertex3f(width, height, length);
        GL11.glVertex3f(-width, height, length);
        GL11.glVertex3f(-width, -height, length);
        GL11.glVertex3f(width, -height, length);

        //Back
        GL11.glVertex3f(width, -height, -length);
        GL11.glVertex3f(-width, -height, -length);
        GL11.glVertex3f(-width, height, -length);
        GL11.glVertex3f(width, height, -length);

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

}