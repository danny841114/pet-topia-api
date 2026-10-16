package petTopia.repository.vendor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import petTopia.model.vendor.VendorTagCertification;

import java.util.List;

public interface VendorTagCertificationRepository extends JpaRepository<VendorTagCertification, Integer> {
    boolean existsByVendorIdAndTagId(int vendorId, int certificationTagId);

    List<VendorTagCertification> findByVendorId(Integer vendorId);

    List<VendorTagCertification> findByVendorIdAndStatus(Integer vendorId, String status);

    @Query("SELECT vtc.tag.name FROM VendorTagCertification vtc WHERE vtc.vendor.id IN :vendorIds")
    List<String> findSlogansByVendorIds(@Param("vendorIds") List<Integer> vendorIds);

    @Query("""
            SELECT DISTINCT vtc.tag.name
            FROM VendorTagCertification vtc
            WHERE vtc.vendor.id = :vendorId
            AND vtc.status = '已認證'
            """)
    List<String> findCertifiedSlogansByVendorId(@Param("vendorId") Integer vendorId);
}
