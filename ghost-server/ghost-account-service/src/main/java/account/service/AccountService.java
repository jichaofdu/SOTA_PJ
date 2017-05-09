package account.service;

import account.domain.*;

public interface AccountService {

    LoginResult login(LoginInfo li);

    RegisterResult create(RegisterInfo ri);

    Account findByPhoneNum(String phoneNum);

    Account saveChanges(Account account);

    Account changePassword(NewPasswordInfo npi);

}
