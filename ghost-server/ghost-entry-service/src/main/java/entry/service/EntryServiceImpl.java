package entry.service;

import entry.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EntryServiceImpl implements EntryService{

    private RestTemplate restTemplate;


    @Override
    public LoginResult login(LoginInfo li){
        restTemplate = new RestTemplate();
        return restTemplate.postForObject("http://localhost:12344/login/",li,LoginResult.class);
    }

    @Override
    public RegisterResult create(RegisterInfo ri){
        restTemplate = new RestTemplate();
        System.out.println("[Ready to Post to Register]");
        return restTemplate.postForObject("http://localhost:12344/register/",ri,RegisterResult.class);
    }

    @Override
    public Account findByPhoneNum(String phoneNum){
        restTemplate = new RestTemplate();
        return  restTemplate.getForObject("http://localhost:12344/findAccount/" + phoneNum,Account.class);
    }

    @Override
    public Account saveChanges(Account account){
        restTemplate = new RestTemplate();
        return restTemplate.postForObject("http://localhost:12344/saveAccountInfo/",account,Account.class);
    }

    @Override
    public Account changePassword(NewPasswordInfo npi){
        restTemplate = new RestTemplate();
        return restTemplate.postForObject("http://localhost:12344/changePassword/",npi,Account.class);
    }


}
