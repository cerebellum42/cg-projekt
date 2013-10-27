import static org.lwjgl.opengl.GL11.*;
import lenz.opengl.AbstractSimpleBase;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.*;

public class Example extends AbstractSimpleBase {
    DisplayMode dm;
    double displayRatio;
    double size = 10;

	public static void main(String[] args) {
		new Example().start();
	}

	@Override
	protected void initOpenGL() {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
        dm = Display.getDisplayMode();
        displayRatio = (double)dm.getWidth()/dm.getHeight();
        glOrtho(-size*displayRatio, size*displayRatio, -size, size, 0, 1);
		glMatrixMode(GL_MODELVIEW);

		glShadeModel(GL_FLAT);
	}

	@Override
	protected void render() {
        int mouseX = Mouse.getX();
        int mouseY = Mouse.getY();
        glClear(GL_COLOR_BUFFER_BIT);

		/*glBegin(GL_LINES);
		glColor3f(1, 0.5f, 0);
		glVertex2i(400, 300);
		glVertex2i(410, 300);
		glVertex2i(400, 310);
		glVertex2i(410, 310);
		glEnd();*/

        glBegin(GL_POLYGON);
        glColor3f(1, 0.5f, 0);

        drawCircle(.3f, 0, 0);
        drawCircle(.1f, 1, 5);
        drawCircle(.2f, 3, 3);

        glTranslatef(4f, 0, 0);
        glRotatef(2f, 0, 0, 1f);
        glTranslatef(-4f, 0, 0);
	}

    private void drawCircle(float radius, float x, float y) {
        glBegin(GL_POLYGON);
        glColor3f(1, 0.5f, 0);
        double angle = 0;
        int corners = 30;
        for (int i = 0; i < corners; i++) {
            angle = (Math.PI*2/corners) * i;
            glVertex2d(.5*Math.sin(angle) + x, .5*Math.cos(angle) + y);
        }
        glEnd();
    }
}
