package petTopia.service.vendor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petTopia.model.vendor.VendorTagCertification;
import petTopia.repository.vendor.VendorTagCertificationRepository;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class VendorTagService {
    private final VendorTagCertificationRepository vendorTagCertificationRepository;

    public List<VendorTagCertification> findConfirmedTagByVendorId(Integer vendorId) {
        return vendorTagCertificationRepository.findByVendorIdAndStatus(vendorId, "已認證");
    }
}
