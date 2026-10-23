# Spring Boot + MinIO REST Service

**Status:** Work-in-progress portfolio implementation. This repository also contains older experimental code; the new reference controller is under `src/main/java/dev/abhidubey/storage/`. CI currently runs Maven tests; verify integration with an actual MinIO server before production use.

## New API example
- `POST /api/files` — multipart `file` upload, returning a server-assigned UUID key
- `GET /api/files/{key}` — download as binary content
- Java 17, Spring Boot 3, MinIO Java SDK and GitHub Actions

## Configuration

Supply secrets through environment variables, **never commit credentials**:

```bash
export MINIO_ENDPOINT=http://localhost:9000
export MINIO_ACCESS_KEY=your-local-access-key
export MINIO_SECRET_KEY=your-local-secret-key
export MINIO_BUCKET=uploads
mvn test
mvn spring-boot:run
```

Create the bucket in your local MinIO instance first. Example request:

```bash
curl -F 'file=@sample.txt' http://localhost:8080/api/files
```

**Security limitations:** No authentication/authorization, malware scanning, size checks on downloads or structured S3 error handling yet. Do not expose this app to the public Internet. The implementation is an educational prototype, not a production-grade object-storage service.

## Development roadmap
1. Consolidate or isolate legacy classes from the earlier repository.
2. Add integration tests using an isolated MinIO instance.
3. Add authentication, structured error handling, content-disposition and metadata.
4. Validate CI build, Docker Compose and end-to-end uploads/downloads.
