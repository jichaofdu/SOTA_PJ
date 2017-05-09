package account.repository;

import account.domain.Account;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface AccountRepository extends MongoRepository<Account, String> {

    @Query("{ 'id': ?0 }")
    Account findById(UUID id);






    @Query("{ 'phoneNum': ?0 }")
    Account findByPhoneNum(String phoneNum);

}
