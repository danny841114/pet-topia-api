package petTopia.controller.vendor_admin;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import petTopia.dto.vendor_admin.request.NotificationRequest;
import petTopia.model.vendor.ActivityRegistration;
import petTopia.service.vendor_admin.VendorActivityRegistrationService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/vendor_admin")
public class VendorActivityRegistrationController {
    private final VendorActivityRegistrationService vendorActivityRegistrationService;

    @GetMapping("/activity/registration")
    public ResponseEntity<List<ActivityRegistration>> getRegistrations(@RequestParam Integer activityId) {
        List<ActivityRegistration> registrations = vendorActivityRegistrationService.getRegistrationsByActivityId(activityId);
        return ResponseEntity.ok(registrations);
    }

    @PutMapping("/activity/confirmAll")
    public ResponseEntity<Void> confirmAllRegistrations(@RequestParam Integer activityId) {
        vendorActivityRegistrationService.confirmAllRegistrations(activityId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/registration/update/{id}")
    public ResponseEntity<ActivityRegistration> updateRegistrationStatus(@PathVariable Integer id, @RequestBody String status) {
        ActivityRegistration registration = vendorActivityRegistrationService.updateRegistrationStatus(id, status);
        return ResponseEntity.ok(registration);
    }

    @PutMapping("/registration/confirmById/{registrationId}")
    public ResponseEntity<Void> confirmRegistration(@PathVariable Integer registrationId) {
        vendorActivityRegistrationService.confirmRegistrationById(registrationId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/registration/cancelById/{registrationId}")
    public ResponseEntity<Void> cancelRegistration(@PathVariable Integer registrationId) {
        vendorActivityRegistrationService.cancelRegistrationById(registrationId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/registration/deleteById/{registrationId}")
    public ResponseEntity<Void> deleteRegistrationById(@PathVariable Integer registrationId) {
        vendorActivityRegistrationService.deleteRegistrationById(registrationId);
        return ResponseEntity.ok().build();
    }

    // TODO: Should check vendor afterwards
    @PostMapping("/registration/notification")
    public ResponseEntity<Void> sendNotification(@RequestBody NotificationRequest request) {
        vendorActivityRegistrationService.sendNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
