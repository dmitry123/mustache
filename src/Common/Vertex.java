package Common;

public class Vertex {

    public float x = 0.0f;
    public float y = 0.0f;
    public float z = 0.0f;

    public Vertex() {
    }

    public Vertex(float x, float y) {

        this.x = x;
        this.y = y;
    }

    public Vertex(float x, float y, float z) {

        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vertex set(float x, float y) {

        this.x = x;
        this.y = y;

        return this;
    }

    public float getMagnitude() {

        return (float)Math.sqrt(
                (this.x * this.x) +
                        (this.y * this.y) +
                        (this.z * this.z));
    }

    public Vertex getNormalized() {

        float magnitude = this.getMagnitude();

        return new Vertex(
                this.x / magnitude,
                this.y / magnitude,
                this.z / magnitude);
    }

    public Vertex getCross(Vertex vertex) {

        return new Vertex (
                (this.y * vertex.z) - (this.z * vertex.y),
                (this.z * vertex.x) - (this.x * vertex.z),
                (this.x * vertex.y) - (this.y - vertex.x));
    }

    public float getDistance(Vertex vertex) {

        return (float)Math.sqrt(
                Math.pow(this.x - vertex.x, 2.0f) +
                        Math.pow(this.y - vertex.y, 2.0f) +
                        Math.pow(this.z - vertex.z, 2.0f));
    }

    public Vertex add(Vertex vertex) {

        this.x += vertex.x;
        this.y += vertex.y;
        this.z += vertex.z;

        return this;
    }

    public Vertex add(float value) {

        this.x += value;
        this.y += value;
        this.z += value;

        return this;
    }

    public Vertex sub(Vertex vertex) {

        this.x -= vertex.x;
        this.y -= vertex.y;
        this.z -= vertex.z;

        return this;
    }

    public Vertex sub(float value) {

        this.x -= value;
        this.y -= value;
        this.z -= value;

        return this;
    }

    public Vertex mul(Vertex vertex) {

        this.x *= vertex.x;
        this.y *= vertex.y;
        this.z *= vertex.z;

        return this;
    }

    public Vertex mul(float value) {

        this.x *= value;
        this.y *= value;
        this.z *= value;

        return this;
    }

    public Vertex div(Vertex vertex) {

        this.x /= vertex.x;
        this.y /= vertex.y;
        this.z /= vertex.z;

        return this;
    }

    public Vertex div(float value) {

        this.x /= value;
        this.y /= value;
        this.z /= value;

        return this;
    }
}