package qupath.lib.gui.viewer.tools.handlers;

import javafx.scene.input.MouseEvent;
import qupath.lib.regions.ImagePlane;
import qupath.lib.roi.ROIs;
import qupath.lib.roi.interfaces.ROI;

public class BowToolEventHandler extends AbstractPathDraggingROIToolEventHandler {
    @Override
    protected boolean requestPixelSnapping() {
        return false;
    }

    @Override
    protected ROI createNewROI(MouseEvent e, double x, double y, ImagePlane plane) {
        return ROIs.createBowROI(x, y, x, y, plane);
    }
}
