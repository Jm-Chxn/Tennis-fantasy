package com.tennisfantasy.backend.service;

import com.tennisfantasy.backend.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileWatcherService {

    @Autowired
    private SupabaseService supabaseService;

    @Bean
    public ApplicationRunner startFileWatcher() {
        return args -> {
            // Start watching the players.csv file for changes
            startWatching();
        };
    }

    private void startWatching() {
        try {
            // Get the path to the resources directory
            Path resourcesPath = Paths.get("src/main/resources");
            
            WatchService watchService = FileSystems.getDefault().newWatchService();
            resourcesPath.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
            
            System.out.println("👀 Watching for changes to players.csv...");
            
            // Start watching in a separate thread
            new Thread(() -> {
                try {
                    while (true) {
                        WatchKey key = watchService.take();
                        
                        for (WatchEvent<?> event : key.pollEvents()) {
                            if (event.context().toString().equals("players.csv")) {
                                System.out.println("📝 players.csv changed! Auto-updating Supabase...");
                                Thread.sleep(1000); // Wait for file write to complete
                                updateFromFile();
                            }
                        }
                        
                        key.reset();
                    }
                } catch (Exception e) {
                    System.err.println("File watcher error: " + e.getMessage());
                }
            }).start();
            
        } catch (IOException e) {
            System.err.println("Failed to start file watcher: " + e.getMessage());
        }
    }

    private void updateFromFile() {
        try {
            ClassPathResource resource = new ClassPathResource("players.csv");
            List<Player> players = new ArrayList<>();
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("#") || line.trim().isEmpty()) continue;
                    
                    String[] parts = line.split(",");
                    if (parts.length == 5) {
                        Player player = new Player(
                            parts[0].trim(),
                            parts[1].trim(), 
                            parts[2].trim(),
                            Integer.parseInt(parts[3].trim()),
                            new BigDecimal(parts[4].trim())
                        );
                        players.add(player);
                    }
                }
            }
            
            supabaseService.createPlayers(players)
                    .subscribe(
                        savedPlayers -> System.out.println("✅ Auto-updated " + savedPlayers.size() + " players from file!"),
                        error -> System.out.println("❌ Failed to auto-update from file: " + error.getMessage())
                    );
                    
        } catch (Exception e) {
            System.err.println("Error reading players file: " + e.getMessage());
        }
    }
}
