package com.ecommerce.user_service.services;


import com.ecommerce.user_service.dto.SignUpDTO;
import com.ecommerce.user_service.dto.SignUpResponseDTO;
import com.ecommerce.user_service.entities.User;
import com.ecommerce.user_service.exceptions.UserAlreadyExistsException;
import com.ecommerce.user_service.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public SignUpResponseDTO signUp(SignUpDTO signUpDTO) {
        boolean isExist = userRepository.existsByEmail(signUpDTO.getEmail());

        if(isExist){
            throw new UserAlreadyExistsException("User with email " + signUpDTO.getEmail() + " already exists");
        }

        User userToCreate = new ModelMapper().map(signUpDTO, User.class);
        userToCreate.setPassword(passwordEncoder.encode(userToCreate.getPassword()));

        User savedUser = userRepository.save(userToCreate);

        return modelMapper.map(savedUser, SignUpResponseDTO.class);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BadCredentialsException("User not found"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new BadCredentialsException(username + " not found"));
    }
}
