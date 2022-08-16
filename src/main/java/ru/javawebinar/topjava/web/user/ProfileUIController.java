package ru.javawebinar.topjava.web.user;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.SessionStatus;
import ru.javawebinar.topjava.to.UserTo;
import ru.javawebinar.topjava.util.ValidationUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import javax.validation.Valid;

import static ru.javawebinar.topjava.util.ValidationUtil.findLocalizedError;

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
    public String updateProfile(@Valid UserTo userTo, BindingResult result, SessionStatus status) throws BindException {
        if (result.hasErrors()) {
            return "profile";
        } else {
            invokeOrThrowInvalid(userTo, result, () -> super.update(userTo, SecurityUtil.authUserId()));
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
    public String saveRegister(@Valid UserTo userTo, BindingResult result, SessionStatus status, ModelMap model)
            throws BindException {
        if (result.hasErrors()) {
            model.addAttribute("register", true);
            return "profile";
        } else {
            invokeOrThrowInvalid(userTo, result, () -> super.create(userTo));
            status.setComplete();
            return "redirect:/login?message=app.registered&username=" + userTo.getEmail();
        }
    }

    private void invokeOrThrowInvalid(UserTo user, BindingResult bindingResult, Runnable code) throws BindException {
        try {
            code.run();
        } catch (DataIntegrityViolationException ex) {
            findLocalizedError(ValidationUtil.getRootCause(ex).getMessage()).ifPresentOrElse(
                    error -> {
                        if ("email".equals(error.getField())) {
                            bindingResult.addError(error.toFieldError(messageSource, user.getEmail()));
                        } else {
                            bindingResult.addError(error.toFieldError(messageSource));
                        }
                    },
                    () -> {
                        var error = new FieldError("userTo", "",
                                messageSource.getMessage("error.invalidInput"));
                        bindingResult.addError(error);
                    }
            );
            throw new BindException(bindingResult);
        }
    }
}