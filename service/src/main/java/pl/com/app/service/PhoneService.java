package pl.com.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Example;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import pl.com.app.dto.PhoneDTO;
import pl.com.app.dto.PhonesCustomFilterDataDTO;
import pl.com.app.dto.PhonesFilterDataDTO;
import pl.com.app.dto.ProducerDTO;
import pl.com.app.exceptions.exceptions.ExceptionCode;
import pl.com.app.exceptions.exceptions.MyException;
import pl.com.app.mappers.PhoneMapper;
import pl.com.app.model.Phone;
import pl.com.app.repository.PhoneRepository;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PhoneService {

    private final PhoneRepository phoneRepository;
    private final PhoneMapper phoneMapper;
    private final FileService fileService;
    private final PhoneCompareService phoneCompareService;
    private final AmazonService amazonService;


    private MultipartFile createMultipartFile(PhoneDTO phoneDTO)  {
        try {
            File file = new File(phoneDTO.getImgPath());
            return new MockMultipartFile(file.getName(), file.getName(),
                    "text/plain", FileCopyUtils.copyToByteArray(file));
        } catch (Exception e) {
            throw new MyException(ExceptionCode.FILE, e.getMessage());
        }
    }

    @Cacheable(value = "phones")
    public List<PhoneDTO> findAll() {
        try {
            return phoneRepository
                    .findAll()
                    .stream()
                    .map(phoneMapper::phoneToPhoneDTO)
                  //  .peek(phone -> phone.setFile(createMultipartFile(phone)))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    @Cacheable(value = "phones", key = "#root.args[0]")
    public PhoneDTO findOne(Long id) {
        try {
            if (id == null) {
                throw new NullPointerException("PHONE ID IS NULL");
            }

            return phoneRepository
                    .findById(id)
                    .map(phoneMapper::phoneToPhoneDTO)
                    .stream()
                   // .peek(phone -> phone.setFile(createMultipartFile(phone)))
                    .findFirst()
                    .orElseThrow(NullPointerException::new);
        } catch (Exception e) {
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    @Caching(
            evict = @CacheEvict(value = "phones", allEntries = true, beforeInvocation = true),
            put = {@CachePut(value = "phones", key = "#root.args[0]")}
    )
    public PhoneDTO add(PhoneDTO phoneDTO) {

        try {
            if (phoneDTO == null) {
                throw new NullPointerException("PHONE IS NULL");
            }

            String imgPah = amazonService.uploadFile(phoneDTO.getFile());
            phoneDTO.setImgPath(imgPah);
            Phone phoneFromDb = phoneRepository.save(phoneMapper.phoneDTOToPhone(phoneDTO));
            return phoneMapper.phoneToPhoneDTO(phoneFromDb);
        } catch (Exception e) {
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    @Caching(
            evict = @CacheEvict(value = "phones", allEntries = true, beforeInvocation = true),
            put = {@CachePut(value = "phones", key = "#root.args[0]")}
    )
    public PhoneDTO update(Long id, PhoneDTO phoneDTO) {
        try {
            if (id == null) {
                throw new NullPointerException("ID IS NULL");
            }
            if (phoneDTO == null) {
                throw new NullPointerException("PHONE IS NULL");
            }

            Phone phone = phoneRepository.findById(id).orElseThrow(NullPointerException::new);

            if (phoneDTO.getFile() != null) {
                if (phone.getImgPath() != null) {
                    String[] splitFile = phone.getImgPath().split("/");
                    amazonService.deleteFile(splitFile[splitFile.length - 1]);
                }

                String imgPah = amazonService.uploadFile(phoneDTO.getFile());
                phone.setImgPath(imgPah);
            }

            phone.setProducer(phoneDTO.getProducer() == null ? phone.getProducer() : phoneDTO.getProducer());
            phone.setModel(phoneDTO.getModel() == null ? phone.getModel() : phoneDTO.getModel());
            phone.setPrice(phoneDTO.getPrice() == null ? phone.getPrice() : phoneDTO.getPrice());
            phone.setQuantity(phoneDTO.getQuantity() == null ? phone.getQuantity() : phoneDTO.getQuantity());
            phone.setDiagonal(phoneDTO.getDiagonal() == null ? phone.getDiagonal() : phoneDTO.getDiagonal());
            phone.setOperatingSystem(phoneDTO.getOperatingSystem() == null ? phone.getOperatingSystem() : phoneDTO.getOperatingSystem());
            phone.setResolution(phoneDTO.getResolution() == null ? phone.getResolution() : phoneDTO.getResolution());
            phone.setRom(phoneDTO.getRom() == null ? phone.getRom() : phoneDTO.getRom());
            phone.setRam(phoneDTO.getRam() == null ? phone.getRam() : phoneDTO.getRam());
            phone.setDataCommunication(phoneDTO.getDataCommunication() == null ? phone.getDataCommunication() : phoneDTO.getDataCommunication());
            phone.setIsDualSim(phoneDTO.getIsDualSim() == null ? phone.getIsDualSim() : phoneDTO.getIsDualSim());
            phone.setColor(phoneDTO.getColor() == null ? phone.getColor() : phoneDTO.getColor());
            phone.setScreen(phoneDTO.getScreen() == null ? phone.getScreen() : phoneDTO.getScreen());
            phone.setCapacity(phoneDTO.getCapacity() == null ? phone.getCapacity() : phoneDTO.getCapacity());
            phone.setIsBluetooth(phoneDTO.getIsBluetooth() == null ? phone.getIsBluetooth() : phoneDTO.getIsBluetooth());
            phone.setIsWifi(phoneDTO.getIsWifi() == null ? phone.getIsWifi() : phoneDTO.getIsWifi());
            phone.setIsJack(phoneDTO.getIsJack() == null ? phone.getIsJack() : phoneDTO.getIsJack());
            phone.setIsUSB(phoneDTO.getIsUSB() == null ? phone.getIsUSB() : phoneDTO.getIsUSB());
            phone.setMemoryCard(phoneDTO.getMemoryCard() == null ? phone.getMemoryCard() : phoneDTO.getMemoryCard());
            phone.setImgPath(phoneDTO.getImgPath() == null ? phone.getImgPath() : phoneDTO.getImgPath());

            Phone phoneAfterUpdate = phoneRepository.save(phone);
            return phoneMapper.phoneToPhoneDTO(phoneAfterUpdate);
        } catch (Exception e) {
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    @CacheEvict(value = "phones", allEntries = true)
    public PhoneDTO delete(Long id) {

        try {
            if (id == null) {
                throw new NullPointerException("PHONE ID IS NULL");
            }
            Phone phoneToDelete = phoneRepository.findById(id)
                    .orElseThrow(NullPointerException::new);
            phoneRepository.delete(phoneToDelete);

            String[] splitFile = phoneToDelete.getImgPath().split("/");
            amazonService.deleteFile(splitFile[splitFile.length - 1]);
            return phoneMapper.phoneToPhoneDTO(phoneToDelete);


        } catch (Exception e) {
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    public boolean isExistSpecifiedProducerAndModel(String producer, String model) {
        try {
            if (producer == null) {
                throw new NullPointerException("PRODUCER IS NULL");
            }
            if (model == null) {
                throw new NullPointerException("MODEL IS NULL");
            }
            Phone probe = Phone.builder().producer(producer).model(model).build();
            Example<Phone> example = Example.of(probe);
            return phoneRepository.exists(example);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    public List<ProducerDTO> findAllPhoneProducers() {
        try {
            return phoneRepository
                    .findAllPhoneProducers()
                    .stream()
                    .map(x -> ProducerDTO.builder().name(x).build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    public List<PhoneDTO> findAllPhoneByProducers(List<ProducerDTO> producerDTOList) {
        try {
            if (producerDTOList == null) {
                throw new NullPointerException("PRODUCER LIST IS NULL");
            }
            return phoneRepository
                    .findAllByProducerIn(producerDTOList.stream().map(ProducerDTO::getName).collect(Collectors.toList()))
                    .stream()
                    .map(phoneMapper::phoneToPhoneDTO)
                    .sorted(Comparator.comparing(PhoneDTO::getProducer).thenComparing(PhoneDTO::getModel))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    public List<PhoneDTO> findAllChosePhones(List<PhoneDTO> phoneDTOList) {
        try {
            if (phoneDTOList == null) {
                throw new NullPointerException("PHONE LIST IS NULL");
            }
            return phoneRepository
                    .findAllByIdIn(phoneDTOList.stream().map(PhoneDTO::getId).collect(Collectors.toList()))
                    .stream()
                    .map(phoneMapper::phoneToPhoneDTO)
                    .sorted(Comparator.comparing(PhoneDTO::getProducer).thenComparing(PhoneDTO::getModel))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    public PhonesCustomFilterDataDTO findAllFilterPhones(PhonesFilterDataDTO phonesFilterDataDTO) {
        try {
            if (phonesFilterDataDTO == null) {
                throw new NullPointerException("PHONE FILTER LIST IS NULL");
            }

            List<PhoneDTO> phoneListToShow = phonesFilterDataDTO.getPhoneDTOList()
                    .stream()
                    .filter(x -> phonesFilterDataDTO.getPrice() == null || phonesFilterDataDTO.getPrice().equals(x.getPrice()))
                    .filter(x -> phonesFilterDataDTO.getQuantity() == null || phonesFilterDataDTO.getQuantity().equals(x.getQuantity()))
                    .filter(x -> phonesFilterDataDTO.getDiagonals() == null || phonesFilterDataDTO.getDiagonals().contains(x.getDiagonal()))
                    .filter(x -> phonesFilterDataDTO.getOperatingSystems() == null || phonesFilterDataDTO.getOperatingSystems().contains(x.getOperatingSystem()))
                    .filter(x -> phonesFilterDataDTO.getResolutions() == null || phonesFilterDataDTO.getResolutions().contains(x.getResolution()))
                    .filter(x -> phonesFilterDataDTO.getRoms() == null || phonesFilterDataDTO.getRoms().contains(x.getRom()))
                    .filter(x -> phonesFilterDataDTO.getRams() == null || phonesFilterDataDTO.getRams().contains(x.getRam()))
                    .filter(x -> phonesFilterDataDTO.getDataCommunications() == null || phonesFilterDataDTO.getDataCommunications().contains(x.getDataCommunication()))
                    .filter(x -> phonesFilterDataDTO.getDataCommunications() == null || phonesFilterDataDTO.getDataCommunications().contains(x.getDataCommunication()))
                    .filter(x -> phonesFilterDataDTO.getIsDualSim() == null || phonesFilterDataDTO.getIsDualSim().equals(x.getIsDualSim()))
                    .filter(x -> phonesFilterDataDTO.getColor() == null || phonesFilterDataDTO.getColor().equals(x.getColor()))
                    .filter(x -> phonesFilterDataDTO.getColor() == null || phonesFilterDataDTO.getColor().equals(x.getColor()))
                    .filter(x -> phonesFilterDataDTO.getScreens() == null || phonesFilterDataDTO.getScreens().contains(x.getScreen()))
                    .filter(x -> phonesFilterDataDTO.getCapacity() == null || phonesFilterDataDTO.getCapacity().equals(x.getCapacity()))
                    .filter(x -> phonesFilterDataDTO.getIsBluetooth() == null || phonesFilterDataDTO.getIsBluetooth().equals(x.getIsBluetooth()))
                    .filter(x -> phonesFilterDataDTO.getIsWifi() == null || phonesFilterDataDTO.getIsWifi().equals(x.getIsWifi()))
                    .filter(x -> phonesFilterDataDTO.getIsJack() == null || phonesFilterDataDTO.getIsJack().equals(x.getIsJack()))
                    .filter(x -> phonesFilterDataDTO.getIsUSB() == null || phonesFilterDataDTO.getIsUSB().equals(x.getIsUSB()))
                    .filter(x -> phonesFilterDataDTO.getMemoryCards() == null || phonesFilterDataDTO.getMemoryCards().contains(x.getMemoryCard()))
                    .collect(Collectors.toList());

            if (phonesFilterDataDTO.getSortDataDTO() != null) {
                phoneListToShow = phoneCompareService
                        .sort(phoneListToShow, phonesFilterDataDTO.getSortDataDTO().getSortType(), phonesFilterDataDTO.getSortDataDTO().isDescending());
            }

            return PhonesCustomFilterDataDTO.builder()
                    .phoneListBase(phonesFilterDataDTO.getPhoneDTOList())
                    .phoneListToShow(phoneListToShow)
                    .build();
        } catch (Exception e) {
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }
}
