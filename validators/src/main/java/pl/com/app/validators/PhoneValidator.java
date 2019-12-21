package pl.com.app.validators;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import pl.com.app.dto.PhoneDTO;
import pl.com.app.exceptions.exceptions.ExceptionCode;
import pl.com.app.exceptions.exceptions.MyException;
import pl.com.app.model.enums.*;
import pl.com.app.service.PhoneService;

import java.math.BigDecimal;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class PhoneValidator implements Validator {
    private final PhoneService phoneService;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(PhoneDTO.class);
    }

    @Override
    public void validate(Object o, Errors errors) {
        if (o == null) {
            throw new MyException(ExceptionCode.VALIDATION, "OBJECT IS NULL");
        }

        PhoneDTO phoneDTO = (PhoneDTO) o;

        if (StringUtils.isBlank(phoneDTO.getProducer()) || !phoneDTO.getProducer().matches("[A-Z ]+")) {
            errors.rejectValue("producer", "Producer is not correct");
        }
        if (StringUtils.isBlank(phoneDTO.getModel()) || !phoneDTO.getModel().matches("[A-Z0-9\\- ]+")) {
            errors.rejectValue("model", "Model is not correct");
        }
        if (phoneDTO.getPrice() == null || phoneDTO.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            errors.rejectValue("price", "Price is not correct");
        }
        if (phoneDTO.getQuantity() != null && phoneDTO.getQuantity() <= 0) {
            errors.rejectValue("quantity", "Quantity is not correct");
        }
        if (phoneDTO.getQuantity() != null && Arrays.stream(Diagonals.values()).noneMatch(x -> x.equals(phoneDTO.getDiagonal()))) {
            errors.rejectValue("diagonal", "Diagonal is not correct");
        }
        if (phoneDTO.getOperatingSystem() != null && Arrays.stream(OperatingSystem.values()).noneMatch(x -> x.equals(phoneDTO.getOperatingSystem()))) {
            errors.rejectValue("operating_system", "Operating system is not correct");
        }
        if (phoneDTO.getResolution() != null && Arrays.stream(Resolution.values()).noneMatch(x -> x.equals(phoneDTO.getResolution()))) {
            errors.rejectValue("resolution", "Resolution system is not correct");
        }
        if (phoneDTO.getRom() != null && Arrays.stream(Rom.values()).noneMatch(x -> x.equals(phoneDTO.getRom()))) {
            errors.rejectValue("rom", "Rom is not correct");
        }
        if (phoneDTO.getRam() != null && Arrays.stream(Ram.values()).noneMatch(x -> x.equals(phoneDTO.getRam()))) {
            errors.rejectValue("ram", "Ram is not correct");
        }
        if (phoneDTO.getDataCommunication() != null && Arrays.stream(DataCommunication.values()).noneMatch(x -> x.equals(phoneDTO.getDataCommunication()))) {
            errors.rejectValue("data_communication", "Data communication is not correct");
        }
        if (StringUtils.isBlank(phoneDTO.getColor()) || !phoneDTO.getColor().matches("[A-Z ]+")) {
            errors.rejectValue("color", "Color is not correct");
        }
        if (phoneDTO.getScreen() != null && Arrays.stream(Screen.values()).noneMatch(x -> x.equals(phoneDTO.getScreen()))) {
            errors.rejectValue("screen", "Screen is not correct");
        }
        if (phoneDTO.getCapacity() != null && phoneDTO.getCapacity() <= 0) {
            errors.rejectValue("capacity", "Capacity is not correct");
        }
        if (phoneDTO.getMemoryCard() != null && Arrays.stream(MemoryCard.values()).noneMatch(x -> x.equals(phoneDTO.getMemoryCard()))) {
            errors.rejectValue("memory_card", "Memory card is not correct");
        }

        if (!errors.hasErrors()) {
            if (phoneDTO.getId() == null && phoneService.isExistSpecifiedProducerAndModel(phoneDTO.getProducer(), phoneDTO.getModel())) {
                errors.reject("producer_model_exist", "PRODUCER AND MODEL EXIST.");
            }
        }
    }
}
