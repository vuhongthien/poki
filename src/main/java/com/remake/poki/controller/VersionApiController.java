package com.remake.poki.controller;

import com.remake.poki.model.Version;
import com.remake.poki.repo.VersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VersionApiController {

    private final VersionRepository versionRepository;

    /**
     * API lấy thông tin version mới nhất
     * GET /api/version/latest
     */
    @GetMapping("/version/latest")
    public ResponseEntity<Map<String, Object>> getLatestVersion() {
        Map<String, Object> response = new HashMap<>();

        try {
            Version version = versionRepository.findFirstByOrderByIdDesc()
                    .orElse(null);

            if (version != null) {
                response.put("success", true);
                response.put("version", version.getVersion());
                response.put("linkDownload", version.getLinkDownload());
                response.put("bank", version.getBank());
                response.put("number", version.getNumber());
                response.put("name", version.getName());

                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Không tìm thấy thông tin version");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi lấy thông tin version: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * API lấy thông tin version theo version string
     * GET /api/version/{versionName}
     */
    @GetMapping("/version/{versionName}")
    public ResponseEntity<Map<String, Object>> getVersionByName(@PathVariable String versionName) {
        Map<String, Object> response = new HashMap<>();

        try {
            Version version = versionRepository.findByVersion(versionName)
                    .orElse(null);

            if (version != null) {
                response.put("success", true);
                response.put("version", version.getVersion());
                response.put("linkDownload", version.getLinkDownload());
                response.put("bank", version.getBank());
                response.put("number", version.getNumber());
                response.put("name", version.getName());

                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Không tìm thấy version: " + versionName);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi lấy thông tin version: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * API lấy thông tin ngân hàng (bank info)
     * GET /api/bank-info
     */
    @GetMapping("/bank-info")
    public ResponseEntity<Map<String, Object>> getBankInfo() {
        Map<String, Object> response = new HashMap<>();

        try {
            Version version = versionRepository.findFirstByOrderByIdDesc()
                    .orElse(null);

            if (version != null) {
                response.put("success", true);
                response.put("bank", version.getBank());
                response.put("number", version.getNumber());
                response.put("name", version.getName());

                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Không tìm thấy thông tin ngân hàng");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi lấy thông tin ngân hàng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * API lấy link download
     * GET /api/download-link
     */
    @GetMapping("/download-link")
    public ResponseEntity<Map<String, Object>> getDownloadLink() {
        Map<String, Object> response = new HashMap<>();

        try {
            Version version = versionRepository.findFirstByOrderByIdDesc()
                    .orElse(null);

            if (version != null) {
                response.put("success", true);
                response.put("version", version.getVersion());
                response.put("linkDownload", version.getLinkDownload());

                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Không tìm thấy link download");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi lấy link download: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}