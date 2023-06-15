package rw.aime.ne.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import rw.aime.ne.dtos.SignInDTO;
import rw.aime.ne.dtos.SignUpDTO;
import rw.aime.ne.enums.ERole;
import rw.aime.ne.models.User;
import rw.aime.ne.payload.ApiResponse;
import rw.aime.ne.payload.JWTAuthenticationResponse;
import rw.aime.ne.security.JwtTokenProvider;
import rw.aime.ne.services.IUserService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/api/v1/auth")
public class UserController {

    private final IUserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserController(IUserService userService, BCryptPasswordEncoder bCryptPasswordEncoder, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping(path = "/me")
    public ResponseEntity<ApiResponse> currentlyLoggedInUser() {
        return ResponseEntity.ok(new ApiResponse(true, userService.getLoggedInUser()));
    }

    @PostMapping(path = "/register/as-standard")
    public ResponseEntity<ApiResponse> registerAsStandard(@Valid @RequestBody SignUpDTO dto) {

        User user = new User();

        String encodedPassword = bCryptPasswordEncoder.encode(dto.getPassword());

        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setGender(dto.getGender());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setNationalId(dto.getNationalId());
        user.setPassword(encodedPassword);
        user.setRole(ERole.STANDARD);

        User entity = this.userService.create(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, entity));
    }

    @PostMapping(path = "/register/as-admin")
    public ResponseEntity<ApiResponse> registerAsAdmin(@Valid @RequestBody SignUpDTO dto) {

        User user = new User();

        String encodedPassword = bCryptPasswordEncoder.encode(dto.getPassword());

        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setGender(dto.getGender());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setNationalId(dto.getNationalId());
        user.setPassword(encodedPassword);
        user.setRole(ERole.ADMIN);

        User entity = this.userService.create(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, entity));
    }

    @PostMapping(path = "/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody SignInDTO signInDTO){
        String jwt = null;
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInDTO.getEmail(),signInDTO.getPassword()));
        try{
            SecurityContextHolder.getContext().setAuthentication(authentication);
            jwt = jwtTokenProvider.generateToken(authentication);
        }
        catch (Exception e){
        }

        return ResponseEntity.ok(ApiResponse.success(new JWTAuthenticationResponse(jwt)));
    }

    @GetMapping(path = "/routes/admin")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ApiResponse> adminRoute(){
        return ResponseEntity.ok(ApiResponse.success("Admin route"));
    }

    @GetMapping(path = "/routes/standard")
    @PreAuthorize("hasAnyAuthority('STANDARD')")
    public ResponseEntity<ApiResponse> standardRoute(){
        return ResponseEntity.ok(ApiResponse.success("Standard route"));
    }
}