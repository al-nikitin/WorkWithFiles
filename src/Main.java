import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static String root = "C:\\Users\\Administrator\\Desktop\\test\\";

    public static void main(String[] args) throws IOException {
/*        try {
            for (String file : listFilesUsingFileWalkAndVisitor("")) System.out.println(Path.of(file).toAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
        String file = "in.txt";
        //writeToTxtFile(file, readTxtFile(root + file));
        createLog();
    }

    public static Set<String> listFilesUsingFilesList(String dir) throws IOException {
        try (Stream<Path> stream = Files.list(Paths.get(dir))) {
            return stream
                .filter(file -> Files.isDirectory(file))
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

    public static void createLog() throws IOException {
        ArrayList<String> text = new ArrayList<>(Arrays.asList("first line of log", "second line of log"));
        Files.write(Path.of(root + "log.txt"), text);
    }
}