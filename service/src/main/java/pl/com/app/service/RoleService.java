package pl.com.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import pl.com.app.dto.RoleDTO;
import pl.com.app.exceptions.exceptions.ExceptionCode;
import pl.com.app.exceptions.exceptions.MyException;
import pl.com.app.mappers.RoleMapper;
import pl.com.app.repository.RoleRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public Set<RoleDTO> getAllRoles() {
        try {
            return roleRepository.findAll()
                    .stream()
                    .map(roleMapper::roleToRoleDTO)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    public RoleDTO getOneRole(Long roleId) {
        try {
            if (roleId == null) {
                throw new NullPointerException("ROLE ID IS NULL");
            }

            return roleRepository
                    .findById(roleId)
                    .map(roleMapper::roleToRoleDTO)
                    .orElseThrow(NullPointerException::new);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }
}
