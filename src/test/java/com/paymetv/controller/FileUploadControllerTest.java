package com.paymetv.controller;

import com.paymetv.app.AppApplication;
import com.paymetv.app.service.FileUploadService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.CompletableFuture;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for FileUploadController.
 *
 * Tests all endpoints exposed by the controller:
 * - GET /api/files/upload - Redirect to upload UI
 * - POST /api/files/upload - File upload with validation
 *
 * Uses MockMvc for testing REST endpoints and mocks KafkaTemplate
 * to avoid requiring a real Kafka broker during tests.
 */
@SpringBootTest(classes = AppApplication.class)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("FileUploadControllerTest")
@WithMockUser  // Provides authenticated user context for Spring Security
public class FileUploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FileUploadService fileUploadService;

    @MockitoBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    @Order(1)
    @DisplayName("context loads successfully")
    void contextLoads() {
        // Verify that Spring context loads without errors
    }

    @Test
    @Order(2)
    @DisplayName("GET /api/files/upload redirects to /upload UI")
    void testRedirectToUploadUI() throws Exception {
        mockMvc.perform(get("/api/files/upload"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/upload"));
    }

    @Test
    @Order(3)
    @DisplayName("upload valid JPEG file returns 200 with metadata")
    void testUploadValidJpegFile() throws Exception {
        // Create a small valid JPEG file
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "photo.jpg",
                "image/jpeg",
                new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0} // JPEG header
        );

        // Mock Kafka to avoid actual message sending
        when(kafkaTemplate.send(anyString(), any())).thenReturn(CompletableFuture.completedFuture(null));

        mockMvc.perform(multipart("/api/files/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("uploaded"))
                .andExpect(jsonPath("$.originalName").value("photo.jpg"))
                .andExpect(jsonPath("$.storedName").exists())
                .andExpect(jsonPath("$.size").value(4))
                .andExpect(jsonPath("$.contentType").value("image/jpeg"))
                .andExpect(jsonPath("$.path").exists())
                .andExpect(jsonPath("$.timestamp").exists());

        // Verify Kafka message was sent
        verify(kafkaTemplate, times(1)).send(eq("file-uploaded"), anyString());
    }

    @Test
    @Order(4)
    @DisplayName("upload valid PNG file returns 200 with metadata")
    void testUploadValidPngFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "image.png",
                "image/png",
                new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47} // PNG header
        );

        when(kafkaTemplate.send(anyString(), any())).thenReturn(CompletableFuture.completedFuture(null));

        mockMvc.perform(multipart("/api/files/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("uploaded"))
                .andExpect(jsonPath("$.originalName").value("image.png"))
                .andExpect(jsonPath("$.contentType").value("image/png"));

        verify(kafkaTemplate, times(1)).send(eq("file-uploaded"), anyString());
    }

    @Test
    @Order(5)
    @DisplayName("upload valid PDF file returns 200 with metadata")
    void testUploadValidPdfFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "document.pdf",
                "application/pdf",
                "%PDF-1.4".getBytes()
        );

        when(kafkaTemplate.send(anyString(), any())).thenReturn(CompletableFuture.completedFuture(null));

        mockMvc.perform(multipart("/api/files/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("uploaded"))
                .andExpect(jsonPath("$.originalName").value("document.pdf"))
                .andExpect(jsonPath("$.contentType").value("application/pdf"));

        verify(kafkaTemplate, times(1)).send(eq("file-uploaded"), anyString());
    }

    @Test
    @Order(6)
    @DisplayName("upload valid MP4 video file returns 200 with metadata")
    void testUploadValidMp4File() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "video.mp4",
                "video/mp4",
                new byte[]{0x00, 0x00, 0x00, 0x18, 0x66, 0x74, 0x79, 0x70} // MP4 header
        );

        when(kafkaTemplate.send(anyString(), any())).thenReturn(CompletableFuture.completedFuture(null));

        mockMvc.perform(multipart("/api/files/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("uploaded"))
                .andExpect(jsonPath("$.originalName").value("video.mp4"))
                .andExpect(jsonPath("$.contentType").value("video/mp4"));

        verify(kafkaTemplate, times(1)).send(eq("file-uploaded"), anyString());
    }


    @Test
    @Order(7)
    @DisplayName("upload empty file returns 400 Bad Request")
    void testUploadEmptyFile() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.jpg",
                "image/jpeg",
                new byte[0] // Empty file
        );

        mockMvc.perform(multipart("/api/files/upload").file(emptyFile))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value(containsString("empty")))
                .andExpect(jsonPath("$.timestamp").exists());

        // Verify Kafka was NOT called for failed upload
        verify(kafkaTemplate, never()).send(anyString(), any());
    }

    @Test
    @Order(8)
    @DisplayName("upload file with disallowed MIME type returns 400 Bad Request")
    void testUploadInvalidMimeType() throws Exception {
        MockMultipartFile textFile = new MockMultipartFile(
                "file",
                "script.sh",
                "text/plain", // Not in allowed types
                "echo hello".getBytes()
        );

        mockMvc.perform(multipart("/api/files/upload").file(textFile))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value(containsString("not allowed")))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(kafkaTemplate, never()).send(anyString(), any());
    }

    @Test
    @Order(9)
    @DisplayName("upload file exceeding size limit returns 400 Bad Request")
    void testUploadFileTooLarge() throws Exception {
        // Create a file larger than 2MB (MAX_FILE_SIZE_BYTES)
        byte[] largeContent = new byte[3 * 1024 * 1024]; // 3 MB
        MockMultipartFile largeFile = new MockMultipartFile(
                "file",
                "large-image.jpg",
                "image/jpeg",
                largeContent
        );

        mockMvc.perform(multipart("/api/files/upload").file(largeFile))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value(containsString("exceeds")))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(kafkaTemplate, never()).send(anyString(), any());
    }

    @Test
    @Order(10)
    @DisplayName("upload file with null content type returns 400 Bad Request")
    void testUploadFileWithNullContentType() throws Exception {
        MockMultipartFile fileWithNullType = new MockMultipartFile(
                "file",
                "unknown.bin",
                null, // Null content type
                new byte[]{1, 2, 3}
        );

        mockMvc.perform(multipart("/api/files/upload").file(fileWithNullType))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value(containsString("not allowed")))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(kafkaTemplate, never()).send(anyString(), any());
    }

    @Test
    @Order(11)
    @DisplayName("upload file with executable MIME type returns 400 Bad Request")
    void testUploadExecutableFile() throws Exception {
        MockMultipartFile execFile = new MockMultipartFile(
                "file",
                "malware.exe",
                "application/x-msdownload",
                new byte[]{0x4D, 0x5A} // EXE header
        );

        mockMvc.perform(multipart("/api/files/upload").file(execFile))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value(containsString("not allowed")));

        verify(kafkaTemplate, never()).send(anyString(), any());
    }

    @Test
    @Order(12)
    @DisplayName("upload GIF file returns 200 with metadata")
    void testUploadValidGifFile() throws Exception {
        MockMultipartFile gifFile = new MockMultipartFile(
                "file",
                "animation.gif",
                "image/gif",
                new byte[]{0x47, 0x49, 0x46, 0x38, 0x39, 0x61} // GIF89a header
        );

        when(kafkaTemplate.send(anyString(), any())).thenReturn(CompletableFuture.completedFuture(null));

        mockMvc.perform(multipart("/api/files/upload").file(gifFile))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("uploaded"))
                .andExpect(jsonPath("$.originalName").value("animation.gif"))
                .andExpect(jsonPath("$.contentType").value("image/gif"));

        verify(kafkaTemplate, times(1)).send(eq("file-uploaded"), anyString());
    }

    @Test
    @Order(13)
    @DisplayName("upload WebP file returns 200 with metadata")
    void testUploadValidWebPFile() throws Exception {
        MockMultipartFile webpFile = new MockMultipartFile(
                "file",
                "modern-image.webp",
                "image/webp",
                new byte[]{0x52, 0x49, 0x46, 0x46} // RIFF header (WebP)
        );

        when(kafkaTemplate.send(anyString(), any())).thenReturn(CompletableFuture.completedFuture(null));

        mockMvc.perform(multipart("/api/files/upload").file(webpFile))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("uploaded"))
                .andExpect(jsonPath("$.originalName").value("modern-image.webp"))
                .andExpect(jsonPath("$.contentType").value("image/webp"));

        verify(kafkaTemplate, times(1)).send(eq("file-uploaded"), anyString());
    }

    @Test
    @Order(14)
    @DisplayName("upload MPEG video file returns 200 with metadata")
    void testUploadValidMpegFile() throws Exception {
        MockMultipartFile mpegFile = new MockMultipartFile(
                "file",
                "video.mpeg",
                "video/mpeg",
                new byte[]{0x00, 0x00, 0x01, (byte) 0xBA} // MPEG header
        );

        when(kafkaTemplate.send(anyString(), any())).thenReturn(CompletableFuture.completedFuture(null));

        mockMvc.perform(multipart("/api/files/upload").file(mpegFile))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("uploaded"))
                .andExpect(jsonPath("$.originalName").value("video.mpeg"))
                .andExpect(jsonPath("$.contentType").value("video/mpeg"));

        verify(kafkaTemplate, times(1)).send(eq("file-uploaded"), anyString());
    }
}
