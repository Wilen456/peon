package jobplatform.dto;
//dto == Data Transfer Object, takes user input, the job type, and will feed this to our JSON later.
public class CreateJobRequest {

    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}