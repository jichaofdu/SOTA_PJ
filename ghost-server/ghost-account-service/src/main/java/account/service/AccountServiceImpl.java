package account.service;

import account.domain.*;
import account.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AccountServiceImpl implements AccountService{

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public LoginResult login(LoginInfo li){
        if(li == null){
            System.out.println("[Account Service][Login] Fail.Account not found.");
            LoginResult lr = new LoginResult();
            lr.setStatus(false);
            lr.setMessage("Account Not Found");
            lr.setAccount(null);
            return lr;
        }
        Account result = accountRepository.findByPhoneNum(li.getPhoneNum());
        if(result != null &&
                result.getPassword() != null && li.getPassword() != null
                && result.getPassword().equals(li.getPassword())){
            result.setPassword("");
            System.out.println("[Account Service][Login] Success.");
            LoginResult lr = new LoginResult();
            lr.setStatus(true);
            lr.setMessage("Success");
            lr.setAccount(result);
            return lr;
        }else{
            System.out.println("[Account Service][Login] Fail.Wrong Password.");
            LoginResult lr = new LoginResult();
            lr.setStatus(false);
            lr.setMessage("Password Wrong");
            lr.setAccount(null);
            return lr;
        }
    }

    @Override
    public RegisterResult create(RegisterInfo ri){
        Account oldAcc = accountRepository.findByPhoneNum(ri.getPhoneNum());
        if(oldAcc != null){
            RegisterResult rr = new RegisterResult();
            rr.setStatus(false);
            rr.setMessage("Account Already Exists");
            rr.setAccount(null);
            System.out.println("[Account Service][Register] Fail.Account already exists.");
            return rr;
        }
        Account account = new Account();
        account.setId(UUID.randomUUID());
        account.setPhoneNum(ri.getPhoneNum());
        account.setPassword(ri.getPassword());
        account.setGender(ri.getGender());
        Account resultAcc = accountRepository.save(account);
        resultAcc.setPassword("");
        System.out.println("[Account Service][Register] Success.");
        RegisterResult rr = new RegisterResult();
        rr.setStatus(true);
        rr.setMessage("Success");
        rr.setAccount(account);
        return rr;
    }

    @Override
    public Account findByPhoneNum(String phoneNum){
        Account account = accountRepository.findByPhoneNum(phoneNum);
        if(account == null){
            System.out.println("[Account Service][FindAccountByPhoneNum] Fail.Can not found account.");
        }else{
            System.out.println("[Account Service][FindAccountByPhoneNum] Success.");
        }
        return account;
    }

    @Override
    public Account saveChanges(Account account){
        Account oldAccount = accountRepository.findById(account.getId());
        if(oldAccount == null){
            System.out.println("[Account Service][ModifyInfo] Fail.Can not found account.");
            return null;
        }else{
            oldAccount.setGender(account.getGender());
            oldAccount.setPhoneNum(account.getPhoneNum());
            accountRepository.save(oldAccount);
            oldAccount.setPassword("");
            System.out.println("[Account Service][ModifyInfo] Success.");
            return oldAccount;
        }
    }

    @Override
    public Account changePassword(NewPasswordInfo npi){
        System.out.println("[Change Password]");
        Account oldAccount = accountRepository.findById(npi.getId());
        if(oldAccount == null){
            System.out.println("[Account Service][ChangePassword] Fail.Can not found account.");
            return null;
        }else{
            if(npi != null && npi.getOldPassword() != null &
                    npi.getNewPassword() != null &&
                    oldAccount.getPassword().equals(npi.getOldPassword())){
                oldAccount.setPassword(npi.getNewPassword());
                accountRepository.save(oldAccount);
                oldAccount.setPassword("");
                System.out.println("[Account Service][ChangePassword] Success.");
                return oldAccount;
            }else{
                System.out.println("[Account Service][ChangePassword] Fail.Wrong Password.");
                return null;
            }
        }
    }


}
