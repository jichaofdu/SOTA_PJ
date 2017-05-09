package entry.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Account {

    @Id
    private UUID id;

    private String password;

    private String phoneNum;

    private int gender;

    public Account(){
        gender = Gender.OTHER.getCode();
        password = "defaultPassword";
        phoneNum = "0123456789";
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
}
