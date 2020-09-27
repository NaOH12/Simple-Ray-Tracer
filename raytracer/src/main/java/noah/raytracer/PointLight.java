package noah.raytracer;

public class PointLight extends  RenderObject {

    private float radius;
    private Vector lightEmission;

    public PointLight(float radius, Vector center) {
        this(radius, center, new Vector(1,1,1), new Vector(0,0,0),
                new Vector(1,1,1), new Vector(0,0,0), new Vector(0,0,0));
    }

    public PointLight(float radius, Vector center, Vector ambientCo, Vector diffuseCo, Vector lightEmission, Vector specularCo,Vector reflectCo) {
        super(center,ambientCo,diffuseCo, specularCo, reflectCo);
        this.radius = radius;
        this.lightEmission = lightEmission;
    }

    public float getRadius() {
        return radius;
    }

    public Vector getLightDirection(Vector object) {
        return Vector.normalise(Vector.subtract(getCenter(),object));
    }

    public Vector getLightEmission() {
        return lightEmission;
    }

    @Override
    public float intersect(Ray ray) {
        
        return this.intersect(ray.getDirection(), ray.getOrigin());
        
    }

    @Override
    public float intersect(Vector direction, Vector origin) {
        
        float a = Vector.dotProduct(direction, direction);
        float b = 2 * Vector.dotProduct(direction, Vector.subtract(origin, this.getCenter()));
        Vector cc = Vector.subtract(origin, this.getCenter());
        float c = Vector.dotProduct(cc,cc) - (this.getRadius() * this.getRadius());

        float t = (-b + (float)Math.sqrt( (b*b) - (4*a*c)))/(2*a);
        return t;

    }

}
