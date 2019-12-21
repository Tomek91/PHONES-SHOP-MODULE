package pl.com.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import pl.com.app.dto.PhoneDTO;
import pl.com.app.dto.ProfileDTO;
import pl.com.app.exceptions.exceptions.ExceptionCode;
import pl.com.app.exceptions.exceptions.MyException;
import pl.com.app.mappers.PhoneMapper;
import pl.com.app.mappers.ProfileMapper;
import pl.com.app.model.Phone;
import pl.com.app.model.Profile;
import pl.com.app.repository.PhoneRepository;
import pl.com.app.repository.ProfileRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final ProfileMapper profileMapper;
    private final PhoneRepository phoneRepository;
    private final PhoneMapper phoneMapper;

    public List<ProfileDTO> findAll() {
        try {
            return profileRepository
                    .findAll()
                    .stream()
                    .map(profileMapper::profileToProfileDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    public ProfileDTO findOne(Long id) {
        try {
            if (id == null) {
                throw new NullPointerException("PROFILE ID IS NULL");
            }

            return profileRepository
                    .findById(id)
                    .map(profileMapper::profileToProfileDTO)
                    .orElseThrow(NullPointerException::new);
        } catch (Exception e) {
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    public ProfileDTO add(ProfileDTO profileDTO) {

        try {
            if (profileDTO == null) {
                throw new NullPointerException("PROFILE IS NULL");
            }

            Profile profileFromDb = profileRepository.save(profileMapper.profileDTOToProfile(profileDTO));
            return profileMapper.profileToProfileDTO(profileFromDb);
        } catch (Exception e) {
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    public ProfileDTO update(Long id, ProfileDTO profileDTO) {
        try {
            if (id == null) {
                throw new NullPointerException("ID IS NULL");
            }
            if (profileDTO == null) {
                throw new NullPointerException("PROFILE IS NULL");
            }

            Profile profile = profileRepository.findById(id).orElseThrow(NullPointerException::new);

            profile.setBatteryThreshold(profileDTO.getBatteryThreshold() == null ? profile.getBatteryThreshold() : profileDTO.getBatteryThreshold());
            profile.setDiagonalThreshold(profileDTO.getDiagonalThreshold() == null ? profile.getDiagonalThreshold() : profileDTO.getDiagonalThreshold());
            profile.setIsJackThreshold(profileDTO.getIsJackThreshold() == null ? profile.getIsJackThreshold() : profileDTO.getIsJackThreshold());
            profile.setName(profileDTO.getName() == null ? profile.getName() : profileDTO.getName());
            profile.setIsWifiThreshold(profileDTO.getIsWifiThreshold() == null ? profile.getIsWifiThreshold() : profileDTO.getIsWifiThreshold());
            profile.setIsUSBThreshold(profileDTO.getIsUSBThreshold() == null ? profile.getIsUSBThreshold() : profileDTO.getIsUSBThreshold());
            profile.setRomThreshold(profileDTO.getRomThreshold() == null ? profile.getRomThreshold() : profileDTO.getRomThreshold());
            profile.setRamThreshold(profileDTO.getRamThreshold() == null ? profile.getRamThreshold() : profileDTO.getRamThreshold());

            Profile profileAfterUpdate = profileRepository.save(profile);
            return profileMapper.profileToProfileDTO(profileAfterUpdate);
        } catch (Exception e) {
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    public ProfileDTO delete(Long id) {

        try {
            if (id == null) {
                throw new NullPointerException("PROFILE ID IS NULL");
            }
            Profile profileToDelete = profileRepository.findById(id)
                    .orElseThrow(NullPointerException::new);
            profileRepository.delete(profileToDelete);
            return profileMapper.profileToProfileDTO(profileToDelete);

        } catch (Exception e) {
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    public List<PhoneDTO> findAllByProfile(Long id) {
        try {
            if (id == null) {
                throw new NullPointerException("ID IS NULL");
            }
            Profile profile = profileRepository.findById(id).orElseThrow(NullPointerException::new);

            Phone probe = Phone.builder()
                    .isJack(profile.getIsJackThreshold())
                    .isUSB(profile.getIsUSBThreshold())
                    .isWifi(profile.getIsWifiThreshold())
                    .capacity(profile.getBatteryThreshold())
                    .diagonal(profile.getDiagonalThreshold())
                    .ram(profile.getRamThreshold())
                    .rom(profile.getRomThreshold())
                    .build();

            Example<Phone> example = Example.of(probe);

            return phoneRepository
                    .findAll(example)
                    .stream()
                    .map(phoneMapper::phoneToPhoneDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }
}
