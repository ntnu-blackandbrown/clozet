package stud.ntnu.no.backend.itemimage.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class LocalFileStorageServiceTest {

    @TempDir
    Path tempDir;

    private LocalFileStorageService fileStorageService;
    private MockMultipartFile testFile;

    @BeforeEach
    void setUp() {
        // Initialize service with temp directory
        fileStorageService = new LocalFileStorageService(tempDir.toString());
        
        // Create test file
        testFile = new MockMultipartFile(
            "test-file.jpg",
            "test-file.jpg",
            "image/jpeg",
            "test image content".getBytes()
        );
    }

    @AfterEach
    void tearDown() throws IOException {
        // Clean up any additional files if needed
        // Note: @TempDir will automatically clean up the temporary directory
    }

    @Test
    void storeFile_shouldStoreFileAndReturnPath() throws IOException {
        // Arrange
        Long itemId = 1L;

        // Act
        String result = fileStorageService.storeFile(testFile, itemId);

        // Assert
        assertNotNull(result);
        assertTrue(result.startsWith("/images/" + itemId + "/"));
        
        // Verify the file actually exists in the expected location
        String filename = result.substring(result.lastIndexOf("/") + 1);
        Path itemDir = tempDir.resolve(itemId.toString());
        Path storedFilePath = itemDir.resolve(filename);
        
        assertTrue(Files.exists(itemDir), "Item directory should exist");
        assertTrue(Files.exists(storedFilePath), "Stored file should exist");
        
        // Verify file content
        byte[] storedContent = Files.readAllBytes(storedFilePath);
        assertArrayEquals("test image content".getBytes(), storedContent);
    }

    @Test
    void storeFile_withSameItemId_shouldStoreInSameDirectory() throws IOException {
        // Arrange
        Long itemId = 2L;
        MockMultipartFile secondFile = new MockMultipartFile(
            "second-file.png",
            "second-file.png",
            "image/png",
            "second test content".getBytes()
        );

        // Act
        String firstResult = fileStorageService.storeFile(testFile, itemId);
        String secondResult = fileStorageService.storeFile(secondFile, itemId);

        // Assert
        assertNotNull(firstResult);
        assertNotNull(secondResult);
        assertNotEquals(firstResult, secondResult, "File paths should be different");
        
        assertTrue(firstResult.startsWith("/images/" + itemId + "/"));
        assertTrue(secondResult.startsWith("/images/" + itemId + "/"));
        
        // Verify both files exist in the same directory
        Path itemDir = tempDir.resolve(itemId.toString());
        assertTrue(Files.exists(itemDir), "Item directory should exist");
        assertEquals(2, Files.list(itemDir).count(), "Directory should contain 2 files");
    }

    @Test
    void storeFile_withFileWithoutExtension_shouldStoreCorrectly() throws IOException {
        // Arrange
        Long itemId = 3L;
        MockMultipartFile fileWithoutExtension = new MockMultipartFile(
            "file-without-extension",
            "file-without-extension",
            "image/jpeg",
            "content for file without extension".getBytes()
        );

        // Act
        String result = fileStorageService.storeFile(fileWithoutExtension, itemId);

        // Assert
        assertNotNull(result);
        assertTrue(result.startsWith("/images/" + itemId + "/"));
        
        // Verify the file exists
        String filename = result.substring(result.lastIndexOf("/") + 1);
        Path itemDir = tempDir.resolve(itemId.toString());
        Path storedFilePath = itemDir.resolve(filename);
        
        assertTrue(Files.exists(storedFilePath), "Stored file should exist");
        assertFalse(filename.contains("."), "Filename should not contain extension");
    }

    @Test
    void storeFile_withSpecialCharactersInFilename_shouldSanitize() throws IOException {
        // Arrange
        Long itemId = 4L;
        MockMultipartFile fileWithSpecialChars = new MockMultipartFile(
            "../../../dangerous-path.jpg",
            "../../../dangerous-path.jpg",
            "image/jpeg",
            "content with special chars in filename".getBytes()
        );

        // Act
        String result = fileStorageService.storeFile(fileWithSpecialChars, itemId);

        // Assert
        assertNotNull(result);
        assertTrue(result.startsWith("/images/" + itemId + "/"));
        
        // Verify the file exists in the correct location (not outside the temp dir)
        String filename = result.substring(result.lastIndexOf("/") + 1);
        Path itemDir = tempDir.resolve(itemId.toString());
        Path storedFilePath = itemDir.resolve(filename);
        
        assertTrue(Files.exists(storedFilePath), "Stored file should exist");
        
        // Verify no files were created outside the temp directory
        Path dangerousPath = Paths.get(tempDir.toString()).getParent().getParent().getParent();
        Path potentialDangerousFile = dangerousPath.resolve("dangerous-path.jpg");
        assertFalse(Files.exists(potentialDangerousFile), "File should not exist outside temp directory");
    }
} 