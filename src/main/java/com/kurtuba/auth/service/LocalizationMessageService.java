package com.kurtuba.auth.service;

import com.kurtuba.auth.data.dto.LocalizationMessageDto;
import com.kurtuba.auth.data.model.LocalizationMessage;
import com.kurtuba.auth.data.repository.LocalizationMessageRepository;
import com.kurtuba.auth.error.enums.ErrorEnum;
import com.kurtuba.auth.error.exception.BusinessLogicException;
import jakarta.validation.Valid;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Validated
public class LocalizationMessageService {

    final
    LocalizationMessageRepository localizationMessageRepository;

    public LocalizationMessageService(LocalizationMessageRepository localizationMessageRepository) {
        this.localizationMessageRepository = localizationMessageRepository;
    }

    public Optional<LocalizationMessage> finById(String id) {
        return localizationMessageRepository.findById(id);
    }

    public List<LocalizationMessage> findAll() {
        return (List<LocalizationMessage>) localizationMessageRepository.findAll();
    }

    public List<LocalizationMessage> findByLanguageCode(String languageCode) {
        return localizationMessageRepository.findByLanguageCode(languageCode);
    }

    public List<LocalizationMessage> findByKey(String key) {
        return localizationMessageRepository.findByMessageKey(key);
    }

    @Cacheable(value = "localization", key = "#languageCode + '_' + #key")
    public Optional<LocalizationMessage> findByLanguageCodeAndKeyAndReturnOptional(String languageCode, String key) {
        return localizationMessageRepository.findByLanguageCodeAndMessageKey(languageCode, key);
    }

    @Cacheable(value = "localization", key = "#languageCode + '_' + #messageKey")
    public LocalizationMessage findByLanguageCodeAndMessageKey(String languageCode, String messageKey) {
        return localizationMessageRepository.findByLanguageCodeAndMessageKey(languageCode, messageKey).orElseThrow(() ->
                new BusinessLogicException(ErrorEnum.LOCALIZATION_INVALID_RESOURCE_PARAMETER));
    }

    @CachePut(value = "localization", key = "#localizationMessageDto.languageCode + '_' + #localizationMessageDto.key")
    @Transactional
    public LocalizationMessage create(@Valid LocalizationMessageDto localizationMessageDto) {
        findByLanguageCodeAndKeyAndReturnOptional(localizationMessageDto.getLanguageCode(), localizationMessageDto.getKey())
                .ifPresent(localization -> {
            throw new BusinessLogicException(ErrorEnum.LOCALIZATION_ALREADY_EXISTS);
        });
        return localizationMessageRepository.save(LocalizationMessage.builder()

                .languageCode(localizationMessageDto.getLanguageCode())
                .messageKey(localizationMessageDto.getKey())
                .message(localizationMessageDto.getMessage())
                .createdDate(Instant.now()).build());
    }

    @CacheEvict(value = "localization", key = "#localizationMessageDto.languageCode + '_' + #localizationMessageDto.key")
    @Transactional
    public LocalizationMessage update(@Valid LocalizationMessageDto localizationMessageDto) {
        LocalizationMessage localizationMessage = localizationMessageRepository.findById(localizationMessageDto.getId()).orElseThrow(() ->
                new BusinessLogicException(ErrorEnum.LOCALIZATION_INVALID_RESOURCE_ID));
        return localizationMessageRepository.save(
                LocalizationMessage.builder()
                        .id(localizationMessage.getId())
                        .languageCode(localizationMessageDto.getLanguageCode())
                        .messageKey(localizationMessageDto.getKey())
                        .message(localizationMessageDto.getMessage())
                        .createdDate(localizationMessage.getCreatedDate())
                        .updatedDate(Instant.now())
                        .build());
    }

}
