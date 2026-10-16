package petTopia.repository.vendor;

import org.springframework.data.jpa.repository.JpaRepository;
import petTopia.model.vendor.VendorTag;

public interface VendorTagRepository extends JpaRepository<VendorTag, Integer> {
}
