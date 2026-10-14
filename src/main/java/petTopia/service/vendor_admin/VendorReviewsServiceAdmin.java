package petTopia.service.vendor_admin;

import java.util.*;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import petTopia.model.vendor.ReviewPhoto;
import petTopia.model.vendor.VendorReview;
import petTopia.repository.user.MemberRepository;
import petTopia.repository.vendor.ReviewPhotoRepository;
import petTopia.repository.vendor.VendorRepository;
import petTopia.repository.vendor.VendorReviewRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class VendorReviewsServiceAdmin {
    private final VendorReviewRepository vendorReviewRepository;
    private final ReviewPhotoRepository reviewPhotoRepository;
    private final MemberRepository memberRepository;
    private final VendorRepository vendorRepository;

    public VendorReview getReviewById(Integer reviewId) {
        return vendorReviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review ID '%s' not found".formatted(reviewId)));
    }

    public List<VendorReview> getReviewsByVendorId(Integer vendorId) {
        return vendorReviewRepository.findByVendorId(vendorId);
    }

    public List<ReviewPhoto> getPhotosByReviewId(Integer reviewId) {
        return reviewPhotoRepository.findByVendorReviewId(reviewId);
    }

    @Transactional
    public void deleteReview(Integer reviewId) {
        vendorReviewRepository.findById(reviewId)
                .ifPresent(vendorReviewRepository::delete);
    }

    public List<Integer> findPhotoIdsByReviewId(Integer reviewId) {
        List<Integer> photoIds = new ArrayList<>();

        vendorReviewRepository.findById(reviewId)
                .map(VendorReview::getReviewPhotos)
                .stream()
                .flatMap(List::stream)
                .forEach(photo -> photoIds.add(photo.getId()));

        return photoIds;
    }

    public byte[] downloadPhotoById(@RequestParam Integer photoId) {
        return reviewPhotoRepository.findById(photoId)
                .map(ReviewPhoto::getPhoto)
                .orElseThrow(() -> new EntityNotFoundException("Photo byte array not found"));
    }
}
