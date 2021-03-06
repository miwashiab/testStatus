package net.miwashi.teststatus.model;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true, value = { "requirements" })
@Entity
public class Subject {

    @Column(name = "SUBJECT_ID")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date lastTested;
    
    public Date getLastTested() {
		return lastTested;
	}

	public void setLastTested(Date lastTested) {
		this.lastTested = lastTested;
	}

	@Column(unique = true)
    private String key;

    private String name;

    @Transient
    private String group;

    @Transient
    private Map<String, Requirement> requirements = new HashMap<String,Requirement>();

    public Subject() {
        super();
    }

    public Subject(String name, String key) {
        super();
        this.name = name;
        this.key = key;
    }

    public long getId() {
		return id;
	}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Requirement> getRequirements() {
    	List<Requirement> result = new ArrayList<Requirement>();
    	result.addAll(requirements.values());
        return result;
    }

    public void add(Requirement requirement){
    	Requirement transientRequirement = new Requirement();
    	
    	
    	transientRequirement.setId(requirement.getId());
    	transientRequirement.setKey(requirement.getKey());
    	
    	requirements.put(requirement.getKey(),transientRequirement);
    }
    
    public int getNumberOfRequirements(){
    	return requirements.size();
    }
    
    public int getNumberOfFailedRequirements(){
    	int numFails = 0;
    	for(Requirement row : requirements.values()){
    		if(row.isFailed()){
    			numFails++;
    		}
    	}
    	return numFails;
    }
    
    public int getNumberOfSucceededRequirements(){
    	int numFails = 0;
    	for(Requirement row : requirements.values()){
    		if(row.isFailed()){
    			numFails++;
    		}
    	}
    	return numFails;
    }
    
    public String getNumberOfFailedRequirementsShare(){
    	int numFails = 0;
    	for(Requirement row : requirements.values()){
    		if(row.isFailed()){
    			numFails++;
    		}
    	}
    	return numFails + "%";
    }
    
    public int getNumberOfUnstableRequirements(){
    	int numUnstable = 0;
    	for(Requirement row : requirements.values()){
    		if(row.isUnstable()){
    			numUnstable++;
    		}
    	}
    	return numUnstable;
    }
    
    public String getNumberOfUnstableRequirementsShare(){
    	int numUnstable = 0;
    	for(Requirement row : requirements.values()){
    		if(row.isUnstable()){
    			numUnstable++;
    		}
    	}
    	return numUnstable + "%";
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
    
    public boolean isUnstable(){
    	boolean isUnstable = (this.getNumberOfUnstableRequirements()>0) && (this.getNumberOfFailedRequirements()==0);
    	
    	return isUnstable;
    }
    
    public boolean isFailed(){
    	boolean isFailed = (this.getNumberOfFailedRequirements()>0);
    	
    	return isFailed;
    }
    
    public boolean isSucceeded(){
    	boolean isSucceeded = (this.getNumberOfFailedRequirements()==0);
    	return isSucceeded;
    }

    public String getTouched() {
        if(lastTested == null){
            return "not touched";
        }

        DateTime now = DateTime.now();
        DateTime thisTime = new DateTime(lastTested.getTime());
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

    public long getTouchedDays(){
        if(lastTested == null){
            return Integer.MAX_VALUE;
        }

        DateTime now = DateTime.now();
        DateTime thisTime = new DateTime(lastTested.getTime());
        long duration = now.getMillis() - thisTime.getMillis();
        if(duration < 0){
            return Integer.MAX_VALUE;
        }
        long days = Math.floorDiv(duration, (1000 * 3600 * 24 ));
        duration = duration - days * (1000 * 3600 * 24 );
        long hours = Math.floorDiv(duration, (1000 * 3600 ));
        duration = duration - days * (1000 * 3600);
        long minutes = Math.floorDiv(duration, (1000 * 60 ));
        duration = duration - days * (1000 * 60);
        long seconds = Math.floorDiv(duration, (1000));

        return days;
    }

    public long getTouchedHours(){
        if(lastTested == null){
            return Integer.MAX_VALUE;
        }

        DateTime now = DateTime.now();
        DateTime thisTime = new DateTime(lastTested.getTime());
        long duration = now.getMillis() - thisTime.getMillis();
        if(duration < 0){
            return Integer.MAX_VALUE;
        }
        long days = Math.floorDiv(duration, (1000 * 3600 * 24 ));
        duration = duration - days * (1000 * 3600 * 24 );
        long hours = Math.floorDiv(duration, (1000 * 3600 ));
        duration = duration - days * (1000 * 3600);
        long minutes = Math.floorDiv(duration, (1000 * 60 ));
        duration = duration - days * (1000 * 60);
        long seconds = Math.floorDiv(duration, (1000));

        return hours;
    }

    public long getTouchedMinutes(){
        if(lastTested == null){
            return Integer.MAX_VALUE;
        }

        DateTime now = DateTime.now();
        DateTime thisTime = new DateTime(lastTested.getTime());
        long duration = now.getMillis() - thisTime.getMillis();
        if(duration < 0){
            return Integer.MAX_VALUE;
        }
        long days = Math.floorDiv(duration, (1000 * 3600 * 24 ));
        duration = duration - days * (1000 * 3600 * 24 );
        long hours = Math.floorDiv(duration, (1000 * 3600 ));
        duration = duration - days * (1000 * 3600);
        long minutes = Math.floorDiv(duration, (1000 * 60 ));
        duration = duration - days * (1000 * 60);
        long seconds = Math.floorDiv(duration, (1000));

        return minutes;
    }

    public long getTouchedSeconds(){
        if(lastTested == null){
            return Integer.MAX_VALUE;
        }

        DateTime now = DateTime.now();
        DateTime thisTime = new DateTime(lastTested.getTime());
        long duration = now.getMillis() - thisTime.getMillis();
        if(duration < 0){
            return Integer.MAX_VALUE;
        }
        long days = Math.floorDiv(duration, (1000 * 3600 * 24 ));
        duration = duration - days * (1000 * 3600 * 24 );
        long hours = Math.floorDiv(duration, (1000 * 3600 ));
        duration = duration - days * (1000 * 3600);
        long minutes = Math.floorDiv(duration, (1000 * 60 ));
        duration = duration - days * (1000 * 60);
        long seconds = Math.floorDiv(duration, (1000));

        return seconds;
    }
    
    public int getDuration(){
    	int duration = 0;
    	for(Requirement row : getRequirements()){
    		duration += row.getDuration();
    	}
    	return duration;
    }
}
