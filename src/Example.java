import static org.lwjgl.opengl.GL11.*;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL15.*;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import lenz.opengl.AbstractSimpleBase;
import lenz.opengl.utils.ShaderProgram;
import lenz.opengl.utils.Texture;
import org.lwjgl.util.vector.Vector4f;

public class Example extends AbstractSimpleBase {
    final float nearPlane = 1;
    final float farPlane = 100;
    final float fieldOfView = 60f;

    Matrix4f projection;
    Matrix4f view;

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
        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);

        initProjection();
        initView();
        loadAssets();
    }

    private void loadAssets() {
        cube = new Cube(3);
        cube.modelMatrix = new Matrix4f().translate(new Vector3f(10f, 2, 0));
        spPhong = new ShaderProgram("phong");

        teapot = new ObjModel("teapot.obj", spPhong);
        teapot.modelMatrix = new Matrix4f()
                .scale(new Vector3f(.3f, .3f, .3f))
                .translate(new Vector3f(-15, -3, 0))
                .rotate((float) Math.toRadians(-90), new Vector3f(1, 0, 0));
        teapot.writeBuffers();
    }

    private void initProjection() {
        DisplayMode dm = Display.getDisplayMode();
        float aspectRatio = (float)dm.getWidth() / dm.getHeight();
        float yScale = 1 / (float)Math.tan(Math.toRadians( (double)fieldOfView / 2 ));
        float xScale = yScale / aspectRatio;
        float frustumLength = farPlane - nearPlane;

        projection = new Matrix4f();
        projection.m00 = xScale;
        projection.m11 = yScale;
        projection.m22 = -((farPlane + nearPlane) / frustumLength);
        projection.m23 = -1;
        projection.m32 = -((2 * nearPlane * farPlane) / frustumLength);
        projection.m33 = 0;
    }

    private void initView() {
        view = new Matrix4f()
                .rotate((float)Math.toRadians(20), new Vector3f(1,0,0))
                .translate(new Vector3f(0, -15, -25));
    }

    private void updateTime() {
        if (time != 0) {
            timePassed = System.currentTimeMillis() - time;
        }
        else {
            timePassed = 0;
            time = System.currentTimeMillis();
        }

        time = time + timePassed;
        secondsPassed = timePassed/1000f;
    }

    @Override
    protected void render() {
        glClearColor(1, 1, 1, 1);
        glClear(GL_COLOR_BUFFER_BIT);
        glClear(GL_DEPTH_BUFFER_BIT);

        updateTime();

        // rotate camera
        view.rotate(.5f * secondsPassed, new Vector3f(0, 1, 0));

        // rotate cube
        cube.modelMatrix.rotate(.5f * secondsPassed, new Vector3f(1,1,0));

        FloatBuffer projectionBuf = BufferUtils.createFloatBuffer(16);
        projection.store(projectionBuf);
        projectionBuf.flip();

        FloatBuffer viewBuf = BufferUtils.createFloatBuffer(16);
        view.store(viewBuf);
        viewBuf.flip();

        cube.render(viewBuf, projectionBuf);
        teapot.render(view, projection);
    }
}