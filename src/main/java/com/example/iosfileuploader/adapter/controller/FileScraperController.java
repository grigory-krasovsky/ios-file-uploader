package com.example.iosfileuploader.adapter.controller;

//import com.example.iosfileuploader.core.service.FileScraper;
import com.example.iosfileuploader.core.service.FileScraper;
import com.example.iosfileuploader.core.service.FileTransferEngine;
import com.example.iosfileuploader.core.service.impl.FileTransferEngineImpl;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/scraper")
@AllArgsConstructor
public class FileScraperController {

    private final FileTransferEngineImpl fileTransferEngine;


    private final FileScraper fileScraper;

    @GetMapping("/scrape")
    public void scrape() throws IOException {
        fileScraper.getFile();
//        fileTransferEngine.uploadFile();
//        fileScraper.downloadFile("https://cvws.icloud-content.com/S/AemeoCQ6b3e2ZnlMsqoiip_zlHkN/camphoto_959030623.jpg?o=Ap5YgIT_r26tUIulLE4KKxcOBPu_8F_eTDFLN-4-6c_0&v=1&z=https%3A%2F%2Fp113-content.icloud.com%3A443&x=1&a=CAogdBg0aaICrgMX0vd1jNNO0QUjJM9tZ3cZecTCh5xfz9cSZRDciJmw7DIY3J-stewyIgEAUgTzlHkNaiW9L_KqQ0IJh2-wT8MKUCwPlCfsB-48bV0efdH4q3D2d856VR4TciVUEeoXEMdtqYZW0Yy0AbAgNddPRAFxgcQhm6c1mdcYVoSnv1M9&e=1747089821&r=8ec88f54-a5bc-4c50-9ac2-24dba67dfcf4-2&s=sr8QjZ-oz7NcBoI3eEx1fyUDcwM", "test.jpg");
    }
}
