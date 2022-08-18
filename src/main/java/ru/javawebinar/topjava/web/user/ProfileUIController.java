package ru.javawebinar.topjava.web.user;

import org.springframework.context.support.MessageSourceAccessor;
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

import static ru.javawebinar.topjava.util.ValidationUtil.findLocalizedError;
import static ru.javawebinar.topjava.util.exception.ErrorType.DATA_ERROR;
import static ru.javawebinar.topjava.web.ExceptionUtil.logError;

@Controller
@RequestMapping("/profile")
public class ProfileUIController extends AbstractUserController {

    private final MessageSourceAccessor messageSource;

    public ProfileUIController(MessageSourceAccessor messageSource) {
        this.messageSource = messageSource;
    }

    @GetMapping
    public String profile() {
        return "profile";
    }

    @PostMapping
    public String updateProfile(HttpServletRequest req, @Valid UserTo userTo, BindingResult result, SessionStatus status) {
        invokeOrSetInvalid(req, result, () -> super.update(userTo, SecurityUtil.authUserId()));
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
        invokeOrSetInvalid(req, result, () -> super.create(userTo));
        if (result.hasErrors()) {
            model.addAttribute("register", true);
            return "profile";
        } else {
            status.setComplete();
            return "redirect:/login?message=app.registered&username=" + userTo.getEmail();
        }
    }

    private void invokeOrSetInvalid(HttpServletRequest req, BindingResult bindingResult, Runnable code) {
        try {
            if (!bindingResult.hasErrors()) {
                code.run();
            }
        } catch (DataIntegrityViolationException ex) {
            logError(log, req, ex, DATA_ERROR);
            findLocalizedError(ex.getMessage(), messageSource).ifPresentOrElse(
                    error -> {
                        if (error.toLowerCase().contains("email")) {
                            bindingResult.rejectValue("email", error);
                        } else {
                            bindingResult.reject(error);
                        }
                    },
                    () -> bindingResult.reject(messageSource.getMessage("error.invalidInput"))
            );
        }
    }
}