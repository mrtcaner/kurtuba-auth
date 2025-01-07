package com.kurtuba.auth.data.repository;

import com.kurtuba.auth.data.enums.MetaOperationType;
import com.kurtuba.auth.data.model.UserMetaChange;
import org.springframework.data.repository.CrudRepository;
;import java.time.LocalDateTime;
import java.util.Optional;

public interface UserMetaChangeRepository extends CrudRepository<UserMetaChange, String> {

    Optional<UserMetaChange> findByLinkParam(String linkParam);

    void deleteAllByExecutedIsFalseAndUserIdAndMetaOperationType(String userId, MetaOperationType metaOperationType);

    Optional<UserMetaChange> findByUserIdAndMetaOperationTypeAndExpirationDateAfterAndExecutedIsFalse(String userId,
                                                                                            MetaOperationType metaOperationType,
                                                                                            LocalDateTime after);

    Optional<UserMetaChange> findByLinkParamAndMetaOperationTypeAndExpirationDateAfterAndExecutedIsFalse(String linkParam,
                                                                                               MetaOperationType metaOperationType,
                                                                                               LocalDateTime after);

}
