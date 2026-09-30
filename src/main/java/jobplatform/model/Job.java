package jobplatform.model;

public class Job {

        private long id;
        private String type;
        private String status;

        //boilerplate get/setters + default constructor. Note that status is auto-assigned
        public void setStatus(String newStatus){
            this.status = newStatus;
        }
        
        public long getId(){
            return id;
        }
        public String getType(){
            return type;
        }
        public String getStatus(){
            return status;
        }
        
        public Job(long id, String type){
            this.id = id;
            this.type = type;
            this.status = "QUEUED";
        }

}
