package dev.strongtino.soteria.license;

import dev.strongtino.soteria.Soteria;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class LicenseController {

    @GetMapping("/license")
    public LicenseRequestResponse licenseRequestResponse(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse,
            @RequestParam(value = "key", defaultValue = "Unknown") String key,
            @RequestParam(value = "software", defaultValue = "Unknown") String software) throws IOException {

        License license = Soteria.INSTANCE.getLicenseService().getLicenseByKeyAndSoftware(key, software);
        ValidationType type = license == null ? ValidationType.INVALID : ValidationType.VALID;

        Soteria.INSTANCE.getRequestService().insertRequest(httpRequest.getRemoteAddr(), key, software, type);

        if (Soteria.INSTANCE.getRequestService().detectedTooManyRequests(httpRequest.getRemoteAddr())) {
            HttpStatus status = HttpStatus.TOO_MANY_REQUESTS;

            httpResponse.setStatus(status.value());
            httpResponse.getWriter().write("Error " + status.value() + ": " + status.getReasonPhrase());

            return null;
        }
        if (license == null) {
            return new LicenseRequestResponse(type);
        }
        LicenseRequestResponse response = new LicenseRequestResponse(type);

        response.setUser(license.getUser());
        response.setSoftware(license.getSoftware());

        return response;
    }

    @RequiredArgsConstructor
    @Getter
    @Setter
    public static class LicenseRequestResponse {

        private final ValidationType validationType;

        private String user;
        private String software;
    }

    public enum ValidationType {
        VALID,
        INVALID,
    }
}
