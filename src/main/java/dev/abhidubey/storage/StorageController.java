package dev.abhidubey.storage;

import io.minio.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
public class StorageController {
  private final MinioClient minio;
  private final String bucket;
  public StorageController(@Value("${app.minio.endpoint}") String endpoint,
      @Value("${app.minio.access-key}") String accessKey,
      @Value("${app.minio.secret-key}") String secretKey,
      @Value("${app.minio.bucket}") String bucket) {
    if (accessKey.isBlank() || secretKey.isBlank()) throw new IllegalArgumentException("Configure MINIO_ACCESS_KEY and MINIO_SECRET_KEY");
    this.minio = MinioClient.builder().endpoint(endpoint).credentials(accessKey, secretKey).build();
    this.bucket = bucket;
  }
  @PostMapping(consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) throws Exception {
    if (file.isEmpty()) return ResponseEntity.badRequest().body("File must not be empty");
    String key = UUID.randomUUID().toString(); // server-generated key prevents path traversal
    try(InputStream in=file.getInputStream()) {
      minio.putObject(PutObjectArgs.builder().bucket(bucket).object(key).stream(in,file.getSize(),-1)
          .contentType(file.getContentType()==null?"application/octet-stream":file.getContentType()).build());
    }
    return ResponseEntity.status(HttpStatus.CREATED).body(key);
  }
  @GetMapping("/{key}")
  public ResponseEntity<byte[]> download(@PathVariable String key) throws Exception {
    if(!key.matches("[0-9a-fA-F-]{36}")) return ResponseEntity.badRequest().build();
    try(InputStream in=minio.getObject(GetObjectArgs.builder().bucket(bucket).object(key).build())) {
      return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(in.readAllBytes());
    }
  }
}
