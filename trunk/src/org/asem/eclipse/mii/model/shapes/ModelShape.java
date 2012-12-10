package org.asem.eclipse.mii.model.shapes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.asem.eclipse.mii.db.Config;
import org.asem.eclipse.mii.model.abs.AbstractModelShape;
import org.asem.eclipse.mii.model.abs.IXMLElement;
import org.asem.eclipse.mii.model.abs.ShapeConstants;
import org.asem.eclipse.mii.model.shapes.wizards.model.ShapeModelWizard;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@SuppressWarnings("unused")
public class ModelShape extends AbstractModelShape {
    private static final long serialVersionUID = 1L;

    private Set<COLUMN> columns = new HashSet<COLUMN>();
    private String url;
    private transient Label label = new Label();
    public static transient final String MODEL_ICON_PATH = "icons/model16.gif";
    public static transient final Image MODEL_ICON = createImage("icons/model16.gif");

    public ModelShape() {
        Map<String, Object> adds = new HashMap<String, Object>();
        adds.put(ShapeConstants.NAME, getLocalizedString(ShapeConstants.NAME));
        adds.put(ShapeConstants.XA_QUERY, getLocalizedString(ShapeConstants.XA_QUERY));
        init(adds);
    }

    @Override
    public void setPropertyValue(Object propertyId, Object value) {
        if (ShapeConstants.NAME.equals(propertyId)) {
            label.setText((String) value);
        }
        
        if (ShapeConstants.XA_QUERY.equals(propertyId)) {
            String query = (String)value;
            value = query.replaceAll("%2F", "/");
        }

        super.setPropertyValue(propertyId, value);
    }

    @Override
    public IFigure createFigure() {
        RoundedRectangle figure = new RoundedRectangle() {
            public Rectangle getClientArea(Rectangle rect) {
                this.setBackgroundColor(ColorConstants.lightBlue);
                Rectangle clientArea = super.getClientArea(rect);
                clientArea.shrink(ShapeConstants.CLIENT_AREA_INSETS);
                return clientArea;
            }
        };

        label.setText((String) getPropertyValue(ShapeConstants.NAME));
        label.setIcon(getIcon());

        figure.setSize(150, 40);
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        figure.setLayoutManager(layout);
        
        label.setTextAlignment(PositionConstants.LEFT);
        figure.add(label);
        
        for (COLUMN col : columns) {
            Label lbl = new Label(col.name + ": " + col.type);
            figure.add(lbl);
        }
        
        return figure;
    }

    @Override
    public Image getIcon() {
        return MODEL_ICON;
    }

    @Override
    public Element store(Document doc) {
        /*
         * Create element for the diagram
         */
        Element element = super.store(doc);
        
        for (COLUMN col : getColumns()) {
            Element con = col.store(doc);
            element.appendChild(con);
        }

        return element;
    }

    @Override
    protected void addCustomObject(IXMLElement obj) {
        if (obj instanceof COLUMN) {
            columns.add((COLUMN)obj);
        }
    }

    @Override
    public boolean hasWizard() {
        return true;
    }

    @Override
    public void invokeWizard() {
        Shell shell = Config.getShell();
        ShapeModelWizard wizard = new ShapeModelWizard();
        WizardDialog dialog = new WizardDialog(shell, wizard);
        dialog.create();
        dialog.open();
        setUrl (wizard.getUrl());
        setColumns(wizard.getColumns());
    }

    private void setUrl(String url)
    {
        this.getProperties().put(ShapeConstants.XA_QUERY, url);
    }

    public Set<COLUMN> getColumns() {
        return columns;
    }

    public void setColumns(Set<COLUMN> columns) {
        this.columns.clear();
        if (columns != null)
            this.columns.addAll(columns);
    }

    @Override
    public boolean canConnectTo(Object target) {
        if (target instanceof ControllerShape 
            || target instanceof StoreShape)
            return true;

        return false;
    }

    @Override
    public boolean allowConnectFrom(Object source) {
        return false;
    }
}
