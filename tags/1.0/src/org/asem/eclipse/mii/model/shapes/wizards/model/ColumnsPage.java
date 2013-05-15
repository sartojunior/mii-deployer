package org.asem.eclipse.mii.model.shapes.wizards.model;

import java.util.HashSet;
import java.util.Set;

import org.asem.eclipse.mii.model.shapes.COLUMN;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;


public class ColumnsPage extends WizardPage {
    private Text columnName;
    private Text columnType;
    private TableViewer viewer;
    private Set<COLUMN> columns = new HashSet<COLUMN>();
    private Listener listener;
    private Button set, delete;

    protected ColumnsPage(String pageName) {
        super(pageName);
        setTitle(pageName);
    }

    @Override
    public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        composite.setLayout(layout);

        viewer = new TableViewer(composite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
        viewer.setContentProvider(ArrayContentProvider.getInstance());

        TableViewerColumn col = new TableViewerColumn(viewer, SWT.NONE);
        col.getColumn().setWidth(200);
        col.getColumn().setText("Name");
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                COLUMN col = (COLUMN) element;
                return col.name;
            }
        });

        col = new TableViewerColumn(viewer, SWT.NONE);
        col.getColumn().setWidth(200);
        col.getColumn().setText("Type");
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                COLUMN col = (COLUMN) element;
                return col.type;
            }
        });

        viewer.setInput(columns);

        Table table = viewer.getTable();
        table.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
        table.addListener(SWT.Selection, getListener());

        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        addRightPanel(composite);

        setControl(composite);
    }

    private void addRightPanel(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_VERTICAL));

        Label label = new Label(composite, SWT.NONE);
        label.setText("Name:");

        GridData gd  = new GridData(GridData.FILL_HORIZONTAL);
        gd.widthHint = 150;

        columnName = new Text(composite, SWT.SINGLE | SWT.BORDER);
        columnName.setLayoutData(gd);

        label = new Label(composite, SWT.NONE);
        label.setText("Type:");

        columnType = new Text(composite, SWT.SINGLE | SWT.BORDER);
        columnType.setLayoutData(gd);

        set = new Button(composite, SWT.PUSH);
        set.setText("Add/Update");
        set.addListener(SWT.Selection, getListener());
        
        delete = new Button(composite, SWT.PUSH);
        delete.setText("Delete");
        delete.addListener(SWT.Selection, getListener());
    }

    private Listener getListener() {
        if (listener != null)
            return listener;
        
        listener = new Listener() {
            @SuppressWarnings("unchecked")
            public void handleEvent(Event event) {
                if (event.widget.equals(set)) {
                    String name = columnName.getText();
                    String type = columnType.getText();
                    Set<COLUMN> cols = (Set<COLUMN>) viewer.getInput();
                    COLUMN col = new COLUMN(name, type);
                    cols.remove(col);
                    cols.add(col);
                    viewer.setInput(cols);
                }
                else if (event.widget.equals(delete)) {
                    TableItem[] items = viewer.getTable().getSelection();
                    if (items.length > 0) {
                        Set<COLUMN> cols = (Set<COLUMN>) viewer.getInput();
                        COLUMN col = (COLUMN)items[0].getData();
                        cols.remove(col);
                        viewer.setInput(cols);
                    }
                }
                else if (event.widget instanceof Table) {
                    TableItem[] items = ((Table)event.widget).getSelection();
                    if (items.length > 0) {
                        COLUMN col = (COLUMN)items[0].getData();
                        columnName.setText(col.name);
                        columnType.setText(col.type);
                    }
                }
            }
        };
        
        return listener;
    }

    @SuppressWarnings("unchecked")
    public Set<COLUMN> getColumns() {
        return (Set<COLUMN>) viewer.getInput();
    }

    public void setColumns(Set<COLUMN> columns) {
        this.columns = columns;

        if (viewer != null) {
            viewer.setInput(columns);
        }
    }
}
