package ubersuper.utils.storage;

import ubersuper.clients.Client;
import ubersuper.clients.ClientList;
import ubersuper.tasks.Task;
import ubersuper.tasks.TaskList;
import ubersuper.utils.LoadedResult;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ClientStorage extends DataStorage<ClientList> {
    public ClientStorage() {
        super("uberSuperClients.txt");
    }

    /**
     * Loads clients from disk into a fresh {@link ClientList}.
     * <ul>
     *   <li>Silently skips malformed lines and keeps a count in {@link LoadedResult#skipped()}.</li>
     *   <li>Creates the {@code data/} folder and file if they do not exist.</li>
     *   <li>Parses timestamps using {@link LocalDateTime#parse(CharSequence)} (expects ISO format).</li>
     * </ul>
     *
     * @return a {@link LoadedResult} containing the populated {@link ClientList}, number of clients loaded,
     * and number of lines skipped.
     **/
    public LoadedResult<ClientList> load() {
        ClientList clients = new ClientList(this);
        int skipped = 0;
        try {
            if (Files.notExists(dataPath.getParent())) {
                Files.createDirectories(dataPath.getParent());
            }
            if (Files.notExists(dataPath)) {
                Files.createFile(dataPath);
                return new LoadedResult<ClientList>(clients, 0, 0);
            }
            List<String> lines = Files.readAllLines(dataPath, StandardCharsets.UTF_8);
            // parse each line -> Task or null
            List<Client> parsedTasks = lines.stream()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .map(line -> {
                        try {
                            String[] parts = line.split("\\|");
                            for (int i = 0; i < parts.length; i++) {
                                parts[i] = parts[i].trim();
                            }

                            if (parts.length < 3) {
                                return null;
                            }
                            String name = parts[0];
                            String phone = parts[1];
                            String email = parts[2];
                            return new Client(name, phone, email);
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .toList();

            // add valid clients to ClientList
            parsedTasks.stream()
                    .filter(Objects::nonNull)
                    .forEach(clients::add);

            // count skipped lines
            skipped = (int) parsedTasks.stream().filter(Objects::isNull).count();

            return new LoadedResult<ClientList>(clients, clients.size(), skipped);

        } catch (IOException ioe) {
            return new LoadedResult<ClientList>(clients, 0, 0);
        }
    }

    /**
     * Saves the current {@link ClientList} to disk, overwriting the previous content.
     * <ul>
     *   <li>Ensures the {@code data/} directory exists.</li>
     *   <li>Serializes each task via {@link Client#formatString()}.</li>
     *   <li>Writes using UTF-8; truncates the file first.</li>
     * </ul>
     *
     * @param clients list of tasks to be saved
     */
    public void save(ClientList clients) {
        try {
            if (Files.notExists(dataPath.getParent())) {
                Files.createDirectories(dataPath.getParent());
            }
            List<String> lines = clients.stream().map(Client::formatString).collect(Collectors.toList());
            Files.write(dataPath,
                    lines,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException ioe) {
            System.out.print("Could not save tasks!");
        }
    }
}

