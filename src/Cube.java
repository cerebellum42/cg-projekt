import lenz.opengl.utils.ShaderProgram;
import lenz.opengl.utils.Texture;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL30.*;

public class Cube {
    public int vaoId;
    public int vertVboId;
    public int textVboId;
    public final float size;
    private Texture woodTexture;
    private ShaderProgram spWoodBox;
    private static final float degreeToRadian = (float)Math.PI / 180f;

    public Matrix4f modelMatrix;

    public Cube(float size) {
        this.size = size;
        init();
    }
    
    private void init() {
        modelMatrix = new Matrix4f();
        createArrays();
        woodTexture = new Texture("plain_wood.jpg", 4);
        spWoodBox = new ShaderProgram("woodenbox");
        glUseProgram(spWoodBox.getId());
        glBindAttribLocation(spWoodBox.getId(), 0, "ecken");
        glBindAttribLocation(spWoodBox.getId(), 1, "vertexUv");
        glUseProgram(0);
    }
    
    private void createArrays() {
        //GEGEN DEN URZEIGERSINN <- AUßENSEITE

        float[] ecken = new float[]{
                //front
                size,size,size,
                -size,size,size,
                -size,-size,size,
                size,-size,size,

                //right
                size,size,-size,
                size,size,size,
                size,-size,size,
                size,-size,-size,

                //back
                -size,-size,-size,
                -size,size,-size,
                size,size,-size,
                size,-size,-size,

                //left
                -size,size,size,
                -size,size,-size,
                -size,-size,-size,
                -size,-size,size,

                //top
                -size,size,-size,
                -size,size,size,
                size,size,size,
                size,size,-size,

                //bottom
                -size,-size,size,
                -size,-size,-size,
                size,-size,-size,
                size,-size,size
        };

        FloatBuffer edgeBuffer = BufferUtils.createFloatBuffer(ecken.length);
        edgeBuffer.put(ecken);
        edgeBuffer.flip();


        float[] textureUv = new float[] {
                //front
                1,0,
                1,1,
                0,1,
                0,0,

                //right
                1,0,
                1,1,
                0,1,
                0,0,

                //back
                0,1,
                1,1,
                1,0,
                0,0,

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

        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        vertVboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vertVboId);
        glBufferData(GL_ARRAY_BUFFER, edgeBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        textVboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, textVboId);
        glBufferData(GL_ARRAY_BUFFER, uvBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindVertexArray(0);
    }
    
    public void render(FloatBuffer view, FloatBuffer projection) {
        FloatBuffer modelBuf = BufferUtils.createFloatBuffer(16);
        modelMatrix.store(modelBuf);
        modelBuf.flip();

        int spId = spWoodBox.getId();
        glBindVertexArray(vaoId);

        glUseProgram(spWoodBox.getId());
        glUniform1i(glGetUniformLocation(spId, "time"), (int) (System.currentTimeMillis() % 3600));
        glUniform1f(glGetUniformLocation(spId, "degreeToRadian"), degreeToRadian);
        glUniformMatrix4(glGetUniformLocation(spId, "m"), false, modelBuf);
        glUniformMatrix4(glGetUniformLocation(spId, "v"), false, view);
        glUniformMatrix4(glGetUniformLocation(spId, "p"), false, projection);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glDrawArrays(GL_QUADS, 0, 24);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(0);

        glBindVertexArray(0);
    }
}
