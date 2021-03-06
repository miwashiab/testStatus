package net.miwashi.teststatus.model;


import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import net.miwashi.config.ApplicationConfig;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(name = "RESULTS")
public class Result implements Comparable<Result>{

    @Column(name = "RESULT_ID")
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

    @Column(name = "REQUIREMENT_ID")
	private long requirementId;

    @OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    @JoinColumn(name = "PLATFORM_ID")
    private Platform platform;

    @OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    @JoinColumn(name = "BROWSER_ID")
    private Browser browser;
    
    @OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    @JoinColumn(name = "JOB_ID")
    private Job job;

    @Column(name = "TIMESTAMP")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date timeStamp;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private TestState status;

    @Column(name = "STARTTIME")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date startTime;

    @Column(name = "COMPLETIONTIME")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date completionTime;
    
    //@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    //@JoinColumn(name = "RESULT_ID", nullable = false)
    @Transient
    private List<Fail> fails = new ArrayList<Fail>();

    @Column(name = "OLD_KEY", length=1024)
	private String oldKey;
    
    public Result(){
        super();
    }

    public Result(String status){
    	setStatus(status);
        this.timeStamp = new Date();
    }
    
    public Result(long requirementId, String status) {
        this.requirementId = requirementId;
        setStatus(status);
    }
    
    public boolean isSuccess(){
    	return TestState.PASS.equals(status);
    }

    public boolean isSkip(){
    	return TestState.SKIP.equals(status);
    }
    
    public boolean isFail(){
    	return TestState.FAIL.equals(status);
    }
    
    public boolean isStarted(){
    	return TestState.STARTED.equals(status);
    }
    
    public boolean isUnknown(){
    	return TestState.UNKNOWN.equals(status);
    }

    

    public String toString(){
		return id + " - " + status;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

    public long getRequirementId() {
        return requirementId;
    }

    public void setRequirementId(long requirementId) {
        this.requirementId = requirementId;
    }

    public void setStatus(String status) {
    	this.status = TestState.UNKNOWN;
    	if("success".equalsIgnoreCase(status)){
    		this.status = TestState.PASS;
    	}
    	if("pass".equalsIgnoreCase(status)){
    		this.status = TestState.PASS;
    	}
    	if("fail".equalsIgnoreCase(status)){
    		this.status = TestState.FAIL;
    	}
    	if("failure".equalsIgnoreCase(status)){
    		this.status = TestState.FAIL;
    	}
    	if("failed".equalsIgnoreCase(status)){
    		this.status = TestState.FAIL;
    	}
    	if("skip".equalsIgnoreCase(status)){
    		this.status = TestState.SKIP;
    	}
    	if("started".equalsIgnoreCase(status)){
    		this.status = TestState.STARTED;
    	}
    }

    public TestState getStatus(){
        return status;
    }
    
    @JsonProperty("status")
    public void setStatus(TestState status){
        this.status = status;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Browser getBrowser() {
        return browser;
    }

    public void setBrowser(Browser browser) {
        this.browser = browser;
    }

    public void setJob(Job job) {
		this.job = job;
	}
    
    public Job getJob(){
    	return job;
    }
    
    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public Date getStartTime() {
        return startTime;
    }

    public String getStartTimeStr() {
        if(startTime==null){
            return "n/a";
        }
        return ApplicationConfig.TIME_STAMP.format(startTime);
    }

    @JsonProperty("startTime")
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public void setStartTime(DateTime startTime) {
        if(startTime==null){
            return;
        }
        this.startTime = startTime.toDate();
    }

    public Date getCompletionTime() {
        return completionTime;
    }

    @JsonProperty("completionTime")
    public void setCompletionTime(Date completionTime) {
        this.completionTime = completionTime;
    }

    public void setCompletionTime(DateTime completionTime) {
        if(completionTime==null){
            return;
        }
        this.completionTime = completionTime.toDate();
    }

    public long getDuration(){
        if((completionTime == null)||(startTime==null)){
            return 0;
        }
        return completionTime.getTime() - startTime.getTime();
    }

    @Override
    public int compareTo(Result comparedResult) {
        if((comparedResult==null)||(comparedResult.getStartTime()==null)||(this.getStartTime()==null)){
            return 0;
        }
        return this.getStartTime().compareTo(comparedResult.getStartTime());
    }
    
    public String getTouched() {
        if(startTime == null){
            return "not touched";
        }

        DateTime now = DateTime.now();
        DateTime thisTime = new DateTime(startTime.getTime());
        long duration = now.getMillis() - thisTime.getMillis();
        if(duration < 0){
            return "not touched";
        }
        long days = Math.floorDiv(duration, (1000 * 3600 * 24 ));
        duration = duration - days * (1000 * 3600 * 24 );
        long hours = Math.floorDiv(duration, (1000 * 3600 ));
        duration = duration - days * (1000 * 3600);
        long minutes = Math.floorDiv(duration, (1000 * 60 ));
        duration = duration - days * (1000 * 60);
        long seconds = Math.floorDiv(duration, (1000));

        if(days>0){
            return days + " days ago!";
        }

        if(hours>0){
            return hours + " hour, " + ((hours>1)?"s":"") + " minutes ago!";
        }

        if(minutes>0){
            return minutes + " minute" + ((minutes>1)?"s":"") + " ago!";
        }

        return seconds + " seconds ago!";
    }

	public String getOldKey() {
		return oldKey;
	}

	public void setOldKey(String oldKey) {
		this.oldKey = oldKey;
	}

	public List<Fail> getFails() {
		return fails;
	}

	public void add(Fail fail) {
		this.fails.add(fail);
	}
}
