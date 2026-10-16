package petTopia.service.vendor_admin;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petTopia.dto.vendor_admin.TagCertificationDto;
import petTopia.model.vendor.VendorTag;
import petTopia.model.vendor.Vendor;
import petTopia.model.vendor.VendorTagCertification;
import petTopia.repository.vendor.VendorRepository;
import petTopia.repository.vendor.VendorReviewRepository;
import petTopia.repository.vendor.VendorTagCertificationRepository;
import petTopia.repository.vendor.VendorTagRepository;

import java.util.*;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class VendorTagAdminService {
    private final VendorTagCertificationRepository vendorTagCertificationRepository;
    private final VendorTagRepository vendorTagRepository;
    private final VendorRepository vendorRepository;
    private final VendorReviewRepository vendorReviewRepository;

    public boolean checkIfExists(int vendorId, int certificationTagId) {
        return vendorTagCertificationRepository.existsByVendorIdAndTagId(vendorId, certificationTagId);
    }

    @Transactional
    public void createVendorCertificationWithTag(Integer vendorId, Integer tagId) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new EntityNotFoundException("Vendor not found"));
        VendorTag tag = vendorTagRepository.findById(tagId)
                .orElseThrow(() -> new EntityNotFoundException("Certification Tag not found"));

        VendorTagCertification vendorTagCertification = new VendorTagCertification();
        vendorTagCertification.setVendor(vendor);
        vendorTagCertification.setStatus("申請中");
        vendorTagCertification.setRequestDate(new Date());
        vendorTagCertificationRepository.save(vendorTagCertification);

        VendorTag vendorTag = vendorTagRepository.findById(tagId)
                .orElseThrow(() -> new EntityNotFoundException("Certification tag not found"));

        boolean meetsStandard = checkMeetsStandard(vendorId, vendorTag);

        vendorTagCertification.setMeetsStandard(meetsStandard);
        vendorTagCertificationRepository.save(vendorTagCertification);
    }

    public List<VendorTag> getAllVendorTags() {
        return vendorTagRepository.findAll();
    }

    public List<TagCertificationDto> getVendorTags() {
        List<VendorTagCertification> certifications = vendorTagCertificationRepository.findAll();
        return certifications.stream().map(TagCertificationDto::fromEntity).toList();
    }

    @Transactional
    public TagCertificationDto updateCertificationStatus(Integer certificationId, String status, String reason) {
        VendorTagCertification certification = vendorTagCertificationRepository.findById(certificationId)
                .orElseThrow(() -> new EntityNotFoundException("Certification not found with id: " + certificationId));

        certification.setStatus(status);
        certification.setReason(reason);
        certification.setApprovedDate(new Date());
        VendorTagCertification updatedCertification = vendorTagCertificationRepository.save(certification);

        return TagCertificationDto.fromEntity(updatedCertification);
    }

    @Transactional
    public void cancelCertificationById(Integer certificationId) {
        if (vendorTagCertificationRepository.existsById(certificationId)) {
            vendorTagCertificationRepository.deleteById(certificationId);
        }
    }

    public List<TagCertificationDto> getCertificationsByVendorId(Integer vendorId) {
        return vendorTagCertificationRepository.findByVendorId(vendorId)
                .stream()
                .map(TagCertificationDto::fromEntity)
                .collect(Collectors.toList());
    }

    private boolean checkMeetsStandard(Integer vendorId, VendorTag tag) {
        String keywordsStr = tag.getKeywords();
        if (keywordsStr == null || keywordsStr.isBlank()) return false;

        List<String> keywords = Arrays.asList(keywordsStr.split(","));
        while (keywords.size() < 5) {
            keywords.add(""); // 避免 SQL 查詢錯誤
        }

        int matchingReviewCount = vendorReviewRepository.countMatchingReviews(
                vendorId,
                keywords.get(0),
                keywords.get(1),
                keywords.get(2),
                keywords.get(3),
                keywords.get(4)
        );

        return matchingReviewCount >= 3; // 例如至少 10 則符合評論才算合格
    }
}
