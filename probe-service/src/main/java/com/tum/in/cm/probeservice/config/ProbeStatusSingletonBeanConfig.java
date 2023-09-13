package com.tum.in.cm.probeservice.config;

import com.tum.in.cm.probeservice.util.Constants;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class ProbeStatusSingletonBeanConfig {
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ProbeStatusSingletonBean getProbeStatusSingletonBean() {
        return new ProbeStatusSingletonBean();
    }

    public static class ProbeStatusSingletonBean {
        private Constants.ProbeStatus probeStatus;

        public void setConnected() {
            probeStatus = Constants.ProbeStatus.CONNECTED;
        }

        public void setRunning() {
            probeStatus = Constants.ProbeStatus.RUNNING;
        }

        public Constants.ProbeStatus getProbeStatus() {
            return probeStatus;
        }
    }
}
