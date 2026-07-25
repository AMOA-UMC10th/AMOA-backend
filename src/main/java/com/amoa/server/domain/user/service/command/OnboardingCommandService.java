package com.amoa.server.domain.user.service.command;

import com.amoa.server.domain.common.entity.DesignTag;
import com.amoa.server.domain.common.repository.DesignTagRepository;
import com.amoa.server.domain.common.entity.Region;
import com.amoa.server.domain.common.repository.RegionRepository;
import com.amoa.server.domain.term.entity.Term;
import com.amoa.server.domain.term.repository.TermRepository;
import com.amoa.server.domain.user.dto.request.OnboardingSaveReqDTO;
import com.amoa.server.domain.user.dto.request.OnboardingSaveReqDTO.AgreementRequest;
import com.amoa.server.domain.user.dto.response.OnboardingSaveResDTO;
import com.amoa.server.domain.user.entity.User;
import com.amoa.server.domain.user.entity.mapping.UserAgreement;
import com.amoa.server.domain.user.entity.mapping.UserDesignTag;
import com.amoa.server.domain.user.entity.mapping.UserRegion;
import com.amoa.server.domain.user.exception.code.UserErrorCode;
import com.amoa.server.domain.user.repository.UserAgreementRepository;
import com.amoa.server.domain.user.repository.UserDesignTagRepository;
import com.amoa.server.domain.user.repository.UserInterestedRegionRepository;
import com.amoa.server.domain.user.repository.UserRepository;
import com.amoa.server.global.apiPayload.exception.GeneralException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OnboardingCommandService {

    private final UserRepository userRepository;
    private final DesignTagRepository designTagRepository;
    private final RegionRepository regionRepository;
    private final TermRepository termRepository;

    private final UserDesignTagRepository userDesignTagRepository;
    private final UserInterestedRegionRepository userInterestedRegionRepository;
    private final UserAgreementRepository userAgreementRepository;

    public OnboardingSaveResDTO saveOnboarding(
            Long userId,
            OnboardingSaveReqDTO request
    ) {
        User user = findUser(userId);

        validateOnboardingUser(user);
        validateNickname(request.nickname(), userId);
        validateDuplicateIds(request);

        List<DesignTag> designTags =
                findDesignTags(request.designTagIds());

        List<Region> regions =
                findRegions(request.interestedRegionIds());

        List<Term> terms =
                findAndValidateTerms(request.agreements());

        updateUserProfile(user, request);

        saveUserDesignTags(user, designTags);
        saveUserRegions(user, regions);
        saveUserAgreements(user, terms, request.agreements());

        user.completeOnboarding();

        return OnboardingSaveResDTO.builder()
                .userId(user.getId())
                .onboardingCompleted(true)
                .build();
    }

    private User findUser(
            Long userId
    ) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserException(
                                UserErrorCode.USER_NOT_FOUND
                        )
                );
    }

    private void validateOnboardingUser(
            User user
    ) {
        if (user.getRole() != Role.NEW_USER) {
            throw new UserException(
                    UserErrorCode.ONBOARDING_ALREADY_COMPLETED
            );
        }
    }

    private void validateNickname(
            String nickname,
            Long userId
    ) {
        boolean duplicated =
                userRepository.existsByNicknameAndIdNot(
                        nickname,
                        userId
                );

        if (duplicated) {
            throw new UserException(
                    UserErrorCode.NICKNAME_DUPLICATED
            );
        }
    }

    private void validateDuplicateIds(
            OnboardingSaveReqDTO request
    ) {
        if (hasDuplicate(request.designTagIds())) {
            throw new UserException(
                    UserErrorCode.DUPLICATED_DESIGN_TAG
            );
        }

        if (hasDuplicate(request.interestedRegionIds())) {
            throw new UserException(
                    UserErrorCode.DUPLICATED_INTERESTED_REGION
            );
        }

        List<Long> termIds = request.agreements().stream()
                .map(AgreementReqDTO::termId)
                .toList();

        if (hasDuplicate(termIds)) {
            throw new UserException(
                    UserErrorCode.DUPLICATED_TERM_AGREEMENT
            );
        }
    }

    private boolean hasDuplicate(
            List<Long> ids
    ) {
        return ids.size() != new HashSet<>(ids).size();
    }

    private List<DesignTag> findDesignTags(
            List<Long> designTagIds
    ) {
        List<DesignTag> designTags =
                designTagRepository.findAllById(designTagIds);

        if (designTags.size() != designTagIds.size()) {
            throw new UserException(
                    UserErrorCode.DESIGN_TAG_NOT_FOUND
            );
        }

        return designTags;
    }

    private List<Region> findRegions(
            List<Long> regionIds
    ) {
        List<Region> regions =
                regionRepository.findAllById(regionIds);

        if (regions.size() != regionIds.size()) {
            throw new UserException(
                    UserErrorCode.REGION_NOT_FOUND
            );
        }

        return regions;
    }

    private List<Term> findAndValidateTerms(
            List<AgreementReqDTO> agreementRequests
    ) {
        List<Long> termIds = agreementRequests.stream()
                .map(AgreementReqDTO::termId)
                .toList();

        List<Term> requestedTerms =
                termRepository.findAllById(termIds);

        if (requestedTerms.size() != termIds.size()) {
            throw new UserException(
                    UserErrorCode.TERM_NOT_FOUND
            );
        }

        Map<Long, Boolean> agreementMap =
                agreementRequests.stream()
                        .collect(Collectors.toMap(
                                AgreementReqDTO::termId,
                                AgreementReqDTO::agreed
                        ));

        List<Term> requiredTerms =
                termRepository.findAllByIsRequiredTrue();

        boolean requiredTermsAgreed =
                requiredTerms.stream()
                        .allMatch(term ->
                                Boolean.TRUE.equals(
                                        agreementMap.get(term.getId())
                                )
                        );

        if (!requiredTermsAgreed) {
            throw new UserException(
                    UserErrorCode.REQUIRED_TERM_NOT_AGREED
            );
        }

        return requestedTerms;
    }

    private void updateUserProfile(
            User user,
            OnboardingSaveReqDTO request
    ) {
        user.updateOnboardingProfile(
                request.nickname(),
                request.phoneNumber()
        );
    }

    private void saveUserDesignTags(
            User user,
            List<DesignTag> designTags
    ) {
        List<UserDesignTag> userDesignTags =
                designTags.stream()
                        .map(designTag ->
                                UserDesignTag.create(
                                        user,
                                        designTag
                                )
                        )
                        .toList();

        userDesignTagRepository.saveAll(userDesignTags);
    }

    private void saveUserRegions(
            User user,
            List<Region> regions
    ) {
        List<UserRegion> userRegions =
                regions.stream()
                        .map(region ->
                                UserRegion.create(
                                        user,
                                        region
                                )
                        )
                        .toList();

        userInterestedRegionRepository.saveAll(userRegions);
    }

    private void saveUserAgreements(
            User user,
            List<Term> terms,
            List<AgreementReqDTO> agreementRequests
    ) {
        Map<Long, Boolean> agreementMap =
                agreementRequests.stream()
                        .collect(Collectors.toMap(
                                AgreementReqDTO::termId,
                                AgreementReqDTO::agreed
                        ));

        List<UserAgreement> userAgreements =
                terms.stream()
                        .map(term ->
                                UserAgreement.create(
                                        user,
                                        term,
                                        Boolean.TRUE.equals(
                                                agreementMap.get(term.getId())
                                        )
                                )
                        )
                        .toList();

        userAgreementRepository.saveAll(userAgreements);
    }
}