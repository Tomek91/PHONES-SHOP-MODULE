package pl.com.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.com.app.dto.*;
import pl.com.app.exceptions.exceptions.ExceptionCode;
import pl.com.app.exceptions.exceptions.MyException;
import pl.com.app.mappers.*;
import pl.com.app.parsers.json.*;
import pl.com.app.repository.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
//@Transactional
@RequiredArgsConstructor
public class DataInitializerService {
    private final PhonesConverter phonesConverter;
    private final PhoneMapper phoneMapper;
    private final PhoneRepository phoneRepository;
    private final RoleMapper roleMapper;
    private final UserMapper userMapper;
    private final ProfileMapper profileMapper;
    private final ProfileParametersMapper profileParametersMapper;
    private final UsersConverter usersConverter;
    private final RolesConverter rolesConverter;
    private final ProfilesConverter profilesConverter;
    private final ProfilesParametersConverter profilesParametersConverter;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ProfileRepository profileRepository;
    private final ProfileParametersRepository profileParametersRepository;
    private final PasswordEncoder passwordEncoder;


    public InfoDTO initData() {
        try {
            profileRepository.deleteAll();
            profileParametersRepository.deleteAll();
            phoneRepository.deleteAll();
            userRepository.deleteAll();
            roleRepository.deleteAll();

            List<PhoneDTO> phoneDTOList = phonesConverter.fromJson().orElseThrow(NullPointerException::new);
            List<UserDTO> userDTOList = usersConverter.fromJson().orElseThrow(NullPointerException::new);
            List<RoleDTO> roleDTOList = rolesConverter.fromJson().orElseThrow(NullPointerException::new);
            List<ProfileDTO> profileDTOList = profilesConverter.fromJson().orElseThrow(NullPointerException::new);
            List<ProfileParametersDTO> profileParametersDTOList = profilesParametersConverter.fromJson().orElseThrow(NullPointerException::new);

            profileRepository.saveAll(
                    profileDTOList
                            .stream()
                            .map(profileMapper::profileDTOToProfile)
                            .collect(Collectors.toList())
            );

            profileParametersRepository.saveAll(
                    profileParametersDTOList
                            .stream()
                            .map(profileParametersMapper::profileParametersDTOToProfileParameters)
                            .collect(Collectors.toList())
            );
            roleRepository.saveAll(
                    roleDTOList
                            .stream()
                            .map(roleMapper::roleDTOToRole)
                            .collect(Collectors.toList())
            );

            userRepository.saveAll(
                    userDTOList
                            .stream()
                            .map(userMapper::userDTOToUser)
                            .peek(x -> x.setPassword(passwordEncoder.encode(x.getPassword())))
                            .peek(x -> x.setRole(roleRepository.findByName(x.getRole().getName()).orElseThrow(() -> new NullPointerException("DATA INITIALIZER: FIND ROLE BY NAME EXCEPTION"))))
                            .collect(Collectors.toList())
            );

            phoneRepository.saveAll(
                    phoneDTOList
                            .stream()
                            .map(phoneMapper::phoneDTOToPhone)
                            .collect(Collectors.toList())
            );


            return InfoDTO.builder().info("Init data OK.").build();

        } catch (Exception e) {
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }
}
