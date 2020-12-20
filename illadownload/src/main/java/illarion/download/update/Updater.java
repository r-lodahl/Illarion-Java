package illarion.download.update;

import com.jsoniter.JsonIterator;
import org.jetbrains.annotations.NonNls;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

public class Updater {

    private static final @NonNls String clientManifest = "file://C:/Users/Robert/Desktop/IllarionClientManifest.json";
        //"https://illarion.org/media/java/update/IllarionClientManifest.json";

    public void updateClient() {
        ApplicationManifest manifest = downloadManifestFile();
        System.out.println("A");
    }

    private ApplicationManifest downloadManifestFile() {
        //try (InputStream inputStream = new URL(clientManifest).openStream()) {
        try {
            InputStream is = new FileInputStream("C:/Users/Robert/Desktop/IllarionClientManifest.json");
            return JsonIterator.deserialize(is.readAllBytes(), ApplicationManifest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
