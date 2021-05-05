/*
 * This file is part of the gsyn
 *
 * (c) Newclass
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package pl.newclass.gsync.provider;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import pl.newclass.gsync.IBackupProvider;

/**
 * @author Michal Tomczak <michal.tomczak@newclass.pl>
 */
public class GoogleDriveBackupProvider implements IBackupProvider {

  private static final String APPLICATION_NAME = "GSynd";
  private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
  private final String credentialPath;
  private final String tokenPath;
  private final Integer authPort;
  private static final List<String> SCOPES = new ArrayList<>();
  private static final String MIMETYPE_DIR = "application/vnd.google-apps.folder";

  public GoogleDriveBackupProvider(String credentialPath, String tokenPath, Integer authPort) {
    this.credentialPath = credentialPath;
    this.tokenPath = tokenPath;
    this.authPort = authPort;
    SCOPES.add(DriveScopes.DRIVE_FILE);
    SCOPES.add(DriveScopes.DRIVE_METADATA_READONLY);
  }

  public Drive connect() throws GeneralSecurityException, IOException {
    final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
        .setApplicationName(APPLICATION_NAME)
        .build();
  }

  @Override
  public void send(File file, String path) throws IOException {
    try {
      var service = connect();
      List<String> parts = Arrays.stream(path.split("/")).collect(Collectors.toList());
      String fileName = parts.remove(parts.size() - 1);

      var parentId = getParentId(service, parts.toArray(new String[0]));
      var googleFile = new com.google.api.services.drive.model.File();
      googleFile.setName(fileName).setParents(Collections.singletonList(parentId));
      googleFile.setModifiedTime(new DateTime(file.lastModified()));

      FileContent fileContent = null;
      if (file.isDirectory()) {
        googleFile.setMimeType(MIMETYPE_DIR);
      } else {
        googleFile.setMimeType("application/vnd.google-apps.file"); //fixme detect mimetype
        fileContent = new FileContent("text/html", file);
      }

      com.google.api.services.drive.model.File result = null;
      if (null == fileContent) {
        result = service.files().create(googleFile).execute();
      } else {
        result = service.files().create(googleFile, fileContent).execute();
      }

      System.err.println(path);
    } catch (GeneralSecurityException e) {
      throw new IOException(e);
    }
  }

  private String getParentId(Drive service, String[] parts) throws IOException {
    var parentId = "root";
    for (String part : parts) {
      if (part.equals("")) {
        continue;
      }

      var dirId = getDirId(service, part, parentId);

      if (null == dirId) {
        dirId = createDir(service, part, parentId);
      }

      parentId = dirId;
    }

    return parentId;
  }

  private String createDir(Drive service, String name, String parentId) throws IOException {
    var googleFile = new com.google.api.services.drive.model.File();
    googleFile.setName(name).setParents(Collections.singletonList(parentId))
        .setMimeType(MIMETYPE_DIR);
    var file = service.files().create(googleFile).execute();

    return file.getId();
  }

  private String getDirId(Drive service, String name, String parentId) throws IOException {
    var result = service.files().list().setQ(
        String.format(
            "'%s' in parents and mimeType = '%s' and name = '%s'"
            , parentId, MIMETYPE_DIR, name))
        .setFields("files(id)").execute();
    List<com.google.api.services.drive.model.File> files = result.getFiles();
    if (files == null || files.isEmpty()) {
      return null;
    }

    return files.get(0).getId();
  }

  private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
      throws IOException {

    InputStream in = new FileInputStream(credentialPath);
    var clientSecrets = GoogleClientSecrets
        .load(JSON_FACTORY, new InputStreamReader(in));

    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
        .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(tokenPath)))
        .setAccessType("offline")
        .build();
    LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(authPort).build();
    return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
  }
}
