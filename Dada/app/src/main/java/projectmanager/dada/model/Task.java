package projectmanager.dada.model;

import java.io.Serializable;
import java.util.Date;

public class Task implements Serializable{

    private int      taskId;
    private String   title;
    private String   description;
    private User     publisher;
    private Date     publishedTime;
    private Date     deadline;
    private Location location;
    private String[] tags;
    private int      status;
    private int      credit;
    private User     accepter;

    public Task(int taskId, String title, String description, User publisher, Date publishedTime, Date deadline, Location location, String[] tags,int credit, int status, User accepter) {
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.publisher = publisher;
        this.publishedTime = publishedTime;
        this.deadline = deadline;
        this.location = location;
        this.tags = tags;
        this.status = status;
        this.credit = credit;
        this.accepter = accepter;
    }

    public Task(){}

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getPublisher() {
        return publisher;
    }

    public void setPublisher(User publisher) {
        this.publisher = publisher;
    }

    public Date getPublishedTime() {
        return publishedTime;
    }

    public void setPublishedTime(Date publishedTime) {
        this.publishedTime = publishedTime;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public User getAccepter() {
        return accepter;
    }

    public void setAccepter(User accepter) {
        this.accepter = accepter;
    }
}
