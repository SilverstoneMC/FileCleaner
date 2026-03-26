// Copyright JasonHorkles and contributors
// SPDX-License-Identifier: GPL-3.0-or-later
package net.silverstonemc.filecleaner;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class VersionChecker {
    private static final String PLUGIN_ID = "O7A4dexn";

    public static final String PLUGIN_URL = "https://modrinth.com/plugin/" + PLUGIN_ID + "/changelog";

    @Nullable
    public String getLatestVersion() {
        try {
            // Send the request
            InputStream url = new URI("https://api.modrinth.com/v2/project/" + PLUGIN_ID + "/version").toURL()
                .openStream();

            // Read the response
            JSONObject response = new JSONArray(new String(
                url.readAllBytes(),
                StandardCharsets.UTF_8)).getJSONObject(0);
            url.close();

            return response.getString("version_number");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
