package petTopia.controller.vendor_admin;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import petTopia.model.vendor.ReviewPhoto;
import petTopia.model.vendor.VendorReview;
import petTopia.service.vendor_admin.VendorReviewsServiceAdmin;
import petTopia.util.HeadersUtil;

@Slf4j
@RequiredArgsConstructor
@RestController
public class VendorReviewsController {
    private final VendorReviewsServiceAdmin vendorReviewsService;

    @GetMapping("/api/vendor_admin/vendor-reviews/{reviewId}")
    public ResponseEntity<VendorReview> getReviewById(@PathVariable Integer reviewId) {
        VendorReview review = vendorReviewsService.getReviewById(reviewId);
        return ResponseEntity.ok(review);
    }

    @GetMapping("/api/vendor_admin/review")
    public ResponseEntity<List<VendorReview>> getReviewsByVendorId(@RequestParam Integer vendorId) {
        List<VendorReview> vendorReviews = vendorReviewsService.getReviewsByVendorId(vendorId);
        return ResponseEntity.ok(vendorReviews);
    }

    @GetMapping("/api/vendor_admin/review/photos/{reviewId}")
    public List<ReviewPhoto> getPhotosByReviewId(@PathVariable Integer reviewId) {
        return vendorReviewsService.getPhotosByReviewId(reviewId);
    }

    @GetMapping("/review_photos/ids")
    public ResponseEntity<List<Integer>> findPhotoIds(@RequestParam Integer vendorReviewId) {
        List<Integer> photoIds = vendorReviewsService.findPhotoIdsByReviewId(vendorReviewId);
        return ResponseEntity.ok(photoIds);
    }

    @GetMapping("/review_photos/download")
    public ResponseEntity<byte[]> downloadPhoto(@RequestParam Integer photoId) {
        byte[] photoByteArray = vendorReviewsService.downloadPhotoById(photoId);
        return ResponseEntity.ok()
                .headers(HeadersUtil.createHeadersWithMediaTypeJpg())
                .body(photoByteArray);
    }

    @DeleteMapping("/api/vendor_admin/vendor-reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Integer reviewId) {
        vendorReviewsService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }
}
