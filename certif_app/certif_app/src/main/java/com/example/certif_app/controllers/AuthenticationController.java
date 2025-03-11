package com.example.certif_app.controllers;

import com.example.certif_app.dto.request.SignUpRequest;
import com.example.certif_app.dto.request.SigninRequest;
import com.example.certif_app.dto.response.JwtAuthenticationResponse;
import com.example.certif_app.services.security.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jboss.logging.BasicLogger;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin(origins="*")
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    @CrossOrigin(origins="*")
    @PostMapping("/signup")
    public ResponseEntity<JwtAuthenticationResponse> signup(@RequestBody SignUpRequest request) {
        return ResponseEntity.ok(authenticationService.SignUp(request));
    }
    @CrossOrigin(origins = "*",methods = {RequestMethod.POST})
    @PostMapping("/signin")
    public ResponseEntity<JwtAuthenticationResponse> signin(@RequestBody SigninRequest request, HttpServletResponse response) {
        JwtAuthenticationResponse jwtResponse = authenticationService.Signin(request);

        if (jwtResponse != null && jwtResponse.getToken() != null) {
            // Set the token in the response header
            //Header Details:
            //
            //Access-Control-Expose-Headers:
            //Specifies which headers the client is allowed to access in the response.
            //Adds Authorization to make the token visible to the client.
            //Access-Control-Allow-Headers:
            //Specifies the headers the server accepts in a request.
            //Includes standard headers (Content-Type, Authorization) and custom headers (X-Custom-header).
            //Authorization:
            //Sets the Authorization header with the Bearer token. This allows clients to store and use the token for authentication in subsequent requests.
            response.setHeader("Access-Control-Expose-Headers", "Authorization");
            response.setHeader("Access-Control-Allow-Headers", "Authorization, X-Pingother, Origin, X-Requested-with, Content-Type, Accept, X-Custom-header");
            response.setHeader("Authorization", "Bearer " + jwtResponse.getToken());            // Return a response with user details in the body
            JSONObject responseBody = new JSONObject();
            responseBody.put("userID", jwtResponse.getUserId());
            responseBody.put("role", jwtResponse.getRole());
            return ResponseEntity.ok(jwtResponse);
        } else {
            return ResponseEntity.badRequest().body(jwtResponse); // Assuming jwtResponse can be null
        }
    }


}
