package entry.controller;

import entry.domain.*;
import entry.service.EntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
public class EntryController {


    @Autowired
    private EntryService entryService;

    private RestTemplate restTemplat;

    @RequestMapping(path = "/welcome", method = RequestMethod.GET)
    public String home() {
        return "Welcome to [ Entry Service ] !";
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public LoginResult login(@RequestBody LoginInfo li){
        return entryService.login(li);
    }

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public RegisterResult login(@RequestBody RegisterInfo li){
        return entryService.create(li);
    }

    @RequestMapping(path = "/modifyInfo", method = RequestMethod.POST)
    public Account modifyAccountInfo(@RequestBody Account account){
        return entryService.saveChanges(account);
    }

    @RequestMapping(path = "/changePassword", method = RequestMethod.POST)
    public Account modifyAccountPassword(@RequestBody NewPasswordInfo npi){
        return entryService.changePassword(npi);
    }

    @RequestMapping(path = "/findAccount/{phoneNum}", method = RequestMethod.GET)
    public Account findAccount(@PathVariable String phoneNum){
        return entryService.findByPhoneNum(phoneNum);
    }

}
