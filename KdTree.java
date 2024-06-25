import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.util.ArrayList;

public class KdTree {
    private KdNode root;
    private int size;

    public KdTree() {
        root = null;
        size = 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void insert(Point2D point) {
        if (point == null) throw new IllegalArgumentException();
        if (contains(point)) return;

        RectHV rect = root == null ? new RectHV(0, 0, 1, 1) : root.rect;
        root = insert(root, point, rect, 0);
    }

    private KdNode insert(KdNode node, Point2D point, RectHV rect, int depth) {
        if (node == null) {
            size += 1;
            return new KdNode(point, rect, depth);
        }

        int compareResult = compare(node, point);

        if (compareResult < 0) {
            RectHV lbRect;

            if (node.left == null) {
                if (depth % 2 == 0) {
                    lbRect = new RectHV(rect.xmin(), rect.ymin(), node.point.x(), rect.ymax());
                }
                else {
                    lbRect = new RectHV(rect.xmin(), rect.ymin(), rect.xmax(), node.point.y());
                }
            }
            else {
                lbRect = node.left.rect;
            }

            node.left = insert(node.left, point, lbRect, depth + 1);
        }

        if (compareResult > 0) {
            RectHV rtRect;

            if (node.right == null) {
                if (depth % 2 == 0) {
                    rtRect = new RectHV(node.point.x(), rect.ymin(), rect.xmax(), rect.ymax());
                }
                else {
                    rtRect = new RectHV(rect.xmin(), node.point.y(), rect.xmax(), rect.ymax());
                }
            }
            else {
                rtRect = node.right.rect;
            }

            node.right = insert(node.right, point, rtRect, depth + 1);
        }

        return node;
    }

    private int compare(KdNode node, Point2D point) {
        if (node.depth % 2 == 0) {
            return point.x() == node.point.x() ? Double.compare(point.y(), node.point.y()) :
                   Double.compare(point.x(), node.point.x());
        }
        else {
            return point.y() == node.point.y() ? Double.compare(point.x(), node.point.x()) :
                   Double.compare(point.y(), node.point.y());
        }
    }

    public boolean contains(Point2D point) {
        if (point == null) throw new IllegalArgumentException();
        return contains(root, point);
    }

    private boolean contains(KdNode node, Point2D point) {
        if (node == null) return false;

        int compareResult = compare(node, point);
        if (compareResult < 0) return contains(node.left, point);
        if (compareResult > 0) return contains(node.right, point);

        return true;
    }

    public void draw() {
        draw(root);
    }

    private void draw(KdNode node) {
        if (node == null) return;

        if (node.depth % 2 == 0) {
            StdDraw.setPenRadius();
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(node.point.x(), node.rect.ymin(), node.point.x(), node.rect.ymax());
        }
        else {
            StdDraw.setPenRadius();
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(node.rect.xmin(), node.point.y(), node.rect.xmax(), node.point.y());
        }

        StdDraw.setPenRadius(0.01);
        StdDraw.setPenColor(StdDraw.BLACK);
        node.point.draw();

        draw(node.left);
        draw(node.right);
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException();

        ArrayList<Point2D> points = new ArrayList<>();
        range(root, rect, points);

        return points;
    }

    private void range(KdNode node, RectHV rect, ArrayList<Point2D> points) {
        if (node == null) return;
        if (!node.rect.intersects(rect)) return;

        if (rect.contains(node.point)) {
            points.add(node.point);
        }

        range(node.left, rect, points);
        range(node.right, rect, points);
    }

    public Point2D nearest(Point2D point) {
        if (point == null) throw new IllegalArgumentException();
        if (root == null) return null;

        return nearest(root, point, root.point);
    }

    private Point2D nearest(KdNode node, Point2D query, Point2D target) {
        if (node == null) return target;

        double min = target.distanceSquaredTo(query);
        double rectDistance = node.rect.distanceSquaredTo(query);
        if (rectDistance >= min) return target;

        double distance = node.point.distanceSquaredTo(query);
        if (distance < min) target = node.point;

        int compareResult = compare(node, query);

        if (compareResult < 0) {
            target = nearest(node.left, query, target);
            target = nearest(node.right, query, target);
        }

        if (compareResult > 0) {
            target = nearest(node.right, query, target);
            target = nearest(node.left, query, target);
        }

        return target;
    }

    private static class KdNode {
        private final Point2D point;
        private final RectHV rect;
        private final int depth;
        private KdNode left;
        private KdNode right;

        public KdNode(Point2D point, RectHV rect, int depth) {
            this.point = point;
            this.rect = rect;
            this.depth = depth;
        }
    }
}
