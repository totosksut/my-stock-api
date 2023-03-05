package com.example.stock.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FilesUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(FilesUtils.class);
	
	public static void copyFile(String path ,String pathType ,String pathTarget) throws Exception{
		if("SP".equals(pathType)) {
			logger.info("**********Copy : SP*********");
			doProcess(path,pathTarget+"STCRD_SP.dbf");
		}else if("MC".equals(pathType)) {
			logger.info("**********Copy : MC*********");
			doProcess(path,pathTarget+"STCRD_MC.dbf");
		}else if("SF".equals(pathType)) {
			logger.info("**********Copy : SF*********");
			doProcess(path,pathTarget+"STCRD_SF.dbf");
		}else if("AP".equals(pathType)) {
			logger.info("**********Copy : AP*********");
			doProcess(path,pathTarget+"STCRD_AP.dbf");
		}
	}
	
	public static void copyFileARMAS(String path ,String pathType ,String pathTarget) throws Exception{
		if("SP".equals(pathType)) {
			logger.info("**********Copy : ARMAS SP*********");
			doProcess(path,pathTarget+"ARMAS_SP.dbf");
		}else if("MC".equals(pathType)) {
			logger.info("**********Copy : ARMAS MC*********");
			doProcess(path,pathTarget+"ARMAS_MC.dbf");
		}else if("SF".equals(pathType)) {
			logger.info("**********Copy : ARMAS SF*********");
			doProcess(path,pathTarget+"ARMAS_SF.dbf");
		}else if("AP".equals(pathType)) {
			logger.info("**********Copy : ARMAS AP*********");
			doProcess(path,pathTarget+"ARMAS_AP.dbf");
		}
	}
	
	public static void copyFileARTRN(String path ,String pathType ,String pathTarget) throws Exception{
		if("SP".equals(pathType)) {
			logger.info("**********Copy : ARTRN SP*********");
			doProcess(path,pathTarget+"ARTRN_SP.dbf");
		}else if("MC".equals(pathType)) {
			logger.info("**********Copy : ARTRN MC*********");
			doProcess(path,pathTarget+"ARTRN_MC.dbf");
		}else if("SF".equals(pathType)) {
			logger.info("**********Copy : ARTRN SF*********");
			doProcess(path,pathTarget+"ARTRN_SF.dbf");
		}else if("AP".equals(pathType)) {
			logger.info("**********Copy : ARTRN AP*********");
			doProcess(path,pathTarget+"ARTRN_AP.dbf");
		}
	}
	
	public static void doProcess(String pathSource, String pathTarget) throws InterruptedException, IOException {
		long start = System.nanoTime();

		// copy files using java.nio FileChannel
		start = System.nanoTime();
		
		Path source = Paths.get(pathSource);
		Path target = Paths.get(pathTarget);
		
		Files.copy(source, target,StandardCopyOption.REPLACE_EXISTING);
		
		logger.info("Time taken by Channel Copy = " + (System.nanoTime() - start));

	}
	
	public static void zip(final String sourcNoteseDirPath, final String zipFilePath) throws IOException {
        Path zipFile = Files.createFile(Paths.get(zipFilePath));

        Path sourceDirPath = Paths.get(sourcNoteseDirPath);
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(zipFile));
             Stream<Path> paths = Files.walk(sourceDirPath)) {
            paths
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(sourceDirPath.relativize(path).toString());
                        try {
                            zipOutputStream.putNextEntry(zipEntry);
                            Files.copy(path, zipOutputStream);
                            zipOutputStream.closeEntry();
                        } catch (IOException e) {
                            System.err.println(e);
                        }
                    });
        }

        logger.info("Zip is created at : "+zipFile);
    }
	
}
