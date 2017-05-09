package entry.service;

import entry.domain.*;

public interface EntryService {

    LoginResult login(LoginInfo li);

    RegisterResult create(RegisterInfo ri);

    Account findByPhoneNum(String phoneNum);

    Account saveChanges(Account account);

    Account changePassword(NewPasswordInfo npi);

}
