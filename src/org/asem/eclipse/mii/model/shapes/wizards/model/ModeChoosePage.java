package org.asem.eclipse.mii.model.shapes.wizards.model;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class ModeChoosePage extends WizardPage {
    private Combo chooser;
    
    public int getMode () {
        return chooser.getSelectionIndex();
    }

    public static final String[] MODES = {
        "Load columns from MII transaction",
        "Enter column manually"
    }; 
    
    protected ModeChoosePage(String pageName) {
        super(pageName);
        setTitle(pageName);
    }

    @Override
    public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        composite.setLayout(layout);

        Label lbl = new Label(composite, SWT.NONE);
        lbl.setText("Choose mode:");

        chooser = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
        for (String mode : MODES) {
            chooser.add(mode);
        }
        chooser.select(0);

        setControl(composite);
    }

}
