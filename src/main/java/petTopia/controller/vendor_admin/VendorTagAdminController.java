package petTopia.controller.vendor_admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import petTopia.dto.vendor_admin.TagCertificationDto;
import petTopia.dto.vendor_admin.request.AddCertificationRequest;
import petTopia.dto.vendor_admin.request.UpdateCertificationRequest;
import petTopia.model.vendor.VendorTag;
import petTopia.service.vendor_admin.VendorTagAdminService;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/vendor_admin")
@RestController
public class VendorTagAdminController {
    private final VendorTagAdminService vendorTagAdminService;

    @GetMapping("/tags")
    public ResponseEntity<List<VendorTag>> getAllVendorTags() {
        List<VendorTag> vendorTags = vendorTagAdminService.getAllVendorTags();
        return ResponseEntity.ok(vendorTags);
    }

    @PostMapping("/certifications")
    public ResponseEntity<Void> addCertification(@RequestBody AddCertificationRequest request) {
        vendorTagAdminService.addCertification(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/vendors/{vendorId}/tags/{tagId}")
    public ResponseEntity<TagCertificationDto> getCertification(@PathVariable Integer vendorId, @PathVariable Integer tagId) {
        return vendorTagAdminService.getCertification(vendorId, tagId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/vendors/{vendorId}/certifications")
    public ResponseEntity<List<TagCertificationDto>> getCertificationsByVendorId(@PathVariable Integer vendorId) {
        List<TagCertificationDto> certifications = vendorTagAdminService.getCertificationsByVendorId(vendorId);
        return ResponseEntity.ok(certifications);
    }

    @DeleteMapping("/certifications/{certificationId}")
    public ResponseEntity<Void> cancelCertification(@PathVariable Integer certificationId) {
        vendorTagAdminService.cancelCertificationById(certificationId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/certifications/{certificationId}")
    public ResponseEntity<TagCertificationDto> updateCertification(@PathVariable Integer certificationId,
                                                                   @RequestBody UpdateCertificationRequest request) {
        TagCertificationDto updatedCertification = vendorTagAdminService.updateCertification(certificationId, request);
        return ResponseEntity.ok(updatedCertification);
    }
}
