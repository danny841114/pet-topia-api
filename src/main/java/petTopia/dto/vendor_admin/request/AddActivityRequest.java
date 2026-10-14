package petTopia.dto.vendor_admin.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;
import petTopia.model.vendor.ActivityType;

import java.util.Date;

@Getter
@Setter
public class AddActivityRequest {
    private Integer vendorId;
    private String activityName;
    private ActivityType typeId;
    private String description;
    private String address;
    private String isRegistrationRequired;
    private Integer maxParticipants;
    private MultipartFile[] files;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date endTime;
}
