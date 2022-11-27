package com.example.springdartsgame.oauth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

@Configuration
@EnableAuthorizationServer
public class OAuthConfiguration extends AuthorizationServerConfigurerAdapter {

    private final AuthenticationManager authenticationManager;
    private final String[] authorizedGrantTypes = new String[]{"password", "authorization_code", "refresh_token"};

    public OAuthConfiguration(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        this.authenticationManager = authenticationConfiguration.getAuthenticationManager();
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer.checkTokenAccess("permitAll()");
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        String clientId = "hyperdarts";
        String clientSecret = "secret";
        int accessTokenValiditySeconds = 3600;
        int refreshTokenValiditySeconds = 3600;
        clients.inMemory()
                .withClient(clientId)
                .secret("{noop}" + clientSecret) // NoOpPasswordEncoder
                .accessTokenValiditySeconds(accessTokenValiditySeconds)
                .refreshTokenValiditySeconds(refreshTokenValiditySeconds)
                .authorizedGrantTypes(authorizedGrantTypes)
                .scopes("read", "write", "update")
                .resourceIds("api");
    }

    @Override
    public void configure(final AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints
                .accessTokenConverter(accessTokenConverter())
                .authenticationManager(authenticationManager);
    }

    @Bean
    JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        String jwtSigningKey = "-----BEGIN RSA PRIVATE KEY-----" +
                "MIICWwIBAAKBgQDQ+7yKlJGuvYtf1soMsJjkQJGAXe90QAxqppycf+3JT5ehnvvWtwS8ef+UsqrNa5Rc9" +
                "tyyHjP7ZXRN145SlRTZzc0d03Ez10OfAEVdhGACgRxS5s+GZVtdJuVcje3Luq3VIvZ8mV/P4eRcV3yVKD" +
                "wQEenMuL6Mh6JLH48KxgbNRQIDAQABAoGAd5k5w41W+kvbcZO4uh5uwWH6Yx5fJYZqFLcZNa845Fa6jnI" +
                "v6id/fGXNUMoXWcxRcgqNLxp94Uekkc/k0XokHaEac21ReDDVmufgwujoUHVacDEWWkkool0FVBirmlWJ" +
                "hM8Kt0Tyr7GmUilktekTt2QC/pL0LJCbo8Exmg3DnFkCQQDpb89ftQ35zBqs+BAl9zCa3cxYGGHlBLKLP" +
                "Kk0MZeCSQ8iY37fwTPlpY/fmNo/rQTDLDemJ/CYNxLOFyrPBVfDAkEA5S7ZFMH+c8D2O+73p82m0ZH96a" +
                "fYC2kA0UFoitAntUL/hjxfWMPU5QnK5n+2gCTHynVSogCPGQovZfoHsax+VwJAH3Zba9naPV2+BqwUeRl" +
                "86pKUVRdMMnLUoaGWaJt6gSvZp1fjpMLEfOI4pvlSCR0HtEqEYZemfM2HclF7CpX8wwJARt7Hzj13HABt" +
                "pHbvKnrTvTayGBEJI+4ijJL3awWXYdwH/FCrA137daAjmEeh/dph1d+V3/bgSVP2+EfrHSxEHQJALeyli" +
                "JOUCrXM6hqksKuJlSOxArd3UiQe9t/q6woGTC3Y2tz7Yw5CZkDPqHchmGv7+ZZv5dh2EHtxsM1SXUFVfQ" +
                "==-----END RSA PRIVATE KEY-----";
        converter.setSigningKey(jwtSigningKey);
        return converter;
    }
}
