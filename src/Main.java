import java.io.*;
import java.net.URI;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Main {
    public static String root = "C:\\Users\\Administrator\\Desktop\\test\\";

    public static void main(String[] args) throws IOException {
        String file = "in.txt";
        String dirPath = "new_dir\\new_dir2\\";
        //writeToTxtFile(file, readTxtFile(root + file));
        //createLog();
        createDirectoryWithBoolean(dirPath);
        copyFile(file, dirPath);
        //listFilesUsingDirectoryStream(root);
        //zipDirectory(root + "file to zip.txt");
        //zipDirectory(root + "folder to zip");
        //unzip(root + "folder to zip.zip");
    }

    public static void unzip(String zipPath) throws IOException {
        String destDirectory = zipPath.substring(0, zipPath.indexOf(".")) + "\\";
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        destDirectory = destDirectory.substring(0, destDirectory.lastIndexOf("\\", destDirectory.lastIndexOf("\\") - 1));
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipPath));
        ZipEntry entry = zipIn.getNextEntry();
        // iterates over entries in the zip file
        while (entry != null) {
            System.out.println(entry.getName());
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                // if the entry is a file, extracts it
                extractFile(zipIn, filePath);
            } else {
                // if the entry is a directory, make the directory
                File dir = new File(filePath);
                dir.mkdirs();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
    }

    private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[4096];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }

    public static void zipDirectory(String sourceFile) throws IOException {
        String zipName = sourceFile;
        if (zipName.contains(".")) zipName = zipName.substring(0, zipName.indexOf('.'));
        zipName += ".zip";
        try ( ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipName)) ) {
            File fileToZip = new File(sourceFile);
            zipFile(fileToZip, fileToZip.getName(), zipOut);
        }
    }

    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("\\")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "\\"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "\\" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }

    private static void listFilesUsingDirectoryStream(String dir) throws IOException {
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Path.of(dir))) {
            for (Path path : directoryStream) {
                if (!Files.isDirectory(path)) {
                    if (path.toString().matches(".*\\.txt")) System.out.println(path.toAbsolutePath());
                }
            }
        }
    }

    public static Set<String> listFilesUsingFilesList(String dir) throws IOException {
        try (Stream<Path> stream = Files.list(Paths.get(dir))) {
            return stream
                .filter(Files::isDirectory)
                .map(Path::getFileName)
                .map(Path::toString)
                .collect(Collectors.toSet());
        }
    }

    public static Set<String> listFilesUsingFileWalkAndVisitor(String dir) throws IOException {
        Set<String> files = new HashSet<>();

        Files.walkFileTree(Paths.get(dir), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (!Files.isDirectory(file)) {
                    files.add(file.toString());
                }
                return FileVisitResult.CONTINUE;
            }
        });

        return files;
    }

    public static String readTxtFile(String file) throws IOException {
        String s = "";

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                if (line.contains("sailors")) line = line.replaceAll("sailors = \\d+", "sailors = 0");
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }

            s = sb.toString();
        }

        return s;
    }

    public static void writeToTxtFile(String file, String content) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(content);
        }
    }

    public static void copyFile(String file, String dirPath) throws IOException {
        if ( Files.exists(Path.of(root + file)) ) {
            Files.deleteIfExists(Path.of(root + dirPath + file));
            Files.copy(Path.of(root + file), Path.of(root + dirPath + file));
        } else System.out.println(root + file + " does not exist in source");
    }

    public static void createLog() throws IOException {
        ArrayList<String> text = new ArrayList<>(Arrays.asList("first line of log", "second line of log"));
        Files.write(Path.of(root + "log.txt"), text);
    }

    public static void createDirectoryWithBoolean(String dirPath) {
        boolean directoryAlreadyExists = false;
        if (Files.exists(Path.of(root + dirPath))) {
            System.out.println( root + dirPath + " already exists");
        } else {
            try {
                if (dirPath.contains("\\")) {
                    Files.createDirectory(Path.of(root + dirPath));
                } else {
                    Files.createDirectories(Path.of(root + dirPath));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}