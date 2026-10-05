package jobplatform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;




//dto == Data Transfer Object, takes user input, the job type, and will feed this to our JSON later.
public class CreateJobRequest {
    @NotBlank(message ="type is required!")
    @Size (max=50, message ="type must be 50 characters or fewer")
    //validation for blank and excessive job types
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}