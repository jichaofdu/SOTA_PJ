package account.controller;

import account.domain.*;
import account.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
public class AccountController {

    @Autowired
    private AccountService accountService;

    private RestTemplate restTemplate;

    @RequestMapping(path = "/welcome", method = RequestMethod.GET)
    public String home() {
        return "Welcome to [ Accounts Service ] !";
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public LoginResult login(@RequestBody LoginInfo li){
        LoginResult lr = accountService.login(li);
        if(lr.getStatus() == false){
            System.out.println("[Account Service][Login] Login Fail. No token generate.");
            return lr;
        }else{
            //Post token to the sso
            System.out.println("[Account Service][Login] LoginSuccess. Put token to sso.");
//            UUID token = UUID.randomUUID();
//            lr.setToken(token.toString());
//            restTemplate = new RestTemplate();
//            String tokenResult = restTemplate.getForObject("http://ghost-sso-service:12349/loginPutToken/" + token.toString(),String.class);
//            System.out.println("[Account Service][Login] Post to sso:" + tokenResult);
            return lr;
        }
    }

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public RegisterResult createNewAccount(@RequestBody RegisterInfo ri){
        System.out.println("[Test] create new account : " + ri.getPhoneNum());
        return accountService.create(ri);
    }

    @RequestMapping(path = "/findAccount/{phoneNum}", method = RequestMethod.GET)
    public Account findAccount(@PathVariable String phoneNum){
        return accountService.findByPhoneNum(phoneNum);
    }

    @RequestMapping(path = "/saveAccountInfo", method = RequestMethod.POST)
    public Account saveAccountInfo(@RequestBody Account account){
        return accountService.saveChanges(account);
    }

    @RequestMapping(path = "/changePassword", method = RequestMethod.POST)
    public Account changePassword(@RequestBody NewPasswordInfo npi){
        return accountService.changePassword(npi);
    }


}
