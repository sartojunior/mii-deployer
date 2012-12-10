package org.asem.eclipse.mii.db;

/**
 * Class represents constants for SQL queries to operate SAP MII data 
 * @author ASementsov
 */
public interface DBConsts {
	String CREATE_PATH_SQL = "INSERT INTO XMII_PATHS (ID, FullPath, ParentID, Created, CreatedBy) VALUES (?, ?, ?, ?, ?)";
	String PATHID_BY_NAME_SQL = "SELECT ID FROM XMII_PATHS WHERE FULLPATH = ?";
	String LOAD_FILE = "SELECT ID, DESCRIPTION, TEXT FROM XMII_FILES WHERE PATHID = ? AND NAME = ?";
	String GET_FILE_ID = "SELECT ID FROM XMII_FILES WHERE PATHID = ? AND NAME = ?";
	String LIST_FILES = "SELECT ID, NAME, TEXT FROM XMII_FILES WHERE PATHID = ?";
	String PATHS_FOR_PROJECT_SQL = "SELECT ID, FULLPATH FROM XMII_PATHS WHERE FULLPATH LIKE ?";
	String SEQ_FOR_UPDATE = "SELECT SEQVAL FROM XMII_Sequence WHERE Name = ? FOR UPDATE";
	String PATHID_SEQUENCE = "PATHID";
	String FILEID_SEQUENCE = "FILEID";
	String FILE_INSERT = "INSERT INTO XMII_FILES (ID, PATHID, NAME, TEXT, CREATED, CREATEDBY) VALUES (?, ?, ?, ?, ? ,?)";
	String FILE_UPDATE = "UPDATE XMII_FILES SET TEXT = ?, MODIFIED = ?, MODIFIEDBY = ? WHERE ID = ?";

	String LOAD_PROFILE = "SELECT XMLDATA FROM XMII_PROFILES WHERE NAME = ?";
	String COUNT_PROFILES = "SELECT COUNT(*) FROM XMII_PROFILES WHERE NAME = ?";
	String PROFILE_UPDATE = "UPDATE XMII_PROFILES SET XMLDATA = ?, MODIFIED = ?, MODIFIEDBY = ? WHERE NAME = ?";
	String PROFILE_INSERT = "INSERT INTO XMII_PROFILES (PROFILETYPE, NAME, XMLDATA, CREATED, CREATEDBY) VALUES (?, ?, ?, ?,?)";

	/*
	 * Preferences
	 */
	String JDBC_DRIVER = "jdbcDriver";
	String JDBC_URL = "database";
	String JDBC_USER = "user";
	String JDBC_PASSWORD = "password";
	String JDBC_TEST_QUERY = "testQuery";
	String NW_WEB_URL = "webURL";
	String NW_WEB_USER = "nwUser";
	String NW_WEB_PASSWORD = "nwPass";
	String MII_PROJECT = "MII_project";
}
