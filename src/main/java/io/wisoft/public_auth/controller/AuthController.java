package io.wisoft.public_auth.controller;

import com.corbado.entities.SessionValidationResult;
import com.corbado.exceptions.StandardException;
import com.corbado.generated.model.Identifier;
import com.corbado.sdk.Config;
import com.corbado.sdk.CorbadoSdk;
import io.wisoft.public_auth.persistance.User;
import io.wisoft.public_auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AuthController {

  @Value("${projectID}")
  private String projectID;

  @Value("${apiSecret}")
  private String apiSecret;

  private CorbadoSdk sdk;
  private Config config;
  private final UserService userService;

  @Autowired
  public AuthController(@Value("${projectID}") final String projectID,
                        @Value("${apiSecret}") final String apiSecret,
                        UserService userService) throws StandardException {
    this.config = new Config(projectID, apiSecret);
    this.userService = userService;
  }

  @GetMapping("/")
  public String index(final Model model) throws StandardException {
    model.addAttribute("PROJECT_ID", projectID);

    this.config.setShortSessionCookieName("cbo_short_session");
    this.config.setShortSessionLength(300);
    this.config.setBackendApi("https://backendapi.cloud.corbado.io");
    this.sdk = new CorbadoSdk(config);


    return "index";  // index.html을 반환
  }


  @RequestMapping("/imsi")
  public String profile(
      @CookieValue("cbo_short_session") final String cboShortSession,
      final Model model) {
    Map<String, Object> responseBody = new HashMap<>();
    User user;
    try {

      // Validate user from token
      final SessionValidationResult validationResp =
          sdk.getSessions().getAndValidateCurrentUser(cboShortSession);
//      // Get user details
      List<Identifier> emails = sdk.getIdentifiers().listAllEmailsByUserId(validationResp.getUserID());
      String userEmail = emails.getFirst().getValue();
//      String userName = validationResp.getFullName();

      List<String> companyDomains = userService.getCompanyDomainByEmail(userEmail);
      model.addAttribute("companyDomains", companyDomains);

      return "imsi";

    } catch (final Exception e) {
      responseBody.put("ERROR", e.getMessage());
      return "error"; // 에러 응답 반환
    }
  }

}