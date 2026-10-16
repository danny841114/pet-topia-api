package petTopia.controller.vendor_admin;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import petTopia.dto.vendor_admin.TagCertificationDto;
import petTopia.service.vendor_admin.VendorTagAdminService;

@RequiredArgsConstructor
@RestController
public class ManageCertificationController {
    private final VendorTagAdminService vendorTagAdminService;

    @GetMapping("/api/admin/certification")
    public ResponseEntity<List<TagCertificationDto>> getAllCertificationsWithTags() {
        List<TagCertificationDto> vendorTags = vendorTagAdminService.getVendorTags();
        return ResponseEntity.ok(vendorTags);
    }

    @PutMapping("/api/admin/certification/status/update/{certificationId}")
    public ResponseEntity<TagCertificationDto> updateCertificationStatus(@PathVariable Integer certificationId,
                                                                         @RequestParam String status,
                                                                         @RequestParam String reason) {
        TagCertificationDto updatedCertification = vendorTagAdminService.updateCertificationStatus(certificationId, status, reason);
        return ResponseEntity.ok(updatedCertification);
    }
}
