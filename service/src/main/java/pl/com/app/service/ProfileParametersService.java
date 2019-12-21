package pl.com.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import pl.com.app.dto.PhoneDTO;
import pl.com.app.dto.ProfileParametersDTO;
import pl.com.app.exceptions.exceptions.ExceptionCode;
import pl.com.app.exceptions.exceptions.MyException;
import pl.com.app.mappers.PhoneMapper;
import pl.com.app.mappers.ProfileParametersMapper;
import pl.com.app.model.Phone;
import pl.com.app.model.ProfileParameters;
import pl.com.app.model.enums.DataCommunication;
import pl.com.app.model.enums.Screen;
import pl.com.app.repository.PhoneRepository;
import pl.com.app.repository.ProfileParametersRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProfileParametersService {

    private final ProfileParametersRepository profileParametersRepository;
    private final PhoneRepository phoneRepository;
    private final PhoneMapper phoneMapper;
    private final ProfileParametersMapper profileParametersMapper;

    public List<ProfileParametersDTO> findAll() {
        try {
            return profileParametersRepository
                    .findAll()
                    .stream()
                    .map(profileParametersMapper::profileParametersToProfileParametersDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    public ProfileParametersDTO findOne(Long id) {
        try {
            if (id == null) {
                throw new NullPointerException("PROFILE PARAMETERS ID IS NULL");
            }

            return profileParametersRepository
                    .findById(id)
                    .map(profileParametersMapper::profileParametersToProfileParametersDTO)
                    .orElseThrow(NullPointerException::new);
        } catch (Exception e) {
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    public ProfileParametersDTO update(Long id, ProfileParametersDTO profileParametersDTO) {
        try {
            if (id == null) {
                throw new NullPointerException("ID IS NULL");
            }
            if (profileParametersDTO == null) {
                throw new NullPointerException("PROFILE PARAMETERS IS NULL");
            }

            ProfileParameters profileParameters = profileParametersRepository.findById(id).orElseThrow(NullPointerException::new);

            profileParameters.setBatteryBusiness(profileParametersDTO.getBatteryBusiness() == null ? profileParametersDTO.getBatteryBusiness() : profileParametersDTO.getBatteryBusiness());
            profileParameters.setBatteryEntertainment(profileParametersDTO.getBatteryEntertainment() == null ? profileParametersDTO.getBatteryEntertainment() : profileParametersDTO.getBatteryEntertainment());
            profileParameters.setRamEntertainment(profileParametersDTO.getRamEntertainment() == null ? profileParametersDTO.getRamEntertainment() : profileParametersDTO.getRamEntertainment());
            profileParameters.setDiagonalEntertainment(profileParametersDTO.getDiagonalEntertainment() == null ? profileParametersDTO.getDiagonalEntertainment() : profileParametersDTO.getDiagonalEntertainment());
            profileParameters.setRomEntertainment(profileParametersDTO.getRomEntertainment() == null ? profileParametersDTO.getRomEntertainment() : profileParametersDTO.getRomEntertainment());


            ProfileParameters profileParametersAfterUpdate = profileParametersRepository.save(profileParameters);
            return profileParametersMapper.profileParametersToProfileParametersDTO(profileParametersAfterUpdate);
        } catch (Exception e) {
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    public List<PhoneDTO> findAllBusinessProfile () {
        try {
            ProfileParameters profileParameters = profileParametersRepository
                    .findAll()
                    .stream()
                    .findFirst()
                    .orElseThrow(NullPointerException::new);

            Phone probe = Phone.builder()
                    .isDualSim(Boolean.TRUE)
                    .isBluetooth(Boolean.TRUE)
                    .isWifi(Boolean.TRUE)
                    .isJack(Boolean.TRUE)
                    .isUSB(Boolean.TRUE)
                    .dataCommunication(DataCommunication.DC_LTE)
                    .build();

            Example<Phone> example = Example.of(probe);

            return phoneRepository
                    .findAll(example)
                    .stream()
                    .filter(x -> x.getCapacity() >= profileParameters.getBatteryBusiness())
                    .map(phoneMapper::phoneToPhoneDTO)

                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    public List<PhoneDTO> findAllEntertainmentProfile () {
        try {
            ProfileParameters profileParameters = profileParametersRepository
                    .findAll()
                    .stream()
                    .findFirst()
                    .orElseThrow(NullPointerException::new);

            return phoneRepository
                    .findAll()
                    .stream()
                    .filter(x -> x.getRom().compareTo(profileParameters.getRomEntertainment()) >= 0)
                    .filter(x -> x.getRam().compareTo(profileParameters.getRamEntertainment()) >= 0)
                    .filter(x -> x.getCapacity().compareTo(profileParameters.getBatteryEntertainment()) >= 0)
                    .filter(x -> x.getDiagonal().compareTo(profileParameters.getDiagonalEntertainment()) >= 0)
                    .filter(x -> x.getScreen().equals(Screen.AMOLED) || x.getScreen().equals(Screen.SUPER_LCD))
                    .map(phoneMapper::phoneToPhoneDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }
}
