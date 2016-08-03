package org.cvrgrid.achuploader.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

public class AchLoaderScriptGenerator {

	public AchLoaderScriptGenerator(String achRoot, String limit, String processedFile, String batchFile) throws IOException {

		FileVisitor<Path> fileProcessor;
		fileProcessor = new ProcessFile(limit, processedFile, batchFile);
		Files.walkFileTree(Paths.get(achRoot), fileProcessor);

	}

	private static final class ProcessFile extends SimpleFileVisitor<Path> {

		String currentFolder, processedFile, batchFile;
		int dirCount, limit, fileCount;
		ArrayList<String> visitedDirs, processedFiles;
		Path currentDir;

		public ProcessFile(String limit, String processedFile, String batchFile) {
			setCurrentFolder("");
			setDirCount(0);
			setFileCount(0);
			if (new Integer(limit).intValue() > 0) {
				setLimit(new Integer(limit).intValue());
			} else {
				setLimit(999999999);				
			}
			setProcessedFile(processedFile);
			setVisitedDirs(new ArrayList<String>());
			setProcessedFiles(new ArrayList<String>());
			File processedFileContents = new File (getProcessedFile());
			if (processedFileContents.exists()) setProcessedFiles(getPFiles(processedFileContents, getProcessedFiles()));
			setBatchFile(batchFile);

		}


		public String getCurrentFolder() {
			return currentFolder;
		}


		public void setCurrentFolder(String currentFolder) {
			this.currentFolder = currentFolder;
		}


		public Path getCurrentDir() {
			return currentDir;
		}


		public void setCurrentDir(Path currentDir) {
			this.currentDir = currentDir;
		}


		public int getDirCount() {
			return dirCount;
		}

		public void setDirCount(int dirCount) {
			this.dirCount = dirCount;
		}

		public int getLimit() {
			return limit;
		}

		public void setLimit(int limit) {
			this.limit = limit + 2;
		}

		public int getFileCount() {
			return fileCount;
		}


		public void setFileCount(int fileCount) {
			this.fileCount = fileCount;
		}


		public ArrayList<String> getVisitedDirs() {
			return visitedDirs;
		}

		public void setVisitedDirs(ArrayList<String> visitedDirs) {
			this.visitedDirs = visitedDirs;
		}

		public String getProcessedFile() {
			return processedFile;
		}

		public void setProcessedFile(String processedFile) {
			this.processedFile = processedFile;
		}

		public ArrayList<String> getProcessedFiles() {
			return processedFiles;
		}

		public void setProcessedFiles(ArrayList<String> processedFiles) {
			this.processedFiles = processedFiles;
		}

		public String getBatchFile() {
			return batchFile;
		}


		public void setBatchFile(String batchFile) {
			this.batchFile = batchFile;
		}


		@Override public FileVisitResult visitFile(
				Path aFile, BasicFileAttributes aAttrs
				) throws IOException {
			if (aAttrs.isRegularFile())
				if (!getProcessedFiles().contains(getCurrentDir().toString()))
					if (aFile.toFile().toString().endsWith("RFP.csv")) {
						try {
							FileWriter writer = new FileWriter(getBatchFile(), true);
							writer.write("java -jar csvuploader.jar -f " + aFile + " -r " + getCurrentFolder() + "\r\n");
							setFileCount(getFileCount() + 1);
							writer.close();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}

					}
			return FileVisitResult.CONTINUE;
		}

		@Override public FileVisitResult preVisitDirectory(
				Path aDir, BasicFileAttributes aAttrs
				) throws IOException {
			ArrayList<String> tempPaths = getVisitedDirs();
			String splitString = File.separator;
			if (File.separator.equalsIgnoreCase("\\")) splitString += File.separator;
			if (aDir.toString().split(splitString).length > 1) setCurrentFolder(aDir.toString().split(splitString)[1]);
			setCurrentDir(aDir);
			setDirCount(getDirCount() + 1);
			if (getDirCount() < 2)
				try {
					FileWriter writer = new FileWriter(getBatchFile());
					writer.write("");
					writer.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if (getDirCount() < getLimit()) {
				if (!getProcessedFiles().contains(aDir.toString())) {
					if(!(getCurrentFolder().isEmpty())) tempPaths.add(aDir.toString());
					setVisitedDirs(tempPaths);
				}
				return FileVisitResult.CONTINUE;
			} else {
				try {
					FileWriter writer = new FileWriter(getProcessedFile(), true);
					for (String visited : getVisitedDirs()) writer.write(visited + "\r\n");
					writer.close();
					System.out.println(getVisitedDirs().size() + " new directories visited and logged in " + getProcessedFile());
					System.out.println("Batch script written to " + getBatchFile() + " for " + getFileCount() + " files");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return FileVisitResult.TERMINATE;				
			}
		}

		private static ArrayList<String> getPFiles(File processedFileRecord, ArrayList<String> processedFiles) {
			try {
				FileReader fr = new FileReader(processedFileRecord);
				BufferedReader br = new BufferedReader(fr); 
				String filePath; 
				while((filePath = br.readLine()) != null) { 
					processedFiles.add(filePath);
				} 
				br.close();
				fr.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return processedFiles;
		}
	}
}
