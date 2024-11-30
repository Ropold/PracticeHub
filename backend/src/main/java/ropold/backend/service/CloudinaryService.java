package ropold.backend.service;

import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile image) throws IOException {
        File fileToUpload = File.createTempFile("practice-hub", "");
        image.transferTo(fileToUpload);

        // Upload durchführen und URL extrahieren
        Map uploadResult = cloudinary.uploader().upload(fileToUpload, Collections.emptyMap());
        return (String) uploadResult.get("url");
    }
}
