package com.pochemuto.homealone;

import com.pochemuto.homealone.ikea.NewItemsMailSender;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile(Profiles.INTEGRATION_TEST)
public class TestConfiguration {
    @MockBean
    private NewItemsMailSender mailSender;
}
