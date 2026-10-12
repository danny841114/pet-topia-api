package petTopia.dto.vendor.response;

import lombok.*;
import petTopia.model.vendor.VendorReview;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorReviewInfo {
    private Integer id;
    private String content;
    private Date time;
    private Integer ratingEnv;
    private Integer ratingPrice;
    private Integer ratingService;
    private List<String> photoUrls;
    private MemberInfo member;
    private VendorInfo vendor;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberInfo {
        private Integer id;
        private String name;
        private String photoUrl;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VendorInfo {
        private Integer id;
        private String name;
    }

    public static VendorReviewInfo fromEntity(VendorReview review) {
        Integer memberId = review.getMember().getId();
        MemberInfo memberInfo = MemberInfo.builder()
                .id(memberId)
                .name(review.getMember().getName())
                .photoUrl("/api/member/" + memberId + "/photo")
                .build();

        Integer vendorId = review.getVendor().getId();
        String vendorName = review.getVendor().getName();
        VendorInfo vendorInfo = VendorInfo.builder()
                .id(vendorId)
                .name(vendorName)
                .build();

        String endpoint = "/api/vendor/" + vendorId + "/review/" + review.getId() + "/photo/";
        List<String> photoUrls = review.getReviewPhotos()
                .stream()
                .map(photo -> endpoint + photo.getId())
                .toList();

        return VendorReviewInfo.builder()
                .id(review.getId())
                .time(review.getReviewTime())
                .content(review.getReviewContent())
                .ratingEnv(review.getRatingEnvironment())
                .ratingPrice(review.getRatingPrice())
                .ratingService(review.getRatingService())
                .photoUrls(photoUrls)
                .member(memberInfo)
                .vendor(vendorInfo)
                .build();
    }
}
