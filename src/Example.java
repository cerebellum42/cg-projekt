import static org.lwjgl.opengl.GL11.*;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL15.*;

import java.awt.event.KeyEvent;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.Sys;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import lenz.opengl.AbstractSimpleBase;
import lenz.opengl.utils.ShaderProgram;
import lenz.opengl.utils.Texture;

public class Example extends AbstractSimpleBase {

    Matrix4f projection = new Matrix4f();
    Matrix4f mvp;
    ShaderProgram spGouraud;
    int vaoId, vboIdE, vboIdC, vboIdUv;
    long time;
    long timePassed;
    float[] ecken;
    float[] textureUv;
    Texture woodTexture;

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

        wuerfel();
        woodTexture = new Texture("plain_wood.jpg", 4);

        spGouraud = new ShaderProgram("gouraud");
        glBindAttribLocation(spGouraud.getId(), 0, "ecken");
        glBindAttribLocation(spGouraud.getId(), 1, "color");
        glBindAttribLocation(spGouraud.getId(), 2, "vertexUv");

        glUseProgram(spGouraud.getId());

        glEnable(GL_CULL_FACE);
        //glEnable(GL_DEPTH_TEST);
    }

    /*
     * Draws a cube
     */
    protected void wuerfel(){
        //GEGEN DEN URZEIGERSINN <- AUßENSEITE

        ecken = new float[]{
                //front
                4,4,4,
                -4,4,4,
                -4,-4,4,
                4,-4,4,

                //right
                4,4,-4,
                4,4,4,
                4,-4,4,
                4,-4,-4,

                //back
                -4,-4,-4,
                -4,4,-4,
                4,4,-4,
                4,-4,-4,

                //left
                -4,4,4,
                -4,4,-4,
                -4,-4,-4,
                -4,-4,4,

                //top
                -4,4,-4,
                -4,4,4,
                4,4,4,
                4,4,-4,

                //bottom
                -4,-4,4,
                -4,-4,-4,
                4,-4,-4,
                4,-4,4
        };

        //Create Model
        FloatBuffer edgeBuffer = BufferUtils.createFloatBuffer(ecken.length);
        edgeBuffer.put(ecken);
        edgeBuffer.flip();

        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);
        vboIdE = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboIdE);
        glBufferData(GL_ARRAY_BUFFER, edgeBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(0,3,GL_FLOAT,false,0,0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        textureUv = new float[] {
                //front
                1,1,
                0,1,
                0,0,
                1,0,

                //right
                1,0,
                1,1,
                0,1,
                0,0,

                //back
                0,0,
                0,1,
                1,1,
                1,0,

                //left
                1,1,
                1,0,
                0,0,
                0,1,

                //top
                0,0,
                0,1,
                1,1,
                1,0,

                //bottom
                0,1,
                0,0,
                1,0,
                1,1
        };

        //Create UV
        FloatBuffer uvBuffer = BufferUtils.createFloatBuffer(textureUv.length);
        uvBuffer.put(textureUv);
        uvBuffer.flip();

        vboIdUv = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboIdUv);
        glBufferData(GL_ARRAY_BUFFER, uvBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(2,2,GL_FLOAT,false,0,0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    @Override
    protected void render() {
        if (time) timePassed = System.currentTimeMillis() - time;
        time = time + timePassed;

        glClear(GL_COLOR_BUFFER_BIT);

        //translate projection matrix
        mvp.rotate(0.01f,new Vector3f(0,1,0));

        //Draw Object
        glBindVertexArray(vaoId);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(2);
        glDrawArrays(GL_QUADS,0,ecken.length/3);
        glDisableVertexAttribArray(2);
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);


        FloatBuffer fbM = BufferUtils.createFloatBuffer(16);
        mvp.store(fbM);
        fbM.flip();
        glUniformMatrix4(glGetUniformLocation(spGouraud.getId(), "frustMatrix"), false, fbM);
    }
}