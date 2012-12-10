package org.asem.eclipse.mii.properties;

import org.asem.eclipse.mii.db.Config;
import org.asem.eclipse.mii.db.DBConsts;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;


public class MiiPropertyPage extends PropertyPage {
    private static int LABEL_WIDTH = 100;

    private String  jdbcDriver;
    private String  dbName;
    private String  user;
    private String  password;
    private String  testQuery;
    private String  webURL;
    private String  nwUser;
    private String  nwPass;
    private String  miiProject;
    
    private Combo   jdbcDriverCombo;
    private Text    dbUrlText;
    private Text    userText;
    private Text    passwordText;
    private Text    testQueryText;
    private Text    webURLText;
    private Text    nwUserText;
    private Text    nwPassText;
    private Text    miiProjectText;

    /**
     * Constructor for SamplePropertyPage.
     */
    public MiiPropertyPage() {
        super();
    }

    private void addSeparator(Composite parent) {
        Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        separator.setLayoutData(gridData);
    }

    private void addJdbcDriverCombo (Composite parent) {
        Composite composite = createDefaultComposite(parent);

        Label pathLabel = new Label(composite, SWT.NONE);
        pathLabel.setText("Jdbc Driver:");
        GridData gd = new GridData();
        gd.widthHint = LABEL_WIDTH;
        pathLabel.setLayoutData(gd);

        jdbcDriverCombo = new Combo(composite, SWT.DROP_DOWN);
        jdbcDriverCombo.add("oracle.jdbc.OracleDriver");
        jdbcDriverCombo.add("com.sap.dbtech.jdbc.DriverSapDB");
        jdbcDriverCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        if (jdbcDriver != null && !jdbcDriver.isEmpty())
            jdbcDriverCombo.setText(jdbcDriver);
    }
    
    private Text addText(Composite parent, String name, String value) {
        Composite composite = createDefaultComposite(parent);

        Label pathLabel = new Label(composite, SWT.NONE);
        pathLabel.setText(name);
        GridData gd = new GridData();
        gd.widthHint = LABEL_WIDTH;
        pathLabel.setLayoutData(gd);

        Text element = new Text(composite, SWT.SINGLE | SWT.BORDER);
        element.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        if (value != null && !value.isEmpty())
            element.setText(value);
        
        return element;
    }
    
    private Text addPassword(Composite parent, String name, String value) {
        Composite composite = createDefaultComposite(parent);

        Label pathLabel = new Label(composite, SWT.NONE);
        pathLabel.setText(name);
        GridData gd = new GridData();
        gd.widthHint = LABEL_WIDTH;
        pathLabel.setLayoutData(gd);

        Text element = new Text(composite, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD);
        element.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        if (value != null && !value.isEmpty())
            element.setText(value);
        
        return element;
    }

    /**
     * @see PreferencePage#createContents(Composite)
     */
    protected Control createContents(Composite parent) {
        IResource prj = (IResource) getElement();
        jdbcDriver = Config.getValue(prj, DBConsts.JDBC_DRIVER, "");
        dbName = Config.getValue(prj, DBConsts.JDBC_URL, "");
        user = Config.getValue(prj, DBConsts.JDBC_USER, "");
        password = Config.getValue(prj, DBConsts.JDBC_PASSWORD, "");
        testQuery = Config.getValue(prj, DBConsts.JDBC_TEST_QUERY, "select * from dual");
        webURL = Config.getValue(prj, DBConsts.NW_WEB_URL, "");
        nwUser = Config.getValue(prj, DBConsts.NW_WEB_USER, "");
        nwPass = Config.getValue(prj, DBConsts.NW_WEB_PASSWORD, "");
        miiProject = Config.getValue(prj, DBConsts.MII_PROJECT, "");

        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        composite.setLayout(layout);
        GridData data = new GridData(GridData.FILL);
        data.grabExcessHorizontalSpace = true;
        composite.setLayoutData(data);

        addJdbcDriverCombo (composite);
        
        dbUrlText = addText(composite, "URL:", dbName);
        userText = addText(composite, "User:", user);
        passwordText = addPassword(composite, "Password:", password);
        testQueryText = addText(composite, "Test Query:", testQuery);
        webURLText = addText(composite, "WEB Url:", webURL);
        nwUserText = addText(composite, "NW user:", nwUser);
        nwPassText = addPassword(composite, "NW password:", nwPass);
        
        addSeparator(composite);
        
        miiProjectText = addText(composite, "Mii Project:", miiProject);

        return composite;
    }

    private Composite createDefaultComposite(Composite parent) {
        Composite composite = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        composite.setLayout(layout);

        GridData data = new GridData();
        data.verticalAlignment = GridData.FILL;
        data.horizontalAlignment = GridData.FILL;
        composite.setLayoutData(data);

        return composite;
    }

    protected void performDefaults() {
        super.performDefaults();
        dbUrlText.setText(dbName);
        userText.setText(user);
        passwordText.setText(password);
        testQueryText.setText(testQuery);
        webURLText.setText(webURL);
        nwUserText.setText(nwUser);
        nwPassText.setText(nwPass);
        miiProjectText.setText(miiProject);
    }

    public boolean performOk() {
        IResource prj = (IResource) getElement();
        
        Config.setValue(prj, DBConsts.JDBC_DRIVER, jdbcDriverCombo.getText());
        Config.setValue(prj, DBConsts.JDBC_URL, dbUrlText.getText());
        Config.setValue(prj, DBConsts.JDBC_USER, userText.getText());
        Config.setValue(prj, DBConsts.JDBC_PASSWORD, passwordText.getText());
        Config.setValue(prj, DBConsts.JDBC_TEST_QUERY, testQueryText.getText());
        Config.setValue(prj, DBConsts.NW_WEB_URL, webURLText.getText());
        Config.setValue(prj, DBConsts.NW_WEB_USER, nwUserText.getText());
        Config.setValue(prj, DBConsts.NW_WEB_PASSWORD, nwPassText.getText());
        Config.setValue(prj, DBConsts.MII_PROJECT, miiProjectText.getText());

        return true;
    }

    @Override
    protected void performApply() {
        super.performApply();
        performOk();
    }
}