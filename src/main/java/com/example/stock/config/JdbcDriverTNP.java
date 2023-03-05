package com.example.stock.config;

import org.h2.jdbc.JdbcConnection;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.*;

import org.springframework.context.annotation.Configuration;


@Configuration
public class JdbcDriverTNP implements Driver{

	private static final String PREFIX = "jdbc:dbschemaTNP:dbf:";
    private static final String H2_LOCATION = "~/.DbSchema/jdbc-dbf-cache/";

    public static final Logger LOGGER = Logger.getLogger( JdbcDriverTNP.class.getName() );

    static {
        try {
            DriverManager.registerDriver( new JdbcDriverTNP());
            LOGGER.setLevel(Level.ALL);
            final ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.ALL);
            consoleHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(consoleHandler);

            final FileHandler fileHandler = new FileHandler("%h/.DbSchema/logs/DbfJdbcDriver.log");
            fileHandler.setFormatter( new SimpleFormatter());
            fileHandler.setLevel(Level.ALL);
            LOGGER.addHandler(fileHandler);
        } catch ( Exception ex ){
            ex.printStackTrace();
        }
    }

    @Override
    public Connection connect( String url, Properties info ) throws SQLException {
        if ( acceptsURL(PREFIX)) {
            String defaultCharset = null;
            String path = url.substring(PREFIX.length());
            int idxQuestionMark = path.indexOf('?');
            if ( idxQuestionMark > 0 ){
                String params = path.substring( idxQuestionMark+1);
                path = path.substring(0, idxQuestionMark );
                for ( String paramSet: params.split("&")){
                    String[] pair = paramSet.split("=");
                    if ( pair.length == 2 ){
                        if ( "log".equalsIgnoreCase( pair[0]) || "logs".equalsIgnoreCase( pair[0])){
                            LOGGER.setLevel(Level.INFO);
                            ConsoleHandler handler = new ConsoleHandler();
                            handler.setFormatter( new SimpleFormatter());
                            LOGGER.addHandler(handler);
                        } else if ( "charset".equalsIgnoreCase( pair[0]) ) {
                            defaultCharset = pair[1];
                        }
                    }
                }
            }
            return getConnection( path, defaultCharset );
        } else {
            throw new SQLException("Incorrect URL. Expected jdbc:dbschemaTNP:dbf:<folderPath>");
        }
    }

    private final List<String> h2Databases = new ArrayList<>();


    private Connection getConnection( String databasePath, String defaultCharsetName ) throws SQLException {
        final File folder = new File(databasePath);
        
        if (!folder.exists()) {
            throw new SQLException("Folder does not exists: '" + folder + "'");
        }
        if (!folder.isDirectory()) {
            throw new SQLException("Expected path is not folder: '" + folder + "'");
        }
        final String h2DbName = md5Java( databasePath );
        final String h2DatabasePath = getH2DatabasePath( h2DbName );
        final String h2JdbcUrl = "jdbc:h2:file:" + h2DatabasePath + ";database_to_lower=true;case_insensitive_identifiers=true;DB_CLOSE_DELAY=-1";
        //final String h2JdbcUrl = "jdbc:h2:mem:dbfdriver;database_to_lower=true";
        LOGGER.log(Level.INFO, "Create H2 database '" + h2JdbcUrl + "'");

        final JdbcConnection h2NativeConnection = (JdbcConnection) (new org.h2.Driver().connect( h2JdbcUrl, new Properties() ));
        final H2Connection h2Connection = new H2Connection( h2NativeConnection, defaultCharsetName);
//        if ( !h2Databases.contains( h2DbName )){
//            h2Connection.transferFolder(folder, folder, h2NativeConnection, defaultCharsetName != null ? Charset.forName( defaultCharsetName ) : null );
//            h2Databases.add(h2DbName);
//        }
        h2Connection.transferFolder(folder, folder, h2NativeConnection, defaultCharsetName != null ? Charset.forName( defaultCharsetName ) : null );
        //h2Databases.add(h2DbName);
        return h2Connection;
    }


    private String getH2DatabasePath(String h2DbName ){
        final File h2File = new File(H2_LOCATION);
        if ( !h2File.exists()) {
            h2File.mkdirs();
        }
        return H2_LOCATION + h2DbName;
    }

    @Override
    public boolean acceptsURL(String url) {
        return url.startsWith(PREFIX);
    }

    static class ExtendedDriverPropertyInfo extends DriverPropertyInfo {
        ExtendedDriverPropertyInfo( String name, String value, String[] choices, String description ){
            super( name, value);
            this.description = description;
            this.choices = choices;
        }
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) {
        DriverPropertyInfo[] result = new DriverPropertyInfo[1];
        result[0] = new ExtendedDriverPropertyInfo("log", "true", new String[]{"true", "false"}, "Activate driver INFO logging");
        return result;
    }

    @Override
    public int getMajorVersion()
    {
        return 1;
    }


    @Override
    public int getMinorVersion()
    {
        return 0;
    }

    @Override
    public boolean jdbcCompliant() {
        return true;
    }

    @Override
    public Logger getParentLogger() {
        return null;
    }

    private static String md5Java(String message){
        String digest = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(message.getBytes("UTF-8"));

            //converting byte array to Hexadecimal String
            StringBuilder sb = new StringBuilder(2*hash.length);
            for(byte b : hash){
                sb.append(String.format("%02x", b&0xff));
            }

            digest = sb.toString();

        } catch (UnsupportedEncodingException | NoSuchAlgorithmException ex) {
            Logger.getLogger(JdbcDriverTNP.class.getName()).log(Level.SEVERE, null, ex);
        }
        return digest;
    }

}
