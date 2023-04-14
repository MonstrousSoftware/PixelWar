package com.monstrous.pixelwar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.GeometryUtils;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Disposable;

public class Terrain implements Disposable {

    public static final int MAP_SIZE = 129;     // grid size
    public static final float SCALE  = Settings.worldSize;       // terrain size
    public static final float AMPLITUDE  = 20f;

    private Model model;
    public ModelInstance modelInstance;
    private float heightMap[][];
    private float vertPositions[];  // for collision detection, 3 floats per vertex
    private short indices[];    // 3 indices per triangle
    private int numIndices;
    private Vector3 normalVectors[][] = new Vector3[MAP_SIZE][MAP_SIZE];
    private Vector3 position; // position of terrain in world coordinates

    public Terrain() {

        Texture noiseTexture = new Texture(Gdx.files.internal("noiseTexture.png")); // assumed to be MAP_SIZE x MAP_SIZE
        if (!noiseTexture.getTextureData().isPrepared()) {
            noiseTexture.getTextureData().prepare();
        }
        Pixmap pixmap = noiseTexture.getTextureData().consumePixmap();
        Color sample = new Color();

        int tw = pixmap.getWidth();
        int th = pixmap.getHeight();
        heightMap = new float[MAP_SIZE][MAP_SIZE];
        for (int x = 0; x < MAP_SIZE; x++) {
            for (int y = 0; y < MAP_SIZE; y++) {
                int tx = (x * tw)/MAP_SIZE;
                int ty = (y * th)/MAP_SIZE;
                int rgba = pixmap.getPixel(ty,tx);
                sample.set(rgba);
                heightMap[y][x] = AMPLITUDE*(sample.r -0.5f );
            }
        }
        noiseTexture.getTextureData().disposePixmap();

        Texture terrainTexture = new Texture(Gdx.files.internal("sand.png"), true);
        terrainTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        terrainTexture.setFilter(Texture.TextureFilter.MipMap, Texture.TextureFilter.Nearest);

        position = new Vector3(-Settings.worldSize/2, 0, -Settings.worldSize/2);    // place the terrain so the centre is at the origin

        Material material =  new Material(TextureAttribute.createDiffuse(terrainTexture));
        model = makeGridModel(heightMap, SCALE, MAP_SIZE-1, GL20.GL_TRIANGLES, material);
        modelInstance =  new ModelInstance(model, position);
    }

    @Override
    public void dispose() {
        model.dispose();
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
        Vector3 positions[] = new Vector3[numVerts];
        Vector3 normals[] = new Vector3[numVerts];

        vertPositions = new float[3 * numVerts];      // todo redundant?
        indices = new short[3 * numTris];

        meshBuilder.ensureVertices(numVerts);
        meshBuilder.ensureTriangleIndices(numTris);

        Vector3 pos = new Vector3();
        float posz;

        for (int y = 0; y <= N; y++) {
            float posy = ((float) y / (float) N);        // y in [0.0 ..1.0]
            for (int x = 0; x <= N; x++) {
                float posx = ((float) x / (float) N);        // x in [0.0 .. 1.0]

                posz = heightMap[y][x];
                // have a slope down on the edges
                if(x == 0 || x == N || y == 0 || y == N)
                    posz = -10f;
                pos.set(posx * scale, posz, posy * scale);            // swapping z,y to orient horizontally

                positions[y * (N + 1) + x] = new Vector3(pos);
                normals[y * (N + 1) + x] = new Vector3(0, 0, 0);
            }
        }

        numIndices = 0;
        for (int y = 1; y <= N; y++) {
            short v0 = (short) ((y - 1) * (N + 1));    // vertex number at top left of this row
            for (int x = 0; x <= N-1; x++, v0++) {
                addRect(meshBuilder, positions, normals, (short) (v0 + N + 1), (short) (v0 + N + 2), (short) (v0 + 1), v0);
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
            normal.set(normals[i]);     // sum of normals to get smoothed normals
            normal.nor();               // take average



            int x = i % (N+1);	// e.g. in [0 .. 3] if N == 3
            int y = i / (N+1);

            normalVectors[y][x] = new Vector3(normal);

            float reps=16;
            float u = (x*reps)/(float)(N+1);
            float v = (y*reps)/(float)(N+1);
            vert.position.set(positions[i]);
            vert.normal.set(normal);
            vert.uv.x = u;					// texture needs to have repeat wrapping enables to handle u,v > 1
            vert.uv.y = v;
            meshBuilder.vertex(vert);

            vertPositions[numFloats++] = vert.position.x;
            vertPositions[numFloats++] = vert.position.y;
            vertPositions[numFloats++] = vert.position.z;
        }

        Model model = modelBuilder.end();
        return model;
    }

    private void addRect(MeshBuilder meshBuilder, final Vector3[] vertices, Vector3[] normals, short v0, short v1, short v2, short v3) {
        meshBuilder.rect(v0, v1, v2, v3);
        calcNormal(vertices, normals, v0, v1, v2);
        calcNormal(vertices, normals, v2, v3, v0);
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

    private void calcNormal(final Vector3[] vertices, Vector3[] normals, short v0, short v1, short v2) {

        Vector3 p0 = vertices[v0];
        Vector3 p1 = vertices[v1];
        Vector3 p2 = vertices[v2];

        v = new Vector3(p2).sub(p1);
        u = new Vector3(p0).sub(p1);
        n = new Vector3(v).crs(u).nor();

        normals[v0].add(n);
        normals[v1].add(n);
        normals[v2].add(n);
    }

    public boolean intersect(Ray ray, Vector3 intersection ) {
        ray.origin.sub(position);  // make ray relative to terrain space
        boolean hit = Intersector.intersectRayTriangles(ray, vertPositions, indices, 3, intersection);
        intersection.add(position); // convert local terrain coordinate to world coordinate
        return hit;
    }


    private Vector2 baryCoord = new Vector2();

    public float getHeight(float x, float z) {
        // position relative to terrain origin
        float relx = x - position.x;
        float relz = z - position.z;

        // position in grid (rounded down)
        int mx = (int)Math.floor(relx * (MAP_SIZE-1) / Settings.worldSize);
        int mz = (int)Math.floor(relz * (MAP_SIZE-1) / Settings.worldSize);

        if(mx < 0 ||mx >= (MAP_SIZE-1) || mz < 0 || mz >= (MAP_SIZE-1))
            return 0;
        float cellSize = Settings.worldSize / (MAP_SIZE-1);
        float xCoord = (relx % cellSize)/cellSize;
        float zCoord = (relz % cellSize)/cellSize;
        float ht;
        if( xCoord < 1f - zCoord) {   // top triangle
            baryCoord.set(xCoord, zCoord);
            ht = GeometryUtils.fromBarycoord(baryCoord, heightMap[mz][mx], heightMap[mz][mx+1], heightMap[mz+1][mx]);
        }
        else { // bottom triangle
            baryCoord.set(1f-xCoord, 1f-zCoord);
            ht =  GeometryUtils.fromBarycoord(baryCoord, heightMap[mz+1][mx+1], heightMap[mz+1][mx], heightMap[mz][mx+1]);
        }
        return ht;
    }

    public void getNormal(float x, float z, Vector3 outNormal) {
        // position relative to terrain origin
        float relx = x - position.x;
        float relz = z - position.z;

        // position in grid (rounded down)
        int mx = (int)Math.floor(relx * (MAP_SIZE-1) / Settings.worldSize);
        int mz = (int)Math.floor(relz * (MAP_SIZE-1) / Settings.worldSize);

        if(mx < 0 ||mx >= (MAP_SIZE-1) || mz < 0 || mz >= (MAP_SIZE-1)) {
            outNormal.set(0,1,0);
            return;
        }
        // note: we're using one normal per quad, not per triangle to reduce jitter of the tank
        outNormal.set(normalVectors[mz][mx]);           // use smoothed vertex normal
    }

}
