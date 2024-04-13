package net.silverstonemc.filecleaner;

import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class VersionChecker {
    public @Nullable String getLatestVersion() {
        final String pluginName = "FileCleaner";
        try {
            // Send the request
            InputStream url = new URI("https://api.github.com/repos/SilverstoneMC/" + pluginName + "/releases/latest")
                .toURL().openStream();

            // Read the response
            JSONObject response = new JSONObject(new String(url.readAllBytes(), StandardCharsets.UTF_8));
            url.close();

            return response.getString("tag_name");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
