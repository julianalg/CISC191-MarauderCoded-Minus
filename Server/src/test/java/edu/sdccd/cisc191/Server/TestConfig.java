package edu.sdccd.cisc191.Server;

import edu.sdccd.cisc191.Server.API.BaseballGetter;
import edu.sdccd.cisc191.Server.API.BasketballGetter;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

@TestConfiguration
class TestConfig {
    @Bean
    public BaseballGetter baseballGetter() {
        return mock(BaseballGetter.class);
    }

    @Bean
    public BasketballGetter basketballGetter() {
        return mock(BasketballGetter.class);
    }
}