package ca.gc.agr.mbb.itisproxy;

import java.io.File;

public class TestUtil{

    static final String DB_TEST_DIR = "bdb_test_dir";
    public static final String getTestDbDir(){
	String base = TestUtil.class.getResource(File.separator).getFile();
	return base + DB_TEST_DIR;
    }


    static final String DB_TEST_OVERWRITE_DIR = "bdb_test_overwrite_dir";
    public static final String getTestOverwriteDbDir(){
	String base = TestUtil.class.getResource(File.separator).getFile();
	return base + DB_TEST_OVERWRITE_DIR;
    }

}

