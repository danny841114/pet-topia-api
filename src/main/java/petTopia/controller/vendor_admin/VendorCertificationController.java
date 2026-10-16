//package petTopia.controller.vendor_admin;
//
//import java.util.List;
//import java.util.Map;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import petTopia.dto.vendor_admin.TagCertificationDto;
//import petTopia.model.vendor.VendorTag;
//import petTopia.service.vendor_admin.VendorTagAdminService;
//
//@RequiredArgsConstructor
//@RestController
//public class VendorCertificationController {
//    private final VendorTagAdminService vendorTagAdminService;
//
//    @PostMapping("/api/vendor_admin/certification/add")
//    public ResponseEntity<String> AddCertification(@RequestParam Integer vendorId, @RequestParam Integer tagId) {
//        vendorTagAdminService.createVendorCertificationWithTag(vendorId, tagId);
//        return ResponseEntity.ok().build();
//    }
//
//    // TODO: Response body should be modified
//    @GetMapping("/api/vendor_admin/certification/exists/{vendorId}/{certificationTagId}")
//    public ResponseEntity<Map<String, Object>> checkIfExists(@PathVariable Integer vendorId,
//                                                             @PathVariable Integer certificationTagId) {
//        boolean exists = vendorTagAdminService.checkIfExists(vendorId, certificationTagId);
//        return ResponseEntity.ok(Map.of("exists", exists));
//    }
//
//    @GetMapping("/api/certification_type/all")
//    public ResponseEntity<List<VendorTag>> getAllCertificationTypes() {
//        List<VendorTag> certificationTypes = vendorTagAdminService.getAllVendorTags();
//        return ResponseEntity.ok(certificationTypes);
//    }
//
//    @GetMapping("/api/vendor_admin/certification/{vendorId}")
//    public ResponseEntity<List<TagCertificationDto>> getCertificationsByVendorId(@PathVariable Integer vendorId) {
//        List<TagCertificationDto> certifications = vendorTagAdminService.getCertificationsByVendorId(vendorId);
//        return ResponseEntity.ok(certifications);
//    }
//
//    @DeleteMapping("/api/vendor_admin/certification/delete/{certificationId}")
//    public ResponseEntity<Void> cancelCertification(@PathVariable Integer certificationId) {
//        vendorTagAdminService.cancelCertificationById(certificationId);
//        return ResponseEntity.ok().build();
//    }
//}
