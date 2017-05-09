package account.domain;

public class RegisterInfo {

    private String password;

    private int gender;

    private String phoneNum;

    public RegisterInfo(){
        gender = Gender.OTHER.getCode();
        password = "defaultPassword";
        phoneNum = "352323";
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
