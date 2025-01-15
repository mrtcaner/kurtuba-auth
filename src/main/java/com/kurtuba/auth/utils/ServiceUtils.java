package com.kurtuba.auth.utils;

import com.kurtuba.auth.data.model.UserMetaChange;
import com.kurtuba.auth.error.enums.ErrorEnum;
import com.kurtuba.auth.error.exception.BusinessLogicException;
import com.kurtuba.auth.service.UserMetaChangeService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Component
public class ServiceUtils {

    final
    UserMetaChangeService userMetaChangeService;

    public ServiceUtils(UserMetaChangeService userMetaChangeService) {
        this.userMetaChangeService = userMetaChangeService;
    }

    // if there is code mismatch then a transaction is required to save the result. Just for that scenario, there is
    // Propagation.REQUIRES_NEW and no roll back for BusinessLogicException
    @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = BusinessLogicException.class)
    public void validateUserMetaChange(UserMetaChange userMetaChange, String code) {

        if (userMetaChange.getMaxTryCount() != null && userMetaChange.getTryCount() >= userMetaChange.getMaxTryCount()) {
            throw new BusinessLogicException(ErrorEnum.USER_META_CHANGE_CODE_EXPIRED);
        }

        if (StringUtils.hasLength(code) && !userMetaChange.getCode().equals(code)) {
            updateUserMetaChangeTryCount(userMetaChange);
            throw new BusinessLogicException(ErrorEnum.USER_META_CHANGE_CODE_MISMATCH);
        }

        if (userMetaChange.isExecuted()) {
            throw new BusinessLogicException(ErrorEnum.USER_META_CHANGE_CODE_EXPIRED);
        }

        if (userMetaChange.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new BusinessLogicException(ErrorEnum.USER_META_CHANGE_CODE_EXPIRED);
        }
    }

    @Transactional
    public void updateUserMetaChangeTryCount(UserMetaChange userMetaChange) {
        userMetaChange.setTryCount(userMetaChange.getTryCount() + 1);
        userMetaChange.setUpdatedDate(LocalDateTime.now());
        userMetaChangeService.update(userMetaChange);
    }


}
