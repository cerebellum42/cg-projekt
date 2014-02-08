import com.sun.deploy.util.BufferUtil;
import com.sun.prism.ps.Shader;
import lenz.opengl.utils.ShaderProgram;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import java.io.*;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glBindAttribLocation;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL30.*;

public class ObjModel {
    private int vaoId;
    private int vertexVboId;
    private int texVboId;
    private int normalVboId;

    private int spId;
    private ArrayList<Vector3f> vertices;
    private ArrayList<Vector2f> uvs;
    private ArrayList<Vector3f> normals;
    private ArrayList<int[][]> faces;

    public Matrix4f modelMatrix;

    public ObjModel(String resourceName, ShaderProgram sp) {
        load(resourceName);
        modelMatrix = new Matrix4f();
        spId = sp.getId();
    }

    private void load(String resourceName) {
        Scanner s = new Scanner(createInputStreamFromResourceName(resourceName));
        s.useDelimiter("\r\n");
        Scanner ln;
        String line;

        vertices = new ArrayList<>();
        uvs = new ArrayList<>();
        faces = new ArrayList<>();
        normals = new ArrayList<>();

        while (s.hasNext()) {
            line = s.next();
            if (line.startsWith("#") || line.isEmpty())
                continue;

            ln = new Scanner(line);
            ln.useDelimiter(" +");
            switch (ln.next()) {
                case "v":
                    vertices.add(new Vector3f(
                            Float.parseFloat(ln.next()),
                            Float.parseFloat(ln.next()),
                            Float.parseFloat(ln.next())
                    ));
                    break;
                case "vt":
                    uvs.add(new Vector2f(
                            Float.parseFloat(ln.next()),
                            Float.parseFloat(ln.next())
                    ));
                    break;
                case "vn":
                    normals.add(new Vector3f(
                            Float.parseFloat(ln.next()),
                            Float.parseFloat(ln.next()),
                            Float.parseFloat(ln.next())
                    ));
                    break;
                case "f":
                    int[][] face = new int[3][];
                    for (int i = 0; i < 3; i++) {
                        String[] currentIndices = ln.next().split("/");
                        face[i] = new int[] {
                                Integer.parseInt(currentIndices[0]),
                                Integer.parseInt(currentIndices[1]),
                                Integer.parseInt(currentIndices[2])
                        };
                    }
                    faces.add(face);
                    break;
                default:
                    break;
            }
        }
    }

    public void writeBuffers() {
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(faces.size() * 3 * 3);
        FloatBuffer uvBuffer = BufferUtils.createFloatBuffer(faces.size() * 3 * 2);
        FloatBuffer normalBuffer = BufferUtils.createFloatBuffer(faces.size() * 3 * 3);

        for (int[][] face : faces) {
            for (int i = 0; i < face.length; i++) {
                vertices.get(face[i][0] - 1).store(vertexBuffer);
                uvs.get(face[i][1] - 1).store(uvBuffer);
                normals.get(face[i][2] - 1).store(normalBuffer);
            }
        }

        vertexBuffer.flip();
        uvBuffer.flip();
        normalBuffer.flip();

        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        vertexVboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vertexVboId);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        texVboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, texVboId);
        glBufferData(GL_ARRAY_BUFFER, uvBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        normalVboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, normalVboId);
        glBufferData(GL_ARRAY_BUFFER, normalBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindVertexArray(0);
    }

    public void render(FloatBuffer view, FloatBuffer projection) {
        FloatBuffer modelBuf = BufferUtils.createFloatBuffer(16);
        modelMatrix.store(modelBuf);
        modelBuf.flip();

        glBindVertexArray(vaoId);

        glUseProgram(spId);
        glBindAttribLocation(spId, 0, "vertex");
        glBindAttribLocation(spId, 1, "vertexUv");
        glBindAttribLocation(spId, 1, "vertexNormal");
        glUniformMatrix4(glGetUniformLocation(spId, "m"), false, modelBuf);
        glUniformMatrix4(glGetUniformLocation(spId, "v"), false, view);
        glUniformMatrix4(glGetUniformLocation(spId, "p"), false, projection);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glDrawArrays(GL_TRIANGLES, 0, faces.size() * 3 * 4);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(0);

        glBindVertexArray(0);
    }

    private InputStream createInputStreamFromResourceName(String resourceName) {
        if (!resourceName.startsWith("/")) {
            resourceName = "/res/models/" + resourceName;
        }
        return this.getClass().getResourceAsStream(resourceName);
    }
}
