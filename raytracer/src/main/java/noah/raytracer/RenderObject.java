package noah.raytracer;

public abstract class RenderObject {

    private Vector center;
    private Vector ambientCo;
    private Vector diffuseCo;
    private Vector specularCo;
    private Vector reflectCo;

    public RenderObject(Vector center, Vector ambientCo, Vector diffuseCo, Vector specularCo, Vector reflectCo) {
        this.center = center;
        this.ambientCo = ambientCo;
        this.diffuseCo = diffuseCo;
        this.specularCo = specularCo;
        this.reflectCo = reflectCo;
    }

    public abstract float intersect(Ray ray);

    public abstract float intersect(Vector direction, Vector origin);

    public Vector getCenter() {
        return center;
    }

    public Vector getAmbientCo() {
        return ambientCo;
    }

    public Vector getDiffuseCo() {
        return diffuseCo;
    }

    public Vector getSpecularCo() {
        return specularCo;
    }

    public Vector getReflectCo() {
        return reflectCo;
    }

}
