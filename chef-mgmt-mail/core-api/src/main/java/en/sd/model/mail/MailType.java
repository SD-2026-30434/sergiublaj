package en.sd.model.mail;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MailType {

    ORDER_PLACED("order-placed"),
    CHEF_WELCOME("chef-welcome");

    private final String template;
}
