package holidayBot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

public class WorkWithFiles {
    private String filename;

    public WorkWithFiles(String file) {
        filename = file;
    }

    public List<Storage> getDataFromFile() throws IOException {
        List<Storage> dataStorage = new ArrayList<Storage>();
        Path pathToFile = Path.of(filename);
        if (fileExistence(pathToFile)) {
            List<String> fileData = readFile(pathToFile);
            for (String line : fileData) {
                if (Objects.equals(line, ""))
                    continue;
                Storage element = formatStorage(line);
                if (element == null) {
                    continue;
                }
                dataStorage.add(element);
            }
        }

        return dataStorage;
    }

    private Storage formatStorage(String line) {
        String[] lineParts = line.split(":");
        String[] dataParts = lineParts[2].split("-");
        int year = Integer.parseInt(dataParts[0]);
        int month = Integer.parseInt(dataParts[1]);
        int day = Integer.parseInt(dataParts[2]);
        LocalDate date = LocalDate.of(year, month, day);
        Storage element = null;
        if (lineParts.length == 3) {
            element = new Storage(lineParts[0], lineParts[1], date, null);
        } else if (lineParts.length == 4) {
            element = new Storage(lineParts[0], lineParts[1], date, lineParts[3]);
        }
        return element;
    }

    public void writeDataToTheFile(String data) throws IOException {
        Path pathToFile = Path.of(filename);
        if (fileExistence(pathToFile)) {
            Files.writeString(pathToFile, data, StandardOpenOption.APPEND);
        } else {
            createNewFile(pathToFile);
            Files.writeString(pathToFile, data, StandardOpenOption.APPEND);
        }

    }

    private void createNewFile(Path pathToFile) throws IOException {
        Files.createFile(pathToFile);
    }

    private boolean fileExistence(Path pathToFile) throws IOException {
        return Files.exists(pathToFile);
    }

    private List<String> readFile(Path pathToFile) throws IOException {
        return Files.readAllLines(pathToFile);
    }

    public void rewriteAllFile(Storage elemToRewrite) throws IOException {
        Path pathToFile = Path.of(filename);
        List<String> oldData = readFile(pathToFile);
        for (String userData : oldData) {
            String[] dataParts = userData.split(":");
            if (Objects.equals(elemToRewrite.getNickname(), dataParts[0]) && Objects.equals(elemToRewrite.getPassword(), dataParts[1])) {
                String newUserData = dataParts[0] + ':' + dataParts[1] + ':' + LocalDate.now();
                oldData.remove(userData);
                oldData.add(newUserData);
                break;
            }
        }
        Files.write(pathToFile, oldData);
    }
}