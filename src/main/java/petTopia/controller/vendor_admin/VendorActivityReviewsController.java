package petTopia.controller.vendor_admin;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import petTopia.dto.vendor.ActivityReviewDto;
import petTopia.service.vendor.VendorActivityReviewService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/vendor_admin")
public class VendorActivityReviewsController {
    private final VendorActivityReviewService vendorActivityReviewService;

    @GetMapping("/activity-reviews")
    public ResponseEntity<List<ActivityReviewDto>> getReviews(@RequestParam Integer activityId) {
        List<ActivityReviewDto> reviews = vendorActivityReviewService.getReviewsByActivityId(activityId);
        return ResponseEntity.ok(reviews);
    }

    @DeleteMapping("/activity-reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Integer reviewId) {
        vendorActivityReviewService.deleteReviewById(reviewId);
        return ResponseEntity.ok().build();
    }
}
