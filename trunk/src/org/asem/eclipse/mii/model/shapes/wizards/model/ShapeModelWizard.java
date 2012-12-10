package org.asem.eclipse.mii.model.shapes.wizards.model;

import java.util.Set;

import org.asem.eclipse.mii.model.shapes.COLUMN;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;


public class ShapeModelWizard extends Wizard {
    private static final String illuminator = "QueryTemplate=";
    
    private ModeChoosePage      selectMode;
    private MiiPage             miiPage;
    private ColumnsPage         columnPage;
    private Set<COLUMN>         columns;
    
    @Override
    public void addPages() {
        selectMode = new ModeChoosePage("Choose wizard mode");
        addPage(selectMode);

        miiPage = new MiiPage("Enter MII transaction");
        addPage(miiPage);

        columnPage = new ColumnsPage("Edit columns");
        addPage(columnPage);
    }
    
    @Override
    public IWizardPage getNextPage(IWizardPage page) {
        if (page instanceof ModeChoosePage) {
            int mode = ((ModeChoosePage)page).getMode();
            switch (mode) {
                case 0:
                    return miiPage;

                case 1:
                    return columnPage;
            }
        }
        else if (page instanceof MiiPage) {
            if (!loadMiiTransaction())
                return page;
        }

        return super.getNextPage(page);
    }
    
    @Override
    public boolean performFinish() {
        columns = columnPage.getColumns();
        return (columns != null && columns.size() > 0);
    }

    private boolean loadMiiTransaction () {
        Set<COLUMN> columns = miiPage.loadTransaction();
        if (columns.size() > 0) {
            columnPage.setColumns(columns);
            return true;
        }
        
        return false;
    }
    
    public String getUrl() {
        String text = miiPage.httpUrl;
        int start = text.indexOf(illuminator);
        if (start == -1)
            return "";

        start += illuminator.length();
        int end = text.indexOf('&', start);
        if (end == -1)
            end = text.length() - 1;

        return text.substring(start, end);
    }
    
    public Set<COLUMN> getColumns() {
        return columns;
    }
}
