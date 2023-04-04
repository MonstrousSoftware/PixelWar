package com.monstrous.pixelwar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Disposable;

public class Terrain implements Disposable {

    public static final int MAP_SIZE = 128;     // grid size
    public static final float SCALE  = Settings.worldSize;       // terrain size
    public static final float AMPLITUDE  = 20f;

    private Model model;
    public ModelInstance modelInstance;
    private float heightMap[][];
    private float verts[];  // for collision detection, 3 floats per vertex
    private short indices[];    // 3 indices per triangle
    private int numIndices;
    private Vector3 normalVectors[][] = new Vector3[MAP_SIZE][MAP_SIZE];

    public Terrain() {

        Texture noiseTexture = new Texture(Gdx.files.internal("noiseTexture.png"));
        if (!noiseTexture.getTextureData().isPrepared()) {
            noiseTexture.getTextureData().prepare();
        }
        Pixmap pixmap = noiseTexture.getTextureData().consumePixmap();
        Color sample = new Color();

        heightMap = new float[MAP_SIZE][MAP_SIZE];
        for (int x = 0; x < MAP_SIZE; x++) {
            for (int y = 0; y < MAP_SIZE; y++) {
                int rgba = pixmap.getPixel(y,x);
                sample.set(rgba);
                heightMap[y][x] = AMPLITUDE*(sample.r -0.5f );
            }
        }
        noiseTexture.getTextureData().disposePixmap();

        Texture terrainTexture = new Texture(Gdx.files.internal("sand.png"), true);
        terrainTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        terrainTexture.setFilter(Texture.TextureFilter.MipMap, Texture.TextureFilter.Nearest);

        //Material material =  new Material(ColorAttribute.createDiffuse(Color.BROWN));
        Material material =  new Material(TextureAttribute.createDiffuse(terrainTexture));
        model = makeGridModel(heightMap, SCALE, MAP_SIZE-1, GL20.GL_TRIANGLES, material);
        modelInstance =  new ModelInstance(model);
    }

    @Override
    public void dispose() {
        model.dispose();
    }

    public void render(ModelBatch modelBatch, Environment environment ) {
        modelBatch.render(modelInstance, environment);
    }

    // make a Model consisting of a square grid
    public Model makeGridModel(float[][] heightMap, float scale, int divisions, int primitive, Material material) {
        final int N = divisions;
        numIndices = 0;
        int numFloats = 0;

        int attr = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates;

        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        MeshBuilder meshBuilder = (MeshBuilder) modelBuilder.part("face", primitive, attr, material);
        final int numVerts = (N + 1) * (N + 1);
        final int numTris = 2 * N * N;
        Vector3 vertices[] = new Vector3[numVerts];
        Vector3 normals[] = new Vector3[numVerts];

        verts = new float[3*numVerts];
        indices = new short[3*numTris];

        meshBuilder.ensureVertices(numVerts);
        meshBuilder.ensureTriangleIndices(numTris);

        Vector3 pos = new Vector3();
        float posz;

        for (int y = 0; y <= N; y++) {
            float posy = ((float) y / (float) N) - 0.5f;        // y in [-0.5f .. 0.5f]
            for (int x = 0; x <= N; x++) {
                float posx = ((float) x / (float) N - 0.5f);        // x in [-0.5f .. 0.5f]

                posz = heightMap[y][x];
                // have a slope down on the edges
                if(x == 0 || x == N-1 || y == 0 || y == N-1)
                    posz = -10f;
                pos.set(posx*scale, posz, posy*scale);			// swapping z,y to orient horizontally


                vertices[y * (N + 1) + x] = new Vector3(pos);
                normals[y * (N + 1) + x] = new Vector3(0,0,0);


            }
            if (y >= 1) {
                // add to index list to make a row of triangles using vertices at y and y-1
                short v0 = (short) ((y - 1) * (N + 1));    // vertex number at top left of this row
                for (short t = 0; t < N; t++) {
                    //addRect(meshBuilder, vertices, normals, v0, (short) (v0 + N + 1), (short) (v0 + N + 2), (short) (v0 + 1) );
                    addRect(meshBuilder, vertices, normals, (short) (v0 + N + 1), (short) (v0 + N + 2), (short) (v0 + 1), v0 );
                    v0++;                // next column
                }
            }
        }

        // now normalize each normal (which is the sum of the attached triangle normals)
        // and pass vertex to meshBuilder
        MeshPartBuilder.VertexInfo vert = new MeshPartBuilder.VertexInfo();
        vert.hasColor = false;
        vert.hasNormal = true;
        vert.hasPosition = true;
        vert.hasUV = true;

        Vector3 normal = new Vector3();
        for (int i = 0; i < numVerts; i++) {
            normal.set(normals[i]);     // sum of normals
            normal.nor();               // take average



            int x = i % (N+1);	// e.g. in [0 .. 3] if N == 3
            int y = i / (N+1);

            normalVectors[x][y] = new Vector3(normal);

            float reps=16;
            float u = (float)(x*reps)/(float)(N+1);
            float v = (float)(y*reps)/(float)(N+1);
            vert.position.set(vertices[i]);
            vert.normal.set(normal);
            vert.uv.x = u;					// texture needs to have repeat wrapping enables to handle u,v > 1
            vert.uv.y = v;
            meshBuilder.vertex(vert);

            verts[numFloats++] = vert.position.x;
            verts[numFloats++] = vert.position.y;
            verts[numFloats++] = vert.position.z;
        }

        Model model = modelBuilder.end();
        return model;
    }

    private void addRect(MeshBuilder meshBuilder, final Vector3[] vertices, Vector3[] normals, short v0, short v1, short v2, short v3) {
        meshBuilder.rect(v0, v1, v2, v3);
        calcNormal(vertices, normals, v0, v1, v2, v3);
        // 6 indices to make 2 triangles, follows order of meshBuilder.rect()
        //
        //     v3 --v2
        //      | /  |
        //     v0 --v1
        // triangle v0,v1,v2 and v2, v3, v0
        indices[numIndices++] = v0;
        indices[numIndices++] = v1;
        indices[numIndices++] = v2;
        indices[numIndices++] = v2;
        indices[numIndices++] = v3;
        indices[numIndices++] = v0;
    }

    /*
     * Calculate the normal
     */
    private Vector3 u = new Vector3();
    private Vector3 v = new Vector3();
    private Vector3 n = new Vector3();

    private void calcNormal(final Vector3[] vertices, Vector3[] normals, short v0, short v1, short v2, short v3) {

        Vector3 p0 = vertices[v0];
        Vector3 p1 = vertices[v1];
        Vector3 p2 = vertices[v2];

        v = new Vector3(p2).sub(p1);
        u = new Vector3(p0).sub(p1);
        n = new Vector3(v).crs(u).nor();

        normals[v0].add(n);
        normals[v1].add(n);
        normals[v2].add(n);
        normals[v3].add(n);
    }

    public boolean intersect(Ray ray, Vector3 intersection ) {
        boolean hit = Intersector.intersectRayTriangles(ray, verts, indices, 3, intersection);
        return hit;
    }

    private static Ray downRay = new Ray();
    private static Vector3 hitPoint = new Vector3();

    public float getHeight(float x, float y) {
        downRay.set(x, AMPLITUDE*2f, y, 0, -1, 0);
        boolean hit = Intersector.intersectRayTriangles(downRay, verts, indices, 3, hitPoint);
        if(!hit)
            return 0;
        return hitPoint.y;
    }

    public void getNormal(float x, float y, Vector3 outNormal) {

        // get normal vector from the closest grid point
        int mx = (int) (MAP_SIZE * 0.5f*(1f + x / Settings.worldSize));
        int mz = (int) (MAP_SIZE * 0.5f*(1f + y / Settings.worldSize));
        if(mx < 0 ||x >= MAP_SIZE || mz < 0 || mz >= MAP_SIZE)
            outNormal.set(0,1,0);
        else
            outNormal.set( normalVectors[mx][mz]);
    }

}
