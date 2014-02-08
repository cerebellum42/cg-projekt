import static org.lwjgl.opengl.GL11.*;

import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL15.*;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import lenz.opengl.AbstractSimpleBase;
import lenz.opengl.utils.ShaderProgram;
import lenz.opengl.utils.Texture;
import org.lwjgl.util.vector.Vector4f;

public class Example extends AbstractSimpleBase {

    Matrix4f projection = new Matrix4f();
    Matrix4f mvp;
    long time = 0;
    long timePassed;
    float secondsPassed;
    Cube cube;
    ObjModel teapot;
    ShaderProgram spPhong;

    public static void main(String[] args) {
        new Example().start();
    }

    @Override
    protected void initOpenGL() {
        float nearPlane = 1;
        float farPlane = 100;
        float right = 1;
        float left = -1;
        float bottom = -1;
        float top = 1;

        float A = (right + left) / (right-left);
        float B = (top+bottom) / (top-bottom);
        float C = -((farPlane+nearPlane) / (farPlane - nearPlane));
        float D = -((2*farPlane*nearPlane)/(farPlane - nearPlane));

        projection.m00 = 2*nearPlane/(right-left);
        projection.m20 = A;

        projection.m11 = 2*nearPlane/(top-bottom);
        projection.m21 = B;

        projection.m22 = C;
        projection.m32 = D;

        projection.m23 = -1;

        mvp = new Matrix4f(projection);
        mvp.translate(new Vector3f(0,-5,-20f));

        cube = new Cube(3);
        spPhong = new ShaderProgram("phong");

        teapot = new ObjModel("teapot.obj", spPhong);
        teapot.transform(new Matrix4f()
                .translate(new Vector3f(4, -4, 0))
                .rotate((float) Math.toRadians(-90), new Vector3f(1, 0, 0))
                .scale(new Vector3f(.4f, .4f, .4f))
        );
        teapot.writeBuffers();


        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
    }

    @Override
    protected void render() {
        glClearColor(1, 1, 1, 1);
        glClear(GL_COLOR_BUFFER_BIT);
        glClear(GL_DEPTH_BUFFER_BIT);

        if (time != 0) {
            timePassed = System.currentTimeMillis() - time;
        }
        else {
            timePassed = 0;
            time = System.currentTimeMillis();
        }

        time = time + timePassed;
        secondsPassed = timePassed/1000f;


        //translate projection matrix
        mvp.rotate(.5f * secondsPassed,new Vector3f(0,1,0));

        FloatBuffer mvpBuf = BufferUtils.createFloatBuffer(16);
        mvp.store(mvpBuf);
        mvpBuf.flip();

        //cube.render(mvpBuf);
        teapot.render(mvpBuf);
    }
}