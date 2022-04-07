package com.collectoryx.collectoryxApi;

import com.collectoryx.collectoryxApi.user.model.User;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.Test;

@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestUser extends BaseTestController{

  //@Autowired
  //private TestEntityManager entityManager;

  @Test
  public void testCreateUser() {
    User user = createUser();

    System.out.println(user);

    /*User savedUser = repo.save(user);

    User existUser = entityManager.find(User.class, savedUser.getId());

    assertThat(user.getEmail()).isEqualTo(existUser.getEmail());*/

  }

}
