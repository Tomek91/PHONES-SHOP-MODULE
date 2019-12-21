package pl.com.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.com.app.dto.MinMaxDataDTO;
import pl.com.app.dto.PhoneDTO;
import pl.com.app.dto.PhonesCompareDataDTO;
import pl.com.app.dto.SpecifiedPhonesDTO;
import pl.com.app.dto.enums.SortType;
import pl.com.app.exceptions.exceptions.ExceptionCode;
import pl.com.app.exceptions.exceptions.MyException;
import pl.com.app.mappers.PhoneMapper;
import pl.com.app.model.enums.*;
import pl.com.app.repository.PhoneRepository;
import pl.com.app.repository.UserRepository;
import pl.com.app.repository.VerificationTokenRepository;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class PhoneCompareService {

    private final PhoneRepository phoneRepository;
    private final UserRepository userRepository;
    private final PhoneMapper phoneMapper;
    private final VerificationTokenRepository verificationTokenRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final PasswordEncoder passwordEncoder;

    public List<PhoneDTO> findAllSorted() {
        try {
            return phoneRepository
                    .findAll()
                    .stream()
                    .map(phoneMapper::phoneToPhoneDTO)
                    .sorted(Comparator.comparing(PhoneDTO::getProducer).thenComparing(PhoneDTO::getModel))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    public PhonesCompareDataDTO findAllSpecified(SpecifiedPhonesDTO specifiedPhonesDTO) {
        try {
            if (specifiedPhonesDTO == null) {
                throw new NullPointerException("SPECIFIED PHONES IS NULL");
            }
            if (specifiedPhonesDTO.getPhonesId() == null) {
                throw new NullPointerException("PHONE LIST ID IS NULL");
            }

            List<PhoneDTO> phoneDTOList = phoneRepository
                    .findAllByIdIn(specifiedPhonesDTO.getPhonesId())
                    .stream()
                    .map(phoneMapper::phoneToPhoneDTO)
                    .sorted(Comparator.comparing(PhoneDTO::getProducer).thenComparing(PhoneDTO::getModel))
                    .collect(Collectors.toList());

            List<PhoneDTO> phonesSortedByPrice = sort(phoneDTOList, SortType.PRICE, false);
            List<PhoneDTO> phonesSortedByDiagonal = sort(phoneDTOList, SortType.DIAGONAL, false);
            List<PhoneDTO> phonesSortedByResolution = sort(phoneDTOList, SortType.RESOLUTION, false);
            List<PhoneDTO> phonesSortedByRom = sort(phoneDTOList, SortType.ROM, false);
            List<PhoneDTO> phonesSortedByRam = sort(phoneDTOList, SortType.RAM, false);
            List<PhoneDTO> phonesSortedByDataCommunication = sort(phoneDTOList, SortType.DATA_COMMUNICATION, false);
            List<PhoneDTO> phonesSortedByScreen = sort(phoneDTOList, SortType.SCREEN, false);
            List<PhoneDTO> phonesSortedByCapacity = sort(phoneDTOList, SortType.CAPACITY, false);
            List<PhoneDTO> phonesSortedByMemoryCard = sort(phoneDTOList, SortType.MEMORY_CARD, false);

            MinMaxDataDTO minMaxDataDTO = MinMaxDataDTO
                    .builder()
                    .maxPrice(phonesSortedByPrice.get(phonesSortedByPrice.size() - 1).getPrice())
                    .minPrice(phonesSortedByPrice.get(0).getPrice())
                    .maxDiagonal(phonesSortedByDiagonal.get(phonesSortedByDiagonal.size() - 1).getDiagonal())
                    .minDiagonal(phonesSortedByDiagonal.get(0).getDiagonal())
                    .maxResolution(phonesSortedByResolution.get(phonesSortedByResolution.size() - 1).getResolution())
                    .minResolution(phonesSortedByResolution.get(0).getResolution())
                    .maxRam(phonesSortedByRam.get(phonesSortedByRam.size() - 1).getRam())
                    .minRam(phonesSortedByRam.get(0).getRam())
                    .maxRom(phonesSortedByRom.get(phonesSortedByRom.size() - 1).getRom())
                    .minRom(phonesSortedByRom.get(0).getRom())
                    .maxDataCommunication(phonesSortedByDataCommunication.get(phonesSortedByDataCommunication.size() - 1).getDataCommunication())
                    .minDataCommunication(phonesSortedByDataCommunication.get(0).getDataCommunication())
                    .isDualSim(Boolean.TRUE)
                    .isBluetooth(Boolean.TRUE)
                    .isWifi(Boolean.TRUE)
                    .isJack(Boolean.TRUE)
                    .isUSB(Boolean.TRUE)
                    .maxScreen(phonesSortedByScreen.get(phonesSortedByScreen.size() - 1).getScreen())
                    .minScreen(phonesSortedByScreen.get(0).getScreen())
                    .maxCapacity(phonesSortedByCapacity.get(phonesSortedByCapacity.size() - 1).getCapacity())
                    .minCapacity(phonesSortedByCapacity.get(0).getCapacity())
                    .maxMemoryCard(phonesSortedByMemoryCard.get(phonesSortedByMemoryCard.size() - 1).getMemoryCard())
                    .minMemoryCard(phonesSortedByMemoryCard.get(0).getMemoryCard())
                    .build();

            List<PhoneDTO> bestPhones = findBestPhones(phoneDTOList, minMaxDataDTO);

            return PhonesCompareDataDTO
                    .builder()
                    .phoneDTOList(phoneDTOList)
                    .minMaxDataDTO(minMaxDataDTO)
                    .bestPhonesList(bestPhones)
                    .build();
        } catch (Exception e) {
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    private List<PhoneDTO> findBestPhones(List<PhoneDTO> phoneDTOList, MinMaxDataDTO minMaxDataDTO) {

        try {
            if (phoneDTOList == null) {
                throw new NullPointerException("PHONES IS NULL");
            }
            if (minMaxDataDTO == null) {
                throw new NullPointerException("SORT DATA IS NULL");
            }

            Map<PhoneDTO, Integer> phoneByMaxData = phoneDTOList
                    .stream()
                    .collect(Collectors.toMap(
                            Function.identity(),
                            x -> {
                                int maxCounter = 0;
                                if (x.getPrice().equals(minMaxDataDTO.getMaxPrice())) {
                                    maxCounter++;
                                }
                                if (x.getDiagonal().equals(minMaxDataDTO.getMaxDiagonal())) {
                                    maxCounter++;
                                }
                                if (x.getResolution().equals(minMaxDataDTO.getMaxResolution())) {
                                    maxCounter++;
                                }
                                if (x.getRam().equals(minMaxDataDTO.getMaxRam())) {
                                    maxCounter++;
                                }
                                if (x.getRom().equals(minMaxDataDTO.getMaxRom())) {
                                    maxCounter++;
                                }
                                if (x.getDataCommunication().equals(minMaxDataDTO.getMaxDataCommunication())) {
                                    maxCounter++;
                                }
                                if (x.getIsDualSim().equals(minMaxDataDTO.getIsDualSim())) {
                                    maxCounter++;
                                }
                                if (x.getIsBluetooth().equals(minMaxDataDTO.getIsBluetooth())) {
                                    maxCounter++;
                                }
                                if (x.getIsWifi().equals(minMaxDataDTO.getIsWifi())) {
                                    maxCounter++;
                                }
                                if (x.getIsUSB().equals(minMaxDataDTO.getIsUSB())) {
                                    maxCounter++;
                                }
                                if (x.getIsJack().equals(minMaxDataDTO.getIsJack())) {
                                    maxCounter++;
                                }
                                if (x.getScreen().equals(minMaxDataDTO.getMaxScreen())) {
                                    maxCounter++;
                                }
                                if (x.getCapacity().equals(minMaxDataDTO.getMaxCapacity())) {
                                    maxCounter++;
                                }
                                if (x.getMemoryCard().equals(minMaxDataDTO.getMaxMemoryCard())) {
                                    maxCounter++;
                                }
                                return maxCounter;
                            }
                    ));

            Integer maxDataValue = phoneByMaxData
                    .values()
                    .stream()
                    .max(Comparator.naturalOrder())
                    .orElse(0);

            return phoneByMaxData
                    .entrySet()
                    .stream()
                    .filter(e -> e.getValue().equals(maxDataValue))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    public List<PhoneDTO> sort(List<PhoneDTO> phones, SortType sortType, boolean descending) {

        try {
            if (phones == null) {
                throw new NullPointerException("PHONES IS NULL");
            }
            if (sortType == null) {
                throw new NullPointerException("SORT TYPE IS NULL");
            }

            Stream<PhoneDTO> sPhones = phones.stream();
            List<PhoneDTO> sortedPhones = null;

            switch (sortType) {
                case PRICE:
                    sortedPhones = sPhones.sorted(Comparator.comparing(PhoneDTO::getPrice))
                            .collect(Collectors.toList());
                    break;
                case DIAGONAL:
                    sortedPhones = sPhones
                            .sorted(Comparator.comparing(PhoneDTO::getDiagonal))
                            .filter(x -> !x.getDiagonal().equals(Diagonals.OTHER))
                            .collect(Collectors.toList());
                    break;
                case RESOLUTION:
                    sortedPhones = sPhones
                            .filter(x -> !x.getResolution().equals(Resolution.OTHER))
                            .sorted(Comparator.comparing(x -> Integer.valueOf(x.getResolution().toString().split("_")[1])))
                            .collect(Collectors.toList());
                    break;
                case ROM:
                    sortedPhones = sPhones
                            .sorted(Comparator.comparing(x -> Integer.valueOf(x.getRom().toString().split("_")[1])))
                            .collect(Collectors.toList());
                    break;
                case RAM:
                    sortedPhones = sPhones
                            .sorted(Comparator.comparing(PhoneDTO::getRam))
                            .filter(x -> !x.getRam().equals(Ram.OTHER))
                            .collect(Collectors.toList());
                    break;
                case DATA_COMMUNICATION:
                    sortedPhones = sPhones
                            .sorted(Comparator.comparing(PhoneDTO::getDataCommunication))
                            .filter(x -> !x.getDataCommunication().equals(DataCommunication.DC_OTHER))
                            .collect(Collectors.toList());
                    break;
                case SCREEN:
                    sortedPhones = sPhones.sorted(Comparator.comparing(PhoneDTO::getScreen))
                            .collect(Collectors.toList());
                    break;
                case CAPACITY:
                    sortedPhones = sPhones.sorted(Comparator.comparing(PhoneDTO::getCapacity))
                            .collect(Collectors.toList());
                    break;
                case MEMORY_CARD:
                    sortedPhones = sPhones
                            .sorted(Comparator.comparing(PhoneDTO::getMemoryCard))
                            .filter(x -> !x.getMemoryCard().equals(MemoryCard.OTHER))
                            .collect(Collectors.toList());
                    break;
            }

            if (descending) {
                Collections.reverse(sortedPhones);
            }

            return sortedPhones;
        } catch (Exception e) {
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }
}
