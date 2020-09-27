package noah.raytracer;

public class Vector {

    private float x,y,z;

    public Vector(float x, float y, float z) {

        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public static Vector getZero() {
        return new Vector(0,0,0);
    }

    public static float dotProduct(Vector a, Vector b) {
        return ((a.getX() * b.getX()) + (a.getY() * b.getY()) + (a.getZ() * b.getZ()));
    }

    public static float dotProduct(Vector a, float b) {
        return ((a.getX() * b) + (a.getY() * b) + (a.getZ() * b));
    }

    public static Vector product(Vector a, Vector b) {
        return new Vector(a.getX() * b.getX(), a.getY() * b.getY(), a.getZ() *  b.getZ());
    }

    public static Vector product(Vector a, float b) {
        return new Vector(a.getX() * b, a.getY() * b, a.getZ() *  b);
    }

    public static Vector subtract(Vector a, Vector b) {
        return new Vector(a.getX() - b.getX(), a.getY() - b.getY(), a.getZ() - b.getZ());
    }

    public static Vector add(Vector a, Vector b) {
        return new Vector(a.getX() + b.getX(), a.getY() + b.getY(), a.getZ() + b.getZ());
    }

    public static boolean equals(Vector a, Vector b) {
        return (a.getX() == b.getX()) && (a.getY() == b.getY()) && (a.getZ() == b.getZ());
    }

    public static float magnitude(Vector a) {
        return (float)Math.sqrt((a.getX() * a.getX()) + (a.getY() * a.getY()) + (a.getZ() * a.getZ()));
    }

    public static Vector normalise(Vector a) {
        float l = magnitude(a);
        return new Vector(a.getX()/l, a.getY()/l, a.getZ()/l);
    }

    public static Vector maxOne(Vector a) {
        return new Vector(Math.max(Math.min(a.getX(), 1.0F),0.0F), Math.max(Math.min(a.getY(), 1.0F),0.0F), Math.max(Math.min(a.getZ(), 1.0F),0.0F));
    }

    public static float sum(Vector a) {
        return (a.getX() + a.getY() + a.getZ());
    }
    
    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }

}
