package org.asem.eclipse.utils;


import org.eclipse.draw2d.GridData;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

@SuppressWarnings("unused")
public class TextDialogCellEditor extends DialogCellEditor {
    Composite  control;
    
    @Override
    protected Control createContents(Composite cell) {
        GridLayout layout = new GridLayout();
        control = new Composite(cell, getStyle());
        control.setLayout(layout);

        Text text =  new Text(control, SWT.MULTI | SWT.WRAP);
        GridData data = new GridData();
        data.heightHint = 200;
        text.setLayoutData(data);

        Object value = getValue();
        if (value != null)
            text.setText((String)value);

        return control;
    }
    
    @Override
    protected Object openDialogBox(Control cellEditorWindow) {
        Shell shell = cellEditorWindow.getShell();
        PropertyDialog dialog = new PropertyDialog(shell);
        
        return getValue();
    }

}
