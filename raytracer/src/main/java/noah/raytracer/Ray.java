package noah.raytracer;

public class Ray {

    private Vector origin;
    private Vector direction;
    private RenderObject[] objects;
    private int depth;

    public static Vector BACKGROUND_COLOUR = new Vector(0.49F, 0.77F, 0.72F);

    public Ray(Vector origin, Vector direction,RenderObject[] objects) {
        this(origin, direction, objects, 1);
    }

    public Ray(Vector origin, Vector direction,RenderObject[] objects, int depth) {
        this.origin = origin;
        this.direction = direction;
        this.objects = objects;
        this.depth = depth;
    }

    public Vector calculate(Vector ambientReflection, float specularInt, RenderObject exclude) {

        float smallestT = 1;
        RenderObject bestObj = null;
        // Iterate over the render objects
        for (RenderObject s : objects) {
            // Itersect with the objects
            float t = s.intersect(this);
            // Find the t < 0 but closest to 0 and isnt the object reflecting the ray
            if (((smallestT > 0 && t < 0) || (smallestT < t && t < 0)) && s != exclude) {
                smallestT = t;
                bestObj = s;
            }
        }

        // If a hit was found
        if (smallestT < 0) {

            // The point of intersection
            Vector newOrigin = getIntersect(smallestT);

            // Get the surface normal of the hit
            Vector surfaceNormal = getSurfaceNormal(newOrigin, smallestT, bestObj.getCenter());

            // ---- Calculate ambient reflection ----
            Vector ambient = Vector.product(ambientReflection, bestObj.getAmbientCo());

            // Initilise diffuse/specular reflection values
            Vector diffusionSum = Vector.getZero();
            Vector specular = Vector.getZero();

            // For each point light 
            for (RenderObject obj : objects) {
                if ((obj instanceof PointLight) && (obj != bestObj)) {

                    // Cast object as a point light
                    PointLight light = (PointLight) obj;
                    // Get the direction to the point light
                    Vector lightDirection = light.getLightDirection(bestObj.getCenter());

                    // ---- Diffuse reflection ----
                    float dotProd = Vector.dotProduct(surfaceNormal, lightDirection);
                    if (dotProd < 0) dotProd = 0;

                    // ---- Specular reflection ----
                    lightDirection = Vector.normalise(Vector.subtract(Vector.getZero(), light.getLightDirection(bestObj.getCenter())));
                    Vector r = Vector.add(Vector.product(surfaceNormal, Vector.dotProduct(surfaceNormal, lightDirection) * 2), lightDirection);
                    Vector v = Vector.normalise(Vector.subtract(Vector.getZero(), direction));

                    // ---- Shadow calculation ----
                    // Shoot out a ray from the hitted object towards the point light
                    Vector newDirection = Vector.subtract(newOrigin, light.getCenter());
                    // Get the t value of the intersection
                    float tValue = light.intersect(newDirection, newOrigin);

                    boolean isOccluded = false;
                    // For each object in the scene, calculate for intersection
                    for (RenderObject occObj : objects) {
                        // If the target object is not currently hit object and point light
                        if (occObj != bestObj && occObj != light) {
                            // Calculate the t value of the intersection
                            float occTValue = occObj.intersect(newDirection, newOrigin);
                            // If the hit is closer than the hit to the point light then there is an occlusion!
                            if (occTValue >= tValue && occTValue < 0) {
                                isOccluded = true;
                            }
                        }
                    }

                    // If there is no occlusion then procede to calculate the diffusion and specular values
                    if (!isOccluded) {
                        diffusionSum = Vector.add(diffusionSum, Vector.product(light.getLightEmission(), dotProd));
                        specular = Vector.product(bestObj.getSpecularCo(), (float) Math.pow(Vector.dotProduct(v, r), specularInt));
                    }
                }
            }

            // ---- Surface reflection ----
            Vector surfaceReflection = Vector.getZero();
            // If the ray still has a depth > 0 and the current object has a reflection coef > 0 then reflect!
            if ((depth > 0) && !Vector.equals(bestObj.getReflectCo(), Vector.getZero())) {

                // Caclulate the new direction
                Vector cameraDir = Vector.normalise(Vector.subtract(Vector.getZero(), direction));
                Vector newDir = Vector.product(surfaceNormal, Vector.dotProduct(surfaceNormal, cameraDir) * 2);

                // Fire off the recursive ray
                Ray secondaryRay = new Ray(newOrigin, newDir, objects, depth - 1);

                // Calculate the recursive rays value
                surfaceReflection = secondaryRay.calculate(ambientReflection, specularInt,bestObj);

                // Scale the reflection according to the coef
                surfaceReflection = Vector.product(surfaceReflection, bestObj.getReflectCo());

            }
            
            // Add the values together and bound to [0,1]
            return Vector.maxOne(Vector.add(
                    Vector.add(Vector.add(ambient, diffusionSum), specular),
                    surfaceReflection));
        } else {
            // If no hit was found then return background colour
            return BACKGROUND_COLOUR;
        }

    }

    public Vector getOrigin() {
        return this.origin;
    }

    public Vector getDirection() {
        return this.direction;
    }

    private Vector getIntersect(float t) {
        return Vector.add(origin, Vector.product(direction, t));
    }

    private Vector getSurfaceNormal(float t, Vector center) {
        Vector p = getIntersect(t);
        return this.getSurfaceNormal(p, t, center);
    }

    private Vector getSurfaceNormal(Vector p, float t, Vector center) {
        Vector normal = Vector.subtract(p, center);
        return Vector.normalise(normal);
    }

}
