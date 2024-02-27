package com.pochemuto.homealone;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile(Profiles.INTEGRATION_TEST)
public class TestConfiguration {
}
