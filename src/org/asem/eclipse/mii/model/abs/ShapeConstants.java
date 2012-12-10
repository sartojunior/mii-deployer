package org.asem.eclipse.mii.model.abs;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Insets;

/**
 * Interface defines common constants for shapes 
 * @author ASementsov
 */
public interface ShapeConstants {
    /** 
     * Property ID to use when the list of outgoing connections is modified. 
     */
    String SOURCE_CONNECTIONS_PROP = "sourceConn";
    /** 
     * Property ID to use when the list of incoming connections is modified.
     */
    String TARGET_CONNECTIONS_PROP = "targetConn";
    /**
    * ID for the Height property value (used for by the corresponding property
    * descriptor).
    */
    String HEIGHT_PROP = "height";
    /** 
    * Property ID to use when the location of this shape is modified. 
    */
    String LOCATION_PROP = "location";
    /** 
     * Property ID to use then the size of this shape is modified. 
     */
    String SIZE_PROP = "size";
    /**
     * ID for the Width property value (used for by the corresponding property descriptor).
     */
    String WIDTH_PROP = "width";
    /**
     * ID for the X property value (used for by the corresponding property descriptor).
     */
    String XPOS_PROP = "xPos";
    /**
     * ID for the Y property value (used for by the corresponding property descriptor).
     */
    String YPOS_PROP = "yPos";
    /** 
     * Property ID to use when a child is added to this diagram. 
     */
    String CHILD_ADDED_PROP = "ShapesDiagram.ChildAdded";
    /** 
     * Property ID to use when a child is removed from this diagram. 
     */
    String CHILD_REMOVED_PROP = "ShapesDiagram.ChildRemoved";
    /**
     * Property ID for name of component
     */
    String NAME = "name";
    /**
     * Client area default insets
     */
    Insets CLIENT_AREA_INSETS = new Insets(10, 10, 21, 21);
    /**
     * Used for indicating that a Connection with solid line style should be created.
     * 
     * @see org.eclipse.gef.examples.shapes.parts.ShapeEditPart#createEditPolicies()
     */
    public static final Integer SOLID_CONNECTION = new Integer(Graphics.LINE_SOLID);
    /**
     * Used for indicating that a Connection with dashed line style should be created.
     * 
     * @see org.eclipse.gef.examples.shapes.parts.ShapeEditPart#createEditPolicies()
     */
    Integer DASHED_CONNECTION = new Integer(Graphics.LINE_DASH);
    /**
     *  Property ID to use when the line style of this connection is modified. 
     */
    String LINESTYLE_PROP = "lineStyle";
    String SOLID_STR = "solid";
    String DASHED_STR = "dashed";

    String DIAGRAM_ELEMENT = "MiiDiagram";
    String CONNECTION_ELEMENT = "Connection";
    String SHAPE_ELEMENT = "Shape";
    
    String ELEMENT_CLASS = "class";
    
    String RELATION = "rel";
    String SOURCE = "source";
    String TARGET = "target";
    String SOURCE_CLASS = "source-class";
    String TARGET_CLASS = "target-class";
    String ENCODING = "utf-8";
    String COLUMN_ELEMENT = "Column";
    String TYPE = "type";
    Object PATH = "path";
    String APPNAME = "appName";
    String XA_QUERY = "QueryTemplate";
    String READ_METHOD = "actionMethods.read";
    String READ_API = "api.read";
    String READER = "reader.type";
    String RECORD = "reader.record";
    String SCRIPT_PATH = "/WEB";
    String AUTOLOAD = "autoLoad";
    String ALIAS = "alias";
    String GRID = "grid";
    String BROWSER_ID = "MiiBrowser";
}
