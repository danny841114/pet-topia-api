package petTopia.repository.vendor;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import petTopia.model.vendor.VendorReview;

public interface VendorReviewRepository extends JpaRepository<VendorReview, Integer> {
    /* 尋找單一店家其所有的評分及留言 */
    List<VendorReview> findByVendorId(Integer vendorId);

    /* 尋找單一會員對單一店家的評分及留言 */
    Optional<VendorReview> findFirstByMemberIdAndVendorId(Integer memberId, Integer vendorId); // 有多筆時僅回傳第一筆

    /* 尋找單一會員對單一活動的評論 */
    List<VendorReview> findByMemberIdAndVendorId(Integer memberId, Integer vendorId);

    /* 尋找單一會員其所有的評分及留言 */
    List<VendorReview> findByMemberId(Integer memberId);

    @Query("""
        SELECT COUNT(vr) FROM VendorReview vr
        WHERE vr.vendor.id = :vendorId
        AND (
            (:keyword1 IS NOT NULL AND :keyword1 != '' AND vr.reviewContent LIKE CONCAT('%', :keyword1, '%')) OR
            (:keyword2 IS NOT NULL AND :keyword2 != '' AND vr.reviewContent LIKE CONCAT('%', :keyword2, '%')) OR
            (:keyword3 IS NOT NULL AND :keyword3 != '' AND vr.reviewContent LIKE CONCAT('%', :keyword3, '%')) OR
            (:keyword4 IS NOT NULL AND :keyword4 != '' AND vr.reviewContent LIKE CONCAT('%', :keyword4, '%')) OR
            (:keyword5 IS NOT NULL AND :keyword5 != '' AND vr.reviewContent LIKE CONCAT('%', :keyword5, '%'))
        )
        """)
    int countMatchingReviews(@Param("vendorId") Integer vendorId,
                             @Param("keyword1") String keyword1,
                             @Param("keyword2") String keyword2,
                             @Param("keyword3") String keyword3,
                             @Param("keyword4") String keyword4,
                             @Param("keyword5") String keyword5);
}
