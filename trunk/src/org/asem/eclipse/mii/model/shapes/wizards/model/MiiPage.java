package org.asem.eclipse.mii.model.shapes.wizards.model;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.asem.eclipse.mii.model.abs.ShapeConstants;
import org.asem.eclipse.mii.model.shapes.COLUMN;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSParser;


public class MiiPage extends WizardPage {
    private Text    queryUrl;
    private Text    userName;
    private Text    password;
    
    public String   httpUrl;
    
    protected MiiPage(String pageName) {
        super(pageName);
        setTitle(pageName);
    }

    public Text getQueryUrl() {
        return queryUrl;
    }

    public Text getUserName() {
        return userName;
    }

    public Text getPassword() {
        return password;
    }

    @Override
    public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        composite.setLayout(layout);

        Label label = new Label(composite, SWT.NONE);
        label.setText("URL:");
        queryUrl = new Text(composite, SWT.SINGLE | SWT.BORDER);
        queryUrl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        label = new Label(composite, SWT.NONE);
        label.setText("Username:");
        userName = new Text(composite, SWT.SINGLE | SWT.BORDER);
        userName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        label = new Label(composite, SWT.NONE);
        label.setText("Password:");
        password = new Text(composite, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD);
        password.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        setControl(composite);
    }
    
    public Set<COLUMN> loadTransaction () {
        if (queryUrl.getText().isEmpty())
            return Collections.emptySet();
        
        Set<COLUMN> columns = new HashSet<COLUMN>();
        try {
            URL url = new URL(queryUrl.getText());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            String auth = userName.getText() + ":" + password.getText();
            String encoding = new String (Base64.encodeBase64(auth.getBytes("UTF-8")));
            con.setRequestProperty("Authorization", "Basic " + encoding);
            int code = con.getResponseCode();
            if (code == 200) {
                InputStream in = con.getInputStream();
                
                DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
                DOMImplementationLS impl = (DOMImplementationLS)registry.getDOMImplementation("LS");
                
                LSInput inp = impl.createLSInput();
                inp.setByteStream(in);
                inp.setEncoding(ShapeConstants.ENCODING);

                LSParser lParser = impl.createLSParser(DOMImplementationLS.MODE_SYNCHRONOUS, null);
                Document doc = lParser.parse(inp);

                NodeList nlist = doc.getElementsByTagName("Column");
                for (int i=0;i<nlist.getLength();i++) {
                    String name = ((Element)nlist.item(i)).getAttribute("Name");
                    String type = ((Element)nlist.item(i)).getAttribute("SQLDataType");
                    COLUMN col = new COLUMN(name, type);
                    columns.add(col);
                }

                in.close();
                
                httpUrl = url.toString();
            }
            else {
                MessageDialog.openError(getShell(), "Error", "Error - HTTP code: " + code);
            }
            con.disconnect();
        }
        catch (Exception ex) {
            MessageDialog.openError(getShell(), "loadTransaction", ex.toString());
        }
        
        return columns;
    }
}
