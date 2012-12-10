package org.asem.eclipse.mii.db;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;

/**
 * 
 * @author ASementsov
 */
public class DBFiles {
    private String jdbcDriver;
    private String dbName;
    private String user;
    private String password;
    private String testQuery;
    private String webURL;
    private String nwUser;
    private String nwPass;

    private final boolean valid;
    private Connection conn;

    private final Map<String, Long> pathsMap = new HashMap<String, Long>();
    private long lastFoundPathId;

    public DBFiles(IResource prj) {
        jdbcDriver = Config.getValue(prj, DBConsts.JDBC_DRIVER, "");
        dbName = Config.getValue(prj, DBConsts.JDBC_URL, "");
        user = Config.getValue(prj, DBConsts.JDBC_USER, "");
        password = Config.getValue(prj, DBConsts.JDBC_PASSWORD, "");
        testQuery = Config.getValue(prj, DBConsts.JDBC_TEST_QUERY, "select * from dual");
        webURL = Config.getValue(prj, DBConsts.NW_WEB_URL, "");
        nwUser = Config.getValue(prj, DBConsts.NW_WEB_USER, "");
        nwPass = Config.getValue(prj, DBConsts.NW_WEB_PASSWORD, "");

        if (jdbcDriver.isEmpty() || dbName.isEmpty() || user.isEmpty()) {
            valid = false;
        }
        else {
            valid = true;
        }
    }
    
    public boolean checkPreferences ()
    {
        if (jdbcDriver.isEmpty() ||
            dbName.isEmpty() || 
            user.isEmpty() ||
            password.isEmpty() ||
            testQuery.isEmpty() ||
            webURL.isEmpty() ||
            nwUser.isEmpty() ||
            nwPass.isEmpty()) {
            return false;
        }

        return true;
    }

    /**
     * Test connection using test query
     * 
     * @param con
     *            - connection to test
     * @return success flag
     */
    private boolean isValid(Connection con) {
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            ResultSet rSet = stmt.executeQuery(testQuery);
            rSet.close();
            stmt.close();
            return true;
        }
        catch (SQLException e) {
        	System.err.println("Connection is not valid: " + e.toString());
            return false;
        }
        finally {
            if (stmt != null)
                try {
                    stmt.close();
                }
                catch (SQLException ex) {
                }
        }
    }

    /**
     * Function retrieve connection to database
     * 
     * @return connection
     * @throws SQLException
     */
    public Connection getConnection() throws SQLException {
        try {
            if (conn != null && isValid(conn))
                return conn;

            Class.forName(jdbcDriver);
            conn = DriverManager.getConnection(dbName, user, password);
            /*
             * Should always use commit or rollback statement
             */
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            return conn;
        }
        catch (ClassNotFoundException ex) {
            System.err.println("getConnection: " + ex.toString());
        }

        return null;
    }

    /**
     * Function fills project path from xMII database
     * 
     * @param project
     *            - project name
     */
    public void fillProjectPaths(String project) {
        pathsMap.clear();

        if (!valid)
            return;

        String pathToFind = project + "%";
        PreparedStatement ps = null;

        try {
            ps = getConnection().prepareStatement(DBConsts.PATHS_FOR_PROJECT_SQL);
            ps.setString(1, pathToFind);
            ResultSet rSet = ps.executeQuery();
            while (rSet.next()) {
                pathsMap.put(rSet.getString(2), rSet.getLong(1));
            }
            rSet.close();
        }
        catch (SQLException e) {
            System.err.println("fillProjectPaths: " + e.toString());
        }
        finally {
            if (ps != null)
                try {
                    ps.close();
                }
                catch (SQLException ex) {
                }
        }
    }

    /**
     * Function build array of path to create
     * 
     * @param path
     *            - pathname to create
     * @return array of paths
     */
    private List<String> buildPathsToCreate(String path) {
        List<String> pathToCreate = new ArrayList<String>();
        int index = path.lastIndexOf('/');
        while (index > 0) {
            String pathToFind = path.substring(0, index);
            pathToCreate.add(0, path);
            /*
             * Parent path exists break the loop
             */
            if (pathsMap.containsKey(pathToFind)) {
                lastFoundPathId = pathsMap.get(pathToFind);
                break;
            }

            path = pathToFind;
            index = path.lastIndexOf('/');
        }

        return pathToCreate;
    }

    /**
     * Function retrieves next value from sequence
     * 
     * @param name
     *            - name of sequence
     * @return next value
     * @throws SQLException
     */
    private long next(String name) throws SQLException {
        long id = -1L;

        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con = null;
        try {
            con = getConnection();
            ps = con.prepareStatement(DBConsts.SEQ_FOR_UPDATE);
            ps.setString(1, name);

            rs = ps.executeQuery();
            if (rs.next()) {
                id = rs.getLong(1) + 1L;
                PreparedStatement pstmt = conn.prepareStatement("UPDATE XMII_Sequence SET SEQVAL = ? WHERE Name = ?");
                pstmt.setLong(1, id);
                pstmt.setString(2, name);
                pstmt.executeUpdate();
                pstmt.close();
            }

            con.commit();
            con = null;
        }
        finally {
            if (ps != null)
                ps.close();
        }

        return id;
    }

    /**
     * Function create paths using array of paths
     * 
     * @param paths
     *            - array of paths to create
     * @return
     */
    private long createPaths(List<String> paths) {
        PreparedStatement ps = null;
        Connection con = null;

        try {
            con = DriverManager.getConnection(dbName, user, password);
            ps = con.prepareStatement(DBConsts.CREATE_PATH_SQL);

            Timestamp now = new Timestamp(new Date().getTime());
            String userName = System.getProperty("user.name");

            for (String path : paths) {
                /*
                 * ` * ID, FullPath, ParentID, Created, CreatedBy
                 */
                long id = next(DBConsts.PATHID_SEQUENCE);
                ps.setLong(1, id);
                ps.setString(2, path);
                ps.setLong(3, lastFoundPathId);
                ps.setTimestamp(4, now);
                ps.setString(5, userName);
                ps.executeUpdate();

                lastFoundPathId = id;
            }
            con.commit();
            con = null;

            return lastFoundPathId;
        }
        catch (SQLException e) {
            System.err.println("create paths: " + e.toString());
        }
        finally {
            if (ps != null)
                try {
                    ps.close();
                }
                catch (SQLException ex) {
                }
        }

        return -1;
    }

    /**
     * Function retrieves path id for file
     * 
     * @param fileObject
     *            - file information
     * @return path id
     */
    public long getFilePathId(IResource fileObject) {
        long id = -1;

        if (!valid)
            return id;

        String relativePath = null;
        IProject prj = fileObject.getProject();
        String miiProject = null;

        try {
            miiProject = prj.getPersistentProperty(new QualifiedName("", DBConsts.MII_PROJECT));
        }
        catch (CoreException e) {
            e.printStackTrace();
        }

        if (miiProject == null)
            return id;

        if (fileObject instanceof IFolder) {
            relativePath = fileObject.getFullPath().toString().replace(prj.getFullPath().toString(), "");
            relativePath = miiProject + relativePath;
        }
        else {
            relativePath = fileObject.getParent().getFullPath().toString().replace(prj.getFullPath().toString(), "");
            relativePath = miiProject + relativePath;
        }

        if (!pathsMap.containsKey(miiProject))
            fillProjectPaths(miiProject);

        if (pathsMap.containsKey(relativePath)) {
            return pathsMap.get(relativePath);
        }

        /*
         * Not found path for this file It needs to create path First of all try to fuind nearest parent path
         */
        List<String> paths = buildPathsToCreate(relativePath);
        /*
         * Create all paths
         */
        long ret = createPaths(paths);
        /*
         * Reread all paths
         */
        fillProjectPaths(miiProject);
        return ret;
    }

    private Map<String, Long> getChildrenPath(long pathId) {
        Map<String, Long> ret = new HashMap<String, Long>();

        /*
         * Find path name by id
         */
        String path = "";
        for (Entry<String, Long> entry : pathsMap.entrySet()) {
            if (entry.getValue() == pathId) {
                path = entry.getKey();
                break;
            }
        }

        if (path.isEmpty())
            return ret;

        for (Entry<String, Long> entry : pathsMap.entrySet()) {
            if (entry.getKey().startsWith(path + "/")) {
                String rel_path = entry.getKey().replace(path + "/", "");
                if (rel_path.contains("/"))
                    continue;

                ret.put(rel_path, entry.getValue());
            }
        }

        return ret;
    }

    /**
     * Function converts file extension regards to file content
     * 
     * @param name
     * @param content
     * @return
     */
    private String fileNameConvert(String name, byte[] content) {
        int index = name.lastIndexOf('.');
        if (index == -1)
            return name;

        String ext = name.substring(index + 1).toUpperCase();
        String solid = name.substring(0, index + 1);
        if ("IRPT".equals(ext)) {
            /*
             * This is IRPT files may contains HTML or JSON code In NetBeans it should be properly renamed To determine
             * what the file was downloaded it's content must be checked. Checks max 1024 byte of the file. All files
             * should using utf-8 charset
             */
            int len = content.length > 1024 ? 1024 : content.length;
            try {
                String strToCheck = new String(content, 0, len, "UTF-8").toUpperCase();
                /*
                 * Remove all spaces from string
                 */
                strToCheck = strToCheck.replaceAll("\\s", "");
                /*
                 * Check is it HTML code
                 */
                if (strToCheck.startsWith("<")) {
                    name = solid + "html";
                }
                else if (strToCheck.startsWith("{") || strToCheck.startsWith("//") || strToCheck.startsWith("/*")) {
                    name = solid + "json";
                }
            }
            catch (UnsupportedEncodingException ex) {
                System.err.println("fileNameConvert: " + ex.toString());
            }
        }

        return name;
    }

    private void loadDirFiles(long pathId, IFolder parent) {
        /*
         * List files
         */
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = getConnection().prepareStatement(DBConsts.LIST_FILES);
            ps.setLong(1, pathId);

            rs = ps.executeQuery();
            while (rs.next()) {
                byte[] content = rs.getBytes(3);
                if (content == null)
                    continue;

                String name = rs.getString(2);

                /*
                 * Convert file names extension
                 */
                name = fileNameConvert(name, content);
                System.out.println("loadFile: " + name + "...");

                ByteArrayInputStream bi = new ByteArrayInputStream(content);

                IFile fileObject = parent.getFile(name);
                if (!fileObject.exists()) {
                    fileObject.create(bi, true, null);
                }
                else {
                    fileObject.setContents(bi, true, true, null);
                }

                System.out.println("loaded");
            }

            rs.close();
        }
        catch (Exception ex) {
            System.out.println("loadDirFiles: " + ex.toString());
        }
        finally {
            if (ps != null)
                try {
                    ps.close();
                }
                catch (SQLException ex) {
                }
        }
    }

    /**
     * Function downloads full directory from xMII server
     * 
     * @param folder
     *            - directory info
     */
    public void getDirectory(IFolder folder) {
        /*
         * Found path
         */
        long pathId = getFilePathId(folder);
        if (pathId == -1)
            return;

        /*
         * First downloads all files in this directory
         */
        loadDirFiles(pathId, folder);

        /*
         * For each children path
         */
        Map<String, Long> pathIds = getChildrenPath(pathId);
        for (Entry<String, Long> entry : pathIds.entrySet()) {
            IFolder newPath = folder.getFolder (entry.getKey());

            if (!newPath.exists()) {
                try {
                    newPath.create (true, true, null);
                }
                catch (CoreException ex) {
                    ex.printStackTrace();
                    continue;
                }
            }

            /**
             * Load recursive all directories and files
             */
            getDirectory(newPath);
        }
    }

    /**
     * Function downloads file from xMII server
     * 
     * @param fileObject
     *            - file info
     * @return file id
     */
    public long getFile(IResource fileObject) {
        long ret = -1;
        if (fileObject instanceof IFolder) {
            getDirectory((IFolder)fileObject);
            return ret;
        }

        /*
         * Found path
         */
        long pathId = getFilePathId(fileObject);
        if (pathId == -1)
            return ret;

        /*
         * Convert fileName
         */
        String fileName = fileObject.getName();
        fileName = fileName.substring(0, fileName.lastIndexOf('.'));
        String ext = convertExt(fileObject.getFileExtension());
        fileName = fileName + "." + ext;

        System.out.println ("loadFile: " + fileName + "...");

        /*
         * If path was found
         */
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = getConnection().prepareStatement(DBConsts.LOAD_FILE);
            ps.setLong(1, pathId);
            ps.setString(2, fileName);

            rs = ps.executeQuery();
            if (rs.next()) {
                ret = rs.getLong(1);
                byte[] content = rs.getBytes(3);
                ((IFile)fileObject).setContents(new ByteArrayInputStream(content), true, true, null);
            }
            rs.close();

            System.out.println ("loaded");
        }
        catch (Exception ex) {
            System.err.println ("getFile: ");
        }
        finally {
            if (ps != null)
                try {
                    ps.close();
                }
                catch (SQLException ex) {
                }
        }

        return ret;
    }

    private long getFileId(IResource fileObject, String fileName) {
        long ret = -1;
        /*
         * Found path
         */
        long pathId = getFilePathId(fileObject);
        if (pathId == -1)
            return ret;

        /*
         * If path was foudnn
         */
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = getConnection().prepareStatement(DBConsts.GET_FILE_ID);
            ps.setLong(1, pathId);
            ps.setString(2, fileName);

            rs = ps.executeQuery();
            if (rs.next()) {
                ret = rs.getLong(1);
            }
            rs.close();
        }
        catch (Exception ex) {
            System.err.println ("getFileId: " + ex.toString());
        }
        finally {
            if (ps != null)
                try {
                    ps.close();
                }
                catch (SQLException ex) {
                }
        }

        return ret;
    }
    
    public long saveFile(IResource fileObject) {
        long ret = -1;

        if (fileObject.getName().startsWith("."))
            return ret;

        if (fileObject instanceof IFolder) {
            saveDirectory((IFolder)fileObject);
            return ret;
        }
        
        if (!(fileObject instanceof IFile))
            return ret;

        /*
         * Convert fileName
         */
        String fileName = fileObject.getName();
        fileName = fileName.substring(0, fileName.lastIndexOf('.'));
        String ext = convertExt(fileObject.getFileExtension());
        fileName = fileName + "." + ext;

        System.out.print ("saveFile: " + fileName + "...");

        /*
         * Find file identifier if file not found the fileId = -1
         */
        long fileId = getFileId(fileObject, fileName);
        long pathId = -1;
        if (fileId == -1) {
            pathId = getFilePathId(fileObject);
            if (pathId == -1)
                return ret;
        }

        /*
         * If path was found
         */
        PreparedStatement ps = null;
        byte[] content = new byte[8192];

        try {
            InputStream is = ((IFile)fileObject).getContents();
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int length;
            while ((length = is.read(content)) > 0) {
                bo.write(content, 0, length);
            }
            is.close();
            bo.close();
            content = bo.toByteArray();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        if (content == null || content.length == 0)
            return ret;

        Timestamp now = new Timestamp(new Date().getTime());
        String userName = System.getProperty("user.name");

        try {
            // INSERT INTO XMII_FILES (ID, PATHID, NAME, TEXT, CREATED, CREATEDBY) VALUES (?, ?, ?, ?, ? ,?)
            if (fileId == -1) {
                ps = getConnection().prepareStatement(DBConsts.FILE_INSERT);
                fileId = next(DBConsts.FILEID_SEQUENCE);
                ps.setLong(1, fileId);
                ps.setLong(2, pathId);
                ps.setString(3, fileName);
                ps.setBytes(4, content);
                ps.setTimestamp(5, now);
                ps.setString(6, userName);
                ps.executeUpdate();
                getConnection().commit();
            }
            else {
                // UPDATE XMII_FILES SET TEXT = ?, MODIFIED = ?, MODIFIEDBY = ?
                // WHERE FILEID = ?
                ps = getConnection().prepareStatement(DBConsts.FILE_UPDATE);
                ps.setBytes(1, content);
                ps.setTimestamp(2, now);
                ps.setString(3, userName);
                ps.setLong(4, fileId);
                ps.executeUpdate();
                getConnection().commit();
            }

            System.out.println ("saved");
        }
        catch (Exception ex) {
            System.err.println ("saveFile: " + ex.toString());
        }
        finally {
            if (ps != null)
                try {
                    ps.close();
                }
                catch (SQLException ex) {
                }
        }

        return ret;
    }
    
  
    private void saveDirectory(IFolder folder) {
        IResource[] children;
        try {
            children = folder.members();
            for (IResource child : children) {
                saveFile(child);
            }
        }
        catch (CoreException e) {
            e.printStackTrace();
        }
    }

    public void updateFolder(String folderName) {
        try {
            String surl = webURL + "/XMII/Catalog?Mode=Blowout&Folder=" + folderName;
            URL url = new URL(surl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            String auth = nwUser + ":" + nwPass;
            String encoding = new String (Base64.encodeBase64(auth.getBytes("UTF-8")));
            con.setRequestProperty("Authorization", "Basic " + encoding);
            int code = con.getResponseCode();
            if (code == 200) {
                InputStream is = con.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuilder response = new StringBuilder(); 
                while((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                
                System.out.println (response);
                is.close();
            }
            else {
                /*
                 * Impossible to use message dialog in this thread. Only in main application thread
                 * dialogs can be used
                 * 
                    Shell shell = Config.getShell();
                    MessageDialog.openError(shell, "Error", "Error - HTTP code: " + code);
                */
                System.out.println ("Error - HTTP code: " + code + " folder: " + folderName);
            }
            
            con.disconnect();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void close() {
        if (conn != null) {
            try {
                conn.close();
            }
            catch (SQLException ex) {
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        close();
    }

    public static String convertExt(String ext) {
        if (ext.equalsIgnoreCase("HTML") || ext.equalsIgnoreCase("JSON"))
            return "irpt";

        return ext;
    }
}
