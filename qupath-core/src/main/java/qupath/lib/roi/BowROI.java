package qupath.lib.roi;

import qupath.lib.geom.Point2;
import qupath.lib.regions.ImagePlane;
import qupath.lib.roi.interfaces.ROI;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class BowROI extends AbstractPathROI implements Serializable {
    private static final long serialVersionUID = 1L;

    private double headX = Double.NaN, headY = Double.NaN, tailX = Double.NaN, tailY = Double.NaN;

    BowROI() {
        super();
    }

    BowROI(double headX, double headY, double tailX, double tailY) {
        this(headX, headY, tailX, tailY, null);
    }

    BowROI(double headX, double headY, double tailX, double tailY, ImagePlane plane) {
        super(plane);
        this.headX = headX;
        this.headY = headY;
        this.tailX = tailX;
        this.tailY = tailY;
    }

    @Override
    public String getRoiName() {
        return "Bow";
    }

    @Override
    public double getLength() {
        return getScaledLength(1, 1);
    }

    @Override
    public double getScaledLength(double pixelWidth, double pixelHeight) {
        double dx = (headX - tailX) * pixelWidth;
        double dy = (headY - tailY) * pixelHeight;
        return Math.hypot(dx, dy);
    }

    @Override
    public ROI duplicate() {
        return new BowROI(headX, headY, tailX, tailY, getImagePlane());
    }

    @Override
    public int getNumPoints() {
        return 2;
    }

    public double getHeadX() {
        return headX;
    }

    public double getHeadY() {
        return headY;
    }

    public double getTailX() {
        return tailX;
    }

    public double getTailY() {
        return tailY;
    }

    @Override
    public boolean isEmpty() {
        return (headX == tailX) && (headY == tailY);
    }

    @Override
    public double getCentroidX() {
        return (headX / 2) + (tailX / 2);
    }

    @Override
    public double getCentroidY() {
        return (headY / 2) + (tailY / 2);
    }

    @Override
    public ROI translate(double dx, double dy) {
        if ((dx == 0) && (dy == 0)) {
            return this;
        }
        return new BowROI(
                headX + dx,
                headY + dy,
                tailX + dx,
                tailY + dy,
                getImagePlane()
        );
    }

    @Override
    public double getBoundsX() {
        return Math.min(headX, tailX);
    }

    @Override
    public double getBoundsY() {
        return Math.min(headY, tailY);
    }

    @Override
    public double getBoundsWidth() {
        return Math.abs(headX - tailX);
    }

    @Override
    public double getBoundsHeight() {
        return Math.abs(headY - tailY);
    }

    @Override
    public List<Point2> getAllPoints() {
        return Arrays.asList(
                new Point2(headX, headY),
                new Point2(tailX, tailY)
        );
    }

    public List<Point2> computePoints() {
        final double hspan = Math.toRadians(90) / 2;
        final int nSegments = 10;

        double dx = headX - tailX;
        double dy = headY - tailY;
        double angleRad = Math.atan2(dy, dx);
        double length = Math.hypot(dy, dx);

        double unit = hspan / nSegments;

        List<Point2> points = new ArrayList<>();

        // construct all points beforehand
        // make lower-half arc
        for (int p = 0; p < nSegments + 1; p++) {
            double dt = angleRad - hspan + unit * p;
            double x = Math.cos(dt) * length + tailX;
            double y = Math.sin(dt) * length + tailY;
            points.add(new Point2(x, y));
        }

        // add tail
        points.add(new Point2(tailX, tailY));

        // make upper-half arc
        for (int p = 0; p < nSegments + 1; p++) {
            double dt = angleRad + unit * p;
            double x = Math.cos(dt) * length + tailX;
            double y = Math.sin(dt) * length + tailY;
            points.add(new Point2(x, y));
        }

        return points;
    }

    @Override
    public Shape getShape() {
        List<Point2> points = computePoints();
        // this method does separate the weird moveto/lineto business from the math, but is it 'better'?
        Path2D path = new Path2D.Float();
        for (int i = 0; i < points.size(); i++) {
            Point2 point = points.get(i);
            if (i == 0) {
                path.moveTo(point.getX(), point.getY());
            } else {
                path.lineTo(point.getX(), point.getY());
            }
        }
        return path;
    }

    @Override
    protected Line2D createShape() {
        return new Line2D.Double(headX, headY, tailX, tailY);
    }

    @Override
    public ROI updatePlane(ImagePlane plane) {
        return new BowROI(headX, headY, tailX, tailY, plane);
    }

    @Override
    public RoiType getRoiType() {
        return RoiType.LINE;
    }

    @Override
    public ROI getConvexHull() {
        return this;
    }

    private Object writeReplace() {
        return new SerializationProxy(this);
    }

    private void readObject(ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException("Proxy required for reading");
    }

    private static class SerializationProxy implements Serializable {
        private static final long serialVersionUID = 1L;

        private final double headX, headY, tailX, tailY;
        private final String name;
        private final int c, z, t;

        SerializationProxy(final BowROI roi) {
            this.headX = roi.headX;
            this.headY = roi.headY;
            this.tailX = roi.tailX;
            this.tailY = roi.tailY;
            this.name = null; // There used to be names... now there aren't
//			this.name = roi.getName();
            this.c = roi.c;
            this.z = roi.z;
            this.t = roi.t;
        }

        private Object readResolve() {
            BowROI roi = new BowROI(
                    headX,
                    headY,
                    tailX,
                    tailY,
                    ImagePlane.getPlaneWithChannel(c, z, t)
            );
//			if (name != null)
//				roi.setName(name);
            return roi;
        }

    }

    @Override
    public double getArea() {
        return 0;
    }

    @Override
    public double getScaledArea(double pixelWidth, double pixelHeight) {
        return 0;
    }

    @Override
    public boolean contains(double x, double y) {
        return false;
    }

    @Override
    public boolean intersects(double x, double y, double width, double height) {
        if (!intersectsBounds(x, y, width, height))
            return false;
        return new Rectangle2D.Double(x, y, width, height).intersectsLine(getHeadX(), getHeadY(), getTailX(), getTailY());
    }

    @Override
    public ROI scale(double scaleX, double scaleY, double originX, double originY) {
        return new BowROI(
                RoiTools.scaleOrdinate(headX, scaleX, originX),
                RoiTools.scaleOrdinate(headY, scaleY, originY),
                RoiTools.scaleOrdinate(tailX, scaleX, originX),
                RoiTools.scaleOrdinate(tailY, scaleY, originY),
                getImagePlane()
        );
    }
}