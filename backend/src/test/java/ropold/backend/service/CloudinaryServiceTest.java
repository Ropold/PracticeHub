package ropold.backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CloudinaryServiceTest {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private Uploader uploader;

    @InjectMocks
    private CloudinaryService cloudinaryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Initialisierung von Mocks
        when(cloudinary.uploader()).thenReturn(uploader);  // Sicherstellen, dass der Uploader richtig gemockt wird
    }

    @Test
    void uploadImage_ValidImage_ReturnsSecureUrl() throws IOException {
        // Simuliere eine MultipartFile (Bilddatei)
        MultipartFile mockImage = mock(MultipartFile.class);
        when(mockImage.getOriginalFilename()).thenReturn("image.jpg");
        doAnswer(invocation -> {
            // Simuliere das Speichern der Datei
            ((File) invocation.getArgument(0)).createNewFile();
            return null;
        }).when(mockImage).transferTo(any(File.class));

        // Simuliere das Ergebnis des Cloudinary-Uploads
        Map<String, String> mockUploadResult = Map.of("secure_url", "https://example.com/image.jpg");
        when(uploader.upload(any(File.class), eq(Collections.emptyMap()))).thenReturn(mockUploadResult);

        // Teste die Methode
        String result = cloudinaryService.uploadImage(mockImage);

        // Verifiziere, dass das Ergebnis korrekt ist
        assertEquals("https://example.com/image.jpg", result);
        verify(uploader, times(1)).upload(any(File.class), eq(Collections.emptyMap()));  // Verifiziere, dass der Uploader aufgerufen wurde
    }

    @Test
    void uploadImage_ThrowsIOException_PropagatesException() throws IOException {
        // Simuliere einen Fehler beim Übertragen der Datei
        MultipartFile mockImage = mock(MultipartFile.class);
        when(mockImage.getOriginalFilename()).thenReturn("image.jpg");
        doThrow(IOException.class).when(mockImage).transferTo(any(File.class));

        // Teste, dass eine IOException korrekt weitergegeben wird
        assertThrows(IOException.class, () -> cloudinaryService.uploadImage(mockImage));
        verify(uploader, never()).upload(any(File.class), eq(Collections.emptyMap()));  // Verifiziere, dass der Uploader nicht aufgerufen wird
    }

    @Test
    void deleteImage_ValidImageUrl_DeletesImage() throws IOException {
        // Beispiel-URL für das Bild, das gelöscht werden soll
        String imageUrl = "https://res.cloudinary.com/demo/image/upload/v1614149342/sample.jpg";
        String expectedPublicId = "sample";

        // Simuliere das Ergebnis des Cloudinary-Löschens (wir brauchen hier keine Rückgabewerte)
        when(uploader.destroy(eq(expectedPublicId), eq(Collections.emptyMap()))).thenReturn(Collections.emptyMap());

        // Teste das Löschen
        cloudinaryService.deleteImage(imageUrl);

        // Verifiziere, dass der Uploader zum Löschen aufgerufen wurde
        verify(uploader, times(1)).destroy(eq(expectedPublicId), eq(Collections.emptyMap()));
    }


    @Test
    void deleteImage_DestroyThrowsIOException_ThrowsRuntimeException() throws IOException {
        // Beispiel-URL für das Bild
        String imageUrl = "https://res.cloudinary.com/demo/image/upload/v1614149342/sample.jpg";
        String expectedPublicId = "sample";

        // Simuliere einen Fehler beim Löschen des Bildes
        doThrow(IOException.class).when(uploader).destroy(eq(expectedPublicId), eq(Collections.emptyMap()));

        // Teste, dass eine RuntimeException geworfen wird, wenn das Löschen fehlschlägt
        RuntimeException exception = assertThrows(RuntimeException.class, () -> cloudinaryService.deleteImage(imageUrl));
        assertTrue(exception.getMessage().contains("Fehler beim Löschen des Bildes"));
        verify(uploader, times(1)).destroy(eq(expectedPublicId), eq(Collections.emptyMap()));  // Verifiziere, dass der Uploader zum Löschen aufgerufen wurde
    }
}
