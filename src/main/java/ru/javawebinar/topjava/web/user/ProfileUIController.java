package ru.javawebinar.topjava.web.user;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.SessionStatus;
import ru.javawebinar.topjava.to.UserTo;
import ru.javawebinar.topjava.web.SecurityUtil;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static ru.javawebinar.topjava.util.ValidationUtil.findConstraintErrorCode;
import static ru.javawebinar.topjava.util.exception.ErrorType.DATA_ERROR;
import static ru.javawebinar.topjava.web.ExceptionUtil.logError;

@Controller
@RequestMapping("/profile")
public class ProfileUIController extends AbstractUserController {

    @GetMapping
    public String profile() {
        return "profile";
    }

    @PostMapping
    public String updateProfile(HttpServletRequest req, @Valid UserTo userTo, BindingResult result, SessionStatus status) {
        validateDataIntegrityViolation(req, result, () -> super.update(userTo, SecurityUtil.authUserId()));
        if (result.hasErrors()) {
            return "profile";
        } else {
            SecurityUtil.get().setTo(userTo);
            status.setComplete();
            return "redirect:/meals";
        }
    }

    @GetMapping("/register")
    public String register(ModelMap model) {
        model.addAttribute("userTo", new UserTo());
        model.addAttribute("register", true);
        return "profile";
    }

    @PostMapping("/register")
    public String saveRegister(HttpServletRequest req, @Valid UserTo userTo, BindingResult result, SessionStatus status,
                               ModelMap model) {
        validateDataIntegrityViolation(req, result, () -> super.create(userTo));
        if (result.hasErrors()) {
            model.addAttribute("register", true);
            return "profile";
        } else {
            status.setComplete();
            return "redirect:/login?message=app.registered&username=" + userTo.getEmail();
        }
    }

    private void validateDataIntegrityViolation(HttpServletRequest req, BindingResult bindingResult, Runnable code) {
        try {
            if (!bindingResult.hasErrors()) {
                code.run();
            }
        } catch (DataIntegrityViolationException ex) {
            logError(log, req, ex, DATA_ERROR);
            findConstraintErrorCode(ex.getMessage()).ifPresentOrElse(
                    errorCode -> {
                        if (errorCode.toLowerCase().contains("email")) {
                            bindingResult.rejectValue("email", errorCode);
                        } else {
                            bindingResult.reject(errorCode);
                        }
                    },
                    () -> bindingResult.reject("error.invalidInput")
            );
        }
    }
}