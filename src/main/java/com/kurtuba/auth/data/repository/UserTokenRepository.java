package com.kurtuba.auth.data.repository;

import com.kurtuba.auth.data.model.ClientType;
import com.kurtuba.auth.data.model.UserToken;
import org.springframework.data.repository.CrudRepository;

public interface UserTokenRepository extends CrudRepository<UserToken, Long> {

	UserToken getUserTokenByUserIdAndClientTypeAndActive(String email, ClientType clientType, boolean active);

}
