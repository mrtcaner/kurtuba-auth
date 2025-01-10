package com.kurtuba.auth.service;

import com.kurtuba.auth.data.dto.RegistrationDto;
import com.kurtuba.auth.data.enums.AuthProviderType;
import com.kurtuba.auth.data.enums.ContactType;
import com.kurtuba.auth.data.enums.MetaOperationType;
import com.kurtuba.auth.data.model.*;
import com.kurtuba.auth.data.repository.LocalizationAvailableLocaleRepository;
import com.kurtuba.auth.data.repository.UserRepository;
import com.kurtuba.auth.error.enums.ErrorEnum;
import com.kurtuba.auth.error.exception.BusinessLogicException;
import com.kurtuba.auth.utils.TestUtils;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMetaChangeService userMetaChangeService;

    @Mock
    private UserRoleService userRoleService;

    @Mock
    private LocalizationAvailableLocaleRepository localizationAvailableLocaleRepository;

    @Mock
    private MessageJobService messageJobService;

    @InjectMocks
    private UserService userService;

    @Spy
    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();


    RegistrationDto registrationDto;

    UserMetaChange emailActivationCodeUserMetaChange;

    UserMetaChange emailActivationLinkUserMetaChange;

    String userMetaChangeByCodeId = "byCode";
    String userMetaChangeByLinkId = "byLink";

    @Nested
    class FirstNestedClass {


        @BeforeEach
        public void setup() {

            registrationDto = RegistrationDto.builder()
                    .name("aa")
                    .surname("bb")
                    .username("")
                    .email("user@user.com")
                    .mobile("+905122345678")
                    .password("a.123456")
                    .authProvider(AuthProviderType.KURTUBA)
                    .preferredVerificationContact(ContactType.EMAIL)
                    .verificationByCode(true)
                    .languageCode("tr")
                    .countryCode("tr")
                    .build();

            UserSetting userSetting = TestUtils.defaultUserSettingBuilder();
            userSetting.getLocale().setLanguageCode(registrationDto.getLanguageCode());
            userSetting.getLocale().setCountryCode(registrationDto.getCountryCode());

            UserRole userRole = TestUtils.defaultUserRoleBuilder();

            User savedUser = User.builder()
                    .id("1")
                    .name(registrationDto.getName())
                    .surname(registrationDto.getSurname())
                    .email(registrationDto.getEmail())
                    .emailVerified(false)
                    .mobile(registrationDto.getMobile())
                    .mobileVerified(false)
                    .username(registrationDto.getUsername())
                    .password(new BCryptPasswordEncoder().encode(registrationDto.getPassword()))
                    .authProvider(registrationDto.getAuthProvider())
                    .activated(false)
                    .locked(false)
                    .failedLoginCount(0)
                    .showCaptcha(false)
                    .userSetting(userSetting)
                    .userRoles(List.of(userRole))
                    .birthdate(null)
                    .createdDate(LocalDateTime.now())
                    .build();
            userRole.setUser(savedUser);
            userSetting.setUser(savedUser);

            emailActivationCodeUserMetaChange = UserMetaChange.builder()
                    .id(userMetaChangeByCodeId)
                    .meta(registrationDto.getEmail())
                    .metaOperationType(MetaOperationType.ACCOUNT_ACTIVATION)
                    .contactType(ContactType.EMAIL)
                    .userId(savedUser.getId())
                    .maxTryCount(3)
                    .tryCount(0)
                    .code("123456")
                    .executed(false)
                    .expirationDate(LocalDateTime.now().plusHours(24)) // todo: get expiration time properly
                    .createdDate(LocalDateTime.now())
                    .build();

            emailActivationLinkUserMetaChange = UserMetaChange.builder()
                    .id(userMetaChangeByLinkId)
                    .meta(registrationDto.getEmail())
                    .metaOperationType(MetaOperationType.ACCOUNT_ACTIVATION)
                    .contactType(ContactType.EMAIL)
                    .userId(savedUser.getId())
                    .linkParam("link")
                    .executed(false)
                    .expirationDate(LocalDateTime.now().plusHours(24)) // todo: get expiration time properly
                    .createdDate(LocalDateTime.now())
                    .build();

            when(userRepository.save(any(User.class))).then(invocationOnMock -> {
                User usr = invocationOnMock.getArgument(0);
                assertTrue(usr.getName().equals(savedUser.getName()));
                assertTrue(usr.getSurname().equals(savedUser.getSurname()));
                assertTrue(usr.getUsername().equals(savedUser.getUsername()));
                assertTrue(usr.getEmail().equals(savedUser.getEmail()));
                assertTrue(usr.isEmailVerified() == (savedUser.isEmailVerified()));
                assertTrue(usr.getMobile().equals(savedUser.getMobile()));
                assertTrue(usr.isMobileVerified() == (savedUser.isMobileVerified()));
                assertTrue(!new BCryptPasswordEncoder().matches(usr.getPassword(), savedUser.getPassword()));
                assertTrue(usr.getAuthProvider().equals(savedUser.getAuthProvider()));
                assertTrue(usr.isActivated() == (savedUser.isActivated()));
                assertTrue(usr.isLocked() == (savedUser.isLocked()));
                assertTrue(usr.getFailedLoginCount() == (savedUser.getFailedLoginCount()));
                assertTrue(usr.isShowCaptcha() == (savedUser.isShowCaptcha()));
                assertTrue(usr.getBirthdate() == (savedUser.getBirthdate()));
                assertTrue(usr.getCreatedDate() != null);
                //todo: check also

                return savedUser;
            });

            when(userMetaChangeService.create((any(UserMetaChange.class)))).then(invocationOnMock -> {
                UserMetaChange usrMtChange = invocationOnMock.getArgument(0);
                if (StringUtils.hasLength(usrMtChange.getCode())) {
                    return emailActivationCodeUserMetaChange;
                } else {
                    return emailActivationLinkUserMetaChange;
                }
            });

            // return whatever passed as param. this test-run is not to test available locales repository/service
            when(localizationAvailableLocaleRepository.findByLanguageCodeAndCountryCode(anyString(), anyString()))
                    .then(invocationOnMock -> Optional.of(LocalizationAvailableLocale.builder()
                            .languageCode(invocationOnMock.getArgument(0))
                            .countryCode(invocationOnMock.getArgument(1))
                            .build()));

            when(userRoleService.create(any(UserRole.class))).then(invocationOnMock -> {
                UserRole usrRole = invocationOnMock.getArgument(0);
                usrRole.setId("userRoleId");
                usrRole.setCreatedDate(LocalDateTime.now());
                return usrRole;
            });
        }

        @Test
        public void createUser_whenGivenValidRegistrationDto_thenReturnSavedUser() {
            doNothing().when(messageJobService).sendAccountActivationCodeMail(anyString(), anyString(), anyString());
            //default registrationDto is set to account activation by email using code
            String metaChangeId = userService.register(registrationDto);
            assertTrue(metaChangeId.equals(emailActivationCodeUserMetaChange.getId()));
        }

        @Test
        public void createUser_whenGivenValidRegistrationDtoWithVerifyByLink_thenReturnSavedUser() {
            doNothing().when(messageJobService).sendAccountActivationLinkMail(anyString(), anyString(), anyString());
            //default registrationDto is set to account activation by email using code
            registrationDto.setVerificationByCode(false);
            String metaChangeId = userService.register(registrationDto);
            assertTrue(metaChangeId.equals(emailActivationLinkUserMetaChange.getId()));
        }
    }

    @Nested
    class SecondNestedClass{

        @BeforeEach
        public void setup() {

            registrationDto = RegistrationDto.builder()
                    .name("aa")
                    .surname("bb")
                    .username("")
                    .email("user@user.com")
                    .mobile("+905122345678")
                    .password("a.123456")
                    .authProvider(AuthProviderType.KURTUBA)
                    .preferredVerificationContact(ContactType.EMAIL)
                    .verificationByCode(true)
                    .languageCode("tr")
                    .countryCode("tr")
                    .build();
        }

        @Test
        public void createUser_whenGivenMissingEmailAndMobile_thenThrowException() {
            registrationDto.setEmail("");
            registrationDto.setMobile("");
            Throwable exception = assertThrows(BusinessLogicException.class, () -> userService.register(registrationDto));
            assertTrue(((BusinessLogicException)exception).getErrorCode() == ErrorEnum.USER_CONTACT_REQUIRED.getCode());

        }
    }




}
