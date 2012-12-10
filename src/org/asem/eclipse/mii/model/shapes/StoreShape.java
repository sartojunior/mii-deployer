package org.asem.eclipse.mii.model.shapes;

import java.util.HashMap;
import java.util.Map;

import org.asem.eclipse.mii.model.abs.AbstractModelShape;
import org.asem.eclipse.mii.model.abs.ShapeConstants;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;


public class StoreShape extends AbstractModelShape {
    private static final long serialVersionUID = 1L;
    public static transient final String MODEL_ICON_PATH = "icons/model16.gif";
    public static transient final Image MODEL_ICON = createImage("icons/model16.gif");
    private transient Label label = new Label();

    public StoreShape () {
        Map<String, Object> adds = new HashMap<String, Object>();
        adds.put(ShapeConstants.NAME, getLocalizedString(ShapeConstants.NAME));
        adds.put(ShapeConstants.XA_QUERY, getLocalizedString(ShapeConstants.XA_QUERY + ".store"));
        adds.put(ShapeConstants.READ_METHOD, getLocalizedString(ShapeConstants.READ_METHOD + ".store"));
        adds.put(ShapeConstants.READ_API, getLocalizedString(ShapeConstants.READ_API + ".store"));
        adds.put(ShapeConstants.READER, getLocalizedString(ShapeConstants.READER + ".store"));
        adds.put(ShapeConstants.RECORD, getLocalizedString(ShapeConstants.RECORD + ".store"));
        adds.put(ShapeConstants.AUTOLOAD, getLocalizedString(ShapeConstants.AUTOLOAD + ".store"));
        init(adds);
    }

    @Override
    public void setPropertyValue(Object propertyId, Object value) {
        if (ShapeConstants.NAME.equals(propertyId)) {
            label.setText((String) value);
        }

        super.setPropertyValue(propertyId, value);
    }

    @Override
    public IFigure createFigure() {
        RoundedRectangle figure = new RoundedRectangle() {
            public Rectangle getClientArea(Rectangle rect) {
                this.setBackgroundColor(ColorConstants.lightGray);
                Rectangle clientArea = super.getClientArea(rect);
                clientArea.shrink(ShapeConstants.CLIENT_AREA_INSETS);
                return clientArea;
            }
        };

        label = new Label();
        label.setText((String) getPropertyValue(ShapeConstants.NAME));
        label.setIcon(getIcon());
        
        figure.setSize(150, 40);
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        figure.setLayoutManager(layout);
        
        label.setTextAlignment(PositionConstants.LEFT);
        figure.add(label);
        return figure;
    }
    
    @Override
    public Image getIcon() {
        return MODEL_ICON;
    }

    @Override
    public boolean canConnectTo(Object target) {
        if (target instanceof ControllerShape
            || target instanceof ViewShape)
            return true;

        return false;
    }

    @Override
    public boolean allowConnectFrom(Object source) {
        if (getTargetConnections().size() > 0)
            return false;

        if (source instanceof ModelShape)
            return true;

        return false;
    }

}
