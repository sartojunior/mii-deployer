package org.asem.eclipse.mii.model.abs;

import org.eclipse.draw2d.IFigure;

public interface IShape {
    boolean hasWizard();
    void invokeWizard();
    IFigure createFigure ();
    
    boolean canConnectTo (Object target);
    boolean allowConnectFrom (Object source);
}
